package reader.com.nipponit.readerapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.client.android.Intents;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import reader.com.nipponit.readerapp.BarcodeReader.*;
import reader.com.nipponit.readerapp.Database.AppDB;
import reader.com.nipponit.readerapp.HttpConnector.DataUploading;
import reader.com.nipponit.readerapp.HttpConnector.JsonBarcode;
import reader.com.nipponit.readerapp.UserController.MyinfoActivity;

public class ScanActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    BarcodeReader barcodeReader;
    //Local Database
    private AppDB LocalDb;
    TextView txtbarcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        txtbarcode = (TextView)findViewById(R.id.lblbcount);
        setScannedBarcodes();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // get the barcode reader instance
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_scanner);


    }

    @Override
    public void onScanned(Barcode barcode) {

        // playing barcode reader beep sound
        barcodeReader.playBeep();
        String _barcode = barcode.rawValue;
        new AddScanBarcodes().execute(_barcode);

    }

    @Override
    public void onScannedMultiple(List<Barcode> list)
    {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String s) {
        Toast.makeText(getApplicationContext(), "Error occurred while scanning " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraPermissionDenied() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.send:
                AlertDialog dialog=new AlertDialog.Builder(ScanActivity.this,R.style.Theme_AppCompat_DayNight_Dialog_Alert).create();
                dialog.setMessage("Are you sure want to send Barcode(s)?");
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new SendBarcodes().execute();
                    }
                });

                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Async task for add scan barcode to local database.
     * check before insert into local db.
     */
    class AddScanBarcodes extends AsyncTask<String,Integer,Integer> {

        boolean isValid = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LocalDb = new AppDB(getApplicationContext());

        }

        @Override
        protected Integer doInBackground(String... params) {
            String myBarcode = params[0].toString();
            //check barcode if already scanned.
            isValid = LocalDb.SaveToLocalBarcode(myBarcode);
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            if(!isValid){
                Toast.makeText(ScanActivity.this, "Already scanned barcode.", Toast.LENGTH_SHORT).show();
            }else{
                setScannedBarcodes();
            }
        }
    }

    private void setScannedBarcodes(){
        try{
            LocalDb = new AppDB(getApplicationContext());
            int count = LocalDb.ReturnBarcodeCount();
            txtbarcode.setText("You have scanned "+count+" Barcode(s).");
        }catch (Exception ex){

        }
    }

    /**
     * Async task for send scanned barcodes to hosting database.
     */
    class SendBarcodes extends AsyncTask<String,String,String>{

        ProgressDialog prgdialog= new ProgressDialog(ScanActivity.this,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LocalDb = new AppDB(getApplicationContext());
            prgdialog.setMessage("Please wait.Sending scanned Barcode(s).");
            prgdialog.setCancelable(false);
            prgdialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            return SendBarcodes();


        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(response!=null){
                try {
                    JSONObject jobj = new JSONObject(response);
                    String res = jobj.getString("State");

                    if(res.equals("0")){
                        Toast.makeText(ScanActivity.this, "Done.You scanned one or more duplicate barcode.", Toast.LENGTH_SHORT).show();
                    }else if(res.equals("1")){
                        Toast.makeText(ScanActivity.this, "Barcode uploaded successfully.", Toast.LENGTH_SHORT).show();
                    }
                    LocalDb.RemoveBarcode();// clear barcode data from localDB after send
                    setScannedBarcodes();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else
                Toast.makeText(ScanActivity.this, "Connection error. Check your internet connection.", Toast.LENGTH_SHORT).show();
            prgdialog.dismiss();
        }
    }


    /**
     * method for send barcodes using json
     * @return
     */
    private String SendBarcodes(){
        try{
            LocalDb = new AppDB(ScanActivity.this);
            String userid = LocalDb.RetrivewID();
            if(!userid.equals("")){
                Cursor BarcodeCursor = LocalDb.RetrivewBarcodeData();
                if(BarcodeCursor!=null && BarcodeCursor.getCount()>0){

                    JsonBarcode jsonBarcode = new JsonBarcode(userid,BarcodeCursor);
                    String jsonString = jsonBarcode.BarcodeJsonArray();

                    DataUploading uploading = new DataUploading(jsonString);
                    uploading.UploadingBarcode();
                    return uploading.getSTATE();

                }
            }
        }catch (Exception ex){
            Log.w("error",ex.getMessage());
        }
        return null;
    }

}
