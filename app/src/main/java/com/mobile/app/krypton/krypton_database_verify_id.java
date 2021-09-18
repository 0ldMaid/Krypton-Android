package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import java.util.Arrays;
import org.spongycastle.util.encoders.Base64;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;





public class krypton_database_verify_id extends SQLiteOpenHelper {



    @Override
    public void onCreate(SQLiteDatabase db) {
        //This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This will upgrade tables, adding columns and new tables.
        //Note that existing columns will not be converted


    }


    //This is a test system before we load the item to be mined. We make sure it's going to pass the tests before we waste time searching for a mining hash.

    krypton_database_verify_id(String id){//**************************************************************************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                String move_item[] = new String[network.listing_size];


                System.out.println("Load ITEM... VERIFY" );

                String query = ("SELECT * FROM listings_db WHERE id=" + id);
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {


                    move_item[0] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));

                    for (int loop = 1; loop < network.listing_size; loop++){

                        try {

                            move_item[loop] = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(loop + 1)));

                        } catch (Exception e) {

                            e.printStackTrace();

                        }//*****************

                    }//*****************************************************

                    cursor.moveToNext();


                }//while

                cursor.close();



                //Build the hash without mining info
                String build_hash = "";

                build_hash = move_item[0];
                for (int loop = 3; loop < move_item.length; loop++) {

                    build_hash = build_hash + move_item[loop];//save everything else

                }//**************************************************

                System.out.println(build_hash);

                try {


                    byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(build_hash.getBytes());

                    System.out.println("TEST HASH " + Base64.toBase64String(sha256_1w));
                    System.out.println("DBH HASH " + move_item[1]);


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


                    boolean text_hash = sigpk3.verify(signatureBytesx3);

                    System.out.println("text_hash " + text_hash);



                    if (!text_hash) {

                        db.execSQL("DELETE FROM unconfirmed_db WHERE id=" + move_item[0]);

                    }//if************


                } catch (Exception e) {e.printStackTrace();}


                System.out.println("Committed the transaction");


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
