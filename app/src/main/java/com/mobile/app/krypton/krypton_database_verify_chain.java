package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.math.*;
import java.util.Arrays;
import org.spongycastle.util.encoders.Base64;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;





public class krypton_database_verify_chain extends SQLiteOpenHelper {


    final protected static char[] hexArray = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    JSONParser parser = new JSONParser();

    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This will upgrade tables, adding columns and new tables.
        //Note that existing columns will not be converted


    }


    //In this class we are testing the two chains to see which one is best. If ours is best we just do nothing and find another node.
    //If the server's database is better then we probable forked somewhere in the past and just need to delete ours and copy the server's database.

    krypton_database_verify_chain() {//******************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//**************************************************


    //Here we are testing our database starting from the last block we have in common the "fork" block.

    String test_blocks_a(int start_block,long fork_block_time){//**************************************************************************

        SQLiteDatabase db = this.getWritableDatabase();

        System.out.println("TESTING BLOCKCHAIN... A");

        String test_results = "0";


        try{


            int test_run = 0;

            boolean blocks_verified = false;
            boolean testx0 = false;
            boolean testx1 = false;
            boolean testx2 = false;
            boolean testx3 = false;
            boolean testx4 = false;


            long start_time = (long) 0;

            BigInteger work_done2 = new BigInteger("0");

            String last_package = "";




            System.out.println("start_block " + start_block);

            //We get the items from our database starting from the fork block.
            String query = ("SELECT * FROM mining_db WHERE xd >= " + start_block + " ORDER BY mining_date ASC");
            Cursor cursor = db.rawQuery(query, null);

            //Just for testing.
            System.out.println("getCount() " + cursor.getCount());

            //Database is before first.
            cursor.moveToFirst();

            //Preload, this is needed for the first block because we don't have anything before our test.
            last_package = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(10)));

            cursor.moveToNext();

            while (!cursor.isAfterLast()) {


                testx0 = false;
                testx1 = false;
                testx2 = false;
                testx3 = false;
                testx4 = false;


                //Save the database items in strings for easy reference.
                String id = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));
                String mining_date = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2)));
                String mining_noose = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(4)));
                String mining_old_block = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5)));
                String mining_new_block = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6)));
                String hash_id = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(8)));
                String packagex = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(10)));
                String pubkid = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(11)));
                String pubsigid = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(12)));


                //We save the old date for the block so it has something to reference.
                if(test_run == 0){start_time = Long.parseLong(mining_date);}




                //Test package creating 1.
                //First we get the size of the package and the size of the last package.
                //We need this information for the next stage.
                //Package testing is a 2 step process.

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
                        Object objx = parser.parse(packagex);
                        jsonObjectx_this = (JSONArray) objx;

                        this_package_sizex = jsonObjectx_this.size();

                    } catch (Exception e) {this_is_package = false;}

                }//***

                if (last_package.length() == 0) {last_is_package = false;}
                else {

                    last_is_package = true;

                    try {

                        //JSONParser parserx = new JSONParser();
                        Object objx = parser.parse(last_package);
                        jsonObjectx_last = (JSONArray) objx;

                        last_package_sizex = jsonObjectx_last.size();

                    } catch (Exception e) {last_is_package = false;}


                }//***



                //Test package creating stage 2.
                //Here we test the package to make sure it conforms to the system.
                //We need to know if a block is in the center of a package in which case it doesn't need a hard difficulty.
                //Or if it is the first block in the package, then it needs to meet the difficulty like all other blocks.
                //We use this information in the next stage to choose what difficulty to test for.

                try {

                    System.out.println("last package " + last_package);
                    System.out.println("this package " + packagex);

                    System.out.println("Last Package " + last_package_sizex);
                    System.out.println("This Package " + this_package_sizex);

                    //Test size.
                    if (this_package_sizex == 0 && last_package_sizex == 0) {testx0 = true;}//Normal operation.
                    else if (this_package_sizex == 0 && last_package_sizex == 1) {testx0 = true;}//Package just ended.
                    else if (this_package_sizex == network.block_compress_size && last_package_sizex < 2) {//1 would be the end of the last package and 0 would be no package.

                        if (this_is_package) {

                            System.out.println("First package block");
                            System.out.println("id1 " + jsonObjectx_this.get(0));
                            System.out.println("id2 " + hash_id);

                            if (jsonObjectx_this.get(0).equals(hash_id)) {testx0 = true;}
                            else {testx0 = false;}

                        }//*****************
                        else {testx0 = false;}

                    }//***********************************************************************************
                    else if ((this_package_sizex + 1) == last_package_sizex && this_package_sizex <= network.block_compress_size) {

                        if (this_is_package && last_is_package) {

                            is_in_package = true;

                            System.out.println("Middle package block");
                            System.out.println("id0 " + jsonObjectx_last.get(1));
                            System.out.println("id1 " + jsonObjectx_this.get(0));
                            System.out.println("id2 " + hash_id);

                            if (jsonObjectx_this.get(0).equals(hash_id) && jsonObjectx_last.get(1).equals(hash_id)) {testx0 = true;}
                            else {testx0 = false;}

                        }//*****************
                        else {testx0 = false;}

                    }//**********************************************************************************************************
                    else {testx0 = false;}

                } catch (Exception e) {testx0 = false;}

                System.out.println("testx0 " + testx0);





                //We only count work done that is NOT inside a block.
                //The work inside a block is not real work isn't only to link blocks together so it can't be counted.
                if (this_package_sizex == 0 || this_package_sizex == network.block_compress_size) {

                    //work_time = (long) Long.parseLong(mining_date) - start_time;
                    work_done2 = work_done2.add(new BigInteger(1, hexToBytes(mining_new_block)));
                    test_results = String.valueOf(work_done2);

                    //Just to see what pass we are on.
                    test_run++;

                }//*******************************************************************************

                System.out.println("test_results1 " + mining_new_block);
                System.out.println("test_results2 " + test_results);





                //Here we get the listing information and make sure it has the right hash.
                //Then we test that hash against the signature to make sure it's a real token.
                //We don't load the old token here so we can't compare it to make sure the db is correct. but it's in our db so it already passed that test.
                //Unless the user is trying to trick themselves.

                String query2 =("SELECT * FROM listings_db WHERE id=" + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))) + " LIMIT 1");
                Cursor cursor2 = db.rawQuery(query2, null);

                cursor2.moveToFirst();

                while (!cursor2.isAfterLast()) {


                    System.out.println("get ID " + cursor2.getString(cursor2.getColumnIndex("seller_id")) );

                    testx1 = false;
                    testx2 = false;

                    //Get item.
                    String[] move_item = new String[network.listing_size];

                    for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//*********

                        //System.out.println(rs2.getString(loop1 + 2));
                        move_item[loop1] = cursor2.getString(cursor2.getColumnIndex(cursor2.getColumnName(loop1 + 1)));

                    }//********************************************************************

                    String build_hash = "";
                    build_hash = build_hash + cursor2.getString(cursor2.getColumnIndex(cursor2.getColumnName(1)));

                    for (int loop1 = 3; loop1 < network.listing_size; loop1++) {//*********

                        build_hash = build_hash + cursor2.getString(cursor2.getColumnIndex(cursor2.getColumnName(loop1 + 1)));

                    }//********************************************************************

                    System.out.println("ID         " + move_item[0]);
                    System.out.println("build_hash " + build_hash);


                    //Test item signature.

                    try {

                        byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(build_hash.getBytes());

                        System.out.println("TESTX " + Base64.toBase64String(sha256_1w));
                        System.out.println("GIVEN " + move_item[1]);
                        System.out.println("MININ " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(8))) );

                        if (Base64.toBase64String(sha256_1w).equals(move_item[1])) {testx1 = true;}
                        else {System.out.println("Bad HASH"); testx1 = false;}

                        System.out.println("testx1 " + testx1);



                        byte[] keyxb3 = Base64.decode(move_item[4]);

                        X509EncodedKeySpec keySpecx3 = new X509EncodedKeySpec(keyxb3);
                        KeyFactory factx3 = KeyFactory.getInstance("RSA");
                        PublicKey pubx3 = factx3.generatePublic(keySpecx3);
                        Arrays.fill(keyxb3, (byte) 0);

                        Signature sigpk3 = Signature.getInstance("SHA1WithRSA");//MD5WithRSA
                        byte[] messagex3 = Base64.toBase64String(sha256_1w).getBytes("UTF8");

                        byte[] signatureBytesx3 = Base64.decode(move_item[2]);

                        sigpk3.initVerify(pubx3);
                        sigpk3.update(messagex3);

                        if(sigpk3.verify(signatureBytesx3)){testx2 = true;}
                        else{testx2 = false;}


                    } catch (Exception e) {e.printStackTrace();}


                    cursor2.moveToNext();


                }//**************

                cursor2.close();

                System.out.println("testx2 " + testx2);



                //Here we test the mining hash to make sure it fits the system, it has to be below the difficulty.
                //If the block is in a package then the difficulty doesn't have to be as tough it just needs to pass the limit.
                //We don't have our own difficulty setting here, which we would if this was a regular test.
                //So we have to just take what they give us for granted and then compare it to our chain.
                //This chain may have a low difficulty and more blocks but that doesn't mean it will win.

                String encode = mining_date + mining_old_block + hash_id + mining_noose + packagex;

                System.out.println("mining_date      " + mining_date);
                System.out.println("mining_old_block " + mining_old_block);
                System.out.println("hash_id          " + hash_id);
                System.out.println("mining_noose     " + mining_noose);

                BigInteger difficultyx = new BigInteger(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))));

                try {


                    byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(encode.getBytes());
                    //System.out.println("SHA1 " + bytesToHex(sha256_1));

                    BigInteger result = new BigInteger(1, sha256_1);

                    //System.out.println("value     " + value);
                    System.out.println("result      " + result);
                    System.out.println("difficultyx " + difficultyx);
                    System.out.println("SHA1 Mining " + bytesToHex(sha256_1));

                    encode = bytesToHex(sha256_1);

                    BigInteger package_difficultyx;

                    //Ff we are building a package then the other items do not need a hard difficulty.
                    if (is_in_package) {package_difficultyx = network.difficultyx_limit;}
                    else {package_difficultyx = difficultyx;}

                    int res = result.compareTo(package_difficultyx);

                    if (res == 0 || res == -1) {testx3 = true;}
                    else {testx3 = false;}


                } catch (Exception e) {e.printStackTrace();}

                System.out.println("testx3 " + testx3);






                //Make sure mining is only done by people who have coins.
                //Since the network is small and no coins are distributed though mining there is no reason.
                //That anyone else besides coin holders should be doing mining.

                System.out.println("key link: " + pubkid);
                System.out.println("sig:      " + pubsigid);

                krypton_database_get_token_pubkey keyl = new krypton_database_get_token_pubkey();
                String pubkey = keyl.getTokenKey(pubkid);

                System.out.println("key ID:   " + pubkey);

                try {


                    byte[] keyxb3 = Base64.decode(pubkey);

                    X509EncodedKeySpec keySpecx3 = new X509EncodedKeySpec(keyxb3);
                    KeyFactory factx3 = KeyFactory.getInstance("RSA");
                    PublicKey pubx3 = factx3.generatePublic(keySpecx3);
                    Arrays.fill(keyxb3, (byte) 0);

                    byte[] sha256_1x = MessageDigest.getInstance("SHA-256").digest(mining_new_block.getBytes());

                    Signature sigpk3 = Signature.getInstance("SHA1WithRSA");//MD5WithRSA
                    byte[] messagex3 = Base64.toBase64String(sha256_1x).getBytes("UTF8");

                    byte[] signatureBytesx3 = Base64.decode(pubsigid);

                    sigpk3.initVerify(pubx3);
                    sigpk3.update(messagex3);

                    System.out.println(sigpk3.verify(signatureBytesx3));

                    if(sigpk3.verify(signatureBytesx3)){testx4 = true;}
                    else{testx4 = false;}


                }catch(Exception e){e.printStackTrace();}

                System.out.println("testx4 " + testx4);





                //test the results.
                int test = 0;
                if (testx0) {test++;}
                if (testx1) {test++;}
                if (testx2) {test++;}
                if (testx3) {test++;}
                if (testx4) {}

                System.out.println("test " + test);

                if (test == 4) {blocks_verified = true;}
                else {blocks_verified = false;}

                //We save this so that in the next loop it's available.
                last_package = packagex;

                //Next db item.
                cursor.moveToNext();

                //If the first block is wrong no need to go though the rest.
                if (!blocks_verified) {break;}


            }//**************

            cursor.close();


            //Add up results this is where we calculate the work down considering time for our database.
            System.out.println("blocks_verified " + blocks_verified);
            System.out.println("");

            work_done2 = work_done2.divide(BigInteger.valueOf(test_run));
            test_results = String.valueOf(work_done2);

            fork_block_time = System.currentTimeMillis() - fork_block_time;

            System.out.println("results 0: " + fork_block_time);
            System.out.println("results 1: " + fork_block_time * test_run);

            work_done2 = work_done2.divide(BigInteger.valueOf(fork_block_time * test_run));
            test_results = String.valueOf(work_done2);

            System.out.println("results 2: " + test_results);

            if(!blocks_verified){System.out.println("blocks_verified 0 " + blocks_verified); test_results = "0";}


        } catch (Exception e) {test_results = "0"; e.printStackTrace();}
        finally {

            db.close();
            System.out.println("finally block executed");

        }

        return test_results;

    }//verify










    //Here we test the listings that have come from the remote server and we have saved in test_listings_db

    String test_blocks_b(String last_packagex,long fork_block_time){//**************************************************************************


        SQLiteDatabase db = this.getWritableDatabase();

        System.out.println("TESTING BLOCKCHAIN... B");

        String test_results = "0";


        try{


            int test_run = 0;

            boolean blocks_verified = false;
            boolean testx0 = false;
            boolean testx1 = false;
            boolean testx2 = false;
            boolean testx3 = false;
            boolean testx4 = false;

            //long work_time = (long) 0;
            long start_time = (long) 0;

            BigInteger work_done2 = new BigInteger("0");

            String last_package = "" + last_packagex;


            //Here we load the info in the test database that has been downloaded from krypton_update_test_block_remote.
            //If that class finds the fork block then it will call this class to test which chain is better.
            //The one from the remote server or the one in our database.
            //This is to allow forked chains to reunite.

            String query = ("SELECT * FROM test_listings_db ORDER BY mining_date ASC");
            Cursor cursor = db.rawQuery(query, null);

            System.out.println("getCount() " + cursor.getCount());

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {


                //Reset the tests in case something went wrong in the last test.
                testx0 = false;
                testx1 = false;
                testx2 = false;
                testx3 = false;
                testx4 = false;


                //Save the IDs so it's easy to reference them.
                //I don't want to make a mistake using 1 and 2 instead of a name.
                String id = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));
                String mining_date = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2)));
                String mining_noose = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(4)));
                String mining_old_block = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5)));
                String mining_new_block = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6)));
                String hash_id = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(8)));
                String packagex = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(10)));
                String pubkid = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(11)));
                String pubsigid = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(12)));



                if(test_run == 0){start_time = Long.parseLong( mining_date );}

                System.out.println("get ID " + cursor.getString(cursor.getColumnIndex("seller_id")) );




                //Test package creating 1.
                //First we get the size of the package and the size of the last package.
                //We need this information for the next stage.
                //Package testing is a 2 step process.

                boolean is_in_package = false;
                boolean last_is_package = false;
                boolean this_is_package = false;
                JSONArray jsonObjectx_last = null;
                JSONArray jsonObjectx_this = null;
                int last_package_sizex = 0;
                int this_package_sizex = 0;

                if (packagex.length() < 5) {this_is_package = false;}
                else {

                    this_is_package = true;

                    try {

                        //JSONParser parserx = new JSONParser();
                        Object objx = parser.parse(packagex);
                        jsonObjectx_this = (JSONArray) objx;

                        this_package_sizex = jsonObjectx_this.size();

                    } catch (Exception e) {this_is_package = false;}

                }//***

                if (last_package.length() < 5) {last_is_package = false;}
                else {

                    last_is_package = true;

                    try {

                        //JSONParser parserx = new JSONParser();
                        Object objx = parser.parse(last_package);
                        jsonObjectx_last = (JSONArray) objx;

                        last_package_sizex = jsonObjectx_last.size();

                    } catch (Exception e) {last_is_package = false;}


                }//***



                //Test package creating stage 2.
                //Here we test the package to make sure it conforms to the system.
                //We need to know if a block is in the center of a package in which case it doesn't need a hard difficulty.
                //Or if it is the first block in the package, then it needs to meet the difficulty like all other blocks.
                //We use this information in the next stage to choose what difficulty to test for.

                try {

                    System.out.println("last package " + last_package);
                    System.out.println("this package " + packagex);

                    System.out.println("Last Package " + last_package_sizex);
                    System.out.println("This Package " + this_package_sizex);

                    //Test size.
                    if (this_package_sizex == 0 && last_package_sizex == 0) {testx0 = true;}//Normal operation.
                    else if (this_package_sizex == 0 && last_package_sizex == 1) {testx0 = true;}//Package just ended.
                    else if (this_package_sizex <= network.block_compress_size && last_package_sizex < 2) {//1 would be the end of the last package and 0 would be no package.

                        if (this_is_package) {

                            System.out.println("First package block");
                            System.out.println("id1 " + jsonObjectx_this.get(0));
                            System.out.println("id2 " + hash_id);

                            if(jsonObjectx_this.get(0).equals(hash_id)){testx0 = true;}
                            else{testx0 = false;}

                        }//*****************
                        else {testx0 = false;}

                    }//***********************************************************************************
                    else if ((this_package_sizex + 1) == last_package_sizex && this_package_sizex <= network.block_compress_size) {

                        if (this_is_package) {

                            is_in_package = true;

                            System.out.println("Middle package block");
                            System.out.println("id0 " + jsonObjectx_last.get(1));
                            System.out.println("id1 " + jsonObjectx_this.get(0));
                            System.out.println("id2 " + hash_id);

                            if (jsonObjectx_this.get(0).equals(hash_id) && jsonObjectx_last.get(1).equals(hash_id)) {testx0 = true;}
                            else {testx0 = false;}

                        }//*****************
                        else {testx0 = false;}

                    }//**********************************************************************************************************
                    else {testx0 = false;}

                } catch (Exception e) {testx0 = false;}

                System.out.println("testx0 " + testx0);





                //We only count work done that is NOT inside a block.
                //The work inside a block is not real work isn't only to link blocks together so it can't be counted.
                if (this_package_sizex == 0 || this_package_sizex == network.block_compress_size) {

                    //work_time = (long) Long.parseLong(mining_date) - start_time;
                    work_done2 = work_done2.add(new BigInteger(1, hexToBytes(mining_new_block)));
                    test_results = String.valueOf(work_done2);

                    //Just to see what pass we are on.
                    test_run++;

                }//*******************************************************************************

                System.out.println("test_results1 " + mining_new_block);
                System.out.println("test_results2 " + test_results);





                //Here we get the listing information and make sure it has the right hash.
                //Then we test that hash against the signature to make sure it's a real token.
                //We don't load the old token here so we can't compare it to make sure the db is correct.
                //So there is a possibility that an attacker if they had enough mining power could create a fake chain that is better then ours and make us download it.
                //However once we reset our chain we would find out and not download their blocks.
                //It could slow us down until we find a better node.

                String[] move_item = new String[network.listing_size];

                int plus1a = 0;

                for (int loop1 = network.miningx_size; loop1 < (network.miningx_size + network.listing_size); loop1++) {//*********

                    move_item[plus1a] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));
                    plus1a++;

                }//****************************************************************************************************************


                String build_hash = "";
                build_hash = build_hash + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(network.miningx_size + 1)));
                System.out.println("B " + build_hash);

                for (int loop1 = (network.miningx_size + 3); loop1 < (network.miningx_size + network.listing_size); loop1++) {//*********

                    build_hash = build_hash + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop1 + 1)));

                }//**********************************************************************************************************************


                System.out.println("ID " + move_item[0]);
                System.out.println("build_hash " + build_hash);


                //Test item signature.

                try {


                    byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(build_hash.getBytes());

                    System.out.println("TESTX " + Base64.toBase64String(sha256_1w));
                    System.out.println("GIVEN " + move_item[1]);
                    System.out.println("MININ " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(8))) );

                    if(Base64.toBase64String(sha256_1w).equals(move_item[1])){testx1 = true;}
                    else{System.out.println("Bad HASH"); testx1 = false;}

                    System.out.println("testm1 " + testx1);



                    byte[] keyxb3 = Base64.decode(move_item[4]);

                    X509EncodedKeySpec keySpecx3 = new X509EncodedKeySpec(keyxb3);
                    KeyFactory factx3 = KeyFactory.getInstance("RSA");
                    PublicKey pubx3 = factx3.generatePublic(keySpecx3);
                    Arrays.fill(keyxb3, (byte) 0);

                    Signature sigpk3 = Signature.getInstance("SHA1WithRSA");//MD5WithRSA
                    byte[] messagex3 = Base64.toBase64String(sha256_1w).getBytes("UTF8");

                    byte[] signatureBytesx3 = Base64.decode(move_item[2]);

                    sigpk3.initVerify(pubx3);
                    sigpk3.update(messagex3);

                    if (sigpk3.verify(signatureBytesx3)) {testx2 = true;}
                    else {testx2 = false;}


                } catch (Exception e) {e.printStackTrace();}

                System.out.println("testm2 " + testx2);






                //Here we test the mining hash to make sure it fits the system, it has to be below the difficulty.
                //If the block is in a package then the difficulty doesn't have to be as tough it just needs to pass the limit.
                //We don't have our own difficulty setting here, which we would if this was a regular test.
                //So we have to just take what they give us for granted and then compare it to our chain.
                //This chain may have a low difficulty and more blocks but that doesn't mean it will win.

                String encode = mining_date + mining_old_block + hash_id + mining_noose + packagex;

                System.out.println("mining_date      " + mining_date);
                System.out.println("mining_old_block " + mining_old_block);
                System.out.println("hash_id          " + hash_id);
                System.out.println("mining_noose     " + mining_noose);

                //Test the block mining.

                BigInteger difficultyx = new BigInteger(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))));

                try {


                    byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(encode.getBytes());
                    //System.out.println("SHA1 " + bytesToHex(sha256_1));

                    BigInteger result = new BigInteger(1, sha256_1);

                    //System.out.println("value     " + value);
                    System.out.println("result      " + result);
                    System.out.println("difficultyx " + difficultyx);
                    System.out.println("SHA1 Mining " + bytesToHex(sha256_1));

                    encode = bytesToHex(sha256_1);

                    BigInteger package_difficultyx;

                    //If we are building a package then the other items do not need a hard difficulty.
                    if (is_in_package) {package_difficultyx = network.difficultyx_limit;}
                    else {package_difficultyx = difficultyx;}

                    int res = result.compareTo(package_difficultyx);

                    if (res == 0 || res == -1) {testx3 = true;}
                    else {testx3 = false;}


                } catch (Exception e) {e.printStackTrace();}

                System.out.println("testm3 " + testx3);






                //Make sure mining is only done by people who have coins
                //Since the network is small and no coins are distributed though mining there is no reason
                //That anyone else besides coin holders should be doing mining.
                //We may not be able to use this test because the remote database may have new signatures that we don't have.

                System.out.println("key link: " + pubkid);
                System.out.println("sig:      " + pubsigid);

                krypton_database_get_token_pubkey keyl = new krypton_database_get_token_pubkey();
                String pubkey = keyl.getTokenKey(pubkid);

                System.out.println("key ID:   " + pubkey);

                try {


                    byte[] keyxb3 = Base64.decode(pubkey);

                    X509EncodedKeySpec keySpecx3 = new X509EncodedKeySpec(keyxb3);
                    KeyFactory factx3 = KeyFactory.getInstance("RSA");
                    PublicKey pubx3 = factx3.generatePublic(keySpecx3);
                    Arrays.fill(keyxb3, (byte) 0);

                    byte[] sha256_1x = MessageDigest.getInstance("SHA-256").digest(mining_new_block.getBytes());

                    Signature sigpk3 = Signature.getInstance("SHA1WithRSA");//MD5WithRSA
                    byte[] messagex3 = Base64.toBase64String(sha256_1x).getBytes("UTF8");

                    byte[] signatureBytesx3 = Base64.decode(pubsigid);

                    sigpk3.initVerify(pubx3);
                    sigpk3.update(messagex3);

                    System.out.println(sigpk3.verify(signatureBytesx3));

                    if (sigpk3.verify(signatureBytesx3)) {testx4 = true;}
                    else {testx4 = false;}


                } catch (Exception e) {e.printStackTrace();}

                System.out.println("testm4 " + testx4);





                //Test the results.
                int test = 0;
                if (testx0) {test++;}
                if (testx1) {test++;}
                if (testx2) {test++;}
                if (testx3) {test++;}
                if (testx4) {}//We might not be able to use this the remote database is not the same.

                System.out.println("test " + test);

                if (test == 4) {blocks_verified = true;}
                else {blocks_verified = false;}

                //This is usually done by the system but here in the test we have to save it ourselves.
                last_package = packagex;

                //Update db loop
                cursor.moveToNext();

                //If the first block is wrong no need to go though the rest.
                if (!blocks_verified) {break;}


            }//**************

            cursor.close();


            //Add up results this is where we calculate the work down considering time for the remote database.
            System.out.println("blocks_verified " + blocks_verified);
            System.out.println("");

            work_done2 = work_done2.divide(BigInteger.valueOf(test_run));
            test_results = String.valueOf(work_done2);

            fork_block_time = System.currentTimeMillis() - fork_block_time;

            System.out.println("results 0: " + fork_block_time);
            System.out.println("results 1: " + fork_block_time * test_run);

            work_done2 = work_done2.divide(BigInteger.valueOf(fork_block_time * test_run));
            test_results = String.valueOf(work_done2);

            System.out.println("results 2: " + test_results);

            if (!blocks_verified) {System.out.println("blocks_verified 0 " + blocks_verified); test_results = "0";}


        } catch (Exception e) {e.printStackTrace(); test_results = "0";}
        finally {

            db.close();
            System.out.println("finally block executed");

        }

        return test_results;

    }//verify









    public static byte[] hexToBytes(String s) {

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;

    }






    public String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for ( int j = 0; j < bytes.length; j++ ) {

            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];

        }//***************************************

        return new String(hexChars);

    }//********************************************









}//class
