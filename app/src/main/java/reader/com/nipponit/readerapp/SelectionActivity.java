package reader.com.nipponit.readerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

public class SelectionActivity extends AppCompatActivity {
    CardView cvScan,cvpoint,cvhistory,cvinfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        cvScan = (CardView) findViewById(R.id.cardScan);
        cvScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intScan = new Intent(SelectionActivity.this,MainActivity.class);
                startActivity(intScan);
            }
        });

        cvpoint = (CardView) findViewById(R.id.cardmypoint);
        cvpoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cvhistory = (CardView)findViewById(R.id.cardhistory);
        cvhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cvinfo = (CardView)findViewById(R.id.cardmyinfo);
        cvinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
