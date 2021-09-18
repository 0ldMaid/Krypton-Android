package com.mobile.app.krypton;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class krypton_database_repair_tools extends SQLiteOpenHelper{
    /* the default framework is embedded*/




    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Here we are loading a token from the listings_db these are the "coins" of the system.

    public krypton_database_repair_tools(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//*********************************







    //Here we get the token using the HASH not the ID

    public List<String> getBlockchainList(){//**********************************************************

        List token_ssp2 = new ArrayList();

        try {


            SQLiteDatabase db = this.getWritableDatabase();

            network.database_in_use = true;


            try{


                System.out.println("Get Blockchain Hash List...");

                //Get the number of blocks in our blockchain. Should be 25k but if this is happening then there is a problem.
                //We know that there are 25k blocks in the system so if we ask for distinct values from the mining blockchain then the block at 25k should be our oldest block.
                //Everything before that isn't needed.

                String query = ("SELECT xd,link_id,hash_id FROM mining_db GROUP BY link_id ORDER BY xd DESC LIMIT " + network.hard_token_limit);
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    token_ssp2.add(cursor.getString(cursor.getColumnIndex("hash_id")));

                    cursor.moveToNext();

                }//while


                cursor.close();


            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("Finally block executed.");

            }//******


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return token_ssp2;

    }//load







    public List<String> getTokenList(){//**********************************************************

        List token_ssp2 = new ArrayList();

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try{


                System.out.println("Get Token Hash List...");

                //Get the number of tokens in our database.
                //It should be 25k but if the program is doing this then there is a problem.

                String query = ("SELECT hash_id FROM listings_db");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    token_ssp2.add(cursor.getString(cursor.getColumnIndex("hash_id")));

                    cursor.moveToNext();

                }//while


                cursor.close();


            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("Finally block executed.");

            }//******


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return token_ssp2;

    }//load






    public boolean addMissingToken(String[] move_item){//*****************************************************

        boolean added = false;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("Add Token...");

                String query = ("SELECT hash_id FROM listings_db WHERE hash_id='" + move_item[1] + "' OR id=" + move_item[0] );
                Cursor cursor = db.rawQuery(query, null);


                //If we don't already have the item. Then we add it
                if (cursor.getCount() == 0) {


                    System.out.println("Item is missing attempt to install...");

                    //Save the listing information.
                    try {

                        //Get rid of the old token first.
                        db.execSQL("DELETE FROM listings_db WHERE id=" + move_item[0]);

                        ContentValues values = new ContentValues();

                        for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//**********************************

                            values.put(network.item_layout[loop1], move_item[(loop1)]); //

                        }//*******************************************************************************************

                        //Inserting Row
                        db.insert("listings_db", null, values);

                        added = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    //The backup database is used to send info to our peers if they are behind or if we have a blockchain fork we have to roll back.
                    try {

                        System.out.println("PS UPDATE BB " + move_item[0]);

                        //delete the old block.
                        db.execSQL("DELETE FROM backup_db WHERE hash_id='" + move_item[1] + "'");

                        ContentValues values = new ContentValues();

                        for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//**********************************

                            values.put(network.item_layout[loop1], move_item[(loop1)]);//

                        }//*******************************************************************************************

                        //Inserting Row
                        db.insert("backup_db", null, values);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }//if



            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("Finally block executed.");

            }//******


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return added;

    }//load






}//get
