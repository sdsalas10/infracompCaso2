import java.util.ArrayList;


public class Buffer {


	private ArrayList<Mensaje> mensajes;	
	private Semafore semaforo;
	private int variable;

	public Buffer(int nMensajes){

		semaforo = new Semafore(nMensajes);
		variable = 0;
		mensajes = new ArrayList<Mensaje>();

	}


	public synchronized void agregarMensaje(Mensaje m){

		semaforo.agregar(m);
		mensajes.add(m);
		variable++;
		
		try {
			m.getPadre().wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public boolean removerMensajeServidor(Servidor s){
		boolean valido = false;
		if(mensajes.size()!=0){
			
		semaforo.liberar(s.getMensaje());
		s.setMensaje(mensajes.get(0));
		mensajes.remove(0);
		valido = true;
		
		}
		return valido;
	}



	public void notificar(Mensaje m){
		synchronized (m){
		m.getPadre().notify();
		System.out.println("LO LIBERE");
		}
	}

	public static void main(String[] args){

		int cant = 5;
		int nC = 8;
		int nS = 1;

		Buffer buffer = new Buffer(cant);
		
		for (int i = 0; i < nS; i++) {

			Servidor s = new Servidor(buffer);
			s.start();
		} 
		
		for (int i = 0; i < nC; i++) {

			int nM = 2; 
			Cliente c = new Cliente(i, nM, buffer);

			c.start();
		}

	}

}
