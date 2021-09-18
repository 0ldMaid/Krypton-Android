package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import org.spongycastle.util.encoders.Base64;


public class mining_new_task_c extends SQLiteOpenHelper {


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


    //Here we don't have a new token but we are mining the oldest block in our system again to renew it.
    //This was the chain can move along like a worm and old blocks can be discarded. This limits the size of the blockchain so it can run on mobile devices.
    //It is possible to save old info like bitcoin from the Genesis block but it's not necessary.

    public mining_new_task_c(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//************************



    String[] new_task_c(){//**************************************************************************

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            //int numberx = 0;
            //String hash_idx = "";


            try {

                System.out.println("Mining new task c...");

                String query = ("SELECT mining_new_block,hash_id FROM mining_db ORDER BY xd DESC LIMIT 1");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                while(!cursor.isAfterLast()){

                    mining.mx_last_block_mining_idx = cursor.getString(cursor.getColumnIndex("mining_new_block"));
                    mining.mx_last_block_idx = cursor.getString(cursor.getColumnIndex("hash_id"));

                    cursor.moveToNext();

                }//**************

                cursor.close();

                System.out.println("mining.mx_last_block_idx        " + mining.mx_last_block_idx);
                System.out.println("mining.mx_last_block_mining_idx " + mining.mx_last_block_mining_idx);

            } catch(Exception e) {e.printStackTrace();}






            //Here we delete the old mining blocks to save space.
            //To clear old mining blocks we first find the last needed mining block.

            try {

                krypton_database_compress_db compressx = new krypton_database_compress_db();
                boolean test1 = compressx.compress_mining_db();
                //boolean test2 = compressx.compress_backup_db();

                System.out.println("test1: " + test1);
                //System.out.println("test2: " + test2);

            } catch(Exception e) {e.printStackTrace();}





            //Here we get the last item in the database so it can be mined and the chain can move along.

            try {

                System.out.println("MOVE THE CHAIN....");

                new_block_id = "";
                new_block_hash = "";

                String block_idx = "";
                move_item = new String[network.listing_size];



                System.out.println("Load ITEM TASK..." );

                //String query = ("SELECT * FROM listings_db WHERE id=103160 ORDER BY xd ASC LIMIT 1");//
                String query = ("SELECT * FROM listings_db ORDER BY xd ASC LIMIT 1");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();


                //ix0 = 0;
                while (!cursor.isAfterLast()) {


                    new_block_id = cursor.getString(cursor.getColumnIndex("id"));
                    new_block_hash = cursor.getString(cursor.getColumnIndex("hash_id"));
                    move_item[0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));

                    block_idx = move_item[0];

                    for (int loop = 1; loop < network.listing_size; loop++){

                        try{

                            //System.out.println("GET ITEM P " + move_item[loop]);
                            move_item[loop] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop + 1)));

                        }catch(Exception e){e.printStackTrace();}

                    }//*****************************************************



                    String build_hash = "";
                    build_hash = build_hash + move_item[0];
                    for (int loop = 3; loop < move_item.length; loop++){

                        build_hash = build_hash + move_item[loop];//save everything else

                    }//*************************************************

                    try {

                        byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(build_hash.getBytes());
                        new_block_hash = Base64.toBase64String(sha256_1w);

                    } catch (Exception e) {e.printStackTrace();}


                    cursor.moveToNext();

                }//while

                cursor.close();

                new_block_id = move_item[0];


            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return move_item;

    }//load






    String[] new_task_c(String id){//**************************************************************************

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {

                System.out.println("Mining new task c id...");

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

                String query = ("SELECT id,hash_id FROM listings_db WHERE id=" + id + " LIMIT 1");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();


                //ix1 = 0;
                //ix2 = 0;
                while (!cursor.isAfterLast()) {//krypton_database_driver.rs.next()

                    //System.out.println(rs.getString(1));

                    System.out.println("rs net " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))));
                    new_block_id = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));
                    new_block_hash = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));

                    //ix1++;

                    cursor.moveToNext();

                }//while

                cursor.close();

            } catch (Exception e) {e.printStackTrace();}



            try {


                if (new_block_id.length() != 0) {


                    System.out.println("MINING NEW BLOCK Load UNCONFIRMED ITEM...");

                    move_item = new String[network.listing_size];


                    String query = ("SELECT * FROM unconfirmed_db WHERE id=" + new_block_id);
                    Cursor cursor = db.rawQuery(query, null);

                    cursor.moveToFirst();


                    //ix0 = 0;
                    while (!cursor.isAfterLast()) {


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
                    for (int loop = 3; loop < move_item.length; loop++) {

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
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return move_item;

    }//load








}//load
