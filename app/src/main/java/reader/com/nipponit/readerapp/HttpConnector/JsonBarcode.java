package reader.com.nipponit.readerapp.HttpConnector;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by manojm on 06/05/2018.
 */

public class JsonBarcode {
    String uid;Cursor barcodeCursor;
    JSONArray jsonArray;
    static String UID="UID",BARCODE="BARCODE",SCAN_TIME="SCAN_DATE_TIME",DATA="DATA";

    public JsonBarcode(String uID, Cursor Barcode){
        this.uid = uID;
        this.barcodeCursor = Barcode;
    }

    public String BarcodeJsonArray(){
        String jsonString = "";
        try {
            jsonArray = new JSONArray();

            JSONObject jMainObject = new JSONObject();
            jMainObject.put(UID,uid);



            if(barcodeCursor != null && barcodeCursor.getCount() >= 0){
                while (barcodeCursor.moveToNext()){
                    JSONObject jObject = new JSONObject();
                    String _barcode = barcodeCursor.getString(0);
                    String _dateTime = barcodeCursor.getString(1);

                    jObject.put(BARCODE,_barcode);
                    jObject.put(SCAN_TIME,_dateTime);
                    jsonArray.put(jObject);
                }
               jMainObject.put(DATA,jsonArray);
            }jsonString = jMainObject.toString();
        } catch (JSONException e) {
            Log.w("err",e.getMessage());
        }
        return jsonString;
    }


}
