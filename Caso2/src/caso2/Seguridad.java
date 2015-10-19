package caso2;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Seguridad {

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	
	public Seguridad(){
		
	}
	
	
	public static KeyPair crearLlaves() throws NoSuchAlgorithmException{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(1024);
		return kpg.genKeyPair();
	}

	
	/**
	* Computes RFC 2104-compliant HMAC signature.
	* * @param data
	* The data to be signed.
	* @param key
	* The signing key.
	* @return
	* The Base64-encoded RFC 2104-compliant HMAC signature.
	* @throws
	* java.security.SignatureException when signature generation fails
	*/
	public static String calculateRFC2104HMAC(String data, String key)
			throws java.security.SignatureException
			{
			String result;
			try {

			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);

			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());

			// base64-encode the hmac
			//result = Encoding.EncodeBase64(rawHmac);
			
			result = transformar(rawHmac);
			
			} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
			}
			return result;
			}
	

		/**
		 * Algoritmo de encapsulamiento a enteros. Convierte los bytes de un String a su representacion como enteros.
		 * @param b Los bytes a representar como enteros.
		 * @return EL string construido con la representacion de bytes como enteros.
		 */
		public static String transformar( byte[] b )
		{
			// Encapsulamiento con hexadecimales
			String ret = "";
			for (int i = 0 ; i < b.length ; i++) {
				String g = Integer.toHexString(((char)b[i])&0x00ff);
				ret += (g.length()==1?"0":"") + g;
			}
			return ret;
		}

		/**
		 * Algoritmo que transforma los enteros en los bytes correspondientes.
		 * @param ss El string con los enteros a transformar.
		 * @return Los bytes en su representacion real.
		 */
		public static byte[] destransformar( String ss)
		{
			// Encapsulamiento con hexadecimales
			byte[] ret = new byte[ss.length()/2];
			for (int i = 0 ; i < ret.length ; i++) {
				ret[i] = (byte) Integer.parseInt(ss.substring(i*2,(i+1)*2), 16);
			}
			return ret;
		}
	
			
	
	
	
	
}
