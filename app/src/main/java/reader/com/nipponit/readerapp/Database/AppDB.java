package reader.com.nipponit.readerapp.Database;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.StrictMode;
import android.text.BoringLayout;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by manojm on 05/22/2018.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class AppDB extends SQLiteOpenHelper {

    private static String DB_Name = "LocalDB";

    private static String Tb_User = "UserTable";
    private static String col_user_id = "UId";
    private static String col_user_name = "UName";
    private static String col_user_nic = "UNic";
    private static String col_user_contact = "UContact";
    private static String col_user_email = "UEmail";
    private static String col_user_password = "UPassword";
    private static String col_user_logstat = "ULogState";

    private static String Tb_Barcodes = "BarcodeTable";
    private static String col_barcode_id = "BId";
    private static String col_barcode_barcode = "BBarcode";
    private static String col_barcode_scanTime = "BScnTime";


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public AppDB(Context context) {
        super(context, DB_Name,null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        db.execSQL("CREATE TABLE "+Tb_User+" ("+col_user_id+" INTEGER ,"+col_user_name+" TEXT,"+col_user_nic+" TEXT,"+col_user_contact+" TEXT" +
                ","+col_user_email+" TEXT,"+col_user_password+" TEXT,"+col_user_logstat+" TEXT)");
        db.execSQL("CREATE TABLE "+Tb_Barcodes+" ("+col_barcode_id+" INTEGER PRIMARY KEY AUTOINCREMENT, "+col_barcode_barcode+" TEXT,"+col_barcode_scanTime+" TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Tb_User+"");
        db.execSQL("DROP TABLE IF EXISTS "+Tb_Barcodes+"");
    }

    public boolean SaveToLocalUser(String uid,String name,String nic,String contact,String email){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isInsert = false;
        try{

            boolean State = returnSaveState(db.delete(Tb_User,null,null));
            if(State) {
                ContentValues values = new ContentValues();
                values.put(col_user_id,uid);
                values.put(col_user_name, name);
                values.put(col_user_nic, nic);
                values.put(col_user_contact, contact);
                values.put(col_user_email, email);
                values.put(col_user_logstat, "x");
                isInsert = returnSaveState(db.insertOrThrow(Tb_User, null, values));
            }
        }catch (SQLiteException ex){
            Log.w("Error",ex.getMessage());
        }
        return isInsert;
    }

    private String CurrentDateTime(){
        String datetime = "";
        try{
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            datetime = dateFormat.format(calendar.getTime());
        }catch (Exception ex){
            Log.w("Error",ex.getMessage());
        }
        return datetime;
    }

    public boolean SaveToLocalBarcode(String barcode){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isInsert = false;
        try{
            if(IsInsert(barcode)) {
                ContentValues values = new ContentValues();
                values.put(col_barcode_barcode, barcode);
                values.put(col_barcode_scanTime, CurrentDateTime());
                isInsert = returnSaveState(db.insertOrThrow(Tb_Barcodes, null, values));
            }
        }catch (SQLiteException ex){
            Log.w("Error",ex.getMessage());
        }
        return isInsert;
    }

    private boolean IsInsert(String barcode){
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isInsert = true;
        try {
            Cursor curList = null;
            String ListCol [] ={col_barcode_id};
            String ListVal [] ={barcode};
            curList = db.query(Tb_Barcodes,ListCol,col_barcode_barcode + "=?",ListVal,null,null,null);
            if(curList.getCount()>0){
                isInsert = false;
            }
        }catch (SQLiteException e){
            Log.w("error",e.getMessage());
        }
        return isInsert;
    }

    public Cursor RetrivewBarcodeData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=null;
        String [] col = {col_barcode_barcode,col_barcode_scanTime};
        try {
            cursor = db
                    .query(Tb_Barcodes,col,null,null,null,null,null);
        }catch (Exception ex){

        }
        return cursor;
    }

    public String RetrivewID(){
        String nic = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String [] col = {col_user_id};
        try{
            Cursor cursor = db.query(Tb_User,col,col_user_logstat+"='x'",null,null,null,null);
            if(cursor!=null){
                while(cursor.moveToNext()) {
                    nic = cursor.getString(0);
                }
            }
        }catch (Exception ex){
            Log.w("error",ex.getMessage());
        }
        return nic;
    }


    private boolean returnSaveState(long res){
        if(res==-1)
            return false;
        else
            return true;
    }

}
