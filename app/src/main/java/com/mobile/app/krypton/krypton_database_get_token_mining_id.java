package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class krypton_database_get_token_mining_id extends SQLiteOpenHelper{
    /* the default framework is embedded*/



    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }



    //Here we get a token we own that has our public key in it. This is needed so that other nodes can verify our mining block came from someone with tokens.
    //The problem is that if you send a token to someone their public key is not yet in it so they would need to update their token at least one time before they can use it for mining.

    public krypton_database_get_token_mining_id(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//*********************************



    //These are tokens the program is requesting.

    public String getMiningID(){//**************************************************************************

        String token_ssp2 = null;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("Load Token..." );

                String query = ("SELECT id FROM listings_db WHERE owner_id='" + network.pub_key_id + "' ORDER BY id ASC LIMIT 1");//network.settingsx[5]
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                token_ssp2 = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));

                cursor.close();


            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return token_ssp2;

    }//load




 
}//get
