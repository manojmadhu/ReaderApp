package reader.com.nipponit.readerapp.HttpConnector;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutionException;

public class CheckConnectivity {
    private static CheckConnectivity ourInstance = null;

    public static CheckConnectivity getInstance() {
        if(ourInstance==null){
            ourInstance = new CheckConnectivity();
        }
        return ourInstance;
    }



    private CheckConnectivity() {
            new Check().execute();
    }


    class Check extends AsyncTask<String,Boolean,Boolean>{
        boolean iscon=false;

        @Override
        protected Boolean doInBackground(String... strings) {
            Socket socket=new Socket();

            try {
                SocketAddress address=new InetSocketAddress("8.8.8.8",53);
                socket.connect(address,1000);
                iscon = socket.isConnected();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return iscon;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}


