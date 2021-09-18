package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigInteger;
import java.util.ArrayList;




public class krypton_database_load extends SQLiteOpenHelper {

    krypton_database_test_chain testc = new krypton_database_test_chain();

    Long longstamp_hold = (long) 0;

    boolean load_database_in_use = false;

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }

    //Main start.

    krypton_database_load() {//*********************************************************************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//*********************************************************************************************


    //This is the external access point other parts of the program will call through here.

    public void load() {//**************************************************************************

        //The load class is called from many threads so if it is already running we don't run it again.
        if(!load_database_in_use && !network.reset_db){load1();}
        else{System.out.println("DB load already running...");}

    }//*********************************************************************************************


    //This is the external access point other parts of the program will call through here.

    public void load_lite() {//**************************************************************************

        //The load class is called from many threads so if it is already running we don't run it again.
        if(!load_database_in_use && !network.reset_db){load2();}
        else{System.out.println("DB load already running...");}

    }//*********************************************************************************************






    //Here we load all current info from our database about the network. Like the number of our tokens and how many blocks are waiting to be added.
    //Then we test the last few blockchain blocks to make sure we don't have any errors.
    //This is called after the start test to make sure it's not running twice this should not be called by any other part of the program.
    //Eventually these methods should be combined together for easy update.

    private void load1() {//*************************************************************************


        try {


            SQLiteDatabase db = this.getWritableDatabase();

            //Show this is active so it doesn't start again while we are running.
            load_database_in_use = true;


            System.out.println("[>>>] Load 1");
            System.out.println("[error] Stale:   " + network.blockchain_errors);
            System.out.println("[error] N:       " + network.block_n_errors);
            System.out.println("[error] Fork:    " + network.fork_errors_one);
            System.out.println("[error] Chain:   " + network.test_chain_errors);
            System.out.println("[error] Package: " + network.test_chain_package);

            Long thisTick = System.currentTimeMillis();



            //We don't want any errors of deleting more blocks then we have just installed.
            //The system tests for 100 blocks the same number that wss just installed to make sure they are correct.
            //However this could theoretically delete the whole blockchian if there was a problem so we stop after 100.
            int rewind_loop = 0;

            boolean testx1 = false;

            System.out.println("Test blockchain integrity.");

            while(!testx1) {

                //Test blockchain integrity.
                //If we don't have all the blocks we don't need to test.
                //if(network.hard_token_limit != network.hard_token_count){break;}
                if(network.hard_token_count < (network.package_block_size + 11)){break;}

                //Test insert error.
                testx1 = testc.test1();

                rewind_loop++;
                if (rewind_loop >= (network.package_block_size + 11)) {System.out.println("Rewind error break!"); break;}

            }//*************

            System.out.println("t1 " + testx1);


            System.out.println("Test package integrity.");

            //Here we are testing package integrity that means we have the full package from start to finish.
            //Sometimes there could be corruption, or the server doesn't have the whole package. Maybe the server is slow.
            //We can test for this problem here. It should only be called if we think our blocks are up to date.
            //Not during a full download.

            rewind_loop = 0;
            boolean testx2 = false;

            while (!testx2) {

                //If we don't have all the blocks we don't need to test.
                //if(network.hard_token_limit != network.hard_token_count){break;}
                if (network.hard_token_count < (network.package_block_size + 11)) {break;}

                //Test package failure. If we are not installing one.
                if (!network.installing_package) {

                    testx2 = testc.test2();

                }//******************************
                else {System.out.println("Installing package break test 2"); break;}

                rewind_loop++;
                if (rewind_loop >= network.package_block_size) {System.out.println("Rewind error break!"); break;}

            }//************

            System.out.println("t2 " + testx2);




            //Each loading system has a differed set of data to load. Some are for speed some are intensive.

            try {

                loading0(db);
                loading1(db);
                loading2(db);
                loading3(db);
                loading4(db);
                loading5(db);

                System.out.println("DB LOADED...");

                //Testing the speed of loading.
                network.loaddbx_longstamp = System.currentTimeMillis() - thisTick;
                System.out.println("Load Time: " + (System.currentTimeMillis() - thisTick));

            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }//******


            //System is ready to use.
            network.database_loaded = true;

        } catch (Exception e) {e.printStackTrace();}
        finally {

            //Trouble with this being called on some of my slow phones.
            load_database_in_use = false;

        }//******


    }//load*****************************************************************************************




    //This is a lite version of the loading system that only has the basics.

    private void load2() {//*************************************************************************


        try {


            SQLiteDatabase db = this.getWritableDatabase();

            //Show this is active so it doesn't start again while we are running.
            load_database_in_use = true;

            System.out.println("[>>>] Load 2");
            System.out.println("[error] Stale:   " + network.blockchain_errors);
            System.out.println("[error] N:       " + network.block_n_errors);
            System.out.println("[error] Fork:    " + network.fork_errors_one);
            System.out.println("[error] Chain:   " + network.test_chain_errors);
            System.out.println("[error] Package: " + network.test_chain_package);

            Long thisTick = System.currentTimeMillis();


            try{

                loading0(db);
                loading1(db);
                loading4(db);
                loading5(db);

                System.out.println("DB LOADED...");

                //Testing the speed of loading.
                network.loaddbx_longstamp = System.currentTimeMillis() - thisTick;
                System.out.println("Load Time: " + (System.currentTimeMillis() - thisTick));

            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }//******


            //System is ready to use.
            network.database_loaded = true;

        } catch (Exception e) {e.printStackTrace();}
        finally {

            //Trouble with this being called on some of my slow phones.
            load_database_in_use = false;

        }//******


    }//load*****************************************************************************************







    private void loading0(SQLiteDatabase db){


        System.out.println("Loading 0");

        try {

            //Get the database size this should always be 25k maybe there's an error.

            String query = ("SELECT xd FROM listings_db GROUP BY hash_id");
            Cursor cursor = db.rawQuery(query, null);

            int rowCountbu = cursor.getCount();

            //We can't show this if the system is in lite SPV mode.
            network.hard_token_count = rowCountbu;

            cursor.close();

            System.out.println("network.hard_token_count " + network.hard_token_count);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//********************



    private void loading1(SQLiteDatabase db){


        System.out.println("Loading 1");

        try {

            //Get our tokens from the database and save them in an array. We need this info when the user is sending or updating their tokens.
            //We also get the number the user has to show in the app.
            //I would like to use full_node true here but it will not be ready when this is first called.
            if (!network.new_database_start && network.full_node) {//network.settingsx[6]

                System.out.println("base58_id " + network.base58_id);

                network.my_listings = new ArrayList<String>();

                String query = ("SELECT id FROM listings_db WHERE seller_id='" + network.base58_id + "' ORDER BY id ASC LIMIT " + network.hard_token_limit);// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                int rowCount4 = 0;
                while (!cursor.isAfterLast()) {

                    network.my_listings.add(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))));

                    rowCount4++;

                    cursor.moveToNext();

                }//while

                cursor.close();

                network.database_listings_owner = rowCount4;
                network.database_listings_for_edit = network.my_listings.size();

                System.out.println("database OWNER1 " + network.database_listings_owner);
                System.out.println("database OWNER2 " + network.my_listings.size());

            }//if****************************************************************

        } catch (Exception e) {
            e.printStackTrace();
        }



        //Get the number of incoming tokens.

        try {

            String query = ("SELECT id FROM unconfirmed_db WHERE seller_id='" + network.base58_id + "' AND  owner_id!='" + network.pub_key_id + "'");// ORDER BY id ASC
            Cursor cursor = db.rawQuery(query, null);

            network.incoming_tokens = cursor.getCount();

        } catch (Exception e) {
            e.printStackTrace();
        }



        //Get the number of outgoing tokens.

        try {

            String query = ("SELECT id FROM unconfirmed_db WHERE seller_id!='" + network.base58_id + "' AND  owner_id='" + network.pub_key_id + "'");// ORDER BY id ASC
            Cursor cursor = db.rawQuery(query, null);

            network.outgoing_tokens = cursor.getCount();

        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("outgoing_tokens " + network.outgoing_tokens);
        System.out.println("incoming_tokens " + network.incoming_tokens);


    }//********************



    private void loading2(SQLiteDatabase db){


        System.out.println("Loading 2");

        try {

            //Here we count the number of items in the send buffer to show the user.
            //This only contains the items the local user has. No other users.

            String query = ("SELECT xd FROM send_buffer LIMIT " + network.hard_token_limit);
            Cursor cursor = db.rawQuery(query, null);

            int rowCountbu = cursor.getCount();
            network.send_buffer_size = rowCountbu;

            System.out.println("network.send_buffer_size " + network.send_buffer_size);

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//********************



    private void loading3(SQLiteDatabase db){


        System.out.println("Loading 3");

        try {

            //Here we get the last unconfirmed ID so that our peers can see if we have anything new.
            //Also we count the number of items waiting to show to the user.

            String query = ("SELECT xd,id,hash_id FROM unconfirmed_db ORDER BY xd DESC");
            Cursor cursor = db.rawQuery(query, null);

            network.database_unconfirmed_total = cursor.getCount();

            cursor.moveToFirst();

            System.out.println("<<>>");

            if (network.database_unconfirmed_total > 0) {

                cursor.moveToFirst();
                network.last_unconfirmed_id = cursor.getString(cursor.getColumnIndex("id"));
                network.last_unconfirmed_idx = cursor.getString(cursor.getColumnIndex("hash_id"));

            }//****************************************
            else {
                network.last_unconfirmed_idx = "";
            }

            //while(!cursor.isAfterLast()){

            //System.out.println("> " + cursor.getString(cursor.getColumnIndex("id")));
            //cursor.moveToNext();

            //}

            System.out.println("unconfirmed TOTAL    " + network.database_unconfirmed_total);
            System.out.println("last_unconfirmed_idx " + network.last_unconfirmed_idx);

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//********************




    private void loading4(SQLiteDatabase db){


        System.out.println("Loading 4");

        try {

            //Here we load the most recent mining information.
            //We use this to calculate the last block time and also to refresh info.

            String query = ("SELECT link_id,mining_date,mining_new_block,mining_old_block,hash_id,package FROM mining_db ORDER BY xd DESC LIMIT 1");
            Cursor cursor = db.rawQuery(query, null);

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                try {

                    longstamp_hold = network.last_block_longstamp1;
                    network.last_block_longstamp1 = Long.parseLong(cursor.getString(cursor.getColumnIndex("mining_date")));
                    network.last_block_longstamp2 = longstamp_hold;
                    if (network.last_block_longstamp2 < 1) {
                        longstamp_hold = network.last_block_longstamp1;
                        network.last_block_longstamp2 = network.last_block_longstamp1;
                    }//network starting up***

                } catch (Exception e) {
                    System.out.println("Cannot get last block timestamp.");
                }

                network.last_block_id = cursor.getString(cursor.getColumnIndex("link_id"));
                network.last_block_mining_idx = cursor.getString(cursor.getColumnIndex("mining_new_block"));
                network.prev_block_mining_idx = cursor.getString(cursor.getColumnIndex("mining_old_block"));
                network.last_block_idx = cursor.getString(cursor.getColumnIndex("hash_id"));
                network.last_block_timestamp = cursor.getString(cursor.getColumnIndex("mining_date"));
                network.last_package_x = cursor.getString(cursor.getColumnIndex("package"));

                cursor.moveToNext();

            }//**************************************

            //System.out.println("last_block_ql[0] " + last_block_ql[0]);
            System.out.println("last_block_id         " + network.last_block_id);
            System.out.println("last_block_idx        " + network.last_block_idx);
            System.out.println("last_block_mining_idx " + network.last_block_mining_idx);
            System.out.println("prev_block_mining_idx " + network.prev_block_mining_idx);
            System.out.println("last_block_timestamp  " + network.last_block_timestamp);
            System.out.println("last_package_x        " + network.last_package_x);

            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }//********************




    //This was originally part of the load class but to speed up confirmations it was separated.
    //Now after a block is added we just call this class and not the slow load class.
    //After all blocks from a package are loaded we can call the heavy load class to show the user what they have.

    public void loading5(SQLiteDatabase db){//**************************************************************************


        System.out.println("[>>>] Load Difficulty");

        Long thisTick = System.currentTimeMillis();

        System.out.println("Loading 5");

        try {

            //Here we get a list of 100 recent blocks to test for difficulty.
            //Difficulty is set by a sum of the last 100 blocks not including packages.

            //Testing
            //Load last few blocks to build difficulty
            network.block_difficulty_test = 0;
            network.block_difficulty_listx = new String[network.block_difficulty_reset];
            network.block_date_listx = new String[network.block_difficulty_reset];
            network.block_hash_listx = new String[network.block_difficulty_reset];
            network.block_noose_listx = new String[network.block_difficulty_reset];


            String query = ("SELECT link_id,mining_difficulty,mining_date,mining_noose FROM mining_db WHERE mining_difficulty!='" + network.difficultyx_package + "' ORDER BY xd DESC LIMIT " + (network.block_difficulty_reset));//we don't want limit items because they are package blocks
            Cursor cursor = db.rawQuery(query, null);

            //krypton_database_driver.rs.last();
            cursor.moveToLast();

            network.block_difficulty_test = 0;

            while (!cursor.isBeforeFirst()){

                try {

                    //Show debug info for db build only....
                    if (network.new_database_start) {

                        System.out.println("network.block_difficulty_test " + network.block_difficulty_test);

                        System.out.println("id:                " + cursor.getInt(cursor.getColumnIndex("id")));
                        System.out.println("mining_date:       " + cursor.getString(cursor.getColumnIndex("mining_date")));
                        System.out.println("mining_difficulty: " + cursor.getString(cursor.getColumnIndex("mining_difficulty")));
                        System.out.println("mining_noose:      " + cursor.getString(cursor.getColumnIndex("mining_noose")));
                        System.out.println("mining_new_block:  " + cursor.getString(cursor.getColumnIndex("mining_new_block")));
                        System.out.println("mining_old_block:  " + cursor.getString(cursor.getColumnIndex("mining_old_block")));
                        System.out.println("hash_id:           " + cursor.getString(cursor.getColumnIndex("hash_id")));
                        System.out.println("");

                    }//****************************


                    network.block_difficulty_listx[network.block_difficulty_test] = cursor.getString(cursor.getColumnIndex("mining_difficulty"));
                    network.block_date_listx[network.block_difficulty_test] = cursor.getString(cursor.getColumnIndex("mining_date"));
                    network.block_noose_listx[network.block_difficulty_test] = cursor.getString(cursor.getColumnIndex("mining_noose"));


                } catch(Exception e) {e.printStackTrace();}

                network.block_difficulty_test++;
                cursor.moveToPrevious();

            }//while

            cursor.close();

        } catch(Exception e) {e.printStackTrace();}

        System.out.println("network.block_difficulty_test " + network.block_difficulty_test);



        try {

            //This is where we calculate difficulty.

            System.out.println("Loading 6");

            //Set difficulty.
            if (network.block_difficulty_test == network.block_difficulty_reset){

                Long l1 = (long) Long.parseLong( network.block_date_listx[0] );
                Long l2 = (long) Long.parseLong( network.block_date_listx[(network.block_difficulty_reset - 1)] );

                System.out.println("l1 " + l1);
                System.out.println("l2 " + l2);

                Long l3 = (long) l2 - l1;

                BigInteger xlx = new BigInteger("0");

                for (int loop = 0; loop < network.block_difficulty_reset; loop++){

                    BigInteger xlxs = new BigInteger(network.block_difficulty_listx[loop]);
                    xlx = xlx.add(xlxs);

                }//***************************************************************

                System.out.println("xlx1 " + xlx);

                xlx = xlx.divide(BigInteger.valueOf(network.block_difficulty_reset));

                System.out.println("xlx2 " + xlx);

                System.out.println("l3 " + l3);

                Long l4 = (long) l3 / network.block_difficulty_reset;

                network.blocktimesx = l4;

                System.out.println("difficultyx a          " + network.difficultyx);
                System.out.println("blocktimesx            " + network.blocktimesx);

                //Convert integer to percent. 100 / 33 is almost the same as 100 * .03
                int percentx = (int) 100 / network.target_block_adjustment;
                System.out.println("percentx               " + percentx);

                BigInteger change = xlx.divide(BigInteger.valueOf(percentx));
                System.out.println("change                 " + change);

                BigInteger new_difficulty_up = xlx.subtract(change);
                BigInteger new_difficulty_down = xlx.add(change);
                BigInteger new_difficulty_up_x2 = xlx.subtract(change.multiply(BigInteger.valueOf(5)));
                BigInteger new_difficulty_down_x2 = xlx.add(change.multiply(BigInteger.valueOf(5)));

                System.out.println("new_difficulty_up      " + new_difficulty_up);
                System.out.println("new_difficulty_down    " + new_difficulty_down);
                System.out.println("new_difficulty_up_x2   " + new_difficulty_up_x2);
                System.out.println("new_difficulty_down_x2 " + new_difficulty_down_x2);

                System.out.println("blocktimesx            " + network.blocktimesx);
                System.out.println("target_block_speed     " + network.target_block_speed);

                if (network.blocktimesx < (network.target_block_speed / network.mining_x2_adjustment)) {
                    System.out.println("DIFFICULTY UP X2");
                    network.difficultyx = new_difficulty_up_x2;
                } else if (network.blocktimesx > (network.target_block_speed * network.mining_x2_adjustment)) {
                    System.out.println("DIFFICULTY DOWN X2");
                    network.difficultyx = new_difficulty_down_x2;
                } else if (network.blocktimesx < network.target_block_speed) {
                    System.out.println("DIFFICULTY UP");
                    network.difficultyx = new_difficulty_up;
                } else if (network.blocktimesx > network.target_block_speed) {
                    System.out.println("DIFFICULTY DOWN");
                    network.difficultyx = new_difficulty_down;
                } else {
                    System.out.println("DIFFICULTY NO CHANGE");
                }

                int res1 = network.difficultyx.compareTo(BigInteger.valueOf(0));
                int res2 = network.difficultyx.compareTo(network.difficultyx_limit);

                if(res1 == -1){network.difficultyx = network.difficultyx_limit;}
                if(res2 == 1){network.difficultyx = network.difficultyx_limit;}//-1 because this is not a package

                //New database start.
                if(network.new_database_start){network.difficultyx = network.difficultyx_limit;}

                System.out.println("difficultyx b " + network.difficultyx);

            }//*********************************************************

        } catch(Exception e) {e.printStackTrace();}


    }//load3


}//load
