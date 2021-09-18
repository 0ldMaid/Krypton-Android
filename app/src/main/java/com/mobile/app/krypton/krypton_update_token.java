package com.mobile.app.krypton;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.util.Arrays;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.PublicKey;
import org.spongycastle.util.encoders.Base64;





public class krypton_update_token extends SQLiteOpenHelper {

    //String cutting class.
    network_trim trimx = new network_trim();


    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //Here the user of this app has changed something in their token and want to update it. We add it to the send_buffer and then when the system is ready it will send it to the server.

    krypton_update_token(String[] transfer_id){//**************************************************************************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

        try {


            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {

                System.out.println("[>>>] UPDATE TOKEN....");

                String build_hash = "";

                String[] move_item = transfer_id;

                //We need to cut the strings of the item if they are too long. We don't want someone making a listing that is too big.
                move_item = trimx.trim(move_item);


                System.out.println("Update ITEM... " + move_item[0]);

                //Test.
                boolean test1 = false;
                boolean test2 = false;
                boolean test3 = false;



                //Build the hash without mining info.

                build_hash = move_item[0];
                for (int loop = 3; loop < move_item.length; loop++){

                    build_hash = build_hash + move_item[loop];//save everything else

                }//*************************************************

                System.out.println(build_hash);

                try {


                    byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(build_hash.getBytes());

                    System.out.println("NEW HASH " + Base64.toBase64String(sha256_1w));
                    System.out.println("OLD HASH " + move_item[1]);

                    if(move_item[1].equals(Base64.toBase64String(sha256_1w))){test1 = true;}


                    byte[] keyp = Base64.decode(network.prv_key_id);//network.settingsx[4]
                    byte[] message = Base64.toBase64String(sha256_1w).getBytes("UTF8");

                    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyp);
                    KeyFactory fact = KeyFactory.getInstance("RSA");
                    PrivateKey prk = fact.generatePrivate(keySpec);
                    Arrays.fill(keyp, (byte) 0);


                    Signature sig = Signature.getInstance("SHA1WithRSA");//MD5WithRSA
                    sig.initSign(prk);
                    sig.update(message);
                    byte[] signatureBytes = sig.sign();

                    String signx = Base64.toBase64String(signatureBytes);

                    System.out.println("");
                    System.out.println("Singature: " + signx);
                    System.out.println("");



                    byte[] keyxb3 = Base64.decode(move_item[4]);

                    X509EncodedKeySpec keySpecx3 = new X509EncodedKeySpec(keyxb3);
                    KeyFactory factx3 = KeyFactory.getInstance("RSA");
                    PublicKey pubx3 = factx3.generatePublic(keySpecx3);
                    Arrays.fill(keyxb3, (byte) 0);

                    Signature sigpk3 = Signature.getInstance("SHA1WithRSA");//MD5WithRSA
                    byte[] messagex3 = Base64.toBase64String(sha256_1w).getBytes("UTF8");

                    byte[] signatureBytesx3 = Base64.decode(signx);

                    sigpk3.initVerify(pubx3);
                    sigpk3.update(messagex3);

                    if(sigpk3.verify(signatureBytesx3)){test2 = true;}


                } catch (Exception e) {e.printStackTrace();}



                System.out.println("Test 1 " + test1);
                System.out.println("Test 2 " + test2);

                test1 = true;
                test2 = true;

                //item cannot have null parts

                test3 = true;

                for (int loop = 0; loop < move_item.length; loop++) {

                    try {

                        if(move_item[loop].length() < 1){}

                    } catch (Exception e) {test3 = false; break;}

                }//**************************************************


                System.out.println("Test 3 " + test3);




                if (test1 && test2) {//******************************************************************************************

                    if (test3) {


                        System.out.println("ADD TO BUFFER");

                        db.execSQL("DELETE FROM send_buffer where id=" + move_item[0]);

                        ContentValues values = new ContentValues();

                        System.out.println("PS UPDATE " + move_item[0]);

                        values.put("id", Integer.parseInt(move_item[0]));

                        for (int loop1 = 1; loop1 < network.listing_size; loop1++) {//********************************

                            System.out.println("PS UPDATE " + move_item[(loop1)]);
                            values.put(network.item_layout[loop1], move_item[(loop1)]); //

                        }//*******************************************************************************************

                        db.insert("send_buffer", null, values);


                    }//iftest3
                    else {System.out.println("TEST3 FAIL...");}

                }//if*********************************************************************************************************************************
                else {System.out.println("TEST1 TEST2 FAIL...");}


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


    }//load



 
}//class
