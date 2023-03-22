package com.codestudioapps.learing.app.data.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.codestudioapps.learing.app.models.favorite.FavoriteModel;

import java.util.ArrayList;

public class FavoriteDbController {

    private SQLiteDatabase db;

    public FavoriteDbController(Context context) {
        db = DbHelper.getInstance(context).getWritableDatabase();
    }

    public int insertData(String postDetails) {

        ContentValues values = new ContentValues();
        values.put(DbConstants.POST_DETAILS, postDetails);

        // Insert the new row, returning the primary key value of the new row
        return (int) db.insert(
                DbConstants.FAVORITE_TABLE_NAME,
                DbConstants.COLUMN_NAME_NULLABLE,
                values);
    }

    public ArrayList<FavoriteModel> getAllData() {


        String[] projection = {
                DbConstants._ID,
                DbConstants.POST_DETAILS
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = DbConstants._ID + " DESC";

        Cursor c = db.query(
                DbConstants.FAVORITE_TABLE_NAME,  // The table name to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return fetchData(c);
    }

    private ArrayList<FavoriteModel> fetchData(Cursor c) {
        ArrayList<FavoriteModel> favDataArray = new ArrayList<>();

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    // get  the  data into array,or class variable
                    int itemId = c.getInt(c.getColumnIndexOrThrow(DbConstants._ID));
                    String postDetails = c.getString(c.getColumnIndexOrThrow(DbConstants.POST_DETAILS));


                    // wrap up data list and return
                    favDataArray.add(new FavoriteModel(itemId, postDetails));
                } while (c.moveToNext());
            }
            c.close();
        }
        return favDataArray;
    }

    public void deleteEachFav(String postDetails) {
        // Which row to update, based on the ID
        String selection = DbConstants.POST_DETAILS + "=?";
        String[] selectionArgs = {postDetails};

        db.delete(
                DbConstants.FAVORITE_TABLE_NAME,
                selection,
                selectionArgs);
    }

    public void deleteAllFav() {
        db.delete(
                DbConstants.FAVORITE_TABLE_NAME,
                null,
                null);
    }

}
