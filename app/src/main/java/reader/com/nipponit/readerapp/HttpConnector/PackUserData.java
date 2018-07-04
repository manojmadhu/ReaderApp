package reader.com.nipponit.readerapp.HttpConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by manojm on 05/24/2018.
 */



public class PackUserData {
    String name,nic,email,contact,password,imei,mobile;


    public  PackUserData(String _name,String _nic, String _email, String _contact, String _password,String _imei,String _mobile){
        this.name = _name;
        this.nic = _nic;
        this.email = _email;
        this.contact = _contact;
        this.password = _password;
        this.imei = _imei;
        this.mobile = _mobile;
    }

    public String userData(){
        JSONObject jobject = new JSONObject();
        StringBuffer dataPacket = new StringBuffer();
        try{
            jobject.put("Name",name);
            jobject.put("Nic",nic);
            jobject.put("Email",email);
            jobject.put("Contact",contact);
            jobject.put("Password",password);
            jobject.put("Imei",imei);
            jobject.put("Mobile",mobile);

            boolean isfirst = true;
            Iterator looper = jobject.keys();

            do{

                String Key = looper.next().toString();
                String Value = jobject.get(Key).toString();

                if(isfirst)
                    isfirst = false;
                else{
                    dataPacket.append("&");
                }

                dataPacket.append(URLEncoder.encode(Key,"UTF-8"));
                dataPacket.append("=");
                dataPacket.append(URLEncoder.encode(Value,"UTF-8"));

            }while (looper.hasNext());

            return dataPacket.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }


}
