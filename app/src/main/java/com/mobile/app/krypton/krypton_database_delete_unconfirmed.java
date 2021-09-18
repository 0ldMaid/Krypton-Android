package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class krypton_database_delete_unconfirmed extends SQLiteOpenHelper {


    String[] token_ssp2 = new String[network.listing_size];


    int ix0 = 0;
    //int ix1 = 0;
    //int ix2 = 0;

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Here we delete an unconfirmed item that is now a block or has errors.

    public krypton_database_delete_unconfirmed(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }



    public boolean deleteAll(){//**************************************************************************

        try{


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {

                db.execSQL("DELETE FROM unconfirmed_db");

            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return true;

    }//load




    public boolean deleteID(String id){//**************************************************************************

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {

                System.out.println("[>>>] Delete Unconfirmed ID");

                System.out.println("Loading 1");

                db.execSQL("DELETE FROM unconfirmed_db where id=" + id);


                System.out.println("Loading 2");

                String query = ("SELECT xd,id,hash_id FROM unconfirmed_db ORDER BY xd DESC");
                Cursor cursor = db.rawQuery(query, null);

                int rowCount5u = cursor.getCount();
                network.database_unconfirmed_total = rowCount5u;

                System.out.println("<<>>");

                if(rowCount5u > 0){

                    cursor.moveToFirst();
                    network.last_unconfirmed_id = cursor.getString(cursor.getColumnIndex("id"));
                    network.last_unconfirmed_idx = cursor.getString(cursor.getColumnIndex("hash_id"));

                }//****************
                else{network.last_unconfirmed_idx = "";}

                System.out.println("unconfirmed TOTAL " + network.database_unconfirmed_total);
                System.out.println("last_unconfirmed_idx " + network.last_unconfirmed_idx);

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

        return true;

    }//load





    public boolean deleteFirst(){//**************************************************************************

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                String queryd = ("SELECT xd FROM unconfirmed_db ORDER BY xd ASC LIMIT 1");
                Cursor cursord = db.rawQuery(queryd, null);

                cursord.moveToFirst();

                String id = cursord.getString(cursord.getColumnIndex(cursord.getColumnName(0)));

                System.out.println("id " + id);


                System.out.println("Loading 1");

                db.execSQL("DELETE FROM unconfirmed_db where xd=" + id);


                System.out.println("Loading 2");

                String query = ("SELECT xd,id,hash_id FROM unconfirmed_db ORDER BY xd DESC");
                Cursor cursor = db.rawQuery(query, null);

                int rowCount5u = cursor.getCount();
                network.database_unconfirmed_total = rowCount5u;

                System.out.println("<<>>");

                if(rowCount5u > 0){

                    cursor.moveToFirst();
                    network.last_unconfirmed_id = cursor.getString(cursor.getColumnIndex("id"));
                    network.last_unconfirmed_idx = cursor.getString(cursor.getColumnIndex("hash_id"));

                }//****************
                else{network.last_unconfirmed_idx = "";}

                System.out.println("unconfirmed TOTAL " + network.database_unconfirmed_total);
                System.out.println("last_unconfirmed_idx " + network.last_unconfirmed_idx);

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

        return true;

    }//load





}//class
