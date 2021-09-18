package com.mobile.app.krypton;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import java.security.KeyFactory;
import java.util.Arrays;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.security.PublicKey;
import org.spongycastle.util.encoders.Base64;





public class krypton_update_token_remote extends SQLiteOpenHelper {

    network_trim trimx = new network_trim();

    boolean token_updated = false;



    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Here we are adding a new item to the unconfirmed database from a peer.
    //Here we are getting a new buffer block from the client because they want to update something.
    //We do basic tests and add it to the unconfirmed list.

    public krypton_update_token_remote(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//*********************************



    boolean update(String[] transfer_id, String[] old_token){//**************************************************************************

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("[>>>] UPDATE TOKEN REMOTE....");

                //Show if update is successful.
                token_updated = false;

                //Hash test.
                String build_hash = "";

                //Get the new token.
                String[] move_item = transfer_id;

                //Trim strings.
                move_item = trimx.trim(move_item);

                System.out.println("Update ITEM... " + move_item[0]);

                //Get the old token.
                String[] tokenx = old_token;


                //Tests.
                boolean test1 = false;
                boolean test2 = false;
                boolean test3 = false;
                boolean test4 = false;
                boolean test5 = false;




                //Build the hash without mining info.

                build_hash = build_hash + move_item[0];

                for (int loop = 3; loop < move_item.length; loop++){

                    build_hash = build_hash + move_item[loop];//Save everything else

                }//*************************************************

                System.out.println(build_hash);

                try {


                    byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(build_hash.getBytes());

                    System.out.println("NEW HASH " + Base64.toBase64String(sha256_1w));
                    System.out.println("BSE HASH " + move_item[1]);

                    if(move_item[1].equals(Base64.toBase64String(sha256_1w))){test1 = true;}
                    else{test1 = false;}

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

                    if (sigpk3.verify(signatureBytesx3)) {test2 = true;}
                    else {test2 = false;}

                } catch (Exception e) {e.printStackTrace();}

                System.out.println("Test 1 " + test1);
                System.out.println("Test 2 " + test2);




                //Make sure item is new.
                if (Long.parseLong(tokenx[3]) <= Long.parseLong(move_item[3])) {test3 = true;}
                else {test3 = false;}

                System.out.println("Test 3 " + test3);



                String old_base58_key = tokenx[60];
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

                System.out.println("old_base58_key " + old_base58_key);
                System.out.println("base58 " + base58);

                System.out.println("old hash " + tokenx[1]);
                System.out.println("new hash " + move_item[1]);

                if (move_item[1].equals(tokenx[1])) {test4 = true;}
                else if (base58.equals(old_base58_key)) {test4 = true;}
                else if (new_base58.equals(old_base58_key)) {test4 = true;}
                else {test4 = false;}

                System.out.println("KEY IDs " + test4);

                System.out.println("Test 4 " + test4);


                //make sure item is new, to prevent replay attacks and also to prevent spam
                //need to modify to check for old and new items

                try {


                    System.out.println("time now    " + System.currentTimeMillis());
                    System.out.println("last update " + move_item[3]);
                    System.out.println("old update  " + tokenx[3]);
                    System.out.println("time        " + (System.currentTimeMillis() - Long.parseLong(move_item[3])));

                    if (Long.parseLong(tokenx[3]) < Long.parseLong(move_item[3])) {test5 = true;}//update
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

                        if ((time_now - old_block_date) > network.block_date_spam) {test5 = true;}//604800000 = 1 week

                    }//chain move

                } catch (Exception e) {e.printStackTrace();}


                System.out.println("Test 5 " + test5);



                int last_test = 0;

                if (test1) {last_test++;}
                if (test2) {last_test++;}
                if (test3) {last_test++;}
                if (test4) {last_test++;}
                if (test5) {last_test++;}

                System.out.println("last_test " + last_test);


                if (last_test == 5) {//********************************************************************************************

                    System.out.println("UPDATE");


                    db.execSQL("DELETE FROM unconfirmed_db where id=" + move_item[0]);

                    ContentValues values = new ContentValues();

                    System.out.println("PS UPDATE " + move_item[0]);
                    values.put("id", Integer.parseInt(move_item[0])); //

                    for (int loop1 = 1; loop1 < network.listing_size; loop1++) {//********************************

                        System.out.println("PS UPDATE " + move_item[(loop1)]);
                        values.put(network.item_layout[loop1], move_item[(loop1)]); //

                    }//*******************************************************************************************

                    db.insert("unconfirmed_db", null, values);

                    token_updated = true;


                }//if************************************************************************************************************
                else {System.out.println("TEST1 TEST2 FAIL...");}


                //Reload database info.
                String query = ("SELECT id FROM unconfirmed_db");
                Cursor cursor = db.rawQuery(query, null);

                int rowCount5u = cursor.getCount();
                network.database_unconfirmed_total = rowCount5u;

                System.out.println("network.unconfirmed TOTAL " + network.database_unconfirmed_total);

                cursor.close();


                System.out.println("DONE");


            } catch (Exception e) {e.printStackTrace(); db.close();}
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




 

}//class
