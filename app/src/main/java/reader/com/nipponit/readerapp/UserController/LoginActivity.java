package reader.com.nipponit.readerapp.UserController;

import android.app.ActionBar;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import reader.com.nipponit.readerapp.Database.AppDB;
import reader.com.nipponit.readerapp.HttpConnector.Connector;
import reader.com.nipponit.readerapp.HttpConnector.PackLoginData;
import reader.com.nipponit.readerapp.R;
import reader.com.nipponit.readerapp.SelectionActivity;

public class LoginActivity extends AppCompatActivity {

    TextView txtnic,txtpassword,txtforgetpw;
    Button btnlogin; TextView btnregister;
    AppDB LocalDb;
    static int REQUSET_CODE=0,RESULT_CODE=0;
    static String UID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(getSupportActionBar()!=null)
            {getSupportActionBar().hide();}

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        txtforgetpw = (TextView)findViewById(R.id.txtforgetPW);
        txtnic = (TextView)findViewById(R.id.textnic);
        txtpassword = (TextView)findViewById(R.id.textpassword);
        btnlogin = (Button)findViewById(R.id.btnlogin);
        btnregister = (TextView)findViewById(R.id.btnregister);

        if (this.isLogged()){
            Intent selectionIntent = new Intent(LoginActivity.this, SelectionActivity.class);
            selectionIntent.putExtra("UID",UID);
            startActivity(selectionIntent);
            finish();
        }


    }

    public void OnResetPW(View view){
        Intent intent = new Intent(LoginActivity.this,ContactNumberActivity.class);
        startActivity(intent);
        Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
    }

    public void OnloginClick(View view){
        new UserAuthorization().execute();
    }
    public void OnregisterClick(View view){
        Intent registerint= new Intent(LoginActivity.this,RegisterActivity.class);
        startActivityForResult(registerint,REQUSET_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUSET_CODE){
            if(resultCode == RESULT_CODE){
                if(data!=null){
                    String nic = data.getStringExtra("nic");
                    txtnic.setText(nic);
                }
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //class for UserValidation

    class UserAuthorization extends AsyncTask<Void,Void,String>{

        ProgressDialog progressDialog;
        String userName,password;

        @Override
        protected String doInBackground(Void... params) {
            try {
                return this.UserLogin();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Checking User Account");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();

            userName = txtnic.getText().toString();
            password = txtpassword.getText().toString();

            LocalDb = new AppDB(getApplicationContext());
        }


        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            progressDialog.dismiss();
            String userResult = response;
            String id="",name="",nic="",contact="",email="";

            if(userResult == null){
                Toast.makeText(LoginActivity.this, "Connection error. Check your internet connection.", Toast.LENGTH_SHORT).show();
            }
            else {
                try {

                    JSONObject Jobject = new JSONObject(userResult);
                    id = Jobject.getString("id");
                    if(!id.equals("0")) {

                        name = Jobject.getString("uname");
                        nic = Jobject.getString("nic");
                        contact = Jobject.getString("contact");
                        email = Jobject.getString("email");

                        //#clear current user and save new user
                        LocalDb.ClearUser();
                        boolean status = LocalDb.SaveToLocalUser(id, name, nic, contact, email);

                        if (status) {
                            Toast.makeText(LoginActivity.this, "Hi, welcome " + name + " !", Toast.LENGTH_SHORT).show();
                            Intent selectionIntent = new Intent(LoginActivity.this, SelectionActivity.class);
                            selectionIntent.putExtra("UID",id);
                            startActivity(selectionIntent);
                            finish();
                        } else
                            Toast.makeText(LoginActivity.this, "Error on local data saving.", Toast.LENGTH_SHORT).show();

                    }else
                        Toast.makeText(LoginActivity.this, "Login fail.", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Login fail.Error connection", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }

        }


        private String UserLogin(){
            String result="";
            HttpURLConnection conn = Connector.connection("http://192.168.101.131:88/rest/users/userlogin.php");
            if(conn==null){
                return null;
            }

            try{
                OutputStream os = conn.getOutputStream();

                BufferedWriter bwritter = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                bwritter.write(new PackLoginData(userName,password).loginData());

                bwritter.flush();

                bwritter.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode==conn.HTTP_OK){
                    BufferedReader breader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer stringBuffer = new StringBuffer();

                    String line;

                    while((line=breader.readLine())!=null){
                        stringBuffer.append(line);
                    }

                    breader.close();
                    return stringBuffer.toString();

                }
            } catch (ConnectException e){
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }

            return null;

        }

    }

    private Boolean isLogged(){
        Boolean islog = false;
        LocalDb = new AppDB(getApplicationContext());
        try{
            UID = LocalDb.RetrivewID();
            if(!UID.equals("")){
                islog = true;
            }
        }catch (Exception ex){

        }
        return islog;
    }

}
