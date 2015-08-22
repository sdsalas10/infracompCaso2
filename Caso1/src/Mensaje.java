
public class Mensaje {

	private String consulta;
	
	private String respuesta;

	private boolean leido;
	
	private Cliente padre;
	
	
	public Mensaje(Cliente padre){
		
		this.padre = padre;
		consulta = "";
		respuesta = "";
		this.leido = false;
	}


	public boolean getLeido(){
		return leido;
	}
	
	public void Leido(){
		leido = true;
	}
	
	public String getConsulta() {
		return consulta;
	}


	public void setConsulta(String consulta) {
		this.consulta = consulta;
	}


	public String getRespuesta() {
		return respuesta;
	}


	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}


	public Cliente getPadre() {
		return padre;
	}
	
	
	
}
