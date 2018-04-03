package limo.mylimo;

// Creatind by Shoaib anwar //
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class MyDatabase extends SQLiteOpenHelper {
    Context context;

    public static final String DATABASE_NAME = "booking.db";
    private static final int DatabaseVersion = 1;
    public static final String NAME_OF_TABLE = "bookingtable";
    public static final String Col_1 = "id";
    public static final String Col_2 = "pikcuplocation";
    public static final String Col_3 = "dropofflocation";
    public static final String Col_4 = "pickuplatlng";
    public static final String Col_5 = "drobofflatlng";
    public static final String Col_6 = "pickupdate";
    public static final String Col_7 = "pickuptime";
    public static final String Col_8 = "estimateddistance";
    public static final String Col_9 = "estimatedfare";
    public static final String Col_10 = "cartype";


    String CREATE_TABLE_CALL = "CREATE TABLE " + NAME_OF_TABLE + "(" + Col_1 + " integer PRIMARY KEY AUTOINCREMENT," + Col_2 + " TEXT, " + Col_3 + " TEXT, " + Col_4 + " TEXT, " + Col_5 + " TEXT, " + Col_6 + " TEXT, " + Col_7 + " TEXT, " + Col_8 + " TEXT, " + Col_9 + " TEXT, " + Col_10 + " TEXT " + ")";
    // String Create_Virtual_Table_Call = "CREATE TABLE " + virtualTable + "(" + virtual_Col_1 + " integer PRIMARY KEY," + virtua_Col_2 + " TEXT" + ")";;

    MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DatabaseVersion);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_CALL);
        //db.execSQL(Create_Virtual_Table_Call);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NAME_OF_TABLE);
        //db.execSQL("DROP TABLE IF EXISTS " + Create_Virtual_Table_Call);

    }

    //inserting post in databse
    public long insertDatatoDb(DbHelper post) {
        long result;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(Col_1, post.getId());
        values.put(Col_2, post.getPickupaddress());
        values.put(Col_3, post.getDroboffaddress());
        values.put(Col_4, post.getPickuplatlng());
        values.put(Col_5, post.getDrobofflatlng());
        values.put(Col_6, post.getPickupdate());
        values.put(Col_7, post.getPickuptime());
        values.put(Col_8, post.getEstimateddistance());
        values.put(Col_9, post.getEstimatedfare());
        values.put((Col_10), post.getCarType());

        //inserting valuse into table columns
        result = db.insert(NAME_OF_TABLE, null, values);
        db.close();
        return result;

    }



    /* fetching records from Database Table*/
    public ArrayList<DbHelper> getOrders() {
        String query = "SELECT * FROM " + NAME_OF_TABLE;
        ArrayList<DbHelper> addingToList = new ArrayList<DbHelper>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            while (c.moveToNext()) {
                DbHelper myHelper = new DbHelper();
                String id = c.getString(c.getColumnIndex(Col_1));
                String pickupaddress = c.getString(c.getColumnIndex(Col_2));
                String dropoffaddress = c.getString(c.getColumnIndex(Col_3));
                String picklatlng = c.getString(c.getColumnIndex(Col_4));
                String dropofflatlng = c.getString(c.getColumnIndex(Col_5));
                String pickdate = c.getString(c.getColumnIndex(Col_6));
                String picktime = c.getString(c.getColumnIndex(Col_7));
                String distance = c.getString(c.getColumnIndex(Col_8));
                String fare = c.getString(c.getColumnIndex(Col_9));
                String carType = c.getString(c.getColumnIndex(Col_10));
                myHelper.setId(id);
                myHelper.setPickupaddress(pickupaddress);
                myHelper.setDroboffaddress(dropoffaddress);
                myHelper.setPickuplatlng(picklatlng);
                myHelper.setDrobofflatlng(dropofflatlng);
                myHelper.setPickupdate(pickdate);
                myHelper.setPickuptime(picktime);
                myHelper.setEstimateddistance(distance);
                myHelper.setEstimatedfare(fare);
                myHelper.setCarType(carType);
                //adding data to array list
                addingToList.add(myHelper);

            }
        }

        db.close();
        return addingToList;

    }

    //Updatating post
    public boolean updateTable(int id, String postToUpdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_2,postToUpdate);
        db.update(NAME_OF_TABLE, contentValues, "id = ?", new String[]{Integer.toString(id)});
        db.close();
        return true;
    }

    //deleting post
    public boolean deleteFromTable(long rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(NAME_OF_TABLE, Col_1 + "=" + rowId, null);
        db.close();

        return true;

    }

}