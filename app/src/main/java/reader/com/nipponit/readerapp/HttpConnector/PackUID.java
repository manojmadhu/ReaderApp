package reader.com.nipponit.readerapp.HttpConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PackUID {
    String uid;

    public PackUID(String _UID){
        this.uid=_UID;
    }

    public String Retrivew_MyPoints(){

        JSONObject jobject = new JSONObject();
        StringBuffer datapacket = new StringBuffer();
        try{

            jobject.put("UID",uid);
            datapacket.append(URLEncoder.encode("UID","UTF-8"));
            datapacket.append("=");
            datapacket.append(URLEncoder.encode(uid,"UTF-8"));
            return datapacket.toString();

        }catch (JSONException jex){
            jex.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

}
