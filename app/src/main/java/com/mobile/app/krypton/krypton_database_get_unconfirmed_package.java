package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class krypton_database_get_unconfirmed_package extends SQLiteOpenHelper {



    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Here we are loading a list of 10 unconfirmed items from the mining class to start building a package block with.

    public krypton_database_get_unconfirmed_package(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }



    public String[][] getTokens(int limit){//**************************************************************************

        String[][] token_ssp2 = null;

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int ix0 = 0;


            try {


                System.out.println("[>>>] Load unconfirmed_db package" );

                String query = ("SELECT * FROM unconfirmed_db ORDER BY xd ASC LIMIT " + limit);
                Cursor cursor = db.rawQuery(query, null);

                token_ssp2 = new String[network.listing_size][cursor.getCount()];

                cursor.moveToFirst();

                ix0 = 0;
                while (!cursor.isAfterLast()) {

                    for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                        token_ssp2[loop1][ix0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));

                    }//********************************************************************

                    ix0++;

                    cursor.moveToNext();

                }//while

                cursor.close();


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





    public String[][] getTokensNewest(int limit){//**************************************************************************

        String[][] token_ssp2 = null;

        try {

            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int ix0 = 0;


            try{


                System.out.println("[>>>] Load unconfirmed_db package newest" );

                String query = ("SELECT * FROM unconfirmed_db ORDER BY xd DESC LIMIT " + limit);
                Cursor cursor = db.rawQuery(query, null);

                token_ssp2 = new String[network.listing_size][cursor.getCount()];

                cursor.moveToFirst();

                ix0 = 0;
                while (!cursor.isAfterLast()) {

                    for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                        token_ssp2[loop1][ix0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));

                    }//********************************************************************

                    ix0++;

                    cursor.moveToNext();

                }//while

                cursor.close();


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





    //here we are getting a list of 10 unconfirmed items to build a mining package with from the mining class.

    public String[][] getMiningPackage(int idx){//**************************************************************************

        String[][] token_ssp2 = null;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            int ix0 = 0;


            try {


                System.out.println("Load unconfirmed_db..." );

                String query = ("SELECT mining_new_block,hash_id FROM mining_db ORDER BY xd DESC LIMIT 1");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    mining.mx_last_block_mining_idx = cursor.getString(cursor.getColumnIndex("mining_new_block"));
                    mining.mx_last_block_idx = cursor.getString(cursor.getColumnIndex("hash_id"));

                    cursor.moveToNext();

                }//****************************

                cursor.close();

                System.out.println("mx_last_block_idx        " + mining.mx_last_block_idx);
                System.out.println("mx_last_block_mining_idx " + mining.mx_last_block_mining_idx);

            } catch(Exception e) {e.printStackTrace();}


            try {


                String query = ("SELECT * FROM unconfirmed_db ORDER BY xd ASC LIMIT " + idx);
                Cursor cursor = db.rawQuery(query, null);

                token_ssp2 = new String[network.listing_size][cursor.getCount()];

                cursor.moveToFirst();

                ix0 = 0;
                while (!cursor.isAfterLast()) {


                    for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                        token_ssp2[loop1][ix0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));

                    }//********************************************************************

                    ix0++;

                    cursor.moveToNext();

                }//while

                cursor.close();


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
