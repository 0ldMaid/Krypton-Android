package com.mobile.app.krypton;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class krypton_database_reset_blockchain extends SQLiteOpenHelper {



    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This will upgrade tables, adding columns and new tables.
        //Note that existing columns will not be converted


    }


    //Here we are deleting all blockchain information by user request.

    krypton_database_reset_blockchain() {//*******************************************************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//*********************************************************************************************



     public Boolean resetBlockchain(){

        boolean deleted = false;

         try {


             network.database_in_use = true;

             SQLiteDatabase db = this.getWritableDatabase();


             try {


                 System.out.println("[>>>] Reset");

                 db.execSQL("DELETE FROM listings_db");
                 db.execSQL("DELETE FROM mining_db");
                 db.execSQL("DELETE FROM backup_db");
                 db.execSQL("DELETE FROM searchx");
                 db.execSQL("DELETE FROM new_update");
                 db.execSQL("DELETE FROM test_listings_db");

                 deleted = true;

             } catch (Exception e) {e.printStackTrace();}
             finally {

                 db.close();
                 System.out.println("finally block executed");

             }


         } catch (Exception e) {e.printStackTrace();}
         finally {

            network.database_in_use = false;

         }

         return deleted;

    }






}//class
