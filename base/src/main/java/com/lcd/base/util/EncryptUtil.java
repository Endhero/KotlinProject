package com.lcd.base.util;

import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 加解密工具类
 * Created by Chen on 2015/3/25.
 */
public class EncryptUtil {

    private static final String DES_KEY = "8yu&well";
    public static final String TAG = EncryptUtil.class.getSimpleName();

    private static Key key;// 密钥的key值
    private static byte[] DESkey;

    static {
        try {
            DESkey = DES_KEY.getBytes(StandardCharsets.UTF_8);// 设置密钥
            DESKeySpec keySpec = new DESKeySpec(DESkey);// 设置密钥参数
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");// 获得密钥工厂
            key = keyFactory.generateSecret(keySpec);// 得到密钥对象
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCheckSum(String mobile, long timestamp, String deviceId) {
        if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(deviceId)) {
            String strTimestamp = String.valueOf(timestamp);
            return MD5Encrypt(mobile.substring(7) + strTimestamp.substring(strTimestamp.length() - 4)
                + deviceId.substring(deviceId.length() - 4) + "yuel").substring(24);
        } else {
            return null;
        }
    }

    /**
     * 解密数据
     * @param decryptString
     * @return
     */
    public static String decryptDES(String decryptString) {
        if (TextUtils.isEmpty(decryptString)) {
            return "";
        }

        int count = 0;
        Cipher cipher;
        byte decryptedData[] = new byte[0];
        try {
            cipher = Cipher.getInstance("DES/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decryptedData = cipher.doFinal(ByteUtil.hexStringToByte(decryptString));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        for(int i=0;i<8;i++){
            if(decryptedData[decryptedData.length-i-1]==0){
                count++;
            }
        }
        byte [] resultByte = new byte[decryptedData.length-count];
        for(int j=0;j<resultByte.length;j++){
            resultByte[j]=decryptedData[j];
        }
        String s = null;
        try {
            s = new String(resultByte, "UTF8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 加密
     * @param decryptString
     * @return
     */
    public static String encryptDES(String decryptString) {
        Cipher cipher = null;
        byte decryptedData[] = new byte[0];
        try {
            cipher = Cipher.getInstance("DES/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            decryptedData = cipher.doFinal(decryptString.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
        return ByteUtil.bytesToHexString(decryptedData);
    }

    public static String MD5Encrypt(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

        md5.update(str.getBytes());
        byte[] hash = md5.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            if ((0xff & b) < 0x10) {
                hexString.append("0" + Integer.toHexString((0xFF & b)));
            }
            else {
                hexString.append(Integer.toHexString(0xFF & b));
            }
        }
        return hexString.toString();
    }

    public static String encryptPhone(String strPhone) {
        StringBuffer stringbuffer = new StringBuffer(strPhone);

        for (int i = 0; i < strPhone.length(); i++){
            if (i < 3 || i >= strPhone.length() - 2){
                stringbuffer.setCharAt(i, '*');
            }
        }

        return stringbuffer.toString();
    }
}
