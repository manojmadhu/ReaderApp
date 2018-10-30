package reader.com.nipponit.readerapp.UserController;

import android.util.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class passwordEncryption {
    private String password_;
    public passwordEncryption(String password){
        this.password_=password;
    }

    public String getPassword_() {
        return password_;
    }

    public void setPassword_(String password_) {
        this.password_ = password_;
    }

    public String EnPassword () throws Exception{
        SecretKey key = genKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] enValue = cipher.doFinal(password_.getBytes());
        return Base64.encodeToString(enValue,Base64.DEFAULT);
    }

    private SecretKeySpec genKey() throws Exception{
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte [] bytes = password_.getBytes("UTF-8");
        messageDigest.update(bytes,0,bytes.length);
        byte [] key = messageDigest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        return secretKeySpec;
    }


}
