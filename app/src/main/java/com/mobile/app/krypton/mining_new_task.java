package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import org.spongycastle.util.encoders.Base64;




public class mining_new_task extends SQLiteOpenHelper {


    String new_block_id = "";
    String new_block_hash = "";

    String move_item[] = new String[network.listing_size];


    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Here we are getting a new block from a user that has updated their infor or sent their token to someone else.

    public mining_new_task(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }



    String[] new_task(){//**************************************************************************

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("Mining new task...");

                //make sure old mining hash is OK
                System.out.println("Loading OLD mining hash ");

                String query = ("SELECT mining_new_block,hash_id FROM mining_db ORDER BY xd DESC LIMIT 1");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {//krypton_database_driver.rs.next()

                    mining.mx_last_block_mining_idx = cursor.getString(cursor.getColumnIndex("mining_new_block"));
                    mining.mx_last_block_idx = cursor.getString(cursor.getColumnIndex("hash_id"));

                    cursor.moveToNext();

                }//**************

                cursor.close();

                System.out.println("mx_last_block_idx        " + mining.mx_last_block_idx);
                System.out.println("mx_last_block_mining_idx " + mining.mx_last_block_mining_idx);


            } catch (Exception e) {e.printStackTrace();}




            try {


                new_block_id = "";
                new_block_hash = "";


                System.out.println("Load unconfirmed db..." );

                String query = ("SELECT id,hash_id FROM unconfirmed_db ORDER BY xd ASC LIMIT 1");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {//krypton_database_driver.rs.next()

                    System.out.println("rs net " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))) );
                    new_block_id = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));
                    new_block_hash = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));

                    cursor.moveToNext();

                }//while

                cursor.close();


            } catch (Exception e) {e.printStackTrace();}




            try {


                if (new_block_id.length() != 0) {


                    move_item = new String[network.listing_size];


                    System.out.println("MINING NEW BLOCK Load UNCONFIRMED ITEM..." );

                    String query = ("SELECT * FROM unconfirmed_db WHERE id=" + new_block_id);
                    Cursor cursor = db.rawQuery(query, null);

                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {//krypton_database_driver.rs.next()

                        move_item[0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));

                        for (int loop = 1; loop < network.listing_size; loop++){

                            try {

                                move_item[loop] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop + 1)));

                            } catch (Exception e) {e.printStackTrace();}//*****************

                        }//*****************************************************

                        cursor.moveToNext();

                    }//while

                    cursor.close();

                    String build_hash = "";
                    build_hash = build_hash + move_item[0];
                    for (int loop = 3; loop < move_item.length; loop++){

                        build_hash = build_hash + move_item[loop];//save everything else

                    }//*************************************************

                    try {

                        byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(build_hash.getBytes());
                        new_block_hash = Base64.toBase64String(sha256_1w);

                    } catch (Exception e) {e.printStackTrace();}


                }//if*********************************


            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();

                //krypton_database_driver.conn.commit();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return move_item;

    }//load





    String[] new_task(String id){//**************************************************************************

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {

                System.out.println("Mining new task id...");

                //make sure old mining hash is OK
                System.out.println("Loading OLD mining hash ");

                String query = ("SELECT mining_new_block,hash_id FROM mining_db ORDER BY xd DESC LIMIT 1");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();


                while (!cursor.isAfterLast()) {

                    mining.mx_last_block_mining_idx = cursor.getString(cursor.getColumnIndex("mining_new_block"));
                    mining.mx_last_block_idx = cursor.getString(cursor.getColumnIndex("hash_id"));

                    cursor.moveToNext();

                }//**************

                cursor.close();

                System.out.println("mx_last_block_idx        " + mining.mx_last_block_idx);
                System.out.println("mx_last_block_mining_idx " + mining.mx_last_block_mining_idx);

            } catch (Exception e) {e.printStackTrace();}



            try {

                new_block_id = "";
                new_block_hash = "";


                System.out.println("Load unconfirmed db...");

                String query = ("SELECT id,hash_id FROM unconfirmed_db WHERE id=" + id + " LIMIT 1");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    //System.out.println(rs.getString(1));

                    System.out.println("rs net " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))));
                    new_block_id = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));
                    new_block_hash = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));

                    cursor.moveToNext();

                }//while

                cursor.close();


            } catch (Exception e) {e.printStackTrace();}



            try {


                if (new_block_id.length() != 0) {


                    move_item = new String[network.listing_size];


                    System.out.println("MINING NEW BLOCK Load UNCONFIRMED ITEM...");

                    String query = ("SELECT * FROM unconfirmed_db WHERE id=" + new_block_id);
                    Cursor cursor = db.rawQuery(query, null);

                    cursor.moveToFirst();

                    while (!cursor.isAfterLast()) {


                        move_item[0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));

                        for (int loop = 1; loop < network.listing_size; loop++){

                            try {

                                move_item[loop] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop + 1)));

                            } catch(Exception e) {e.printStackTrace();}//*****************

                        }//*****************************************************

                        cursor.moveToNext();

                    }//while

                    cursor.close();

                    String build_hash = "";
                    build_hash = build_hash + move_item[0];
                    for (int loop = 3; loop < move_item.length; loop++){

                        build_hash = build_hash + move_item[loop];//save everything else

                    }//*************************************************

                    try {

                        byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(build_hash.getBytes());
                        new_block_hash = Base64.toBase64String(sha256_1w);

                    } catch (Exception e) {e.printStackTrace();}


                }//if*********************************


            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();

                //krypton_database_driver.conn.commit();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return move_item;

    }//load





 
}//class
