package reader.com.nipponit.readerapp.UserController;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import reader.com.nipponit.readerapp.Database.AppDB;
import reader.com.nipponit.readerapp.R;
import reader.com.nipponit.readerapp.SelectionActivity;

public class MyinfoActivity extends AppCompatActivity {


    TextView txtName,txtNic,txtContact,txtEmail,txtUserName;
    AppDB appDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appDB = new AppDB(getApplicationContext());

        txtUserName = (TextView)findViewById(R.id.txtUserName);
        txtName=(TextView)findViewById(R.id.textname);
        txtContact=(TextView)findViewById(R.id.textmobile);
        txtNic=(TextView)findViewById(R.id.textnic);
        txtEmail=(TextView)findViewById(R.id.textemail);


        new LoadUserInfo().execute();

    }

    private class LoadUserInfo extends AsyncTask<String,String,String>{

        String Name="",Nic="",mobile="",email="";

        @Override
        protected String doInBackground(String... strings) {
            Cursor infocursor = appDB.ReturnMyInfo();
            if(infocursor.getCount()>0 && infocursor != null){
                while (infocursor.moveToNext()){
                    Name = infocursor.getString(0);
                    Nic = infocursor.getString(1);
                    mobile = infocursor.getString(2);
                    email = infocursor.getString(3);
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            txtUserName.setText(Name.split(" ")[0]);
            txtName.setText(Name);
            txtNic.setText(Nic);
            txtContact.setText(mobile);
            txtEmail.setText(email);
        }
    }


    public void OnLogout(View v){

        appDB = new AppDB(MyinfoActivity.this);
        AlertDialog dialog=new AlertDialog.Builder(MyinfoActivity.this,R.style.Theme_AppCompat_DayNight_Dialog_Alert).create();
        dialog.setMessage("Are you sure want to Logout?");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Logout", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //appDB.ClearUser();
                Intent intent=new Intent(MyinfoActivity.this,SelectionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.putExtra("Exit",true);
                startActivity(intent);
            }
        });

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
