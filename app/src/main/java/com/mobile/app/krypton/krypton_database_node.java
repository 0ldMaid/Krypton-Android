package com.mobile.app.krypton;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class krypton_database_node extends SQLiteOpenHelper {



    @Override
    public void onCreate(SQLiteDatabase db) {
        //This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This will upgrade tables, adding columns and new tables.
        //Note that existing columns will not be converted


    }


    //Here we add a new .onion address node to our network.

    krypton_database_node() {//*************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//**************************************************






    public boolean addNode(String nodex){

        boolean added = false;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("Add node ID...");

                String query = ("DELETE FROM network WHERE address='" + nodex +"'");
                db.execSQL(query);

                ContentValues values = new ContentValues();

                values.put("address",  nodex);//

                //Inserting Row.
                db.insert("network", null, values);

                System.out.println("Committed the transaction");

                added = true;


            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return added;

    }






    public boolean deleteNode(String id){//**************************************************************************

        boolean deleted = false;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int ix0 = 0;


            try {


                System.out.println("Delete Node..." );

                try {

                    String query2 = ("DELETE FROM network WHERE address='" + id + "'");

                    db.execSQL(query2);
                    deleted = true;

                    System.out.println("deleted " + deleted);

                } catch (Exception e) {e.printStackTrace();}



                System.out.println("Reload Network..." );

                network.network_list = new ArrayList<String>();

                System.out.println("Load Network GX..." );

                String query = ("SELECT * FROM network ORDER BY address");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                ix0 = 0;
                while (!cursor.isAfterLast()) {

                    System.out.println("rs net " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))) );
                    network.network_list.add( cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))) );

                    ix0++;

                    cursor.moveToNext();

                }//while

                cursor.close();

                network.network_size = ix0;

                System.out.println("network size " + network.network_size);


            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return deleted;

    }//load







    public void refresh(){

        try {


            //We can't call this here because it's already on when this is called.
            //network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int ix0;


            try {


                System.out.println("Node refresh active list...");

                network.network_list = new ArrayList<String>();

                System.out.println("Load Network GX..." );

                String query = ("SELECT * FROM network");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                ix0 = 0;
                while (!cursor.isAfterLast()) {

                    System.out.println("Nodes: " +  cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))) );
                    network.network_list.add( cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))) );

                    ix0++;

                    cursor.moveToNext();

                }//while

                network.network_size = ix0;

                System.out.println("Network Size " + network.network_size);

                cursor.close();


            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            //We can't call this here because it's already on when this is called. If we close it the other class won't be finished yet.
            //network.database_in_use = false;

        }


    }//refresh







    public void refreshBlockchain(){

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int ix0;


            try {


                System.out.println("Node refresh active list from Blockchain...");

                krypton_database_node node = new krypton_database_node();

                network.network_list = new ArrayList<String>();

                System.out.println("Load Network GX..." );

                String query = ("SELECT seller_ip FROM listings_db WHERE seller_ip LIKE '%.onion%' ORDER BY xd DESC");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                ix0 = 0;
                while (!cursor.isAfterLast()) {

                    System.out.println("Nodes: " +  cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))) );

                    //network.network_list.add( cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))) );

                    //node.addNode( cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))) );

                    ix0++;

                    cursor.moveToNext();

                }//while

                network.network_size = ix0;

                System.out.println("network size " + network.network_size);

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

    }//refresh




}//class
