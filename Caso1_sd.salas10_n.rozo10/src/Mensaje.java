public class Mensaje {

	
	/**
	 * contenido del mensaje
	 */
	private String consulta;

	/**
	 * contenido de respuesta de la consulta
	 */
	private String respuesta;

	/**
	 * El cliente dueño del mensaje
	 */
	private Cliente padre;

	
	//Constructor
	public Mensaje(Cliente padre){

		this.padre = padre;
		consulta = "";
		respuesta = "";
	}

	
	/**
	 * Retorna una cadena con  la consulta del mensaje
	 * @return
	 */
	public String getConsulta() {
		
		return consulta;
	}
	

	/**
	 * Asigna la consulta del mensaje
	 * @param consulta
	 */
	
	public void setConsulta(String consulta) {
		
		this.consulta = consulta;
	}


	/**
	 * Retorna una cadena con la respuesta del mensaje
	 * @return
	 */
	public String getRespuesta() {
		
		return respuesta;
	}


	/**
	 * Asigna la respuesta del mensaje
	 * @param respuesta
	 */
	public void setRespuesta(String respuesta) {
		
		this.respuesta = respuesta;
	}


	/**
	 * Retorna una cadena con el dueño del mensaje
	 * @return
	 */
	public Cliente getPadre() {
		
		return padre;
	}

	/**
	 * Manda a espera al padre del mensaje que fue almacenado en el buffer
	 */
	public void dormirPadre(){
		
		synchronized(padre){
			
			try {
				System.out.println("Dormir al Usuario: " + padre.getID()+"  dentro de la cola");			
				padre.getB().wait();

			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
	}

	/**
	 * Despierta al padre del mensaje que estaba esperando a que esta esperando respuesta 
	 */
	public void levantarPadre(){
		
		System.out.println("Se levanto el usuario: " + padre.getID());
		padre.getB().notify();
	}



}
