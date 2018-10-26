package reader.com.nipponit.readerapp;

import android.animation.ValueAnimator;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;

import reader.com.nipponit.readerapp.HttpConnector.Connector;
import reader.com.nipponit.readerapp.HttpConnector.PackUID;
import reader.com.nipponit.readerapp.UserController.LoginActivity;

public class MypointsActivity extends AppCompatActivity {

    static String UID="",Rpoints="0",Tpoints="0";
    TextView txtAvbPoints,txtTotPoints,txtRedPoints;
    ProgressBar prgTot,prgRed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypoints);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extra = getIntent().getExtras();
        UID = extra.getString("UID");

        txtAvbPoints = (TextView)findViewById(R.id.txtAvailablePoints);
        txtTotPoints = (TextView)findViewById(R.id.txtTotalPoints);
        txtRedPoints = (TextView)findViewById(R.id.txtRedeem_points);

        prgTot = (ProgressBar)findViewById(R.id.prgtotal);
        prgRed = (ProgressBar)findViewById(R.id.prgredeem);

        new LoadPoints().execute();
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

    private void SetFadeIn(){
        Animation crossFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
        txtAvbPoints.setAnimation(crossFade);
        txtTotPoints.setAnimation(crossFade);
        txtRedPoints.setAnimation(crossFade);
    }

    private void setValueAnimate(String rpoints,String tpoints){

        int tpoints_ = Integer.parseInt(tpoints);
        int rpoints_ = Integer.parseInt(rpoints);
        int apoints_ = tpoints_ - rpoints_;

        ValueAnimator animateBalance = ValueAnimator.ofInt(0,apoints_);
        animateBalance.setDuration(2000);
        animateBalance.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                txtAvbPoints.setText(animation.getAnimatedValue().toString());
            }
        });
        ValueAnimator animateTotal = ValueAnimator.ofInt(0,tpoints_);
        animateTotal.setDuration(2000);
        animateTotal.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                txtTotPoints.setText(animation.getAnimatedValue().toString());
            }
        });
        ValueAnimator animateRedeem = ValueAnimator.ofInt(0,rpoints_);
        animateRedeem.setDuration(2000);
        animateRedeem.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                txtRedPoints.setText(animation.getAnimatedValue().toString());
            }
        });
        animateBalance.start();
        animateTotal.start();
        animateRedeem.start();
    }

    private class LoadPoints extends AsyncTask<String, String, String>{

        String AvbPoints="",TotPoints="",RedPoints="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                return this.MyPoints();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            try {
            String Result = response;

            if(Result==null){
                Toast.makeText(MypointsActivity.this, "Connection error. Check your internet connection.", Toast.LENGTH_SHORT).show();
            }else {

                    JSONObject jsonObject = new JSONObject(Result);
                    String sts = jsonObject.getString("state");
                    if(sts.equals("1")){
                        Rpoints = jsonObject.getString("Rpoints");
                        Tpoints = jsonObject.getString("Tpoints");
                    }
                }
                prgTot.setVisibility(View.GONE);
                prgRed.setVisibility(View.GONE);

                setValueAnimate(Rpoints,Tpoints);
                SetFadeIn();
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }




        private String MyPoints(){
            String result="";
            HttpURLConnection conn = Connector.connection("http://192.168.101.131:88/rest/barcode/myPointManager.php");
            if(conn==null){
                return null;
            }

            try{
                OutputStream os = conn.getOutputStream();
                BufferedWriter bwritter = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                bwritter.write(new PackUID(UID).Retrivew_MyPoints());
                bwritter.flush();
                bwritter.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if(responseCode==conn.HTTP_OK){
                    BufferedReader breader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer stringBuffer = new StringBuffer();

                    String line;

                    while ((line=breader.readLine())!=null){
                        stringBuffer.append(line);
                    }

                    breader.close();
                    return stringBuffer.toString();

                }

            }catch (ConnectException cex){
                cex.printStackTrace();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return null;
        }
    }


}
