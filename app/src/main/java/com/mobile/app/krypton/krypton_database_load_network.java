package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;

//this class is loaded only a few times during operation, on start up and then if something is changed.
//all the loading is done here that isn't related to the blockchain so that krypton_database_load doesn't have to work as hard.

public class krypton_database_load_network extends SQLiteOpenHelper {



    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted

    }


    //This class is called only on start up and on major changes to info added.

    krypton_database_load_network(){//**************************************************************************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

        try {


            network.database_in_use = true;

            System.out.println("SQL 1 " + System.currentTimeMillis());
            SQLiteDatabase db = this.getWritableDatabase();
            System.out.println("SQL 2 " + System.currentTimeMillis());

            int ix0 = 0;



            //int database_test = 0;

            //db.execSQL("DELETE FROM searchx WHERE id != 10000");

            //This was used during testing to repair blockchain errors.
            //krypton_database_load_repair_tools tools = new krypton_database_load_repair_tools();

            System.out.println("[>>>] LOAD NETWORK");



            //If the user is in non full node mode "SPV" then we show the number of tokens they have here.

            try {

                //db.execSQL("DELETE FROM listings_lite_db");

                System.out.println("Get listings_lite_db size...");

                String query = ("SELECT xd FROM listings_lite_db");
                Cursor cursor = db.rawQuery(query, null);

                System.out.println("SPV wallet size " + cursor.getCount());

                cursor.close();

            } catch(Exception e) {e.printStackTrace();}




            //Load the setting database and make sure it is accessible to the rest of the program in the form of settingsx

            System.out.println("Loading 1");

            try {

                network.base58_id = network.pub_key_id;//network.settingsx[5]

                int len = network.base58_id.length();
                byte[] data = new byte[len / 2];

                for (int i = 0; i < len; i += 2) {

                    data[i / 2] = (byte) ((Character.digit(network.base58_id.charAt(i), 16) << 4) + Character.digit(network.base58_id.charAt(i+1), 16));

                }//*******************************

                byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(data);


                byte[] a = "=".getBytes();
                byte[] b = sha256_1;

                byte[] result = new byte[a.length + b.length];
                System.arraycopy(a, 0, result, 0, a.length);
                System.arraycopy(b, 0, result, a.length, b.length);

                String base58 = Base58Encode.encode(result);

                System.out.println("base58 " + base58 + " " + base58.length());

                if(network.use_old_key){network.base58_id = Base58Encode.encode(sha256_1);}
                else{network.base58_id = base58;}


            } catch(Exception e) {e.printStackTrace();}




            //We refresh our node list using the addresses in the blockchain

            try {

                //System.out.println("Rebuild and refresh node list...");

                krypton_database_node node = new krypton_database_node();
                //node.refreshBlockchain();
                node.refresh();

                System.out.println("network.network_size " + network.network_size);

            } catch(Exception e) {e.printStackTrace();}





            //Loading the first token in the database because it is used the most often.

            try {


                System.out.println("Load QL Token..." );

                network.html_block_ql = new String[network.listing_size];

                String query = ("SELECT * FROM listings_db WHERE id=100000 ORDER BY xd DESC LIMIT 1");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                ix0 = 0;
                while (!cursor.isAfterLast()) {

                    for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//***********

                        network.html_block_ql[loop1] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));
                        System.out.println("QKL>>> " + network.html_block_ql[loop1]);

                        ix0++;

                    }//********************************************************************

                    cursor.moveToNext();

                }//while

                //The token will be in hex format change it to strings so it's ready to use
                network.html_block_ql = new network_convert().hex_to_string(network.html_block_ql);

                cursor.close();

                System.out.println("QL Token size: " + ix0);

                //network.database_active = 1;
                //if(database_test == 23){network.database_active = true; System.out.println("DATABASE ACTIVE YES. " + network.database_active);}
                System.out.println("DB LOADED...");



            } catch(Exception e) {e.printStackTrace(); System.out.println("Probably just not a full node yet...");}
            finally {

                System.out.println("SQL 3 " + System.currentTimeMillis());

                db.close();
                System.out.println("finally block executed");

                System.out.println("SQL 4 " + System.currentTimeMillis());

            }//******


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }



    }//load



}//load network
