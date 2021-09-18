package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class krypton_database_get_token_fmh extends SQLiteOpenHelper {




    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Get token from Mining Hash we don't need this class anymore, but it was part of the old version.
    //There are many uses for a mining hash so we still might need this class someday.

    public krypton_database_get_token_fmh(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }



    public String[] getToken(String id){//**************************************************************************

        int modsx = network.listing_size + network.miningx_size;

        String[] token_ssp2 = new String[modsx];

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("[>>>] GET TOKEN FROM MINING HASH");

                for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                    token_ssp2[loop1] = "error";

                }//********************************************************************


                boolean found_item = false;

                int id_mining = -1;

                //sql injection
                id = id.replace("'","");
                id = id.replace("\"","");


                try {

                    String query = ("SELECT * FROM mining_db WHERE mining_old_block=' ? ' ORDER BY mining_date ASC LIMIT 1");
                    Cursor cursor = db.rawQuery(query, new String[]{id});

                    cursor.moveToFirst();

                    //ix0 = 0;
                    while (!cursor.isAfterLast()) {

                        for (int loop1 = 0; loop1 < network.miningx_size; loop1++) {//*********

                            token_ssp2[loop1] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));

                        }//********************************************************************

                        id_mining = cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(1)));

                        found_item = true;

                        cursor.moveToNext();

                    }//while

                    cursor.close();

                    System.out.println("found_item " + found_item);

                } catch(Exception e) {e.printStackTrace();}




                try {

                    System.out.println("Load listings_db... get token FHM");

                    String query = ("SELECT * FROM listings_db WHERE id=" + id_mining + " ORDER BY id ASC LIMIT 1");
                    Cursor cursor = db.rawQuery(query, null);

                    cursor.moveToFirst();

                    //ix0 = 0;
                    while (!cursor.isAfterLast()) {

                        for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                            token_ssp2[loop1 + network.miningx_size] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));

                        }//********************************************************************

                        found_item = true;

                        cursor.moveToNext();

                    }//while

                    cursor.close();

                    System.out.println("found_item " + found_item);

                } catch(Exception e) {e.printStackTrace();}



                //if the item is too old send them the first block
                if (!found_item) {

                    token_ssp2 = null;

                }//***************




                //if the item is too old send them the first block
                if (!found_item && 1 == 2) {

                    id_mining = -1;

                    try {

                        System.out.println("Load mining_db first item..." );

                        String query = ("SELECT * FROM mining_db ORDER BY mining_date ASC LIMIT 1");
                        Cursor cursor = db.rawQuery(query, null);

                        cursor.moveToFirst();

                        //ix0 = 0;
                        while(!cursor.isAfterLast()){


                            for(int loop1 = 0; loop1 < network.miningx_size; loop1++){//***********

                                token_ssp2[loop1] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));

                            }//********************************************************************

                            id_mining = cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(1)));
                            found_item = true;

                            cursor.moveToNext();

                        }//while

                        cursor.close();

                    } catch(Exception e) {e.printStackTrace();}


                    try {

                        System.out.println("Load mining_db first item..." );


                        String query = ("SELECT * FROM listings_db WHERE id=" + id_mining);
                        Cursor cursor = db.rawQuery(query, null);

                        cursor.moveToFirst();

                        //ix0 = 0;
                        while(!cursor.isAfterLast()){


                            for(int loop1 = 0; loop1 < network.listing_size; loop1++){//***********

                                token_ssp2[loop1 + network.miningx_size] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));

                            }//********************************************************************

                            found_item = true;

                            cursor.moveToNext();

                        }//while

                        cursor.close();

                        System.out.println("found_item " + found_item);

                    } catch(Exception e) {e.printStackTrace();}

                }//*************


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
