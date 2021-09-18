package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;




public class krypton_database_get_token_first extends SQLiteOpenHelper {



    @Override
    public void onCreate(SQLiteDatabase db) {
        //This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This will upgrade tables, adding columns and new tables.
        //Note that existing columns will not be converted


    }



    //Here we are trying to get the last necessary mining block. Old blocks that aren't related to our current tokens can be deleted to save space.
    //So first we get the oldest "coin" we have and then find the block that relates to it. Then anything before that block can be deleted.
    //If you wanted to have a chain like Bitcoin you just wouldn't delete the old blocks and the chain would continue forever.
    //But for us the old part of the chain isn't needed anymore so we can skip it.

    public krypton_database_get_token_first(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//**************************************



    public String[] getToken(){//**************************************************************************

        int modsx = network.listing_size + network.miningx_size;

        String[] token_ssp2 = new String[modsx];

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("[>>>] GET TOKEN FROM MINING HASH FIRST");


                for(int loop1 = 0; loop1 < modsx; loop1++){//*************************

                    token_ssp2[loop1] = "error";

                }//*******************************************************************

                boolean found_item = false;

                int id_mining = -1;



                try {


                    //We know that there are 25k blocks in the system so if we ask for distinct values from the mining blockchain then the block at 25k should be our oldest block.
                    //Everything before that isn't needed.

                    String query = ("SELECT * FROM mining_db GROUP BY link_id ORDER BY xd DESC LIMIT " + network.hard_token_limit);
                    Cursor cursor = db.rawQuery(query, null);

                    cursor.moveToLast();

                    //ix0 = 0;
                    while(!cursor.isAfterLast()){

                        for(int loop1 = 0; loop1 < network.miningx_size; loop1++){//***********

                            System.out.println("TT: " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1))));
                            token_ssp2[loop1] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));

                        }//********************************************************************

                        id_mining = cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(1)));

                        found_item = true;

                        cursor.moveToNext();

                    }//while

                    cursor.close();

                } catch (Exception e ){e.printStackTrace(); found_item = false;}

                System.out.println("found_item " + found_item);




                try {

                    System.out.println("Load listings_db... get token FIRST");

                    String query = ("SELECT * FROM listings_db WHERE id=" + id_mining + " ORDER BY id ASC LIMIT 1");
                    Cursor cursor = db.rawQuery(query, null);

                    cursor.moveToFirst();

                    //ix0 = 0;
                    while (!cursor.isAfterLast()) {


                        for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                            token_ssp2[loop1 + network.miningx_size] =  cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));

                        }//********************************************************************

                        found_item = true;

                        cursor.moveToNext();

                    }//while

                    cursor.close();

                    System.out.println("found_item " + found_item);

                } catch (Exception e) {e.printStackTrace();}


            } catch (Exception e) {e.printStackTrace();}
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
