package reader.com.nipponit.readerapp.HttpConnector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectivityStatus extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = cm.getActiveNetworkInfo();
        boolean isConnected = activeInfo!=null && activeInfo.isConnectedOrConnecting();
        if(!isConnected)
            Toast.makeText(context,"Data Connection Unavailable.!",Toast.LENGTH_SHORT).show();
    }
}
