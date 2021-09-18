package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.spongycastle.util.encoders.Base64;
import java.security.MessageDigest;


public class krypton_database_test_chain extends SQLiteOpenHelper {

    JSONParser parserx = new JSONParser();



    @Override
    public void onCreate(SQLiteDatabase db) {
        //This will ensure that all tables are created.


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This will upgrade tables, adding columns and new tables.
        //Note that existing columns will not be converted.


    }


    //Here we are making sure our blockchain is in order. There could have been something inserted incorrectly or someone could have gone wrong.
    //If there is an error we delete all blocks after the error. And then those blocks would be redownloaded.

    krypton_database_test_chain() {//***************************************************************


        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);


    }//*********************************************************************************************


    boolean test1(){

        boolean test0 = false;
        boolean test1 = false;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            String idx = "";


            try {


                System.out.println("[>>>] Test DB chain 1");

                String query = ("SELECT xd FROM mining_db ORDER BY xd DESC LIMIT " + (network.package_block_size + 11));
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToLast();

                idx = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));

                System.out.println("find idx: " + idx);

                cursor.close();


            } catch (Exception e) {e.printStackTrace();}



            String last_package_x = "";

            try {

                String query = ("SELECT * FROM mining_db WHERE xd > " + idx + " ORDER BY xd ASC");
                Cursor cursor = db.rawQuery(query, null);

                System.out.println("TEST " + cursor.getCount());

                //loopx = cursor.getCount();

                cursor.moveToFirst();

                String hashx = "";

                System.out.println("rsxs " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5))));
                System.out.println("rsxs " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6))));

                String new_hash = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6)));
                String prev_hash = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5)));

                //We have to have an old hash to compare to. So we have the last one here.
                last_package_x = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(10)));

                cursor.moveToNext();


                while(!cursor.isAfterLast()){


                    String hash_id = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(8)));
                    String packagex = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(10)));

                    //Decode packages.
                    boolean is_in_package = false;
                    boolean last_is_package = false;
                    boolean this_is_package = false;
                    JSONArray jsonObjectx_last = null;
                    JSONArray jsonObjectx_this = null;
                    int last_package_sizex = 0;
                    int this_package_sizex = 0;

                    if (packagex.length() == 0) {this_is_package = false;}
                    else {

                        this_is_package = true;

                        try {

                            //JSONParser parserx = new JSONParser();

                            Object objx = null;

                            //This sometimes throws an error if we get a response that is corrupted.
                            //This will shutdown the app.
                            //java.lang.Error: Error: could not match input
                            try {

                                //Object objx = parserx.parse(packagex);
                                objx = parserx.parse(packagex);

                            } catch (Error e) {System.out.println("Response is unreadable..");}

                            jsonObjectx_this = (JSONArray) objx;

                            this_package_sizex = jsonObjectx_this.size();

                        } catch (Exception e) {this_is_package = false;}

                    }//***

                    if (last_package_x.length() == 0) {last_is_package = false;}
                    else {

                        last_is_package = true;

                        try {

                            //JSONParser parserx = new JSONParser();

                            Object objx = null;

                            //This sometimes throws an error if we get a response that is corrupted.
                            //This will shutdown the app.
                            //java.lang.Error: Error: could not match input
                            try {

                                //Object objx = parserx.parse(last_package_x);
                                objx = parserx.parse(last_package_x);

                            } catch (Error e) {System.out.println("Response is unreadable..");}

                            jsonObjectx_last = (JSONArray) objx;

                            last_package_sizex = jsonObjectx_last.size();

                        } catch (Exception e) {last_is_package = false;}


                    }//***





                    //Test package creating stage 2.
                    //Here we test the package to make sure it conforms to the system.
                    //We need to know if a block is in the center of a package in which case it doesn't need a hard difficulty.
                    //Or if it is the first block in the package, then it needs to meet the hard difficulty.
                    //We use this information in the next stage to choose what difficulty to test for.

                    try {

                        //System.out.println("last package    " + last_package_x);
                        //System.out.println("this package    " + packagex);

                        //System.out.println("Last Package    " + last_package_sizex);
                        //System.out.println("This Package    " + this_package_sizex);

                        //System.out.println("Last is Package " + last_is_package);
                        //System.out.println("This is Package " + this_is_package);

                        //test size
                        if (this_package_sizex == 0 && last_package_sizex == 0) {test0 = true;}//Normal operation.
                        else if (this_package_sizex == 0 && last_package_sizex == 1) {test0 = true;}//Package just ended.
                        else if (this_package_sizex == network.block_compress_size && last_package_sizex < 2) {//1 would be the end of the last package and 0 would be no package.

                            //System.out.println("First package block");

                            if (this_is_package) {

                                //System.out.println("First package block 2");
                                //System.out.println("id1 " + jsonObjectx_this.get(0));
                                //System.out.println("id2 " + hash_id);

                                if (jsonObjectx_this.get(0).equals(hash_id)) {test0 = true;}
                                else {test0 = false;}

                            }//*****************
                            else {test0 = false;}

                        }//***********************************************************************************
                        else if ((this_package_sizex + 1) == last_package_sizex && this_package_sizex < network.block_compress_size) {

                            //System.out.println("Middle package block");

                            if (this_is_package && last_is_package) {

                                is_in_package = true;

                                //System.out.println("Middle package block 2");
                                //System.out.println("id0 " + jsonObjectx_last.get(1));
                                //System.out.println("id1 " + jsonObjectx_this.get(0));
                                //System.out.println("id2 " + hash_id);

                                if (jsonObjectx_this.get(0).equals(hash_id) && jsonObjectx_last.get(1).equals(hash_id)) {test0 = true;}
                                else {test0 = false;}

                            }//*************************************
                            else {test0 = false;}

                        }//**********************************************************************************************************
                        else {test0 = false;}

                    } catch (Exception e) {test0 = false;}

                    //System.out.println("testm0 " + test0);

                    //After we are done using the new one we save it as the old one for next time.
                    last_package_x = packagex;


                    //Build hash list
                    hashx = hashx + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(8)));

                    //System.out.println("rsx ");
                    //System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))));
                    //System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
                    //System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5))));
                    //System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6))));
                    //System.out.println("rsx ");

                    prev_hash = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5)));

                    if (prev_hash.equals(new_hash)) {test1 = true;}
                    else {System.out.println("break error..."); test1 = false; break;}

                    new_hash = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6)));


                    cursor.moveToNext();


                }//while


                byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(hashx.getBytes());

                System.out.println("TEST HASH " + Base64.toBase64String(sha256_1w));

                cursor.close();

                System.out.println("blockchain test1: " + test1);

                if (test0 && test1) {mining.mining4 = true;}
                else {

                    System.out.println("Blockchain test fail!");

                    network.test_chain_errors++;

                    mining.mining4 = false;

                    krypton_update_block_stale testx = new krypton_update_block_stale();
                    boolean testx1 = testx.update();

                }//**


            } catch (Exception e) {e.printStackTrace();}



            //Here we get a list of hashes to make sure the database isn't missing anything.

            int loopx = 0;
            int loopp = 0;
            String cmdx = "";

            try {


                String query = ("SELECT hash_id FROM mining_db WHERE xd > " + idx + " GROUP BY hash_id ORDER BY xd ASC");
                Cursor cursor = db.rawQuery(query, null);

                System.out.println("TEST " + cursor.getCount());

                loopx = cursor.getCount();

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    //Save all the hashes to test later.
                    cmdx = cmdx + "hash_id='" + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))) + "'";
                    if (loopp < (loopx -1)) {cmdx = cmdx + " OR ";}
                    loopp++;

                    //System.out.println("hash " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))));

                    cursor.moveToNext();

                }//****************************

                cursor.close();


            } catch (Exception e) {e.printStackTrace();}




            //After we get the list above we test it here to make sure the backup hashes are ready in case there is a problem.
            //There should never be a situation where an item doesn't have a backup, if there is then there is a problem.

            try {


                //System.out.println(cmdx);

                String query = ("SELECT hash_id FROM backup_db WHERE " + cmdx + "");
                Cursor cursor = db.rawQuery(query, null);

                int listxp = cursor.getCount();

                System.out.println("listxp " + listxp);

                cursor.moveToFirst();

                //while(!cursor.isAfterLast()){

                    //System.out.println("hash2 " + cursor.getString(cursor.getColumnIndex("hash_id")));
                    //cursor.moveToNext();

                //}//**************************

                cursor.close();

                if (listxp == loopx) {System.out.println("Has backup.");}
                else {

                    System.out.println("Does not have backup!");

                    test1 = false;

                    network.test_chain_errors++;

                    mining.mining4 = false;

                    krypton_update_block_stale testx = new krypton_update_block_stale();
                    boolean testx1 = testx.update();

                }//**


            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return test1;
        
    }



    public boolean test2(){

        boolean test2 = false;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("[>>>] Test DB chain 1");

                String query = ("SELECT * FROM mining_db ORDER BY xd DESC LIMIT 1");
                Cursor cursor = db.rawQuery(query, null);

                System.out.println("TEST " + cursor.getCount());

                cursor.moveToFirst();

                String packagex = cursor.getString(cursor.getColumnIndex("package"));

                cursor.close();

                System.out.println("Chain test 2 packagex " + packagex);

                int sizep = 0;

                //If there is no text in this field then there is no package that is OK.
                if (packagex.length() > 0) {

                    //If there is info but it is in the wrong format then someone is trying to hack the package building system.
                    try {

                        //JSONParser parser = new JSONParser();
                        Object objx = parserx.parse(packagex);
                        JSONArray jsonObjectx = (JSONArray) objx;

                        sizep = jsonObjectx.size();

                    } catch (Exception e) {e.printStackTrace(); sizep = 11;}//Someone is trying to insert fake package information into this field.

                }//*************************
                else {sizep = 0;}

                System.out.println("sizep: " + sizep);

                if (sizep == 1 || sizep == 0) {test2 = true;}//OK!
                else {

                    System.out.println("Installed package failure!");

                    network.test_chain_package++;

                    mining.mining4 = false;

                    krypton_update_block_stale testx = new krypton_update_block_stale();
                    boolean testx1 = testx.update();

                    test2 = false;

                }//**


            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return test2;

    }//*******************





}//class
