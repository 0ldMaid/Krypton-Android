package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;





public class krypton_database_get_token_fmh_n extends SQLiteOpenHelper {



    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Get tokens from mining hash N, this is called when the client has a forked blockchain and wants to find the fork block.
    //This block set is sent from the newest back until the client finds the block they want.

    public krypton_database_get_token_fmh_n(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }



    public String[][] getTokens(String id, int blocks){//**************************************************************************

        String[][] token_ssp2 = null;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            String[][] buffer_ssp1;
            String[][] buffer_ssp2;

            int ix0 = 0;


            try {


                System.out.println("[>>>] GET TOKEN FROM MINING HASH N");



                buffer_ssp1 = new String[network.miningx_size][network.package_block_size];
                buffer_ssp2 = new String[network.listing_size][network.package_block_size];

                //Here we populate the array with errors which will be fixed later.
                //If there is something wrong the user will just get these error lists.
                for(int loop1 = 0; loop1 < network.package_block_size; loop1++){//***********

                    for(int loop2 = 0; loop2 < network.listing_size + network.miningx_size; loop2++){//***********

                        try{buffer_ssp1[loop2][loop1] = "error";}catch(Exception e){}//only 9
                        try{buffer_ssp2[loop2][loop1] = "error";}catch(Exception e){}

                    }//*******************************************************************************************

                }//**************************************************************************


                int id_mining = -1;

                try {

                    //If the user already has a block we use that otherwise we give them our newest block.
                    if(id.equals("")){id = network.prev_block_mining_idx;}

                    String query = ("SELECT xd FROM mining_db WHERE mining_new_block=? LIMIT 1");
                    Cursor cursor = db.rawQuery(query, new String[]{id});

                    cursor.moveToFirst();

                    id_mining = cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(0)));

                    cursor.close();

                } catch (Exception e) {e.printStackTrace();}



                System.out.println("id_mining " + id_mining);

                if (id_mining > network.package_block_size) {

                    //Now we get a list of 25 blocks in order from our mining db and save them until the next stage.
                    List<String> blockx_list = new ArrayList<String>();

                    try {

                        String query = ("SELECT * FROM mining_db WHERE xd <= " + id_mining + " ORDER BY xd DESC LIMIT " + network.package_block_size);
                        Cursor cursor = db.rawQuery(query, null);

                        cursor.moveToFirst();

                        ix0 = 0;
                        while(!cursor.isAfterLast()){

                            for(int loop2 = 0; loop2 < network.miningx_size; loop2++){//***********

                                buffer_ssp1[loop2][ix0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop2 + 1)));

                            }//********************************************************************

                            System.out.println( cursor.getString(cursor.getColumnIndex("hash_id")) );
                            blockx_list.add( cursor.getString(cursor.getColumnIndex("hash_id")) );

                            ix0++;

                            cursor.moveToNext();

                        }//while

                        cursor.close();

                    } catch (Exception e) {e.printStackTrace();}



                    System.out.println("ix0 " + ix0);
                    //System.out.println( blockx_list.get(0) );




                    //Here we build a database command list of blocks to get. sometimes they are not in order so we have to call them by their real ID.

                    System.out.println("Load listings_db... get token FHMN");

                    id = Integer.toString(id_mining);

                    String cmdx = "";


                    //Here we are building the database quarry string.
                    for (int loop1 = 0; loop1 < ix0; loop1++) {//**********

                        cmdx = cmdx + "hash_id='" + blockx_list.get(loop1) + "'";
                        if(loop1 < ix0 -1){cmdx = cmdx + " OR ";}

                    }//****************************************************


                    if(ix0 == 0){cmdx = cmdx + "id=0";}


                    boolean found_item = false;
                    if(ix0 > 0){found_item = true;}

                    int ixp0 = 0;

                    //Now we use the quarry to call for the blocks we want.
                    //If there is any kind of database error or the blocks aren't perfect this will fail.
                    try {

                        System.out.println("cmdx " + cmdx);

                        String query = ("SELECT * FROM backup_db WHERE " + cmdx + "");
                        Cursor cursor = db.rawQuery(query, null);

                        System.out.println("cursor.getCount(): " + cursor.getCount());

                        cursor.moveToFirst();

                        while (!cursor.isAfterLast()) {

                            for (int loop2 = 0; loop2 < network.listing_size; loop2++) {//*********

                                buffer_ssp2[loop2][ixp0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop2 + 1)));

                            }//********************************************************************

                            ixp0++;

                            cursor.moveToNext();

                        }//while

                        cursor.close();

                    } catch (Exception e) {e.printStackTrace();}



                    //Now we have the list of blocks the user wants so we put them together.
                    //If the quarry failed then we will skip this and just send the user our error list.

                    System.out.println("ix0        " + ix0);
                    System.out.println("ixp0       " + ixp0);
                    System.out.println("found_item " + found_item);

                    if(ixp0 == ix0){System.out.println("Good to go...");}
                    else{System.out.println("Error building FHM...");}


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

                            }//********************************************************

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


                System.out.println("Done...");


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
