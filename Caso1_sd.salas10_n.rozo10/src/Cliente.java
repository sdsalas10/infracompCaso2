public class Cliente  extends Thread{

	/**
	 * Identificacion del cliente
	 */
	private int ID;

	/**
	 * Lista que contiene los mensajes del Cliente
	 */
	private Mensaje[] mensajes;

	
	/**
	 * Buffer que hace de intermediario entre la comunicacion entre los servidores y clientes
	 */
	private Buffer buffer;


	//Constructor
	public Cliente(int id, int nMensajes, Buffer buffer){
		mensajes = new Mensaje[nMensajes];
		this.ID = id;
		this.buffer = buffer;

	}

	public void run(){

		for (int i = 0; i < mensajes.length; i++) {

			Mensaje mensaje = new Mensaje(this);
			System.out.println("Cliente: " + ID + " Mensaje #:" + i);
			mensajes[i] = mensaje;			
			buffer.encolarMensaje(mensaje);

		}
		
		buffer.termine();
	}

	
	/**
	 * Retorna la identificacion del cliente
	 * @return
	 */
	public int getID() {
		
		return ID;
	}

	/**
	 * Asigna la identificacion del cliente
	 * @param iD
	 */
	public void setID(int iD) {
		
		ID = iD;
	}
	
	/**
	 * Retorna el buffer 
	 * @return
	 */
	public Buffer getB(){
		
		return buffer;
	}

}


