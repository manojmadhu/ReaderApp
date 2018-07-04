package reader.com.nipponit.readerapp.HttpConnector;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by manojm on 05/24/2018.
 */

public class Connector {

    public static HttpURLConnection connection(String UrlAddress){
     try{
         URL url = new URL(UrlAddress);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();

         //
         connection.setRequestMethod("POST");
         connection.setConnectTimeout(20000);
         connection.setReadTimeout(20000);
         connection.setDoInput(true);
         connection.setDoOutput(true);

         //Return connection
         return connection;

     }catch (MalformedURLException ex){
         Log.w("error",ex.getMessage());
     }
     catch (IOException ex){
         Log.w("error",ex.getMessage());
     }

     return null;
    }


    public static HttpURLConnection Jsonconnection(String UrlAddress){
        try{
            URL url = new URL(UrlAddress);
            HttpURLConnection jsonconnection = (HttpURLConnection)url.openConnection();

            jsonconnection.setRequestMethod("POST");
            jsonconnection.setRequestProperty("Content-Type","application/json");
            jsonconnection.setRequestProperty("Accept","application/json");
            jsonconnection.setConnectTimeout(2000);
            jsonconnection.setReadTimeout(2000);
            jsonconnection.setDoInput(true);
            jsonconnection.setDoOutput(true);
            return jsonconnection;

        }
        catch (MalformedURLException ex){
            Log.w("error",ex.getMessage());
        }
        catch (IOException ex){
            Log.w("error",ex.getMessage());
        }
        return null;
    }


}
