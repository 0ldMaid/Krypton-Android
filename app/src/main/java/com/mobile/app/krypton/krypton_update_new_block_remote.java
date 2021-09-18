package com.mobile.app.krypton;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.KeyFactory;
import java.util.Arrays;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.security.PublicKey;
import org.spongycastle.util.encoders.Base64;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;



public class krypton_update_new_block_remote extends SQLiteOpenHelper {

    //this class is used to verify new blocks as they come in

    final protected static char[] hexArray = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    //We don't want to reload this each time.
    krypton_database_get_last_mining_id idxm = new krypton_database_get_last_mining_id();

    //We don't want to create too many references to this.
    JSONParser parserx = new JSONParser();

    //I'm not sure if we want to confirm with a hash or a TRUE yes
    String token_update_id = "";

    //I'm not sure if we want to confirm with a hash or a TRUE yes
    boolean token_updated = false;

    //We don't want this class to be running from multiple threads at the same time.
    static boolean update_in_use = false;//blockchain is being updated.


    @Override
    public void onCreate(SQLiteDatabase db) {
        //This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This will upgrade tables, adding columns and new tables.
        //Note that existing columns will not be converted


    }


    //This is a very important class used to verify a new block. We have 10 tests to preform on the block to make sure it fits our system.
    //Bitcoin verifies blocks very quickly, not sure how they do that but in our class it takes a long time.

    public krypton_update_new_block_remote(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//**************************************



    //This is the start port

    boolean update(String[] transfer_id, String[] mining_id, String[] old_token) {//*****************

        boolean test = false;

        if(!update_in_use){test = update2(transfer_id, mining_id, old_token);}
        else{System.out.println("Update already in use..."); test = false;}

        return test;

    }//*********************************************************************************************



    //There should be a test before this in the class that is calling this method to check if it is already running or not.

    boolean update2(String[] transfer_id, String[] mining_id, String[] old_token){//*****************

        try {


            //We are working.
            update_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {

                System.out.println("[>>>] UPDATE TOKEN....REMOTE NEW BLOCK");

                //Record the time to install.
                Long thisTick = System.currentTimeMillis();

                //Show if the installation was successful.
                token_updated = false;

                //The ID of this update.
                token_update_id = "";

                //We call this here so we can use the other methods it provides.
                String lasthash = idxm.getLastHash();
                String last_package_x = idxm.getLastPackage();

                //String block_idx = "";
                String[] move_item = transfer_id;//Make the new item
                String[] tokenx = old_token;//Get the old token


                //The new block
                String ID = mining_id[0];
                String mining_date = mining_id[1];
                String mining_difficulty = mining_id[2];
                String mining_noose = mining_id[3];
                String mining_old_block = mining_id[4];
                String mining_new_block = mining_id[5];
                String prev_hash_id = tokenx[1];//old block hash
                String hash_id = mining_id[7];
                String sig_id = mining_id[8];
                String packagex = mining_id[9];
                String pubkid = mining_id[10];
                String pubsigid = mining_id[11];


                //Block has to pass all tests before it can be added to the chain
                boolean testm0 = false;
                boolean testm1 = false;
                boolean testm2 = false;
                boolean testm3 = false;
                boolean testm4 = false;
                boolean testm5 = false;
                boolean testm6 = false;
                boolean testm7 = false;
                boolean testm8 = false;
                boolean testm9 = false;
                boolean testm10 = false;

                //Installation.
                boolean install_listing = false;
                boolean install_backup = false;
                boolean install_block = false;
                boolean install_search = false;
                boolean install_new = false;



                System.out.println("Test Mining... " + move_item[0] + " " + ID);


                //Test package creating 1.
                //First we get the size of the package and the size of the last package.
                //We need this information for the next stage.
                //Package testing is a 2 step process.

                //Decode packages
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
                        Object objx = parserx.parse(packagex);
                        jsonObjectx_this = (JSONArray) objx;

                        this_package_sizex = jsonObjectx_this.size();

                    } catch (Exception e) {this_is_package = false;}

                }//***

                if (last_package_x.length() == 0) {last_is_package = false;}
                else {

                    last_is_package = true;

                    try {

                        //JSONParser parserx = new JSONParser();
                        Object objx = parserx.parse(last_package_x);
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

                    System.out.println("last package    " + last_package_x);
                    System.out.println("this package    " + packagex);

                    System.out.println("Last Package    " + last_package_sizex);
                    System.out.println("This Package    " + this_package_sizex);

                    System.out.println("Last is Package " + last_is_package);
                    System.out.println("This is Package " + this_is_package);

                    //test size
                    if (this_package_sizex == 0 && last_package_sizex == 0) {testm0 = true;}//Normal operation.
                    else if (this_package_sizex == 0 && last_package_sizex == 1) {testm0 = true;}//Package just ended.
                    else if (this_package_sizex == network.block_compress_size && last_package_sizex < 2) {//1 would be the end of the last package and 0 would be no package.

                        System.out.println("First package block");

                        if (this_is_package) {

                            System.out.println("First package block 2");
                            System.out.println("id1 " + jsonObjectx_this.get(0));
                            System.out.println("id2 " + hash_id);

                            if (jsonObjectx_this.get(0).equals(hash_id)) {testm0 = true;}
                            else {testm0 = false;}

                        }//*****************
                        else {testm0 = false;}

                    }//***********************************************************************************
                    else if ((this_package_sizex + 1) == last_package_sizex && this_package_sizex < network.block_compress_size) {

                        System.out.println("Middle package block");

                        if (this_is_package && last_is_package) {

                            is_in_package = true;

                            System.out.println("Middle package block 2");
                            System.out.println("id0 " + jsonObjectx_last.get(1));
                            System.out.println("id1 " + jsonObjectx_this.get(0));
                            System.out.println("id2 " + hash_id);

                            if (jsonObjectx_this.get(0).equals(hash_id) && jsonObjectx_last.get(1).equals(hash_id)) {testm0 = true;}
                            else {testm0 = false;}

                        }//*************************************
                        else {testm0 = false;}

                    }//**********************************************************************************************************
                    else {testm0 = false;}

                } catch (Exception e) {testm0 = false;}

                System.out.println("testm0 " + testm0);






                //here we are just testing to make sure the item has the right ID it can't be lower then 100000 and it can't be higher then 124999.
                //we don't want people adding in new tokens to the system.

                try {

                    if (Integer.parseInt(ID) >= network.base_int && Integer.parseInt(ID) <= (network.base_int + network.hard_token_limit)) {testm1 = true;}
                    else {testm1 = false;}

                } catch (Exception e) {testm1 = false;}

                System.out.println("testm1 " + testm1);






                //Make sure item is new, to prevent replay attacks and also to prevent spam
                //Need to modify to check for old and new items

                try {


                    System.out.println("time now    " + System.currentTimeMillis());
                    System.out.println("last update " + move_item[3]);
                    System.out.println("old update  " + tokenx[3]);
                    System.out.println("time        " + (System.currentTimeMillis() - Long.parseLong(move_item[3])));

                    if (Long.parseLong(tokenx[3]) < Long.parseLong(move_item[3])) {testm2 = true;}//update
                    else if (Long.parseLong(tokenx[3]) == Long.parseLong(move_item[3])) {

                        //if the nonce is the same then the block should be old because the chain is being moved.
                        //if it's new someone is trying to spam the network with useless updates

                        //get the old mining block
                        krypton_database_get_old_mining_block mblock = new krypton_database_get_old_mining_block();
                        String[] mblock2 = mblock.getBlock(move_item[0]);

                        System.out.println("old_block_date " + mblock2[1]);
                        Long old_block_date = (long) Long.parseLong(mblock2[1]);

                        Long time_now = (long) System.currentTimeMillis();
                        System.out.println("time_now       " + time_now);

                        System.out.println("difference     " + (time_now - old_block_date));

                        if((time_now - old_block_date) > network.block_date_spam){testm2 = true;}//604800000 = 1 week
                        else{testm2 = false;}

                    }//chain move

                } catch (Exception e) {e.printStackTrace();}

                System.out.println("testm2 " + testm2);





                //String encode = encode_date + old_block_mining_hash + new_block_hash + Integer.toString(noosex);
                String encode = mining_date + mining_old_block + hash_id + mining_noose + packagex;

                System.out.println("mining_date " + mining_date);
                System.out.println("mining_old_block " + mining_old_block);
                System.out.println("hash_id " + hash_id);
                System.out.println("mining_noose " + mining_noose);

                //Here we test the mining hash to make sure it fits the system, it has to be below the difficulty.
                //If the block is in a package then the difficulty doesn't have to be as tough it just needs to pass the limit.
                //We don't have our own difficulty setting here, which we would if this was a regular test.
                //So we have to just take what they give us for granted and then compare it to our chain.
                //This chain may have a low difficulty and more blocks but that doesn't mean it will win.

                try {

                    byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(encode.getBytes());
                    //System.out.println("SHA1 " + bytesToHex(sha256_1));

                    BigInteger result = new BigInteger(1, sha256_1);

                    //System.out.println("value " + value);
                    System.out.println("result                       " + result);
                    System.out.println("network.difficultyx          " + network.difficultyx);
                    System.out.println("network.difficultyx_limit    " + network.difficultyx_limit);
                    System.out.println("network.difficultyx_package  " + network.difficultyx_package);
                    System.out.println("SHA1 Mining                  " + bytesToHex(sha256_1));

                    encode = bytesToHex(sha256_1);

                    //long package_difficultyx = (long) 0;
                    BigInteger package_difficultyx = new BigInteger("0");

                    //If we are building a package then the other items do not need a hard difficulty.
                    if(is_in_package){package_difficultyx = network.difficultyx_package;}
                    else{package_difficultyx = network.difficultyx;}

                    int res = result.compareTo(package_difficultyx);

                    //if(result < package_difficultyx){testm3 = true;}
                    if (res == -1) {testm3 = true;}//Second value is greater
                    else {testm3 = false;}

                } catch (Exception e) {e.printStackTrace();}

                //testm3 = true;

                System.out.println("testm3 " + testm3);







                //Test mining date
                //The new block needs to be older then the time now but also newer then the last block.
                //We do basic time tests here.

                try {


                    long block_date = (long) Long.parseLong(mining_date);
                    System.out.println("block date     " + block_date);

                    long token_update_time = (long) Long.parseLong(move_item[3]);
                    System.out.println("new block time " + token_update_time);

                    long old_block_date = (long) Long.parseLong(tokenx[3]);
                    System.out.println("old block date " + old_block_date);

                    long time_now = (long) System.currentTimeMillis();
                    System.out.println("time now       " + time_now);

                    //mining time needs to be right.
                    if (block_date > old_block_date && block_date < time_now) {testm4 = true;}
                    else {testm4 = false;}


                } catch (Exception e) {}

                System.out.println("testm4 " + testm4);






                //Test
                //Build the hash without mining info.
                //We need to make sure the hash is the same as the one miner gave us.
                //So we build our own here from the listing info.

                String build_hash = "";
                build_hash = build_hash + move_item[0];
                for (int loop = 3; loop < move_item.length; loop++){

                    build_hash = build_hash + move_item[loop];//save everything else

                }//*************************************************


                //Test signature
                //Here we use our hash and test it against the hash on file and also make sure the signature has signed it.
                //We don't care what info the user has in their token as long as they singed it and it fits the last one on file.
                //Each token has the pubic key in base 64 format so we just convert it back to a public key and test.

                System.out.println(build_hash);

                try {


                    byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(build_hash.getBytes());

                    System.out.println("TEST HASH " + Base64.toBase64String(sha256_1w));
                    System.out.println("BASE HASH " + move_item[1]);

                    if(move_item[1].equals(Base64.toBase64String(sha256_1w))){testm5 = true;}
                    else{testm5 = false;}
                    System.out.println("testm5 " + testm5);

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

                    if (sigpk3.verify(signatureBytesx3)) {testm6 = true;}
                    else {testm6 = false;}
                    System.out.println("testm6 " + testm6);

                } catch (Exception e) {e.printStackTrace();}






                //Here we are getting all the different base 58 hashes to test with.
                //There are a lot of different situations here tokens are being updated tokens are being transferred or even moved.
                //We have to build our own version of the hash first from the users public key and then we test that against their token.
                //It is also possible to use base 58 keys without K in front on this system, so we have to test for that as well.
                String old_base58_key = tokenx[60];

                String base58 = "x";
                String new_base58 = "x";

                try{

                    base58 = move_item[4];

                    int len = base58.length();
                    byte[] data = new byte[len / 2];

                    for (int i = 0; i < len; i += 2) {

                        data[i / 2] = (byte) ((Character.digit(base58.charAt(i), 16) << 4) + Character.digit(base58.charAt(i+1), 16));

                    }//*******************************

                    byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(data);

                    base58 = Base58Encode.encode(sha256_1);

                    byte[] a = "=".getBytes();
                    byte[] b = sha256_1;

                    byte[] result = new byte[a.length + b.length];
                    System.arraycopy(a, 0, result, 0, a.length);
                    System.arraycopy(b, 0, result, a.length, b.length);

                    new_base58 = Base58Encode.encode(result);

                } catch (Exception e) {e.printStackTrace();}


                System.out.println("old_base58_key " + old_base58_key);
                System.out.println("base58         " + base58);
                System.out.println("new base58     " + new_base58);

                System.out.println("old hash       " + tokenx[1]);
                System.out.println("new hash       " + move_item[1]);

                //If the hash is the same then don't bother.
                //This is a very important function that can allow a coin to be updated or transferred.
                //In case A the coin is being updated so the public key will match the old key from the old saved token.
                //In case B the coin is being sent to another wallet in which case the base 58 key will be different but the RSA key will be the same.
                //In case C the coin is being sent to a new wallet address in which case the base 58 key will be different but the RSA key will be the same.
                if (move_item[1].equals(tokenx[1])) {testm7 = true;}//CASE A
                else if (base58.equals(old_base58_key)) {testm7 = true;}//CASE B
                else if (new_base58.equals(old_base58_key)) {testm7 = true;}//CASE C
                else {testm7 = false;}

                System.out.println("testm7 " + testm7);






                //Make sure mining is only done by people who have coins
                //Since the network is small and no coins are distributed though mining there is no reason
                //That anyone else besides coin holders should be doing mining.

                System.out.println("key link: " + pubkid);
                System.out.println("sig:      " + pubsigid);

                String pubkey = network.pub_key_id;//We use our key to stop loading errors. //network.settingsx[5]
                int countx = 0;//Get miner token list needs to be > 50.
                boolean newid = false;

                //We only want to test the first block.
                if (!is_in_package) {

                    krypton_database_get_token_pubkey keyl = new krypton_database_get_token_pubkey();
                    pubkey = keyl.getTokenKey(pubkid);//Make sure Miner is a user of the system.
                    countx = keyl.getMinerTokenList(pubkid,pubkey);//Make sure the Miner has more then 50 tokens.
                    newid = keyl.getMiningKeyList(pubkid);

                }//******************

                System.out.println("key ID:    " + pubkey);
                System.out.println("countx:    " + countx);
                System.out.println("already M: " + newid);

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

                    //Miner needs to be signed and also need to hold some tokens to show they are part of the system.
                    if (sigpk3.verify(signatureBytesx3) && countx >= network.mining_token_limit) {

                        //Miner cannot mine more then 1 block in a row.
                        if(!newid){testm8 = true;}
                        else{testm8 = false;}

                    }//***************************************************************************
                    else {testm8 = false;}

                } catch (Exception e) {e.printStackTrace();}

                //Inside of a package doesn't matter because it's only the first block that is important.
                if (is_in_package) {testm8 = true; System.out.println("8 true in package.");}

                System.out.println("testm8 " + testm8);






                //Make sure the blocks are in line this test can be corrupted if 2 blocks come in too fast
                //So we test this at the end and then quickly update our last mining id to stop this error

                //krypton_database_get_last_mining_id idxm = new krypton_database_get_last_mining_id();
                //String lasthash = idxm.getLastHash();

                System.out.println("1 " + mining_old_block);
                System.out.println("2 " + network.last_block_mining_idx);
                System.out.println("3 " + lasthash);

                if (mining_old_block.equals(lasthash)) {testm9 = true;}
                else {testm9 = false;}
                System.out.println("testm9 " + testm9);





                //Make sure everything is true and then save the block

                int last_test = 0;

                if (testm0) {last_test++;}
                if (testm1) {last_test++;}
                if (testm2) {last_test++;}
                if (testm3) {last_test++;}
                if (testm4) {last_test++;}
                if (testm5) {last_test++;}
                if (testm6) {last_test++;}
                if (testm7) {last_test++;}
                if (testm8) {last_test++;}
                if (testm9) {last_test++;}

                System.out.println("last_test " + last_test);


                //the buffered block is used or useless get a new one.
                network.mining_block_ready = false;
                network.reset_mining_hash = true;



                if (last_test == 10) {//********************************************************************************************


                    //We do this first to stop any other blocks from slipping through while we are updating.
                    network.last_block_mining_idx = mining_new_block;

                    move_item[1] = hash_id;
                    move_item[2] = sig_id;


                    //Start the transaction...
                    db.beginTransaction();

                    //Save the listing information.
                    try {

                        //Get rid of the old token first.
                        db.execSQL("DELETE FROM listings_db WHERE id=" + move_item[0] );

                        ContentValues values = new ContentValues();

                        for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//**********************************

                            values.put(network.item_layout[loop1], move_item[(loop1)]); //

                        }//*******************************************************************************************

                        //Inserting Row
                        db.insert("listings_db", null, values);

                        install_listing = true;

                    } catch (Exception e) {e.printStackTrace(); db.endTransaction();}


                    //Some old phones have errors when installing thing to fast.
                    //I have had errors when the listing was installed but the backup had a database lock error.
                    //Trying a pause here to fix it.
                    //try {Thread.sleep(100);} catch(InterruptedException e){}


                    //The backup database is used to send info to our peers if they are behind or if we have a blockchain fork we have to roll back.
                    try {

                        System.out.println("PS UPDATE BB " + move_item[0]);

                        //delete the old block.
                        db.execSQL("DELETE FROM backup_db WHERE hash_id='" + hash_id + "'");

                        ContentValues values = new ContentValues();

                        for(int loop1 = 0; loop1 < network.listing_size; loop1++){//**********************************

                            values.put(network.item_layout[loop1], move_item[(loop1)]);//

                        }//*******************************************************************************************

                        //Inserting Row
                        db.insert("backup_db", null, values);

                        install_backup = true;

                    } catch (Exception e) {e.printStackTrace(); db.endTransaction();}



                    //Here we are adding the info into the mining blockchain.
                    try {

                        ContentValues values = new ContentValues();

                        values.put("link_id", move_item[0]); //
                        values.put("mining_date", mining_date); //
                        values.put("mining_difficulty", mining_difficulty); //
                        values.put("mining_noose", mining_noose); //
                        values.put("mining_old_block", mining_old_block); //
                        values.put("mining_new_block", mining_new_block); //
                        values.put("previous_hash_id", prev_hash_id); //
                        values.put("hash_id", hash_id); //
                        values.put("sig_id", sig_id); //
                        values.put("package", packagex); //
                        values.put("mining_pkey_link", pubkid); //mining_pkey_link
                        values.put("mining_sig", pubsigid); //mining_sig

                        System.out.println("prev_hash_id " + prev_hash_id);
                        System.out.println("hash_id " + hash_id);
                        System.out.println("sig_id " + sig_id);

                        // Inserting Row
                        db.insert("mining_db", null, values);

                        install_block = true;

                        //If everything is successful then we commit.
                        db.setTransactionSuccessful();

                    } catch (Exception e) {e.printStackTrace(); db.endTransaction();}
                    finally{

                        //End transaction...
                        db.endTransaction();

                    }//*****




                    //This is for remote tor users to search our database.
                    //Since our database is saved in HEX format the title has to be saved in another db for searches.
                    //We are saving that info here.
                    try {

                        db.execSQL("DELETE FROM searchx WHERE id=" + move_item[0]);

                        ContentValues values = new ContentValues();

                        values.put("id", move_item[0]);//
                        values.put("title", new String(hexToBytes(move_item[29])));//
                        values.put("price", new String(hexToBytes(move_item[21])));//
                        values.put("currency", new String(hexToBytes(move_item[6])));//
                        values.put("seller_address_country", new String(hexToBytes(move_item[59])));//
                        values.put("item_search_1", new String(hexToBytes(move_item[32])));//
                        values.put("item_search_2", new String(hexToBytes(move_item[33])));//
                        values.put("item_search_3", new String(hexToBytes(move_item[34])));//

                        //Inserting Row.
                        db.insert("searchx", null, values);

                        System.out.println("Add search X");

                        install_search = true;

                    } catch (Exception e) {e.printStackTrace();}





                    //If the title of the new listing is different then the title of the old one, then this item has been updated or is new.
                    //We save this info to our update database to show users a list of "What's New."
                    if(!move_item[29].equals(tokenx[29]) || !move_item[21].equals(tokenx[21])) {

                        try {

                            db.execSQL("DELETE FROM new_update WHERE id=" + move_item[0]);

                            ContentValues values = new ContentValues();

                            values.put("id", move_item[0]);//
                            values.put("title", new String(hexToBytes(move_item[29])));//
                            values.put("price", new String(hexToBytes(move_item[21])));//
                            values.put("currency", new String(hexToBytes(move_item[6])));//
                            values.put("seller_address_country", new String(hexToBytes(move_item[59])));//
                            values.put("item_search_1", new String(hexToBytes(move_item[32])));//
                            values.put("item_search_2", new String(hexToBytes(move_item[33])));//
                            values.put("item_search_3", new String(hexToBytes(move_item[34])));//

                            // Inserting Row
                            db.insert("new_update", null, values);

                            System.out.println("Add search NEW");

                            install_new = true;

                        } catch (Exception e) {e.printStackTrace();}


                        String query = ("SELECT xd FROM new_update ORDER BY xd DESC LIMIT " + network.new_item_search_limit);
                        Cursor cursor = db.rawQuery(query, null);

                        int rowCount = cursor.getCount();

                        cursor.moveToLast();

                        String lastID = cursor.getString(cursor.getColumnIndex("xd"));

                        System.out.println("New List Size: " + rowCount + " " + lastID);

                        cursor.close();

                        if(rowCount >= network.new_item_search_limit){

                            db.execSQL("DELETE FROM new_update WHERE xd < " + lastID);

                        }//********************************************


                    }//************************************



                    //Get new onion addresses if this token has one.
                    String new_address = move_item[61];
                    new_address = new String(hexToBytes(new_address));

                    System.out.println("new_address: " + new_address);

                    if(new_address.contains(".onion") && !new_address.equals(krypton_net_client.serverOnionAddress)){

                        System.out.println("new_address: TRUE");

                        db.execSQL("DELETE FROM network WHERE address='" + new_address + "'");

                        ContentValues values = new ContentValues();
                        values.put("address", new_address); //Onion address
                        db.insert("network", null, values);

                        krypton_database_node node = new krypton_database_node();
                        node.refresh();

                    }//**********************************************************************************************



                    //Confirmation number.
                    token_update_id = hash_id;

                    //Record the time for display since last block time.
                    network.time_block_added = System.currentTimeMillis();

                    //Someone has found the block, go to the next task.
                    if (!network.new_database_start) {network.reset_mining_hash = true; mining.mining_stop = true;}


                    //Test installation before we continue.
                    int test_install = 0;

                    if (install_listing) {test_install++;}
                    if (install_backup)  {test_install++;}
                    if (install_block)   {test_install++;}
                    if (install_search)  {test_install++;}
                    //if(install_new) {test_install++;}

                    //Success.
                    if (test_install == 4) {

                        token_updated = true;

                    }//*********************
                    else{token_updated = false; System.out.println("Installation failed: " + install_listing + " " + install_backup + " " + install_block + " " + install_search + " " + install_new);}


                }//if************************************************************************************************************
                else {System.out.println("TEST1 TEST2 FAIL..."); token_updated = false; token_update_id = "";}


                //Delete from buffer.
                krypton_database_delete_unconfirmed unconfirmed = new krypton_database_delete_unconfirmed();
                unconfirmed.deleteID(move_item[0]);

                //Delete from buffer.
                krypton_database_delete_buffer bufferd = new krypton_database_delete_buffer();
                bufferd.deleteID(move_item[0]);


                network.dbxadd_longstamp = System.currentTimeMillis() - thisTick;

                System.out.println("DONE. Executions Time: " + (System.currentTimeMillis() - thisTick));


            } catch (Exception e) {e.printStackTrace(); network.mining_block_ready = false;}//network.mining_block_ready = 0;
            finally {

                //Close the database.
                db.close();
                System.out.println("finally block executed");

            }//******


        } catch (Exception e) {e.printStackTrace();}
        finally {

            //Set this first in case the rest fails.
            update_in_use = false;

        }

        System.out.println("[installing] Token updated / block installed: " + token_updated);

        return token_updated;

    }//load*****************************************************************************************




    public static byte[] hexToBytes(String s) {

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;

    }//****************************************



    public String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for ( int j = 0; j < bytes.length; j++ ) {

            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];

        }//***************************************

        return new String(hexChars);

    }//*************************************


 



}//class
