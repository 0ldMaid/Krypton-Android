package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;


public class krypton_database_get_token_pubkey extends SQLiteOpenHelper{




    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }



    //Here we are getting the public key of a user to make sure they are an owner of a token before we let their mined block enter our chain.

    public krypton_database_get_token_pubkey(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//*********************************




    //These are tokens the program is requesting.

    public String getTokenKey(String id){//**************************************************************************

        String token_ssp2 = "";

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                int idx = 0;

                try {

                    idx = Integer.parseInt(id);

                } catch(Exception e) {}


                System.out.println("Load Token Key..." );

                String query = ("SELECT owner_id FROM listings_db WHERE id=" + idx + " LIMIT 1");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    token_ssp2 = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));

                    cursor.moveToNext();

                }//while


                cursor.close();


            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return token_ssp2;

    }//load





    //These are tokens the program is requesting.
    //The miner has to give us the ID of the lowest ID token they have. So we can match it to the mining list.
    //If they don't provide us with the lowest ID they have then we reject the test.

    public Integer getMinerTokenList(String id1, String pubkey){//**************************************************************************

        int countx = 0;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("Load Token Count..." );

                String base58 = pubkey;

                int len = base58.length();
                byte[] data = new byte[len / 2];
                for (int i = 0; i < len; i += 2) {

                    data[i / 2] = (byte) ((Character.digit(base58.charAt(i), 16) << 4) + Character.digit(base58.charAt(i + 1), 16));

                }//*******************************


                byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(data);

                byte[] a = "=".getBytes();
                byte[] b = sha256_1;

                byte[] result = new byte[a.length + b.length];
                System.arraycopy(a, 0, result, 0, a.length);
                System.arraycopy(b, 0, result, a.length, b.length);


                base58 = Base58Encode.encode(result);
                System.out.println("base58 miner " + base58);


                String query = ("SELECT id FROM listings_db WHERE seller_id='" + base58 + "' ORDER BY id ASC");
                Cursor cursor = db.rawQuery(query, null);

                countx = cursor.getCount();

                cursor.moveToFirst();

                String id2 = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0)));

                System.out.println("id1 " + id1);
                System.out.println("id2 " + id2);

                if(id1.equals(id2)){System.out.println("Good...");}
                else{System.out.println("Error...");}

                cursor.close();


            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
            finally {

            network.database_in_use = false;

        }

        return countx;

    }//load




    //Each miner should only be finding a block every so often if they are finding too many we don't allow it.
    //There is absolutely no need for centralized mining in this system.

    public boolean getMiningKeyList(String pubkid){

        boolean foundp = false;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("Load Token From Mining DB..." );

                String query = ("SELECT mining_pkey_link FROM mining_db ORDER BY xd DESC LIMIT " + network.mining_distribution_test);
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                while(!cursor.isAfterLast()){

                    if(pubkid.equals(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))))){foundp = true; break;}

                    cursor.moveToNext();

                }//while

                cursor.close();


            }catch(Exception e){e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return foundp;

    }//**********************************



 
}//get
