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

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;


public class ClienteSinSeguridad {
	
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
	private static BufferedReader br;
	
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
	
	private static KeyPair kp;
	
	
	public ClienteSinSeguridad(){
		

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
	
	/**
	 * Crea un certificado 
	 */
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
		
		
		CertificateFactory  cf = CertificateFactory.getInstance("X.509");
		Certificate certificate = cf.generateCertificate(socket.getInputStream());getClass();
		
		pw.println(RTA+":"+OK);
		
		return certificate.getPublicKey();
		
		
		
	}
	
	public String leerCifrado(PublicKey llavePublica) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		
		String num2Cifrado = br.readLine();
		System.out.println("Num1 Cifrado: "+num2Cifrado);
		byte[] num2Destr = Seguridad.destransformar(num2Cifrado);
		System.out.println("Num1 Destransformado: "+num2Destr);
		String mensajeDescifrado = descifrar(num2Destr, llavePublica);
		return mensajeDescifrado;
		
	}
	
	public String cifrar(PrivateKey pk, String mensaje) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		
		Cipher cifrador = Cipher.getInstance("RSA");
		cifrador.init(Cipher.ENCRYPT_MODE, pk);
		byte[] mCifrado = cifrador.doFinal(mensaje.getBytes());
		String capsula = Seguridad.transformar(mCifrado);
		return capsula;
		
	}
	
	public String cifrar(PrivateKey pk, byte[] mensaje) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		
		Cipher cifrador = Cipher.getInstance("RSA");
		cifrador.init(Cipher.ENCRYPT_MODE, pk);
		byte[] mCifrado = cifrador.doFinal(mensaje);
		String capsula = Seguridad.transformar(mCifrado);
		return capsula;
		
	}
	
	public byte[] cifrarSim(PrivateKey pk, byte[] mensaje) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		
		Cipher cifrador = Cipher.getInstance("RSA");
		cifrador.init(Cipher.ENCRYPT_MODE, pk);
		byte[] mCifrado = cifrador.doFinal(mensaje);
		return mCifrado;
		
	}
	
	public String descifrar(byte[] m, PublicKey llavePublica) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		Cipher descifrador = Cipher.getInstance("RSA");
		descifrador.init(Cipher.DECRYPT_MODE, llavePublica);
		byte[] mCifrado = descifrador.doFinal(m);
		String mensajeDescifrado = new String (mCifrado);
		System.out.println("Mensaje: " + mensajeDescifrado);
		return mensajeDescifrado;
		
	}
	
	public SecretKey generarLlaveHMacSha1() throws NoSuchAlgorithmException{
		
		 KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA1");
		 keyGen.init(64, new SecureRandom());
		 SecretKey key = keyGen.generateKey();
		 return key;
	}
	
	
	public String cifrar(PublicKey llavePublica, String mensaje) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException{
		
		Cipher cifrador = Cipher.getInstance("RSA");
		cifrador.init(Cipher.ENCRYPT_MODE, llavePublica);
		byte[] mCifrado = cifrador.doFinal(mensaje.getBytes());
		String capsula = Seguridad.transformar(mCifrado);
		return capsula;
		
	}
	
	public byte[] cifrar(PublicKey llavePublica, byte[] mensaje) throws NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException{
		
		Cipher cifrador = Cipher.getInstance("RSA");
		cifrador.init(Cipher.ENCRYPT_MODE, llavePublica);
		byte[] mCifrado = cifrador.doFinal(mensaje);
		return mCifrado;
		
	}
	
	
	
	public byte[] crearHash(SecretKey key, String ordenes) throws NoSuchAlgorithmException, InvalidKeyException{
		Mac mac = Mac.getInstance("HMACSHA1");
		mac.init(key);
		byte[] hash = mac.doFinal(ordenes.getBytes());
		return hash;
		
	}
	
	
	
	public byte[] concat(byte[] a, byte[] b) {
		   int aLen = a.length;
		   int bLen = b.length;
		   byte[] c= new byte[aLen+bLen];
		   System.arraycopy(a, 0, c, 0, aLen);
		   System.arraycopy(b, 0, c, aLen, bLen);
		   return c;
		}

	
	public static void main(String[] args){
		
		ClienteSinSeguridad c = new ClienteSinSeguridad();
		System.out.println("Esta es la consola");
		try {
			
			//Etapa 1
			c.crearConexion();
			String algAsim = "RSA";
			String algHmac = "HMACSHA1";
			boolean confirmado = c.enviarAlgoritmos(algAsim, algHmac);
			
			//Etapa 2
			
			int num = (int) (Math.random()*100);
		 	num1 = num+"";
			kp = Seguridad.crearLlaves();
			X509Certificate certificado = c.crearCertificado(kp);
			c.enviarCD(num1, certificado);
			PublicKey llavePublicaServ =  c.recibirCertificadoServidor();
			
			
			
			//Etapa 3
//			String mensajeDescifrado = c.leerCifrado(llavePublicaServ);
			String num2Cifrado = br.readLine();

			String capsula = c.cifrar(kp.getPrivate(), num2);                                                                                                                                                                                                                                                                                                                                                                                  
//			c.enviar(capsula);
			c.enviar(num2);
			
			//Etapa 4
			SecretKey k = c.generarLlaveHMacSha1();
			byte[] llaveCifrada = c.cifrar(llavePublicaServ, k.getEncoded());
			
			System.out.println(llaveCifrada.length);
			
			byte[] parte1 = new byte[117];
			byte[] parte2 = new byte[11];
			
			
			for(int i = 0; i < parte1.length; i++){
				parte1[i] = llaveCifrada[i];
			}
			
			int j=0;
			for(int i=117; i < llaveCifrada.length; i++){
				parte2[j] = llaveCifrada[i];
				j++;
			}
			
			byte[] parte1Cifrada = c.cifrarSim(kp.getPrivate(), parte1);
			byte[] parte2Cifrada = c.cifrarSim(kp.getPrivate(), parte2);
			
			byte[] mensajeFinal = c.concat(parte1Cifrada, parte2Cifrada);
			
			capsula = Seguridad.transformar(mensajeFinal);
			//String mensajeCifradoP2= c.cifrar(kp.getPrivate(), capsula);
//			c.enviar("INIT:"+capsula);
			c.enviar("INIT");
			
			String ordenes = "La orden es poner 5 a Sebastian Salas y Nicolas Rozo";
			capsula = c.cifrar(llavePublicaServ, ordenes);
//			c.enviar(capsula);
			c.enviar(ordenes);
			
			byte[] hash = c.crearHash(k, ordenes);
			byte[] cifrado = c.cifrar(llavePublicaServ, hash);
			capsula = Seguridad.transformar(cifrado);
//			c.enviar(capsula);
			c.enviar(ordenes);
			
			String respuesta = br.readLine();
			System.out.println("Final: "+respuesta);
			
		} catch (IOException | NoSuchAlgorithmException | InvalidKeyException | IllegalStateException | NoSuchProviderException | SignatureException | CertificateException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void enviar(String mensaje){
		pw.println(mensaje);
	}

}
