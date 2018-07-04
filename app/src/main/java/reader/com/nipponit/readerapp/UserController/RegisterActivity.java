package reader.com.nipponit.readerapp.UserController;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import reader.com.nipponit.readerapp.Database.AppDB;
import reader.com.nipponit.readerapp.HttpConnector.Connector;
import reader.com.nipponit.readerapp.HttpConnector.PackUserData;
import reader.com.nipponit.readerapp.R;

public class RegisterActivity extends AppCompatActivity {
    TextView txtname,txtnic,txtemail,txtcontact,txtpassword;
    Button btnregister;
    boolean stsnic=false,stsnumber=false,stsname=false,stspassword=false;
    AppDB LocalDb;
    String LocalNumber="0",ImeiNumber="0";
    private final int REQUSET_PHONE_STATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){

            }else
                requestPermission();
        }

        txtname = (TextView)findViewById(R.id.textname);
        txtnic = (TextView)findViewById(R.id.textnic);
        txtemail = (TextView)findViewById(R.id.textemail);
        txtcontact = (TextView)findViewById(R.id.textmobile);
        txtpassword = (TextView)findViewById(R.id.textpassword);
        btnregister = (Button)findViewById(R.id.btnRegister);

        try {
            TelephonyManager telephone = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            ImeiNumber = telephone.getDeviceId();LocalNumber = telephone.getLine1Number();
            if(LocalNumber==null){
                LocalNumber = "0";
            }
        }catch (Exception ex){
            Log.w("error",ex.getMessage());
        }

        txtname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String txt = txtname.getText().toString();
                if(txt.matches(".*\\d+.*")){
                    txtname.setTextColor(Color.RED);
                    Toast.makeText(RegisterActivity.this, "Invalid Name", Toast.LENGTH_SHORT).show();
                }else{
                    txtname.setTextColor(Color.parseColor("#999999"));
                    stsname=true;
                }
            }
        });

        txtnic.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String txt = txtnic.getText().toString();
                if(!txt.equals("") && txt.length() != 12 && !txt.matches("^[0-9]{9}[vVxX]$")) {
                    txtnic.setTextColor(Color.RED);
                    Toast.makeText(RegisterActivity.this, "Invalid NIC", Toast.LENGTH_SHORT).show();
                }else {
                    txtnic.setTextColor(Color.parseColor("#999999"));
                    stsnic=true;
                }
            }
        });

        txtcontact.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String txt = txtcontact.getText().toString();
                if(!txt.equals("")  && txt.length()!=10){
                    txtcontact.setTextColor(Color.RED);
                    Toast.makeText(RegisterActivity.this, "Invalid Contact Number", Toast.LENGTH_SHORT).show();
                }else {
                    txtcontact.setTextColor(Color.parseColor("#999999"));
                    stsnumber=true;
                }
            }
        });

    }

    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED);
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},
                REQUSET_PHONE_STATE);
    }

    public void OnRegisterMe(View v){
        if(stsname && stsnic && stsnumber && (txtpassword.getText().toString().length()>0)){
            new UserRegistration().execute();
        }else
            Toast.makeText(this, "Please check mandatory fields.", Toast.LENGTH_SHORT).show();
    }


    class UserRegistration extends AsyncTask<Void,Void,String>{
        ProgressDialog progressDialog;
        String name,nic,contact,email,password;

        @Override
        protected String doInBackground(Void... params) {
            LocalDb = new AppDB(getApplicationContext());
            try{
                boolean isOk = true;
                if(isOk){
                    return this.SendData();
                }
            }catch (Exception ex){
                Log.w("Error",ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setMessage("Registering new user account");
            progressDialog.setCancelable(false);
            progressDialog.show();

            name = txtname.getText().toString();
            nic = txtnic.getText().toString();
            contact = txtcontact.getText().toString();
            email = txtemail.getText().toString();
            password = txtpassword.getText().toString();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(response!=null){
            try {
                JSONObject jobj = new JSONObject(response);
                String res = jobj.getString("State");

                if(res.equals("0")){
                    Toast.makeText(RegisterActivity.this, "Registration Unsuccessful.Duplicate details.", Toast.LENGTH_SHORT).show();
                }else if(res.equals("1")){
                    Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

                Intent intent = new Intent();
                intent.putExtra("nic",nic);
                setResult(0,intent);
                finish();
            }
            else
                Toast.makeText(RegisterActivity.this, "Registration Unsuccessful.Check Internet Connection.", Toast.LENGTH_SHORT).show();

            progressDialog.dismiss();

        }




        private String SendData(){
            HttpURLConnection conn = Connector.connection("http://192.168.101.131:88/rest/users/userRegister.php");
            if(conn==null){
                return null;
            }

            try{
                OutputStream os = conn.getOutputStream();

                BufferedWriter bwritter = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                bwritter.write(new PackUserData(name,nic,email,contact,password,ImeiNumber,LocalNumber).userData());

                bwritter.flush();

                bwritter.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if(responseCode==conn.HTTP_OK){
                    BufferedReader breader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer stringBuffer = new StringBuffer();

                    String line;

                    while((line = breader.readLine())!=null){
                        stringBuffer.append(line);
                    }

                    breader.close();
                    return stringBuffer.toString();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
