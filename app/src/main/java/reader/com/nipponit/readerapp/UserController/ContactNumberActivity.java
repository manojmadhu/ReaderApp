package reader.com.nipponit.readerapp.UserController;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import reader.com.nipponit.readerapp.R;

public class ContactNumberActivity extends AppCompatActivity {


    EditText txtcontact;
    boolean stsnumber=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_number);

        txtcontact = (EditText)findViewById(R.id.txtmobile);
        txtcontact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateContact();
            }
        });

    }


    private void validateContact(){
        String txt = txtcontact.getText().toString();
        if(txt.length()>0) {
            if (!(txt.matches("[0-9.?]*")) || (!txt.equals("") && txt.length() != 10)) {
                txtcontact.setTextColor(Color.RED);
                //Toast.makeText(RegisterActivity.this, "Invalid Contact Number", Toast.LENGTH_SHORT).show();
                stsnumber = false;
            } else if (txt.substring(0, 1).equals("0")) {
                txtcontact.setTextColor(Color.parseColor("#999999"));
                stsnumber = true;
            }
        }
    }


    public void OnResetButtonClick(View view){

        if(stsnumber){
            Intent intent=new Intent(ContactNumberActivity.this,PasswordResetActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Invalid mobile number.", Toast.LENGTH_SHORT).show();
        }
    }
}
