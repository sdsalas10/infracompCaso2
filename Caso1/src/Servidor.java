import java.util.ArrayList;


public class Servidor extends Thread{

	
	private Mensaje mensaje;
	
	private Buffer buffer;

	
	public Servidor( Buffer buffer){
		
		mensaje = new Mensaje(null);
		this.buffer = buffer;
	}


	public Mensaje getMensaje() {
		return mensaje;
	}


	public void setMensaje(Mensaje mensaje) {
		this.mensaje = mensaje;
	}
	
	public void run(){
		
		while(true){
			System.out.println("S: Estoy buscando");
			boolean validado = buffer.removerMensajeServidor(this);
			if(validado){
				mensaje.Leido();
				mensaje.setRespuesta("Chao");
				buffer.notificar(this.mensaje);
			}
		}
	}
}
