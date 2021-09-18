package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.spongycastle.util.encoders.Base64;

import java.security.MessageDigest;


public class mining_new_task_c_package extends SQLiteOpenHelper {


    String new_block_id = "";
    String new_block_hash = "";

    String move_item[][];



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

    public mining_new_task_c_package(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//********************************



    String[][] new_task_c_package(){//**************************************************************************

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int ix0 = 0;


            try {

                System.out.println("Mining new task c package...");

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



            //Here we delete the old mining blocks to save space
            //To clear old mining blocks we first find the last needed mining block

            try {

                krypton_database_compress_db compressx = new krypton_database_compress_db();
                boolean test1 = compressx.compress_mining_db();
                //boolean test2 = compressx.compress_backup_db();

                System.out.println("test1: " + test1);
                //System.out.println("test2: " + test2);

            } catch (Exception e) {e.printStackTrace();}



            //Here we get the last item in the database so it can be mined and the chain can move along.

            try {


                System.out.println("MOVE THE CHAIN....");

                new_block_id = "";
                new_block_hash = "";

                //String block_idx = "";
                move_item = new String[network.listing_size][network.block_compress_size];



                System.out.println("Load ITEM TASK..." );

                String query = ("SELECT * FROM listings_db ORDER BY xd ASC LIMIT " + network.block_compress_size);
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();


                ix0 = 0;
                while (!cursor.isAfterLast()) {


                    new_block_id = cursor.getString(cursor.getColumnIndex("id"));
                    new_block_hash = cursor.getString(cursor.getColumnIndex("hash_id"));
                    move_item[0][ix0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));

                    System.out.println("new_block_id " + new_block_id);

                    //block_idx = move_item[0];

                    for (int loop = 1; loop < network.listing_size; loop++){

                        try {

                            //System.out.println("GET ITEM P " + move_item[loop]);
                            move_item[loop][ix0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop + 1)));

                        } catch (Exception e) {e.printStackTrace();}

                    }//*****************************************************



                    String build_hash = "";
                    build_hash = build_hash + move_item[0];
                    for (int loop = 3; loop < move_item.length; loop++) {

                        build_hash = build_hash + move_item[loop];//save everything else

                    }//*************************************************

                    try {

                        byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(build_hash.getBytes());
                        new_block_hash = Base64.toBase64String(sha256_1w);

                    } catch (Exception e) {e.printStackTrace();}


                    ix0++;

                    cursor.moveToNext();


                }//while

                cursor.close();

                new_block_id = move_item[0][0];


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





    //Different compression system.

    String[][] new_task_c_package2(){//**************************************************************************

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int ix0 = 0;


            try {


                System.out.println("Mining new task c package...");

                String query = ("SELECT mining_new_block,hash_id FROM mining_db ORDER BY xd DESC LIMIT 1");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    mining.mx_last_block_mining_idx = cursor.getString(cursor.getColumnIndex("mining_new_block"));
                    mining.mx_last_block_idx = cursor.getString(cursor.getColumnIndex("hash_id"));

                    cursor.moveToNext();

                }//***************************

                cursor.close();

                System.out.println("mx_last_block_idx        " + mining.mx_last_block_idx);
                System.out.println("mx_last_block_mining_idx " + mining.mx_last_block_mining_idx);


            } catch (Exception e) {e.printStackTrace();}



            //Here we delete the old mining blocks to save space
            //To clear old mining blocks we first find the last needed mining block

            try{

                krypton_database_compress_db compressx = new krypton_database_compress_db();
                boolean test1 = compressx.compress_mining_db();
                //boolean test2 = compressx.compress_backup_db();

                System.out.println("test1: " + test1);
                //System.out.println("test2: " + test2);

            } catch (Exception e) {e.printStackTrace();}



            //Here we get the last item in the database so it can be mined and the chain can move along.

            try {

                System.out.println("MOVE THE CHAIN....");

                new_block_id = "";
                new_block_hash = "";

                //String block_idx = "";
                move_item = new String[network.listing_size][network.block_compress_size];



                //Here we get the oldest tokens in the chain and then use those to mine with.
                //We get the oldest tokens by searching for unique link ids limited to our total system token limit.
                //This will exclude all duplicates in a row and leave us with just a list of the real blockchain.
                //This is how we get the oldest links in the chain even though the chain is longer then what we need.

                System.out.println("Load ITEM TASK1...");

                String query1 = ("SELECT link_id FROM mining_db GROUP BY link_id ORDER BY xd DESC LIMIT " + network.hard_token_limit);
                Cursor cursor1 = db.rawQuery(query1, null);

                cursor1.moveToLast();

                String id_list = "";
                for (int loop = 0; loop < network.block_compress_size; loop++) {//***********

                    id_list = id_list + "id=" + cursor1.getString(cursor1.getColumnIndex("link_id")) + "";
                    if(loop < network.block_compress_size -1){id_list = id_list + " OR ";}

                    cursor1.moveToPrevious();

                }//**************************************************************************

                cursor1.close();

                System.out.println("id_list v1 " + id_list);



                //After we get the list of oldest tokens above we load the data here.

                System.out.println("Load ITEM TASK2..." );

                String query2 = ("SELECT * FROM listings_db WHERE " + id_list);
                Cursor cursor2 = db.rawQuery(query2, null);

                cursor2.moveToFirst();

                ix0 = 0;
                while (!cursor2.isAfterLast()) {


                    new_block_id = cursor2.getString(cursor2.getColumnIndex("id"));
                    new_block_hash = cursor2.getString(cursor2.getColumnIndex("hash_id"));
                    move_item[0][ix0] = cursor2.getString(cursor2.getColumnIndex(cursor2.getColumnName(1)));

                    System.out.println("new_block_id " + new_block_id);

                    //block_idx = move_item[0];

                    for (int loop = 1; loop < network.listing_size; loop++) {

                        try {

                            //System.out.println("GET ITEM P " + move_item[loop]);
                            move_item[loop][ix0] = cursor2.getString(cursor2.getColumnIndex(cursor2.getColumnName(loop + 1)));

                        } catch (Exception e) {e.printStackTrace();}

                    }//******************************************************



                    String build_hash = "";
                    build_hash = build_hash + move_item[0];
                    for (int loop = 3; loop < move_item.length; loop++){

                        build_hash = build_hash + move_item[loop];//save everything else

                    }//*************************************************

                    try {

                        byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(build_hash.getBytes());
                        new_block_hash = Base64.toBase64String(sha256_1w);

                    } catch (Exception e) {e.printStackTrace();}


                    ix0++;

                    cursor2.moveToNext();


                }//while

                cursor2.close();

                new_block_id = move_item[0][0];


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





}//load
