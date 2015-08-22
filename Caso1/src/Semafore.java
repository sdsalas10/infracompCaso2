public class Semafore {
	private int contador;
	private int cantidad;

	public Semafore (int cantidad){
		this.cantidad = cantidad;
		contador = 0;
	}

	public void agregar(Mensaje m) {
		synchronized (m){
		while(contador == cantidad){
			try {
				m.getPadre().wait();
				System.out.println(m.getPadre() + "  si estoy esperando");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		contador++;
		this.notify();
		}
	}

	public  void liberar(Mensaje mensaje){
//		while(this.contador == 0){
//			try {
//				wait();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		synchronized (mensaje){
		this.contador--;
		mensaje.getPadre().notify();
		}
	}
}