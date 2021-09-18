package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class krypton_database_print_blocks extends SQLiteOpenHelper {


    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }




    krypton_database_print_blocks() {//*************************************************************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//*********************************************************************************************



    //This will print the last blocks in the blockchain to show in the System out window. The user can request this but they can't see the output unless they have a CMD window.

    String[][] get_blocks(){//**************************************************************************

        String[][] blockchain = new String[9][network.print_blocks_size];

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int ix0 = 0;
            //int ix1 = 0;
            //int ix2 = 0;


            try {


                System.out.println("Loading...");

                String query = ("SELECT * FROM mining_db ORDER BY xd DESC LIMIT " + network.print_blocks_size);//we don't want limit items because they are package blocks
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                while(!cursor.isAfterLast()){

                    try{


                        blockchain[0][ix0] = cursor.getString(cursor.getColumnIndex("xd"));
                        blockchain[1][ix0] = cursor.getString(cursor.getColumnIndex("link_id"));
                        blockchain[2][ix0] = cursor.getString(cursor.getColumnIndex("mining_date"));
                        blockchain[3][ix0] = cursor.getString(cursor.getColumnIndex("mining_noose"));
                        blockchain[4][ix0] = cursor.getString(cursor.getColumnIndex("mining_new_block"));
                        blockchain[5][ix0] = cursor.getString(cursor.getColumnIndex("mining_old_block"));
                        blockchain[6][ix0] = cursor.getString(cursor.getColumnIndex("previous_hash_id"));
                        blockchain[7][ix0] = cursor.getString(cursor.getColumnIndex("hash_id"));
                        blockchain[8][ix0] = cursor.getString(cursor.getColumnIndex("package"));


                    } catch(Exception e) {e.printStackTrace();}

                    ix0++;

                    cursor.moveToNext();

                }//**************

                cursor.close();



            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return blockchain;

    }//load



}//class
