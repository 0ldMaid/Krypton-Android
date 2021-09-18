package com.mobile.app.krypton;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.math.*;
import java.security.MessageDigest;
import java.security.KeyFactory;
import java.util.Arrays;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.security.PublicKey;
import org.spongycastle.util.encoders.Base64;


public class krypton_update_test_block_remote extends SQLiteOpenHelper {

    final protected static char[] hexArray = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    boolean token_updated = false;


    @Override
    public void onCreate(SQLiteDatabase db) {
        //This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This will upgrade tables, adding columns and new tables.
        //Note that existing columns will not be converted


    }


    //Here we have a different blockchain then the server. So we have to test both to see which one is better. First we download their blocks until we reach the fork point then we test both.

    krypton_update_test_block_remote() {//******

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//*****************************************




    boolean update(String[] transfer_id, String[] mining_id){//**************************************************************************

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("[>>>] UPDATE TOKEN....REMOTE TEST BLOCK");

                //Show the install time.
                Long thisTick = System.currentTimeMillis();

                //show if the update was a success.
                token_updated = false;

                //Record error rate.
                network.block_n_errors++;

                //Make the new item.
                String[] move_item = transfer_id;


                //The new block
                String ID = mining_id[0];
                String mining_date = mining_id[1];
                String mining_difficulty = mining_id[2];
                String mining_noose = mining_id[3];
                String mining_old_block = mining_id[4];
                String mining_new_block = mining_id[5];
                String prev_hash_id = mining_id[6];//Old block hash.
                String hash_id = mining_id[7];
                String sig_id = mining_id[8];
                String packagex = mining_id[9];
                String pubkid = mining_id[10];
                String pubsigid = mining_id[11];



                boolean testm0 = false;
                boolean testm1 = false;
                boolean testm2 = false;
                boolean testm3 = false;
                boolean testm4 = false;
                boolean testm5 = false;



                System.out.println("Test Mining... " + move_item[0] + " " + ID);
                if (move_item[0].equals(ID)) {testm0 = true;}

                System.out.println("testm0 " + testm0);



                //String encode = encode_date + old_block_mining_hash + new_block_hash + Integer.toString(noosex);
                String encode = mining_date + mining_old_block + hash_id + mining_noose + packagex;

                System.out.println("mining_date " + mining_date);
                System.out.println("mining_old_block " + mining_old_block);
                System.out.println("hash_id " + hash_id);
                System.out.println("mining_noose " + mining_noose);

                //Test the block mining


                try {

                    byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(encode.getBytes());
                    //System.out.println("SHA1 " + bytesToHex(sha256_1));

                    BigInteger result = new BigInteger(1, sha256_1);

                    //System.out.println("value " + value);
                    System.out.println("result " + result);
                    System.out.println("network.difficultyx " + network.difficultyx);
                    System.out.println("SHA1 Mining " + bytesToHex(sha256_1));

                    encode = bytesToHex(sha256_1);

                    int res = result.compareTo(network.difficultyx_limit);

                    if (res == -1) {testm1 = true;}
                    else{testm1 = false;}

                } catch (Exception e) {e.printStackTrace();}


                System.out.println("testm1 " + testm1);






                System.out.println("Test ITEM... " + move_item[0]);

                //Build the hash without mining info

                String build_hash = "";
                build_hash = build_hash + move_item[0];
                for (int loop = 3; loop < move_item.length; loop++){

                    build_hash = build_hash + move_item[loop];//Save everything else

                }//*************************************************

                //Test signature
                System.out.println(build_hash);

                try {


                    byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(build_hash.getBytes());

                    System.out.println("TEST HASH " + Base64.toBase64String(sha256_1w));
                    System.out.println("BASE HASH " + move_item[1]);

                    if(move_item[1].equals(Base64.toBase64String(sha256_1w))){testm2 = true;}
                    System.out.println("testm2 " + testm2);

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

                    if (sigpk3.verify(signatureBytesx3)) {testm3 = true;}
                    System.out.println("testm3 " + testm3);


                } catch (Exception e) {e.printStackTrace();}




                System.out.println("last_remote_mining_prev_idx " + network.last_remote_mining_prev_idx);
                System.out.println("mining_new_block            " + mining_new_block);
                System.out.println("mining_old_block            " + mining_old_block);


                if (network.last_remote_mining_prev_idx.equals(mining_new_block)) {testm4 = true;}//Working in order
                else if (network.last_remote_mining_prev_idx.equals("")) {testm4 = true;}//Start
                System.out.println("testm4 " + testm4);





                //If we find this item that means we have what we need.
                //Otherwise we add another block to our list
                testm5 = true;

                try {


                    String query = ("SELECT * FROM mining_db WHERE mining_new_block='" + mining_new_block + "' ORDER BY xd ASC LIMIT 1");
                    Cursor cursor = db.rawQuery(query, null);

                    cursor.moveToFirst();

                    //ix0 = 0;
                    long fork_block_time = 0l;
                    int fork_block = 0;
                    String fork_block_package = "";

                    while (!cursor.isAfterLast()) {

                        System.out.println("FOUND " + mining_new_block);

                        fork_block_time = cursor.getLong(cursor.getColumnIndex(cursor.getColumnName(2)));
                        fork_block = cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(0)));
                        fork_block_package = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(10)));

                        testm5 = false;

                        cursor.moveToNext();

                    }//while

                    System.out.println("testm5 " + testm5);

                    cursor.close();

                    if (!testm5) {

                        //Test chains

                        System.out.println("TEST CHAINS FOR THE BEST ONE.");

                        krypton_database_verify_chain testx = new krypton_database_verify_chain();
                        String testa = testx.test_blocks_a(fork_block,fork_block_time);
                        String testb = testx.test_blocks_b(fork_block_package,fork_block_time);

                        System.out.println("testa " + testa);
                        System.out.println("testb " + testb);

                        BigInteger bg1 = new BigInteger(testa);
                        BigInteger bg2 = new BigInteger(testb);

                        int res = bg1.compareTo(bg2);

                        if (res == 1) {

                            System.out.println("New chain is better.");

                            krypton_update_block_stale remotex3 = new krypton_update_block_stale();

                            boolean test = true;

                            while (test) {

                                test = remotex3.update();

                                System.out.println("network.last_block_id " + network.last_block_id);
                                System.out.println("mining_new_block      " + mining_new_block);
                                System.out.println("last_block_mining_idx " + network.last_block_mining_idx);

                                if(network.last_block_mining_idx.equals(mining_new_block)){break;}
                                if(Long.parseLong(network.last_block_timestamp) <= Long.parseLong(mining_date)){break;}

                            }//while

                            db.execSQL("DELETE FROM test_listings_db");

                        }//**********
                        else {

                            System.out.println("RESET TEST DB");

                            db.execSQL("DELETE FROM test_listings_db");

                            token_updated = false;

                            network.last_remote_mining_idx = "";//This is a copy of the remote peer's mining ID
                            network.last_remote_mining_prev_idx = "";//PREV

                        }//***


                    }//*********

                } catch (Exception e) {e.printStackTrace();}




                //If the fork block is back more then a set number of blocks we cannot update.
                //The chance that this would happen is low and the chance that someone is trying to fool us is high.
                //If someone has a blockchain this old they need to delete their app and redownload from a node.
                try {


                    String query = ("SELECT xd FROM test_listings_db");
                    Cursor cursor = db.rawQuery(query, null);

                    int test_size = cursor.getCount();

                    cursor.close();

                    System.out.println("test_size " + test_size);


                    if (test_size > network.test_db_fork_history) {

                        System.out.println("RESET TEST DB FORK IS TOO FAR BACK");

                        db.execSQL("DELETE FROM test_listings_db");

                        token_updated = false;

                        network.last_remote_mining_idx = "";//This is a copy of the remote peer's mining ID
                        network.last_remote_mining_prev_idx = "";//PREV

                    }//******************************************


                } catch (Exception e) {e.printStackTrace();}





                int last_test = 0;

                if (testm0) {last_test++;}
                if (testm1) {last_test++;}
                if (testm2) {last_test++;}
                if (testm3) {last_test++;}
                if (testm4) {last_test++;}
                if (testm5) {last_test++;}


                System.out.println("last_test " + last_test);


                if (last_test == 6) {//********************************************************************************************

                    ContentValues values = new ContentValues();

                    values.put("link_id", Integer.parseInt(ID));
                    values.put("mining_date", mining_date);
                    values.put("mining_difficulty", mining_difficulty);
                    values.put("mining_noose", mining_noose);
                    values.put("mining_old_block", mining_old_block);
                    values.put("mining_new_block", mining_new_block);
                    values.put("previous_hash_id", prev_hash_id);
                    values.put("mining_hash_id", hash_id);
                    values.put("mining_sig_id", sig_id);
                    values.put("package", packagex);
                    values.put("mining_pkey_link", pubkid); //mining_pkey_link
                    values.put("mining_sig", pubsigid); //mining_sig


                    System.out.println("TEST UPDATE " + move_item[0]);

                    //Build listing
                    for(int loop1 = 0; loop1 < network.listing_size; loop1++){//********

                        values.put(network.item_layout[loop1], move_item[loop1]);

                    }//*****************************************************************

                    //Inserting Row
                    db.insert("test_listings_db", null, values);


                    //reset this so the peer knows what ID to go for next.
                    network.last_remote_mining_idx = mining_new_block;
                    network.last_remote_mining_prev_idx = mining_old_block;

                    System.out.println("last_remote_mining_idx " + network.last_remote_mining_idx);

                    token_updated = true;


                }//if************************************************************************************************************
                else {System.out.println("TESTS FAIL..."); token_updated = false;}



                System.out.println("DONE");


            } catch (Exception e) {e.printStackTrace();}
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
