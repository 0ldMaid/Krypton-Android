package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;





public class krypton_database_get_token_fmh_x extends SQLiteOpenHelper {



    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Get tokens from mining hash X this is the regular call when the client wants new blocks from us. They have an old block they just need to be brought up to date.

    public krypton_database_get_token_fmh_x(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }




    public String[][] get_tokens(String id, int blocks){//**************************************************************************

        String[][] token_ssp2 = null;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            String[][] buffer_ssp1;
            String[][] buffer_ssp2;

            int ix0 = 0;
            //int ix1 = 0;
            //int ix2 = 0;

            boolean found_item = false;


            try {


                System.out.println("[>>>] GET TOKEN FROM MINING HASH X");


                buffer_ssp1 = new String[network.miningx_size][network.package_block_size];
                buffer_ssp2 = new String[network.listing_size][network.package_block_size];


                //First we build and error list so that if everything fails we just send a list of errors.
                for (int loop1 = 0; loop1 < network.package_block_size; loop1++) {//*********

                    for (int loop2 = 0; loop2 < network.listing_size + network.miningx_size; loop2++) {//*********

                        try {buffer_ssp1[loop2][loop1] = "error";} catch (Exception e) {}//only 9
                        try {buffer_ssp2[loop2][loop1] = "error";} catch (Exception e) {}

                    }//*******************************************************************************************

                }//**************************************************************************



                //Now we get the last block id using the hash the user sent us.
                int id_mining = -1;

                try {

                    String query = ("SELECT xd FROM mining_db WHERE mining_new_block=? LIMIT 1");
                    Cursor cursor = db.rawQuery(query, new String[]{id});

                    cursor.moveToFirst();

                    id_mining = cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(0)));

                    cursor.close();

                } catch (Exception e) {e.printStackTrace();}




                //Now we can get a list of blocks moving upward from the block the user sent us.
                //These are new blocks the user doesn't have yet.
                System.out.println("id_mining " + id_mining);

                if (id_mining > 0) {

                    List<String> blockx_list = new ArrayList<String>();

                    try {

                        String query = ("SELECT * FROM mining_db WHERE xd > " + id_mining + " ORDER BY xd ASC LIMIT " + network.package_block_size);
                        Cursor cursor = db.rawQuery(query, null);

                        cursor.moveToFirst();

                        ix0 = 0;
                        while (!cursor.isAfterLast()) {

                            for (int loop2 = 0; loop2 < network.miningx_size; loop2++) {//*********

                                buffer_ssp1[loop2][ix0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop2 + 1)));

                            }//********************************************************************

                            System.out.println( cursor.getString(cursor.getColumnIndex("hash_id")) );
                            blockx_list.add( cursor.getString(cursor.getColumnIndex("hash_id")) );

                            ix0++;

                            cursor.moveToNext();

                        }//while

                        cursor.close();

                    } catch(Exception e) {e.printStackTrace();}


                    System.out.println("Load listings_db... get token FHMX");

                    id = Integer.toString(id_mining);

                    String cmdx = "";
                    int ixp0 = 0;

                    for (int loop1 = 0; loop1 < ix0; loop1++) {//**********

                        cmdx = cmdx + "hash_id='" + blockx_list.get(loop1) + "'";
                        if(loop1 < ix0 -1){cmdx = cmdx + " OR ";}

                    }//****************************************************

                    if (ix0 == 0) {cmdx = cmdx + "id=0";}

                    if (ix0 > 0) {found_item = true;}

                    //System.out.println("cmdx " + cmdx);


                    //Because we may have changed the block since the point the user is requesting we need to get the block from the backup database.

                    try {

                        String query = ("SELECT * FROM backup_db WHERE " + cmdx + "");
                        Cursor cursor = db.rawQuery(query, null);

                        int listxp = cursor.getCount();

                        System.out.println("listxp " + listxp);

                        cursor.moveToFirst();

                        while (!cursor.isAfterLast()) {

                            System.out.println( cursor.getString(cursor.getColumnIndex("hash_id")) );

                            for (int loop2 = 0; loop2 < network.listing_size; loop2++) {//*********

                                buffer_ssp2[loop2][ixp0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop2 + 1)));

                            }//********************************************************************

                            ixp0++;

                            cursor.moveToNext();

                        }//while

                        cursor.close();

                    } catch (Exception e) {e.printStackTrace();}


                    System.out.println("ix0        " + ix0);
                    System.out.println("ixp0       " + ixp0);
                    System.out.println("found_item " + found_item);

                    if (ixp0 == ix0) {System.out.println("Good to go...");}
                    else {System.out.println("Error building FHM...");}


                    //Items are not in the same order!
                    //Reset the order here
                    int pp = 0;
                    token_ssp2 = new String[network.listing_size + network.miningx_size][ixp0];

                    boolean foundx = false;

                    for (int loop1 = 0; loop1 < ixp0; loop1++) {//**********

                        pp = 0;

                        for (int loop2 = 0; loop2 < network.miningx_size; loop2++) {//*********

                            token_ssp2[pp][loop1] = buffer_ssp1[loop2][loop1];
                            pp++;

                        }//********************************************************************

                        //Items are not in the same order!

                        foundx = false;

                        for (int loopx = 0; loopx < ixp0; loopx++) {//*********************************************

                            if (buffer_ssp2[1][loopx].equals(buffer_ssp1[7][loop1])) {

                                foundx = true;

                                for (int loop2 = 0; loop2 < network.listing_size; loop2++) {//*********

                                    token_ssp2[pp][loop1] = buffer_ssp2[loop2][loopx];
                                    pp++;

                                }//********************************************************************

                                break;

                            }//******************************************************

                        }//****************************************************************************************

                        if (foundx) {}
                        else {

                            System.out.println("Cannot find: " + buffer_ssp1[7][loop1]);

                        }//***

                    }//*****************************************************

                    //try{Thread.sleep(10000);} catch (InterruptedException e){}

                    System.out.println("ixp0 " + ixp0);
                    System.out.println("found_item " + found_item);


                }//if******************
                else {token_ssp2 = null;}



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
