public class Cliente  extends Thread{

	private int ID;

	private Mensaje[] mensajes;
	
	private Buffer buffer;
	
	public Cliente(int id, int nMensajes, Buffer buffer){
		
	setID(id);	
	mensajes = new Mensaje[nMensajes];
	this.buffer = buffer;
	}
	
	
	
	public void run(){
	
		for (int i = 0; i < mensajes.length; i++) {
			
			Mensaje mensaje = new Mensaje(this);
			System.out.println("Cliente: " + ID + " Mensaje #:" + i);
			mensajes[i] = mensaje;			
			buffer.agregarMensaje(mensaje);
		}
				
		
	}



	public int getID() {
		return ID;
	}



	public void setID(int iD) {
		ID = iD;
	}
	
	
}
