package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;





public class krypton_database_get_buffer extends SQLiteOpenHelper {



    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Here we get the listings in our local buffer. These are listings that we own and want to update.
    //First they are sent to our buffer then we pass them to the network.

    krypton_database_get_buffer() {//**************************************************************************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }


    //Here we are getting a package of buffered items to send to the network.

    public String[][] getTokens(){//**************************************************************************

        String[][] token_ssp2 = null;

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int ix0 = 0;


            try {


                boolean found_item = false;

                //String idx = "";

                System.out.println("Load unconfirmed_db..." );

                String query = ("SELECT * FROM send_buffer ORDER BY xd ASC LIMIT ?");
                Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(network.block_compress_size)});

                token_ssp2 = new String[network.listing_size][cursor.getCount()];

                cursor.moveToFirst();

                ix0 = 0;
                while (!cursor.isAfterLast()) {


                    for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                        token_ssp2[loop1][ix0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));
                        System.out.println("BULD1 " + token_ssp2[loop1][ix0]);

                    }//********************************************************************

                    found_item = true;

                    ix0++;

                    cursor.moveToNext();

                }//while

                cursor.close();

                //System.out.println("idx " + idx);


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




    //Here we are only getting one buffered item by it's ID.

    public String[] getTokenID(String id){//**************************************************************************

        String[] token_ssp2 = null;

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int ix0 = 0;


            try {


                boolean found_item = false;

                //String idx = "";

                System.out.println("Load unconfirmed_db..." );

                String query = ("SELECT * FROM send_buffer WHERE id=? ORDER BY xd ASC LIMIT 1");
                Cursor cursor = db.rawQuery(query, new String[]{id});

                token_ssp2 = new String[network.listing_size];

                cursor.moveToFirst();

                ix0 = 0;
                while (!cursor.isAfterLast()) {


                    for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                        token_ssp2[loop1] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));
                        System.out.println("BULD1 " + token_ssp2[loop1]);

                    }//********************************************************************

                    found_item = true;

                    ix0++;

                    cursor.moveToNext();

                }//while

                cursor.close();

                //System.out.println("idx   " + idx);
                System.out.println("found " + found_item);


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
