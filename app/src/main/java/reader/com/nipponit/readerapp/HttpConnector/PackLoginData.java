package reader.com.nipponit.readerapp.HttpConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by manojm on 05/24/2018.
 */

public class PackLoginData {
    String uname,password;

    public PackLoginData(String _uname,String _password){
        this.uname = _uname;
        this.password = _password;
    }

    public String loginData(){
        JSONObject jobject = new JSONObject();
        StringBuffer datapacket = new StringBuffer();
        try{
            jobject.put("UserName",uname);
            jobject.put("Password",password);

            boolean isfirst = true;
            Iterator loop = jobject.keys();

            do{
                String Key = loop.next().toString();
                String Value = jobject.get(Key).toString();

                if(isfirst)
                    isfirst = false;
                else
                    datapacket.append("&");

                datapacket.append(URLEncoder.encode(Key,"UTF-8"));
                datapacket.append("=");
                datapacket.append(URLEncoder.encode(Value,"UTF-8"));

            }while (loop.hasNext());

            return datapacket.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

}
