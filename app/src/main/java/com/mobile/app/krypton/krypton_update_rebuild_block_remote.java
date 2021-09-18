package com.mobile.app.krypton;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.spongycastle.util.encoders.Base64;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;


public class krypton_update_rebuild_block_remote extends SQLiteOpenHelper {


    final protected static char[] hexArray = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();



    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //When our database is new and we don't have any info yet we have to trust the server and download the info they have. It's a bit dangerous but once we have the info it can be verified.
    //We have less tests here to preform on blocks if our database is not up to date. If it is then we will not use this class we will use krypton_update_new_block_remote

    public krypton_update_rebuild_block_remote(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

        //SQLiteDatabase db = this.getWritableDatabase();


    }//**************************************




    boolean update(String[] transfer_id, String[] mining_id){//**************************************************************************

        //If the installation was a success.
        boolean token_updated = false;

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {

                System.out.println("[>>>] UPDATE TOKEN....REMOTE NEW BLOCK REBUILD");

                //Record the installation time.
                Long thisTick = System.currentTimeMillis();

                //The update ID.
                String token_update_id = "";

                //String block_idx = "";

                //make the new item.
                String[] move_item = transfer_id;


                //The new block.
                String ID = mining_id[0];
                String mining_date = mining_id[1];
                String mining_difficulty = mining_id[2];
                String mining_noose = mining_id[3];
                String mining_old_block = mining_id[4];
                String mining_new_block = mining_id[5];
                String prev_hash_id = "";//There is nothing old to insert here we are a new chain.
                String hash_id = mining_id[7];
                String sig_id = mining_id[8];
                String packagex = mining_id[9];
                String pubkid = mining_id[10];
                String pubsigid = mining_id[11];


                System.out.println("Test Mining... " + move_item[0] + " " + ID);

                boolean testm0 = false;
                boolean testm1 = false;
                boolean testm2 = false;
                boolean testm3 = false;
                boolean testm4 = false;
                boolean testm5 = false;
                boolean testm6 = false;
                boolean testm7 = false;
                boolean testm8 = false;



                //decode packages
                boolean is_in_package = false;
                boolean last_is_package = false;
                boolean this_is_package = false;
                JSONArray jsonObjectx_last = null;
                JSONArray jsonObjectx_this = null;
                int last_package_sizex = 0;
                int this_package_sizex = 0;

                if(packagex.length() == 0){this_is_package = false;}
                else{

                    this_is_package = true;

                    try{

                        JSONParser parserx = new JSONParser();
                        Object objx = parserx.parse(packagex);
                        jsonObjectx_this = (JSONArray) objx;

                        this_package_sizex = jsonObjectx_this.size();

                    }catch(Exception e){this_is_package = false;}

                }//***

                if (network.last_package_x.length() == 0) {last_is_package = false;}
                else {

                    last_is_package = true;

                    try {

                        JSONParser parserx = new JSONParser();
                        Object objx = parserx.parse(network.last_package_x);
                        jsonObjectx_last = (JSONArray) objx;

                        last_package_sizex = jsonObjectx_last.size();

                    } catch (Exception e) {last_is_package = false;}


                }//***




                try {

                    System.out.println("last package " + network.last_package_x);
                    System.out.println("this package " + packagex);

                    System.out.println("Last Package " + last_package_sizex);
                    System.out.println("This Package " + this_package_sizex);

                    //test size
                    if (this_package_sizex == 0 && last_package_sizex == 0) {testm0 = true;}//Normal operation
                    else if (this_package_sizex == 0 && last_package_sizex == 1) {testm0 = true;}//Package just ended.
                    else if (this_package_sizex == network.block_compress_size && last_package_sizex < 2) {//1 would be the end of the last package and 0 would be no package.

                        if (this_is_package) {

                            System.out.println("First package block");
                            System.out.println("id1 " + jsonObjectx_this.get(0));
                            System.out.println("id2 " + ID);

                            if (jsonObjectx_this.get(0).equals(hash_id)) {testm0 = true;}
                            else {testm0 = false;}

                        }//*****************
                        else {testm0 = false;}

                    }//***********************************************************************************
                    else if ((this_package_sizex + 1) == last_package_sizex && this_package_sizex <= network.block_compress_size) {

                        if (this_is_package && last_is_package) {

                            is_in_package = true;

                            System.out.println("Middle package block");
                            System.out.println("id0 " + jsonObjectx_last.get(1));
                            System.out.println("id1 " + jsonObjectx_this.get(0));
                            System.out.println("id2 " + ID);

                            if (jsonObjectx_this.get(0).equals(hash_id) && jsonObjectx_last.get(1).equals(hash_id)) {testm0 = true;}
                            else {testm0 = false;}

                        }//*****************
                        else {testm0 = false;}

                    }//**********************************************************************************************************
                    else {testm0 = false;}

                } catch (Exception e){testm0 = false;}

                //First block will not have package history.
                if (network.hard_token_count == 0) {testm0 = true;}

                System.out.println("testm0 " + testm0);





                try {

                    if (Integer.parseInt(ID) >= network.base_int && Integer.parseInt(ID) <= (network.base_int + network.hard_token_limit)) {testm1 = true;}

                } catch (Exception e) {testm1 = false;}

                System.out.println("testm1 " + testm1);





                System.out.println(mining_old_block);
                System.out.println(network.last_block_mining_idx);

                if (mining_old_block.equals(network.last_block_mining_idx)) {testm2 = true;}
                System.out.println("testm2 " + testm2);





                String encode = mining_date + mining_old_block + hash_id + mining_noose + packagex;

                System.out.println("mining_date " + mining_date);
                System.out.println("mining_old_block " + mining_old_block);
                System.out.println("hash_id " + hash_id);
                System.out.println("mining_noose " + mining_noose);

                //Test the block mining.


                try {


                    byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(encode.getBytes());
                    //System.out.println("SHA1 " + bytesToHex(sha256_1));

                    ByteBuffer buffer = ByteBuffer.wrap(sha256_1);
                    buffer.order(ByteOrder.BIG_ENDIAN);  // if you want little-endian
                    //long result = buffer.getLong();

                    BigInteger result = new BigInteger(1, sha256_1);

                    //System.out.println("value " + value);
                    System.out.println("result " + result);
                    System.out.println("network.difficultyx        " + network.difficultyx);
                    System.out.println("network.difficultyx_limit; " + network.difficultyx_limit);
                    System.out.println("SHA1 Mining " + bytesToHex(sha256_1));

                    encode = bytesToHex(sha256_1);

                    //long package_difficultyx = (long) 0;
                    BigInteger package_difficultyx = new BigInteger("0");

                    //if we are building a package then the other items do not need a hard difficulty.
                    if(is_in_package){package_difficultyx = network.difficultyx_limit;}
                    else{package_difficultyx = network.difficultyx;}

                    //if(result < package_difficultyx && result > 0){testm3 = true;}
                    //if(result < network.difficultyx_limit){testm3 = true;}

                    testm3 = true;


                } catch (Exception e) {e.printStackTrace();}

                System.out.println("testm3 " + testm3);




                //Test mining date

                try {

                    //Long block_date = (long) Long.parseLong(mining_date);
                    //System.out.println("block_date " + block_date);

                    //System.out.println("tokenx[3] " + tokenx[3]);
                    //Long old_block_date = (long) Long.parseLong(tokenx[3]);
                    //System.out.println("old_block_date " + old_block_date);

                    //Long time_now = (long) System.currentTimeMillis();
                    //System.out.println("time_now " + time_now);

                    //if(block_date > old_block_date && block_date < time_now){testm4 = true;}

                    testm4 = true;

                } catch (Exception e) {}

                System.out.println("testm4 " + testm4);




                System.out.println("Test ITEM... " + move_item[0]);

                //Test

                //Build the hash without mining info

                String build_hash = "";
                build_hash = build_hash + move_item[0];
                for (int loop = 3; loop < move_item.length; loop++){

                    build_hash = build_hash + move_item[loop];//save everything else

                }//*************************************************


                //Test signature
                System.out.println(build_hash);
                System.out.println("?");//Android doesn't seem to like the British pound sign.........

                try {


                    byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(build_hash.getBytes());

                    System.out.println("TEST HASH " + Base64.toBase64String(sha256_1w));
                    System.out.println("BASE HASH " + move_item[1]);
                    System.out.println("MINE HASH " + hash_id);

                    if (move_item[1].equals(Base64.toBase64String(sha256_1w)) && move_item[1].equals(hash_id)) {testm5 = true;}
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

                        //System.out.println(sigpk3.verify(signatureBytesx3));

                        if (sigpk3.verify(signatureBytesx3)) {testm6 = true;}
                        System.out.println("testm6 " + testm6);


                } catch (Exception e) {e.printStackTrace();}




                //Get the old key from old token match it to the new public key

                //String old_base58_key = tokenx[60];
                //System.out.println("old_base58_key " + old_base58_key);

                String base58 = "x";
                String new_base58 = "x";

                try {

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


                //System.out.println("old_base58_key " + old_base58_key);
                System.out.println("base58         " + base58);

                //System.out.println("old hash " + tokenx[1]);
                System.out.println("new hash " + move_item[1]);

                //If the hash is the same then don't bother.
                //if(base58.equals(old_base58_key) || move_item[1].equals(tokenx[1])){testm7 = true;}
                testm7 = true;
                System.out.println("testm7 " + testm7);





                try {

                    //Make sure item is new
                    //if(Long.parseLong(tokenx[3]) <= Long.parseLong(move_item[3])){testm8 = true;}
                    testm8 = true;

                } catch (Exception e) {e.printStackTrace();}

                System.out.println("testm8 " + testm8);

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

                System.out.println("last_test " + last_test);


                //The buffered block is used or useless get a new one.
                network.mining_block_ready = false;//network.mining_block_ready = 0;
                network.reset_mining_hash = true;


                if (last_test == 9) {//********************************************************************************************

                    //Start the transaction...
                    db.beginTransaction();


                    boolean delete_old = false;

                    try {

                        String query = ("DELETE FROM listings_db WHERE id=" + move_item[0] );

                        db.execSQL(query);
                        delete_old = true;

                        System.out.println("delete_old " + delete_old);

                    } catch (Exception e) {delete_old = false; db.endTransaction();}



                    System.out.println("UPDATE");


                    move_item[1] = hash_id;
                    move_item[2] = sig_id;


                    try {

                        ContentValues values = new ContentValues();

                        for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//**********************************

                            values.put(network.item_layout[loop1], move_item[(loop1)]); //

                        }//*********************************************************************************************

                        //Inserting Row
                        db.insert("listings_db", null, values);

                    } catch (Exception e) {e.printStackTrace(); db.endTransaction();}



                    try {

                        String query = ("DELETE FROM backup_db WHERE hash_id='" + hash_id + "'");
                        db.execSQL(query);

                        ContentValues values = new ContentValues();

                        for (int loop1 = 0; loop1 < network.listing_size; loop1++) {//********************************

                            values.put(network.item_layout[loop1], move_item[(loop1)]);

                        }//*******************************************************************************************

                        //Inserting Row
                        db.insert("backup_db", null, values);

                    } catch (Exception e) {e.printStackTrace(); db.endTransaction();}



                    try {


                        ContentValues values = new ContentValues();

                        values.put("link_id", move_item[0]);
                        values.put("mining_date", mining_date);
                        values.put("mining_difficulty", mining_difficulty);
                        values.put("mining_noose", mining_noose);
                        values.put("mining_old_block", mining_old_block);
                        values.put("mining_new_block", mining_new_block);
                        values.put("previous_hash_id", prev_hash_id);
                        values.put("hash_id", hash_id);
                        values.put("sig_id", sig_id);
                        values.put("package", packagex);
                        values.put("mining_pkey_link", pubkid);//mining_pkey_link
                        values.put("mining_sig", pubsigid);//mining_sig

                        System.out.println("hash_id " + hash_id);
                        System.out.println("sig_id " + sig_id);

                        //Inserting Row
                        db.insert("mining_db", null, values);


                        //If everything is successful then we commit.
                        db.setTransactionSuccessful();


                    } catch (Exception e) {e.printStackTrace(); db.endTransaction();}
                    finally{

                        //End transaction...
                        db.endTransaction();

                    }//*****




                    try {


                        String query = ("DELETE FROM searchx WHERE id=" + move_item[0] );
                        db.execSQL(query);

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
                        db.insert("searchx", null, values);


                    } catch (Exception e) {e.printStackTrace();}



                    token_update_id = hash_id;

                    network.time_block_added = System.currentTimeMillis();

                    //network.new_database_start == 0
                    if (!network.new_database_start) {mining.mining_stop = true;}//someone has found the block, go to the next task.


                    //krypton_database_driver.s.execute("DELETE FROM unconfirmed_db WHERE id=" + move_item[0]);
                    String query2 = ("DELETE FROM unconfirmed_db WHERE id=" + move_item[0]);
                    boolean delete_old2 = false;

                    try {

                        db.execSQL(query2);
                        delete_old2 = true;

                    } catch (Exception e) {delete_old2 = false;}

                    System.out.println("delete_old2 " + delete_old2);


                    token_updated = true;


                }//if************************************************************************************************************
                else {System.out.println("TEST1 TEST2 FAIL..."); token_updated = false; token_update_id = "0";}


                //Reset the unconfirmed counter.
                String query = ("SELECT id FROM unconfirmed_db");
                Cursor cursor = db.rawQuery(query, null);

                int rowCount5u = cursor.getCount();
                network.database_unconfirmed_total = rowCount5u;

                System.out.println("network.unconfirmed TOTAL " + network.database_unconfirmed_total);


                //Record the time it takes to install.
                network.dbxadd_longstamp = System.currentTimeMillis() - thisTick;

                System.out.println("DONE");


            } catch (Exception e) {e.printStackTrace(); network.mining_block_ready = false;}//network.mining_block_ready = 0;
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return token_updated;

    }//load





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
