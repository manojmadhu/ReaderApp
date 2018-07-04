package reader.com.nipponit.readerapp;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.Manifest.permission;

import com.google.zxing.client.android.BeepManager;

import com.google.zxing.Result;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import reader.com.nipponit.readerapp.Database.AppDB;
import reader.com.nipponit.readerapp.HttpConnector.Connector;
import reader.com.nipponit.readerapp.HttpConnector.JsonBarcode;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private final static int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    AppDB LocalDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                //Toast.makeText(MainActivity.this, "Permission granted" , Toast.LENGTH_SHORT).show();
            }
            else
                requestPermissions();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.send:
                Toast.makeText(this, "Send clicked", Toast.LENGTH_SHORT).show();
                new _sendBarcodes().execute();
                break;
            default:
                return true;
        }
        return true;
    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(MainActivity.this,CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,new String[]{CAMERA},REQUEST_CAMERA);
    }

    public void onRequestPermissionResult(int requestCode,String permission[],int grantResults[]){
        switch (requestCode){
            case REQUEST_CAMERA:
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(shouldShowRequestPermissionRationale(CAMERA)){
                                displayAlertMessage("You need to allow access", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{CAMERA},REQUEST_CAMERA);
                                        }
                                    }
                                });

                            }
                        }
                    }
                }break;
        }
    }


    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK",listener)
                .setNegativeButton("Cancel",null)
                .create()
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                if(scannerView == null){
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }else{
                requestPermissions();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    // This is for send results to ResultActivity
    @Override
    public void handleResult(Result result) {
        // GET SCANNED RESULT
        String scanResult = result.getText();

        BeepManager beepManager = new BeepManager(this);
        beepManager.playBeepSoundAndVibrate();


        new AddScanBarcodes().execute(scanResult);


//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(scanResult);
//        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                  scannerView.resumeCameraPreview(MainActivity.this);
//            }
//        });
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();

//        Intent intent = new Intent();
//        intent.putExtra("barcode",scanResult);
//        setResult(0,intent);
//        finish();

    }

    class AddScanBarcodes extends AsyncTask<String,Integer,Integer>{

        boolean isValid = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LocalDb = new AppDB(getApplicationContext());

        }

        @Override
        protected Integer doInBackground(String... params) {
            String myBarcode = params[0].toString();
            isValid = LocalDb.SaveToLocalBarcode(myBarcode);
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if(!isValid){
                Toast.makeText(MainActivity.this, "Already scanned barcode.", Toast.LENGTH_SHORT).show();
            }
            scannerView.resumeCameraPreview(MainActivity.this);
        }
    }


    class _sendBarcodes extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {

           return SendBarcodes();


        }
    }



    private String SendBarcodes(){
        try{
            LocalDb = new AppDB(MainActivity.this);
            String userid = LocalDb.RetrivewID();
            if(!userid.equals("")){
                Cursor BarcodeCursor = LocalDb.RetrivewBarcodeData();
                if(BarcodeCursor!=null && BarcodeCursor.getCount()>0){

                    JsonBarcode jsonBarcode = new JsonBarcode(userid,BarcodeCursor);
                    String jsonString = jsonBarcode.BarcodeJsonArray();

                    HttpURLConnection jconn = Connector.Jsonconnection("http://192.168.101.131:88/rest/bcode.php");
                    if(jconn==null){
                        return null;
                    }

                    try{
                        OutputStream os = jconn.getOutputStream();
                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                        bufferedWriter.write(jsonString);

                        bufferedWriter.flush();

                        bufferedWriter.close();
                        os.close();

                        int responseCode = jconn.getResponseCode();
                        if(responseCode==jconn.HTTP_OK){
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jconn.getInputStream()));
                            StringBuffer sb = new StringBuffer();

                            String line;
                            while ((line=bufferedReader.readLine())!=null){
                                sb.append(line);
                            }
                            bufferedReader.close();
                            return sb.toString();
                        }
                    }catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }catch (Exception ex){
            Log.w("error",ex.getMessage());
        }
        return null;
    }
}