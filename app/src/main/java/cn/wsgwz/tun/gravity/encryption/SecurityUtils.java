package cn.wsgwz.tun.gravity.encryption;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 工具类
 *
 * 对okHttp3 发送的josn数据加密 返回的json数据解密
 *
 */
public class SecurityUtils {

    private static final String TAG = SecurityUtils.class.getSimpleName();
    private static  PrivateKey privateKey;
    private static PublicKey publicKey;
    private static SecurityUtils securityUtils;
    private SecurityUtils(){}
    public static final SecurityUtils getInstance()  {
        if(securityUtils==null){
            synchronized (SecurityUtils.class){
                    securityUtils = new SecurityUtils();
                    try {
                        publicKey = RSAUtils.loadPublicKey(new ByteArrayInputStream(("-----BEGIN PUBLIC KEY-----\n" +
                                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDzuqTZl24suHwLxA7ULo9sVFA8cQZW77PHVTnv4oAQkwr7eDpPdoHsNNuzlXBYmtb5U0NPb0iv2IF/AGkiqXCjTAza6RHRfqWGHpcINCCiwwbvFbLZIKIy2lJ9IcH+6YA/gSaMfBkOt5DrlgyL+ELmADNssNepHssi+zT2WDr4mwIDAQAB\n" +
                                "-----END PUBLIC KEY-----").getBytes()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        privateKey = RSAUtils.loadPrivateKey(new ByteArrayInputStream(("-----BEGIN PRIVATE KEY-----\n" +
                                "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAPO6pNmXbiy4fAvE\n" +
                                "DtQuj2xUUDxxBlbvs8dVOe/igBCTCvt4Ok92gew027OVcFia1vlTQ09vSK/YgX8A\n" +
                                "aSKpcKNMDNrpEdF+pYYelwg0IKLDBu8VstkgojLaUn0hwf7pgD+BJox8GQ63kOuW\n" +
                                "DIv4QuYAM2yw16keyyL7NPZYOvibAgMBAAECgYBwt7/nlb3xVryfoGOPQ50252NY\n" +
                                "IJli+WZ2aHbr9x9tCuQIWpj1CQSbHfMBgbo8cBe9pQE7Kmc+U+3Hs4Pr8NrS84w/\n" +
                                "eYHfMaLaz3G9+InFwQiiiGNWL2yIFSoLgQW3FTb2XfTl1lVRwD1VrwhLX1b43CSl\n" +
                                "3/V6GmSdJZ3UnpyKIQJBAP+8yPdin2AYOl7Vub++hqjF/8OF9qS3zPFrH5W+/h7u\n" +
                                "eirEIbWNaKmIEzryBhuIh3TXfzEkq3uFbnoYb+p+5msCQQDz+rPpwBUgkfitQuuh\n" +
                                "T7yNAJX74VX5CsKaOZ5fOjdFT6o3iUN24NrFqmYbjbXqoLUpG2XHKSEQFJOe7VvY\n" +
                                "0OKRAkEA2BJdigcduUc/KhlSE5uaksaXzk9FkO5qjh+AVDlG5EOBnNiR0p9jqrl5\n" +
                                "5ffPCTxVlnbs1EyzRm36ZdZ32JzXeQJBAKrbS3kLqf/4GWUEc0yxMUKxQVd2EyXn\n" +
                                "ciZc6VqYwzIHuxVPS+6JQsugLAdpsxbPWm6iICFb3SqMrmBiqqpohfECQQC2PYk6\n" +
                                "KVnAho8bdFmbHHrViPqY2GqziwtPs1bcF0Tc2lHj/Zqj8k/w22yogfmsbwGXL4EO\n" +
                                "Lz9kHpcx3MmPP3jV\n" +
                                "-----END PRIVATE KEY-----\n").getBytes()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }
        }
        return securityUtils;
    }

    /** 
     * 解密 
     * @param cipherText 密文 
     * @return 返回解密后的字符�? 
     * @throws Exception  
     */  
    public String decrypt(String cipherText) throws Exception{
        byte[] decryptByte = RSAUtils.decryptData(Base64Utils.decode(cipherText), privateKey);  
        String decryptStr = new String(decryptByte, "utf-8");
        Log.d(TAG,"解密后--->"+decryptStr);
        return decryptStr;  
    }



    /** 
     * 加密 
     * @param plainTest 明文 
     * @return  返回加密后的密文 
     * @throws Exception  
     */
    public String encrypt(String plainTest) throws Exception{
        byte[] encryptByte = RSAUtils.encryptData(plainTest.getBytes(), publicKey);  
        String afterencrypt = Base64Utils.encode(encryptByte);
         Log.d(TAG,"加密--->"+afterencrypt);
        return afterencrypt;  
    }

}  