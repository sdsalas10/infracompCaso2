import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
public class Buffer {


	/**
	 * Lista que contiene los mensajes que se manejan en el buffer
	 */
	private ArrayList<Mensaje> mensajes;

	/**
	 * Lista que contiene los servidores que responden los mensajes
	 */
	private static ArrayList<Servidor> servidores;
	
	/**
	 * numero que hace la cantidad de mensajes que pueden almacenarse en el buffer
	 */
	private int limite;
	
	/**
	 * contador total de mensajes enviados
	 */
	private int variable;
	
	/**
	 * Contador de los clientes que han sido atendidos
	 */
	private int cantidadClientes;
	
	//Constructor
	public Buffer(int nMensajes, int cantidadClientes){

		variable = 0;
		servidores = new ArrayList<Servidor>();
		mensajes = new ArrayList<Mensaje>();
		limite = nMensajes;
		this.cantidadClientes = cantidadClientes;
	}


	/**
	 * Encola un mensaje a la lista del buffer, si la lista esta llena el Cliente debe esperar 
	 * sino invoca el metodo de agregar mensaje a la lista
	 * @param m
	 */
	public void encolarMensaje(Mensaje m){

		while(mensajes.size()==limite){
			try {
				
				synchronized (this) {
					
					System.out.println("DUERME PARA SIEMPRE------------------------"+m.getPadre() );
					wait();
				}
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}

		agregarMensaje(m);

	}

	
	/**
	 * Un cliente se apropia del buffer para agregar un mensaje (exclusion mutua)
	 * agrega el mensaje a la lista
	 * Aumenta el registro de mensajes enviados
	 * Hace esperar al padre del mensaje hasta que un servidor responda el mensaje
	 * @param m
	 */
	public synchronized void agregarMensaje(Mensaje m){

		mensajes.add(m);			
		variable++;
		System.out.println("SUICIDA");
		m.dormirPadre();
		System.out.println("Total Mensajes: " + variable);

	}


	/**
	 * Un servidor se apropia del buffer para responder un mensaje (exclusion mutua)
	 * Remueve el mensaje de la lista
	 * Despierta, notifica,  al padre que el mensaje se respondio
	 * Notifica a los clientes de que hay un espacio libre en la lista 
	 * @param s
	 */
	public synchronized void remover(Servidor s){

		synchronized(this){
			
			if(!mensajes.isEmpty()){
				
				s.setMensaje(mensajes.get(0));
				mensajes.remove(0);
				s.getMensaje().levantarPadre();
				notifyAll();
				try {
					
					Thread.sleep(3);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			}
		}


	}


	/**
	 * Calcula la cantida dde cleintes restantes donde si se cumple la condicion que 
	 * se acabaron manda a parar la ejecucion de los servidores
	 */
	public void termine(){

		cantidadClientes--;
		System.out.println("Un cliente termino, quedan: " + cantidadClientes);
		if(cantidadClientes==0){

			for(int i=0; i < servidores.size(); i++){
				Servidor s = servidores.get(i);
				s.detener();
			}
		}
	}

	
	/**
	 * inicializa toda la estructura con el 
	 * número de clientes, el número de servidores
	 * y el número de consultas de cada uno de los
	 *  clientes desde el archivo config.properties
	 * @param args
	 */
	public static void main(String[] args){



		Properties prop = new Properties();
		String propFileName = "config.properties";
		InputStream inputS = Buffer.class.getClassLoader().getResourceAsStream(propFileName);

		if(inputS!=null){
			try {
				prop.load(inputS);
			} catch (IOException e) {
			}
		}

		int cantB =  Integer.parseInt(prop.getProperty("cantidadBuffer"));
		int cant =  Integer.parseInt(prop.getProperty("cantidadConsultas"));
		int nC = Integer.parseInt(prop.getProperty("cantidadClientes"));
		int nS = Integer.parseInt(prop.getProperty("cantidadServidores"));

		Buffer buffer = new Buffer(cantB, nC);

		for (int i = 0; i < nC; i++) {

			Cliente c = new Cliente(i, cant, buffer);
			System.out.println("Se creo el cliente: #" + i);
			c.start();
		}

		for (int i = 0; i < nS; i++) {

			Servidor s = new Servidor(buffer);
			servidores.add(s);
			s.start();
		} 

	}

}
