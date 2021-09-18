package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class krypton_database_get_user_token_list extends SQLiteOpenHelper{



    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Here we are loading a token from the listings_db these are the "coins" of the system.

    public krypton_database_get_user_token_list(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//*********************************



    //These are tokens that a remote partial node is requesting.

    public String[][] getTokenList(String id){//**************************************************************************

        String[][] token_ssp2 = null;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int ix0 = 0;


            try{


                System.out.println("Load Tokens..." );

                String query = ("SELECT id,hash_id FROM listings_db WHERE seller_id=? ORDER BY id ASC LIMIT " + network.user_token_list_limit);
                Cursor cursor = db.rawQuery(query, new String[]{id});

                token_ssp2 = new String[2][cursor.getCount()];

                cursor.moveToFirst();

                ix0 = 0;
                while (!cursor.isAfterLast()) {

                    token_ssp2[0][ix0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));
                    token_ssp2[1][ix0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));

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






    //These are tokens that we have gotten from the remote full node and we are saving them in listings_xnode_db

    public String[][] getTokenXnodeList(){//**************************************************************************

        String[][] token_ssp2 = null;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int ix0 = 0;


            try {


                System.out.println("Load Tokens..." );

                String query = ("SELECT id,hash_id FROM listings_lite_db ORDER BY id ASC LIMIT " + network.user_token_list_limit);
                Cursor cursor = db.rawQuery(query, null);

                token_ssp2 = new String[2][cursor.getCount()];

                cursor.moveToFirst();

                ix0 = 0;
                while(!cursor.isAfterLast()){

                    token_ssp2[0][ix0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));
                    token_ssp2[1][ix0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));

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





    public void updateTokenList(){





    }//***************************




 
}//get
