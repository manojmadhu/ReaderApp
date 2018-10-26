package reader.com.nipponit.readerapp.UserController;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import reader.com.nipponit.readerapp.R;

public class PasswordResetActivity extends AppCompatActivity {

    LinearLayout pwlayout;
    EditText n1,n2,n3,n4,txtpassword;
    Button btnreset;
    static String conCode="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        pwlayout=(LinearLayout)findViewById(R.id.layoutresetPW);

        n1=(EditText)findViewById(R.id.prn1);
        n2=(EditText)findViewById(R.id.prn2);
        n3=(EditText)findViewById(R.id.prn3);
        n4=(EditText)findViewById(R.id.prn4);


        n1.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==1){
                    n2.setText("");
                    n2.setFocusable(true);
                    n2.requestFocus();
                }
            }
        });
        n1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                n1.setText("");
                return false;
            }
        });

        n2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==1){
                    n3.setText("");
                    n3.setFocusable(true);
                    n3.requestFocus();
                }else if (s.length()==0){
                    n1.setFocusable(true);
                    n1.requestFocus();
                }
            }
        });
        n2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                n2.setText("");
                return false;
            }
        });

        n3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==1){
                    n4.setText("");
                    n4.setFocusable(true);
                    n4.requestFocus();
                }else if (s.length()==0){
                    n2.setFocusable(true);
                    n2.requestFocus();
                }
            }
        });
        n3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                n3.setText("");
                return false;
            }
        });

        n4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==1){
                    //full code
                    conCode = n1.getText().toString()+n2.getText().toString()+n3.getText().toString()+n4.getText().toString();
                    ConfirmCode();
                }else if (s.length()==0){
                    n3.setFocusable(true);
                    n3.requestFocus();
                }
            }
        });


        n4.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE) {
                    ConfirmCode();
                }
                return false;
            }
        });
        n4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                n4.setText("");
                return false;
            }
        });
    }


    private void ConfirmCode(){
        if(conCode.length()==4){
            Toast.makeText(getApplicationContext(), conCode.toString(), Toast.LENGTH_SHORT).show();
            pwlayout.setVisibility(View.VISIBLE);
        }else
            Toast.makeText(getApplicationContext(), "Invalid CODE", Toast.LENGTH_SHORT).show();
    }
}
