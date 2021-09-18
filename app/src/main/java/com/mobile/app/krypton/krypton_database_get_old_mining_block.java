package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class krypton_database_get_old_mining_block extends SQLiteOpenHelper {



    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }



    public krypton_database_get_old_mining_block(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//********************************************


    //Here we are getting a recent mining block from an ID.

    public String[] getBlock(String listing_id){//**************************************************************************

        int modsx = network.listing_size + network.miningx_size;

        String[] token_ssp2 = new String[modsx];

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int ix0 = 0;


            try {


                System.out.println("GET OLD MINING BLOCK");

                for(int loop1 = 0; loop1 < network.miningx_size; loop1++){//*********

                    token_ssp2[loop1] = "error";

                }//*******************************************************************

                boolean found_item = false;


                try {

                    String query = ("SELECT * FROM mining_db WHERE link_id='" + listing_id + "' ORDER BY xd DESC LIMIT 1");
                    Cursor cursor = db.rawQuery(query, null);

                    cursor.moveToFirst();


                    ix0 = 0;
                    while(!cursor.isAfterLast()){

                        for(int loop2 = 0; loop2 < network.miningx_size; loop2++){//***********

                            token_ssp2[ix0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop2 + 1)));

                            ix0++;

                        }//********************************************************************

                        cursor.moveToNext();

                        found_item = true;

                    }//while


                    cursor.close();

                } catch(Exception e) {e.printStackTrace();}


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





}//class
