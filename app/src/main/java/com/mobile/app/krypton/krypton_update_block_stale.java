package com.mobile.app.krypton;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class krypton_update_block_stale extends SQLiteOpenHelper {


    final protected static char[] hexArray = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    boolean token_updated = false;


    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Here we have a stale block which means a block that we have that is correct but no one else has. The chain forked a different way.
    //We can delete that stale block here. Also the user can use this class from the settings menu to delete the most recent block even if it's not stale.

    krypton_update_block_stale(){//*****************************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//**********************************************************




    boolean update(){//**************************************************************************

        //Already active.

        SQLiteDatabase db = this.getWritableDatabase();


        try {


            System.out.println("[>>>] [installing] UPDATE TOKEN TEST FOR STALE BLOCKCHAIN....");

            if (krypton_update_new_block_remote.update_in_use) {throw new IllegalAccessException("System is being updated.");}
            if (network.installing_package) {throw new IllegalAccessException("System is being updated.");}

            token_updated = false;


            boolean testm0 = false;
            boolean testm1 = false;
            boolean testm2 = false;
            boolean testm3 = false;
            boolean testm4 = false;


            mining.mining_stop = true;

            System.out.println("Load server's mining token..." );

            testm1 = true;
            testm2 = true;

            System.out.println("testm1 " + testm1);
            System.out.println("testm2 " + testm2);



            //If we have a stale block delete it.
            if (testm1 && testm2) {


                //Move from back up blocks.
                String hash_delete = "";
                String previous_hash = "";
                String mining_new_block = "";


                try {


                    String query = ("SELECT hash_id,previous_hash_id,mining_new_block FROM mining_db ORDER BY xd DESC LIMIT 1");
                    Cursor cursor = db.rawQuery(query, null);

                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {

                        //hash_delete = krypton_database_driver.rs.getString(1);
                        hash_delete = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));
                        previous_hash = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));
                        mining_new_block = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2)));

                        System.out.println("hash_delete " + hash_delete);

                        cursor.moveToNext();

                    }//while

                    cursor.close();

                } catch (Exception e) {e.printStackTrace();}


                boolean has_backup = false;
                String[] token_ssp3 = new String[network.listing_size];


                try {


                    String query = ("SELECT * FROM backup_db WHERE hash_id='" + previous_hash + "' LIMIT 1");
                    Cursor cursor = db.rawQuery(query, null);

                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {

                        System.out.println("GETB " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))) );

                        for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                            token_ssp3[loop1] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));

                        }//********************************************************************

                        has_backup = true;

                        cursor.moveToNext();

                    }//while

                    cursor.close();


                } catch (Exception e) {e.printStackTrace();}

                System.out.println("has_backup    " + has_backup);
                System.out.println("token_ssp3[0] " + token_ssp3[0]);


                if (has_backup) {


                    //We have a backup so we can roll back the database correctly.

                    System.out.println("We have a backup.");

                    db.execSQL("DELETE FROM listings_db where id=" + token_ssp3[0]);

                    ContentValues values = new ContentValues();

                    System.out.println("PSV UPDATE " + token_ssp3[0]);

                    for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//******************

                        values.put(network.item_layout[loop1], token_ssp3[loop1]);

                    }//*****************************************************************************

                    //Inserting Row.
                    db.insert("listings_db", null, values);


                    System.out.println("DELETE LAST MINING BLOCK.");
                    db.execSQL("DELETE FROM mining_db where mining_new_block='" + mining_new_block + "'");

                    System.out.println("UPDATE DONE>");

                    network.blockchain_errors++;

                    token_updated = true;


                }//************
                else {


                    //We do not have a backup so we have to rollback without a replacement.
                    //This may destroy the database depending on the damage and what's missing.

                    System.out.println("We don't have a backup.");

                    System.out.println("DELETE LAST MINING BLOCK.");
                    db.execSQL("DELETE FROM mining_db where mining_new_block='" + mining_new_block + "'");

                    System.out.println("DELETE LAST LISTING BLOCK.");
                    db.execSQL("DELETE FROM listings_db where hash_id='" + hash_delete + "'");


                }//**

            }//******************
            else {System.out.println("DELETE LAST BLOCK FAILED!");}


            //Reload needed information.
            //We can't call the reload class because that class also can call this class which could create a loop.
            //Here we call the lite version with no error checking.

            krypton_database_load loadx = new krypton_database_load();
            loadx.load_lite();


        } catch (Exception e) {e.printStackTrace();}
        finally {

            db.close();
            System.out.println("finally block executed");

        }


        return token_updated;

    }//load







    public String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for ( int j = 0; j < bytes.length; j++ ) {

            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];

        }//***************************************

        return new String(hexChars);

    }//********************************************


 


}//class
