package com.mobile.app.krypton;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class krypton_update_listings_lite extends SQLiteOpenHelper {



    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Tools for users using the lite client. Note a full node.

    krypton_update_listings_lite() {//*************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//**************************************************





    //This is used to get individual listings requested by the peer. Sometimes they are updating their system, sometimes they are viewing an item.

    public String[] getTokenFH(String hash){//**************************************************************************

        SQLiteDatabase db = this.getWritableDatabase();

        network.database_in_use = true;

        String[] token_ssp2 = new String[network.listing_size];


        try {

            System.out.println("[>>>] Get Token From Hash");

            for(int loop1 = 0; loop1 < network.listing_size; loop1++){//***********

                token_ssp2[loop1] = "error";

            }//********************************************************************


            System.out.println("Load Token..." );

            String query = ("SELECT * FROM listings_lite_db WHERE hash_id=? ORDER BY id ASC");
            Cursor cursor = db.rawQuery(query, new String[]{hash});

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {


                for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                    token_ssp2[loop1] = cursor.getString((loop1 + 1));

                }//********************************************************************

                cursor.moveToNext();

            }//while


            cursor.close();


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

            db.close();
            System.out.println("finally block executed");

        }//******


        return token_ssp2;

    }//load







    //Add a new token to our lite database.
    //This is when the user doesn't want to download the whole blockchain.

    public boolean addToken(String[] token){

        SQLiteDatabase db = this.getWritableDatabase();

        network.database_in_use = true;

        boolean added = false;


        try {

            db.execSQL("DELETE FROM listings_lite_db WHERE id='" + token[0] + "'");

            ContentValues values = new ContentValues();

            for(int loop1 = 0; loop1 < network.listing_size; loop1++){//**********************************

                System.out.println("PS UPDATE BB " + token[loop1]);
                values.put(network.item_layout[loop1], token[loop1]); //

            }//*******************************************************************************************

            // Inserting Row
            db.insert("listings_lite_db", null, values);

            added = true;

        } catch (Exception e) {e.printStackTrace(); added = false;}
        finally {

            network.database_in_use = false;

            db.close();
            System.out.println("finally block executed");

        }//******


        return added;

    }







    //Removes a listing from the lite database.

    public boolean deleteToken(String hash_id){//**************************************************************************

        SQLiteDatabase db = this.getWritableDatabase();

        network.database_in_use = true;

        boolean deleted = false;

        //int ix0 = 0;


        try {


            System.out.println("Delete Token..." );

            db.execSQL("DELETE FROM listings_lite_db WHERE hash_id='" + hash_id + "'");
            deleted = true;

            System.out.println("deleted " + deleted);


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

            db.close();
            System.out.println("finally block executed");

        }//******


        return deleted;

    }//load








    //Removes a listing from the lite database.

    public boolean loadLiteDB(){//**************************************************************************

        SQLiteDatabase db = this.getWritableDatabase();

        network.database_in_use = true;

        boolean deleted = false;


        try {


            System.out.println("Load Token Listings..." );

            network.my_listings = new ArrayList<String>();

            String query = ("SELECT id FROM listings_lite_db ORDER BY id ASC");
            Cursor cursor = db.rawQuery(query, null);

            System.out.println("Lite DB count: " + cursor.getCount());

            cursor.moveToFirst();

            while(!cursor.isAfterLast()){

                network.my_listings.add( cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))) );

                cursor.moveToNext();

            }//while


            network.database_listings_owner = cursor.getCount();
            network.database_listings_for_edit = network.my_listings.size();

            cursor.close();


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

            db.close();
            System.out.println("finally block executed");

        }//******


        return deleted;

    }//load







    //This is the same as above but it is for loading tokens the user is requesting in the app.
    //Android doesn't need this because it can handel more then one request but in Derby we needed a way to have more then one connection at the same time.

    public String[] getToken(String id){//**************************************************************************

        String[] token_ssp2 = new String[network.listing_size];

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("[>>>] Get Token Lite");

                for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                    token_ssp2[loop1] = "error";

                }//********************************************************************


                //If this fails we don't continue.
                int idx = Integer.parseInt(id);


                System.out.println("Load Token..." );

                String query = ("SELECT * FROM listings_lite_db WHERE id=? ORDER BY id ASC");
                Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(idx)});

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {


                    for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                        token_ssp2[loop1] = cursor.getString((loop1 + 1));

                    }//********************************************************************

                    cursor.moveToNext();

                }//while


                cursor.close();


            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }//******


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return token_ssp2;

    }//load




}//class
