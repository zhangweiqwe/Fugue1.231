package cn.wsgwz.tun.gravity.encryption;


import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.math.BigInteger;  
import java.security.InvalidKeyException;
import java.security.KeyFactory;  
import java.security.KeyPair;  
import java.security.KeyPairGenerator;  
import java.security.NoSuchAlgorithmException;  
import java.security.PrivateKey;  
import java.security.PublicKey;  
import java.security.interfaces.RSAPrivateKey;  
import java.security.interfaces.RSAPublicKey;  
import java.security.spec.InvalidKeySpecException;  
import java.security.spec.PKCS8EncodedKeySpec;  
import java.security.spec.RSAPublicKeySpec;  
import java.security.spec.X509EncodedKeySpec;  
  
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;  
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
  
/** 
 * Created by wk on 2017/2/14. 
 */  
  
public class RSAUtils {  
    private static String RSA = "RSA";  
    /** *//**  
     * RSA�?大加密明文大�?  
     */    
    private static final int MAX_ENCRYPT_BLOCK = 117; 
    /** 
     * 随机生成RSA密钥�?(默认密钥长度�?1024) 
     * 
     * @return 
     */  
    public static KeyPair generateRSAKeyPair()  
    {  
        return generateRSAKeyPair(1024);  
    }  
  
    /** 
     * 随机生成RSA密钥�? 
     * 
     * @param keyLength 
     *            密钥长度，范围：512�?2048<br> 
     *            �?�?1024 
     * @return 
     */  
    public static KeyPair generateRSAKeyPair(int keyLength)  
    {  
        try  
        {  
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);  
            kpg.initialize(keyLength);  
            return kpg.genKeyPair();  
        } catch (NoSuchAlgorithmException e)  
        {  
            e.printStackTrace();  
            return null;  
        }  
    }  
  
    /** 
     * 用公钥加�? <br> 
     * 每次加密的字节数，不能超过密钥的长度值减�?11 
     * 
     * @param data 
     *            �?加密数据的byte数据 
     * @param publicKey 公钥 
     * @return 加密后的byte型数�? 
     */  
    public static byte[] encryptData(byte[] data, PublicKey publicKey)  
    {  
          
        try  
        {  
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");  
            // 编码前设定编码方式及密钥  
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
            // 传入编码数据并返回编码结�?  
            int inputLen = data.length;    
            ByteArrayOutputStream out = new ByteArrayOutputStream();    
            int offSet = 0;    
            byte[] cache;    
            int i = 0;    
            // 对数据分段加�?    
            while (inputLen - offSet > 0) {    
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {    
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);    
                } else {    
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);    
                }    
                out.write(cache, 0, cache.length);    
                i++;    
                offSet = i * MAX_ENCRYPT_BLOCK;    
            }    
            byte[] encryptedData = out.toByteArray();    
            out.close();    
            return encryptedData;    
        } catch (Exception e)  
        {  
            e.printStackTrace();  
            return null;  
        }  
    }  
  
    /** 
     * 用私钥解�? 
     * 
     * @param encryptedData 
     *            经过encryptedData()加密返回的byte数据 
     * @param privateKey 
     *            私钥 
     * @return 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws IOException 
     */  
    public static byte[] decryptData(byte[] encryptedData, PrivateKey privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException  
    {  

    	 //Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
         //cipher.init(2, privateKey);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
         cipher.init(Cipher.DECRYPT_MODE, privateKey);
         int inputLen = encryptedData.length;
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         int offSet = 0;

         for(int i = 0; inputLen - offSet > 0; offSet = i * 128) {
             byte[] cache;
             if(inputLen - offSet > 128) {
                 cache = cipher.doFinal(encryptedData, offSet, 128);
             } else {
                 cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
             }
             //Log.d("AESResponseBody","-------------------------->"+new String(cache,0,cache.length));
             out.write(cache, 0, cache.length);
             ++i;
         }


         byte[] decryptedData = out.toByteArray();
        //Log.d("AESResponseBody","--------------->"+new String(decryptedData,"gbk"));
       // Log.d("AESResponseBody","--->"+);
         out.close();
         return decryptedData;
    }  
  
    /** 
     * 通过公钥byte[](publicKey.getEncoded())将公钥还原，适用于RSA算法 
     * 
     * @param keyBytes 
     * @return 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeySpecException 
     */  
    public static PublicKey getPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException,  
            InvalidKeySpecException  
    {  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);  
        PublicKey publicKey = keyFactory.generatePublic(keySpec);  
        return publicKey;  
    }  
  
    /** 
     * 通过私钥byte[]将公钥还原，适用于RSA算法 
     * 
     * @param keyBytes 
     * @return 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeySpecException 
     */  
    public static PrivateKey getPrivateKey(byte[] keyBytes) throws NoSuchAlgorithmException,  
            InvalidKeySpecException  
    {  
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);  
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);  
        return privateKey;  
    }  
  
    /** 
     * 使用N、e值还原公�? 
     * 
     * @param modulus 
     * @param publicExponent 
     * @return 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeySpecException 
     */  
    public static PublicKey getPublicKey(String modulus, String publicExponent)  
            throws NoSuchAlgorithmException, InvalidKeySpecException  
    {  
        BigInteger bigIntModulus = new BigInteger(modulus);  
        BigInteger bigIntPrivateExponent = new BigInteger(publicExponent);  
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);  
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);  
        PublicKey publicKey = keyFactory.generatePublic(keySpec);  
        return publicKey;  
    }  
  
    /** 
     * 使用N、d值还原私�? 
     * 
     * @param modulus 
     * @param privateExponent 
     * @return 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeySpecException 
     */  
    public static PrivateKey getPrivateKey(String modulus, String privateExponent)  
            throws NoSuchAlgorithmException, InvalidKeySpecException  
    {  
        BigInteger bigIntModulus = new BigInteger(modulus);  
        BigInteger bigIntPrivateExponent = new BigInteger(privateExponent);  
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent);  
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);  
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);  
        return privateKey;  
    }  
  
    /** 
     * 从字符串中加载公�? 
     * 
     * @param publicKeyStr 
     *            公钥数据字符�? 
     * @throws Exception 
     *             加载公钥时产生的异常 
     */  
    public static PublicKey loadPublicKey(String publicKeyStr) throws Exception  
    {  
        try  
        {  
            byte[] buffer = Base64Utils.decode(publicKeyStr);  
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);  
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);  
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);  
        } catch (NoSuchAlgorithmException e)  
        {  
            throw new Exception("无此算法");  
        } catch (InvalidKeySpecException e)  
        {  
            throw new Exception("公钥非法");  
        } catch (NullPointerException e)  
        {  
            throw new Exception("公钥数据为空");  
        }  
    }  
  
    /** 
     * 从字符串中加载私�?<br> 
     * 加载时使用的是PKCS8EncodedKeySpec（PKCS#8编码的Key指令）�?? 
     * 
     * @param privateKeyStr 
     * @return 
     * @throws Exception 
     */  
    public static PrivateKey loadPrivateKey(String privateKeyStr) throws Exception  
    {  
        try  
        {  
            byte[] buffer = Base64Utils.decode(privateKeyStr);  
            // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);  
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);  
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);  
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
        } catch (NoSuchAlgorithmException e)  
        {  
            throw new Exception("无此算法");  
        } catch (InvalidKeySpecException e)  
        {  
            throw new Exception("私钥非法");  
        } catch (NullPointerException e)  
        {  
            throw new Exception("私钥数据为空");  
        }  
    }  
  
    /** 
     * 从文件中输入流中加载公钥 
     * 
     * @param in 
     *            公钥输入�? 
     * @throws Exception 
     *             加载公钥时产生的异常 
     */  
    public static PublicKey loadPublicKey(InputStream in) throws Exception  
    {  
        try  
        {  
            return loadPublicKey(readKey(in));  
        } catch (IOException e)  
        {  
            throw new Exception("公钥数据流读取错�?");  
        } catch (NullPointerException e)  
        {  
            throw new Exception("公钥输入流为�?");  
        }  
    }  
  
    /** 
     * 从文件中加载私钥 
     * 
     * @param in 
     *            私钥文件�? 
     * @return 是否成功 
     * @throws Exception 
     */  
    public static PrivateKey loadPrivateKey(InputStream in) throws Exception  
    {  
        try  
        {  
            return loadPrivateKey(readKey(in));  
        } catch (IOException e)  
        {  
            throw new Exception("私钥数据读取错误");  
        } catch (NullPointerException e)  
        {  
            throw new Exception("私钥输入流为�?");  
        }  
    }  
  
    /** 
     * 读取密钥信息 
     * 
     * @param in 
     * @return 
     * @throws IOException 
     */  
    private static String readKey(InputStream in) throws IOException  
    {  
        BufferedReader br = new BufferedReader(new InputStreamReader(in));  
        String readLine = null;  
        StringBuilder sb = new StringBuilder();  
        while ((readLine = br.readLine()) != null)  
        {  
            if (readLine.charAt(0) == '-')  
            {  
                continue;  
            } else  
            {  
                sb.append(readLine);  
                sb.append('\r');  
            }  
        }  
  
        return sb.toString();  
    }  
  
    /** 
     * 打印公钥信息 
     * 
     * @param publicKey 
     */  
    public static void printPublicKeyInfo(PublicKey publicKey)  
    {  
        RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;  
        System.out.println("----------RSAPublicKey----------");  
        System.out.println("Modulus.length=" + rsaPublicKey.getModulus().bitLength());  
        System.out.println("Modulus=" + rsaPublicKey.getModulus().toString());  
        System.out.println("PublicExponent.length=" + rsaPublicKey.getPublicExponent().bitLength());  
        System.out.println("PublicExponent=" + rsaPublicKey.getPublicExponent().toString());  
    }  
  
    public static void printPrivateKeyInfo(PrivateKey privateKey)  
    {  
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;  
        System.out.println("----------RSAPrivateKey ----------");  
        System.out.println("Modulus.length=" + rsaPrivateKey.getModulus().bitLength());  
        System.out.println("Modulus=" + rsaPrivateKey.getModulus().toString());  
        System.out.println("PrivateExponent.length=" + rsaPrivateKey.getPrivateExponent().bitLength());  
        System.out.println("PrivatecExponent=" + rsaPrivateKey.getPrivateExponent().toString());  
  
    }  
}  