package reader.com.nipponit.readerapp.HttpConnector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class DataUploading {

    private static String URL="http://192.168.101.131:88/rest/main/bcode.php";

    private String Json;
    private String STATE;

    public DataUploading(String json){
        this.Json = json;
    }


    public void UploadingBarcode() {

        String STATE = "";

        HttpURLConnection jconn = Connector.Jsonconnection(URL);
        if (jconn == null) {
            setSTATE(STATE);
        }

        try {
            OutputStream os = jconn.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bufferedWriter.write(this.Json);

            bufferedWriter.flush();

            bufferedWriter.close();
            os.close();

            int responseCode = jconn.getResponseCode();
            if (responseCode == jconn.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jconn.getInputStream()));
                StringBuffer sb = new StringBuffer();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                this.setSTATE(sb.toString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public String getSTATE() {
        return STATE;
    }

    public void setSTATE(String STATE) {
        this.STATE = STATE;
    }
}
