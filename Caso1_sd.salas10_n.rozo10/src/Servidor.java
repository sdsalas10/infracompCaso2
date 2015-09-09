 class Servidor extends Thread{

	
	 /**
	  * El mensaje que atiende
	  */
	private Mensaje mensaje;
	
	/**
	 * Buffer que hace de intermediario entre la comunicacion entre los servidores y clientes
	 */
	private Buffer buffer;

	/**
	 * Variable que responde si debe estar funcionando o no el servidor
	 */
	private boolean corre;
	
	//Constructor
	public Servidor( Buffer buffer){
		
		this.buffer = buffer;
		mensaje = new Mensaje(null);
		corre = true;
	}

	
	/**
	 * Retorna el mensaje respondido
	 * @return
	 */
	public Mensaje getMensaje() {
		
		return mensaje;
	}


	/**
	 * Define el mensaje a responder
	 * @param mensaje
	 */
	public void setMensaje(Mensaje mensaje) {
		this.mensaje = mensaje;
	}
	
	/**
	 * Asigna false la variable corre para detener el thread
	 */
	public void detener(){
		corre = false;
		System.out.println("Se detuvo un servidor");
	}
	
	/**
	 * Contiene las acciones del servidor
	 */
	public void run(){
		
		while(corre){
			
			buffer.remover(this);			
			yield();
		}
	}
}
