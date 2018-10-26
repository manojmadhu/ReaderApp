package reader.com.nipponit.readerapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import reader.com.nipponit.readerapp.HttpConnector.CheckConnectivity;
import reader.com.nipponit.readerapp.HttpConnector.ConnectivityStatus;
import reader.com.nipponit.readerapp.UserController.MyinfoActivity;

public class SelectionActivity extends AppCompatActivity {
    LinearLayout cvScan,cvpoint,cvhistory,cvinfo;
    private static final int RC_BARCODE_CAPTURE = 9001;
    ConnectivityStatus connectivityStatus;
    IntentFilter intentFilter;
    static String UID="";
    static boolean exit=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        Bundle extras=getIntent().getExtras();
        if(extras!=null) {
            UID = extras.getString("UID");
            exit=extras.getBoolean("Exit");

            if(exit) {
                finish();
            }
        }

        //For check internet connection of mobile.(Broadcast receiver)
        connectivityStatus = new ConnectivityStatus();
        intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");



        cvScan = (LinearLayout) findViewById(R.id.cardScan);
        cvScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intScan = new Intent(SelectionActivity.this,ScanActivity.class);
               // startActivityForResult(intScan, RC_BARCODE_CAPTURE);
                startActivity(intScan);

            }
        });

        cvpoint = (LinearLayout) findViewById(R.id.cardmypoint);
        cvpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectionActivity.this,MypointsActivity.class);
                intent.putExtra("UID",UID);
                startActivity(intent);

            }
        });

        cvhistory = (LinearLayout) findViewById(R.id.cardhistory);
        cvhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cvinfo = (LinearLayout) findViewById(R.id.cardmyinfo);
        cvinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intInfo = new Intent(SelectionActivity.this,MyinfoActivity.class);
                startActivity(intInfo);
            }
        });

        registerReceiver(connectivityStatus,intentFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();
            registerReceiver(connectivityStatus,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
            unregisterReceiver(connectivityStatus);
    }
}
