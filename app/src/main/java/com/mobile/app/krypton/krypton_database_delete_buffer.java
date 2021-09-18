package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class krypton_database_delete_buffer extends SQLiteOpenHelper {


    String[] token_ssp2 = new String[network.listing_size];



    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //If our listing we updated is accepted to the network as an unconfirmed block then we delete it from our buffer.
    //Only items we update will be in our buffer. But other peoples listings will be in the unconfirmed buffer.

    public krypton_database_delete_buffer(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }



    public boolean deleteAll(){//**************************************************************************

        try{


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            System.out.println("[>>>] Delete Buffer All");


            try {

                db.execSQL("DELETE FROM send_buffer");

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

            System.out.println("[>>>] Delete Buffer ID");


            try {


                System.out.println("Loading 1");

                db.execSQL("DELETE FROM send_buffer where id=" + id);

                System.out.println("Loading 2");

                String query = ("SELECT xd FROM send_buffer");
                Cursor cursor = db.rawQuery(query, null);

                int rowCountbu = cursor.getCount();
                network.send_buffer_size = rowCountbu;

                System.out.println("send_buffer_size " + network.send_buffer_size);

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



    public boolean deleteFirst(){//**************************************************************************

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                String queryd = ("SELECT xd FROM send_buffer ORDER BY xd ASC LIMIT 1");
                Cursor cursord = db.rawQuery(queryd, null);

                cursord.moveToFirst();

                String id = cursord.getString(cursord.getColumnIndex(cursord.getColumnName(0)));

                System.out.println("id " + id);
                System.out.println("Loading 1");

                db.execSQL("DELETE FROM send_buffer where xd=" + id);

                System.out.println("Loading 2");

                String query = ("SELECT xd FROM send_buffer");
                Cursor cursor = db.rawQuery(query, null);

                int rowCountbu = cursor.getCount();
                network.send_buffer_size = rowCountbu;

                System.out.println("send_buffer_size " + network.send_buffer_size);

                cursor.close();

                System.out.println("Committed the transaction");


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
