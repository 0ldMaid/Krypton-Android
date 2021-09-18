package com.mobile.app.krypton;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class krypton_database_get_last_mining_id extends SQLiteOpenHelper {



    String new_hash = "";
    String prev_hash = "";
    String last_package = "";


    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This will upgrade tables, adding columns and new tables.
        //Note that existing columns will not be converted


    }


    //Here we get the most recent mining hash 000000.... and make sure it fits with the new block that comes after it.

    krypton_database_get_last_mining_id() {//*******************************************************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//*********************************************************************************************



     public String getLastHash(){

         try {


             network.database_in_use = true;

             SQLiteDatabase db = this.getWritableDatabase();

             new_hash = "";
             prev_hash = "";
             last_package = "";


             try {


                 System.out.println("[>>>] Get Last Hash");

                 String query = ("SELECT * FROM mining_db ORDER BY xd DESC LIMIT 1");
                 Cursor cursor = db.rawQuery(query, null);

                 cursor.moveToFirst();

                 System.out.println("rsxs " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5))));
                 System.out.println("rsxs " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6))));
                 System.out.println("rsxs " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(10))));

                 prev_hash = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5)));
                 new_hash = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6)));
                 last_package = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(10)));

                 cursor.close();


             }catch(Exception e){e.printStackTrace();}
             finally {

                 db.close();
                 System.out.println("finally block executed");

             }


         } catch (Exception e) {e.printStackTrace();}
         finally {

            network.database_in_use = false;

         }

         return new_hash;

    }



    public String getPrevHash(){

        return prev_hash;

    }//******************************



    public String getLastPackage(){

        return last_package;

    }//******************************




}//class
