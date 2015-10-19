package caso2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;




public class Cliente {
	
	/*
	 * Puerto del servidor seguro
	 */
	private final static int PORT = 443;
	
	/*
	 * Cadena de contro que indica la solicitud de conexion
	 */
	private final static String INFORMAR = "INFORMAR";
	
	/*
	 * Cadena de contro que indica la aprobacion de la conexion
	 */
	private final static String EMPEZAR = "EMPEZAR";
			
	/*
	 * Cadena de contro que indica los algoritmos a solicitar, va seguida de ':'
	 */
	private final static String ALGORITMOS = "ALGORITMOS";		
	
	/*
	 * Cadena de contro que indica respuesta, va seguida de ':'
	 */
	private final static String RTA = "RTA";
	
	/*
	 * Cadena de contro que indica una respuesta de solicitud
	 */
	private final static String OK = "OK";
	
	/*
	 * Cadena de contro que indica una respuesta de error
	 */
	private final static String ERROR = "ERROR";
	
	/*
	 * Cadena de contro que indica el certificado del cliente
	 */
	private final static String CERTPA = "CERTPA";
	
	/*
	 * Cadena de contro que indica el certificado del servidor
	 */
	private final static String CERTSRV = "CERTSRV";
	
	/*
	 * Cadena de contro que indica las ordenes
	 */
	private final static String ORDENES = "ORDENES";
	
	
	
	/*
	 * El canal de comunicación
	 */
	private Socket socket;
	
	/*
	 * El lector del canal
	 */
	private BufferedReader br;
	
	/*
	 * El escritor del canal
	 */
	private PrintWriter pw;
	
	/*
	 * La llave publica y privada
	 */
	private KeyPair llaves;
	
	/*
	 *  Mensaje que se va a enviar (num1)
	 */
	private static String num1;
	
	private static String num2;
	
	
	public Cliente(){
		

		try {
			socket = new Socket("localhost", PORT);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(socket.getOutputStream(), true);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	public void crearConexion() throws IOException{
		
		pw.println(INFORMAR);
		br.readLine();
		
	}
	
	public boolean enviarAlgoritmos(String algAsim, String algHmac) throws IOException{
		
		boolean confirmado = false;
		
		pw.println(ALGORITMOS+":"+algAsim+":"+algHmac);
		String respuesta = br.readLine();
		
		String[] splitRespuesta = respuesta.split(":");
		if(splitRespuesta[1].equals(OK)){
			confirmado = true;
		}
		return confirmado;
	}
	
	
	public X509Certificate crearCertificado(KeyPair llaves) throws InvalidKeyException, IllegalStateException, NoSuchProviderException, NoSuchAlgorithmException, SignatureException, CertificateException, IOException{

		KeyPair kp = llaves;
		PrivateKey llavePrivada = kp.getPrivate();

		Date from = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        Date to = new Date(System.currentTimeMillis() + 2 * 365 * 24 * 60 * 60 * 1000);
		  
		  
//		Date from = new Date(System.currentTimeMillis());                
//		Date to = new Date(System.currentTimeMillis() + 30L * 365L * 24L * 60L * 60L * 1000L);
		BigInteger sn = new BigInteger(64, new SecureRandom());       

		X509CertInfo info = new X509CertInfo();
		CertificateValidity interval = new CertificateValidity(from, to);
		X500Name owner = new X500Name("CN=TEST");

		info.set(X509CertInfo.VALIDITY, interval);
		info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
		info.set(X509CertInfo.SUBJECT, owner);
		info.set(X509CertInfo.ISSUER, owner);
		info.set(X509CertInfo.KEY, new CertificateX509Key(kp.getPublic()));
		info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
		AlgorithmId algo = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
		info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(algo));

		// Sign the cert to identify the algorithm that's used.
		X509CertImpl cert = new X509CertImpl(info);
		cert.sign(llavePrivada, "SHA256withRSA");

		// Update the algorith, and resign.
		algo = (AlgorithmId)cert.get(X509CertImpl.SIG_ALG);
		info.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algo);
		cert = new X509CertImpl(info);
		cert.sign(llavePrivada, "SHA256withRSA");


		return cert;

	}
	
	
	public void enviarCD(String men, X509Certificate certificado) throws IOException{
		
		byte[] mybyte;
		try {
			mybyte = certificado.getEncoded();
			pw.println(men+":"+CERTPA);
			socket.getOutputStream().write(mybyte);
			socket.getOutputStream().flush();
			
			br.readLine();
			pw.println(RTA+":"+OK);
			
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public PublicKey recibirCertificadoServidor() throws IOException, CertificateException{
		
		String respuesta = br.readLine();
		
		System.out.println(respuesta);
		String[] arregloRespuesta = respuesta.split(":");
		num2 = arregloRespuesta[0];
		pw.println(RTA+":"+OK);
		
		CertificateFactory  cf = CertificateFactory.getInstance("X.509");
		Certificate certificate = cf.generateCertificate(socket.getInputStream());getClass();
		return certificate.getPublicKey();
	}
	
	
	public static void main(String[] args){
		
		Cliente c = new Cliente();
		System.out.println("Esta es la consola");
		try {
			c.crearConexion();
			
			num1 = "1143446331";
			
			String algAsim = "RSA";
			String algHmac = "HMACSHA1";
			boolean confirmado = c.enviarAlgoritmos(algAsim, algHmac);
			
			KeyPair kp = Seguridad.crearLlaves();
			X509Certificate certificado = c.crearCertificado(kp);
			c.enviarCD(num1, certificado);
			
			PublicKey llavePublicaServ =  c.recibirCertificadoServidor();
			

			

			
			
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException | IllegalStateException | NoSuchProviderException | SignatureException | CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
