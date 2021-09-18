package com.mobile.app.krypton;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class krypton_replace_block_stale extends SQLiteOpenHelper {


    final protected static char[] hexArray = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();



    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Here we remove a block that is stale that means it passes the tests but other nodes are using a different one.
    //This class was only for me when I was fixing errors in the database. It's not called in formal use but it can be used to fix broken chains.

    krypton_replace_block_stale(){//*****************************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//**********************************************************




    boolean updaten(){//**************************************************************************

        //already active

        SQLiteDatabase db = this.getWritableDatabase();

        String id = "100193";

        String[] token_ssp0 = new String[network.listing_size];

        boolean token_updated = false;


        try {


            token_updated = false;

            System.out.println("[>>>] TEST REPLACE STALE BLOCKCHAIN....");

            String[] tokenx = token_ssp0;//get the old token

            boolean testm0 = false;
            boolean testm1 = false;
            boolean testm2 = false;
            boolean testm3 = false;
            boolean testm4 = false;

            String[] token_ssp1 = new String[network.miningx_size];
            String[] token_ssp2 = new String[network.listing_size];



            System.out.println("Load server's mining token..." );

            //move from back up blocks.
            String hash_delete = "/fgBFaa1u8KxCWsHqMPcvnaIVzW2MHCB4W9ITAPmOBQ=";

            boolean has_backup = false;
            String[] token_ssp3 = new String[network.listing_size];


            try {


                String query = ("SELECT * FROM backup_db WHERE hash_id='" + hash_delete + "'");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                        token_ssp3[loop1] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));
                        System.out.println("GETB " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1))) );

                    }//********************************************************************

                    has_backup = true;

                    cursor.moveToNext();

                }//while

                cursor.close();


            } catch (Exception e) {e.printStackTrace();}

            System.out.println("has_backup " + has_backup);
            System.out.println("token_ssp3[0] " + token_ssp3[0]);


            if (has_backup) {


                //krypton_database_driver.s.execute("DELETE FROM listings_db where id=" + token_ssp3[0]);
                db.execSQL("DELETE FROM listings_db where id=" + token_ssp3[0]);
                db.execSQL("DELETE FROM backup_db where hash_id='rGMjagjvsAEotMV9fiStjez/n96X5HP3S6wX9pghNDM='");

                ContentValues values = new ContentValues();

                //ps.setInt(1, Integer.parseInt(token_ssp3[0]));
                for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//********************************

                    values.put(network.item_layout[loop1], token_ssp3[loop1]); //
                    System.out.println("PSV UPDATE " + token_ssp3[loop1]);

                }//*******************************************************************************************

                // Inserting Row
                db.insert("listings_db", null, values);


                //db.execSQL("DELETE FROM mining_db where mining_new_block='" + network.last_block_mining_idx + "'");

                System.out.println("UPDATE DONE>");

                token_updated = true;


            }//************
            else {

                System.out.println("Cannot update database server's info is wrong.");
                //JOptionPane.showMessageDialog(null, "Blockchaing has forcked! Find the blockchain with the most work done\nand update your node list accordingly.");

            }


            System.out.println("Committed the transaction");


        } catch (Exception e) {e.printStackTrace();}
        finally {

            db.close();
            System.out.println("finally block executed");

        }

        //Reload all info.
        krypton_database_load loadx = new krypton_database_load();
        loadx.load();

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
