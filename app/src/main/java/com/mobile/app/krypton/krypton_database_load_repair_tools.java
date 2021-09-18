package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;

//this class is loaded only a few times during operation, on start up and then if something is changed.
//all the loading is done here that isn't related to the blockchain so that krypton_database_load doesn't have to work as hard.

public class krypton_database_load_repair_tools extends SQLiteOpenHelper {


    //int ix0 = 0;
    //int ix1 = 0;
    //int ix2 = 0;

    //int database_test = 0;



    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted

    }


    //This class is called only on start up and on major changes to info added.

    krypton_database_load_repair_tools(){//**************************************************************************


        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

        SQLiteDatabase db = this.getWritableDatabase();

        network.database_in_use = true;//network.database_in_use = 1;

        //db.execSQL("DELETE FROM searchx WHERE id != 10000");


        //This was used during testing to repair blockchain errors.
        //krypton_database_test_chain1 test1 = new krypton_database_test_chain1();
        //krypton_database_load_testing test = new krypton_database_load_testing();


        System.out.println("[>>>] LOAD NETWORK");


        boolean x3 = false;

        while(x3){

            krypton_update_block_stale testx = new krypton_update_block_stale();
            boolean test1 = testx.update();

            krypton_database_load loadx = new krypton_database_load();
            loadx.load();

            if(network.last_block_mining_idx.equals("0001AE0043EF58799B86A76BA2BFB7E7F47557232F367CFA0EF3A67CEC13581B")){x3 = false;}

            try{Thread.sleep(100);} catch (InterruptedException e){}

        }//********




        if(1 == 2) {
            try {

                //db.execSQL("DELETE FROM listings_db where xd=160129");

                //String query = ("SELECT id FROM listings_db GROUP BY id HAVING COUNT(*) > 1");
                String query = ("SELECT * FROM listings_db WHERE id = 116778");
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    System.out.println("rsgsx " + cursor.getString(cursor.getColumnIndex("xd")));
                    System.out.println("rsgsx " + cursor.getString(cursor.getColumnIndex("hash_id")));
                    cursor.moveToNext();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        if(1 == 2) {
            try {


                krypton_database_get_token token = new krypton_database_get_token();

                for (int loop = 103000; loop < (100000 + network.hard_token_limit); loop++) {


                    System.gc();

                    String query = ("SELECT * FROM mining_db WHERE link_id = 107267 ORDER BY xd DESC");
                    Cursor cursor = db.rawQuery(query, null);

                    if (cursor.getCount() == 0) {

                        System.out.println("We don't have: " + loop);

                        String[] tokenx = token.getToken(Integer.toString(107267));

                        krypton_update_token update = new krypton_update_token(tokenx);

                        break;

                    }//************************
                    else {
                        System.out.println(loop);
                        //break;
                    }


                }//********************************************************


            } catch (Exception e) {
                e.printStackTrace();
            }
        }




        try{

            //krypton_replace_block_stale xxxxx = new krypton_replace_block_stale();
            //xxxxx.update();

            String query = ("SELECT * FROM mining_db ORDER BY xd ASC");
            //String query = ("SELECT xd,link_id FROM mining_db GROUP BY link_id ORDER BY xd DESC LIMIT " + network.hard_token_limit);
            //String query = ("SELECT * FROM mining_db WHERE xd > 22880 LIMIT 50");
            //String query = ("SELECT * FROM unconfirmed_db ORDER BY xd ASC");
            //String query = ("SELECT * FROM listings_db WHERE hash_id='Qc2nu1jhpNHwX+xwE2LKLhY3cJI1IJA6gHsKPUu/bnU='");
            Cursor cursor = db.rawQuery(query, null);

            String[] columnNames = cursor.getColumnNames();

            for (int loop = 0; loop < columnNames.length; loop++){

                System.out.println(columnNames[loop]);

            }//***************************************************


            int testx = 100000;
            String tests1 = "";
            String tests2 = "";

            System.out.println("TEST " + cursor.getCount());

            System.exit(0);

            cursor.moveToFirst();

            tests1 = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5)));
            tests2 = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6)));

            cursor.moveToNext();


            while(!cursor.isAfterLast()){

                int test = Integer.parseInt(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));

                //if(testx == test){}
                //else{System.out.println("Error " + testx + " " + test); System.exit(0);}


                //System.out.println("rsx ");
                System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))));
                System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
                System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));
                System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))));
                System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(4))));
                System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5))));
                System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6))));
                System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(7))));
                System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(8))));
                //System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(9))));
                //System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(10))));
                //System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(11))));
                //System.out.println("rsx " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(12))));
                //System.out.println("rsx ");


                tests1 = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5)));

                if(tests1.equals(tests2)){System.out.println("Good " + tests1 + " " + tests2);}
                else{System.out.println("Error " + tests1 + " " + tests2); System.exit(0);}

                tests2 = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6)));

                tests1 = tests2;

                testx++;


                cursor.moveToNext();


            }//while


            //byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(hashx1.getBytes());
            //System.out.println("TEST HASH " + Base64.toBase64String(sha256_1w));


            cursor.close();


        }catch(Exception e){e.printStackTrace();}






        if(1 == 2) {

            db.beginTransaction();
            try {

                //(xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, mining_date LONG, mining_difficulty TEXT, mining_noose TEXT, mining_old_block TEXT, mining_new_block TEXT, previous_hash_id TEXT, hash_id TEXT, sig_id TEXT, package TEXT, mining_pkey_link TEXT, mining_sig TEXT)";
                //xd, link_id, mining_date, mining_difficulty, mining_noose, mining_old_block, mining_new_block, previous_hash_id, hash_id, sig_id, package, mining_pkey_link, mining_sig

                System.out.println("step 0");
                db.execSQL("CREATE TEMPORARY TABLE backup (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, mining_date LONG, mining_difficulty TEXT, mining_noose TEXT, mining_old_block TEXT, mining_new_block TEXT, previous_hash_id TEXT, hash_id TEXT, sig_id TEXT, package TEXT, mining_pkey_link TEXT, mining_sig TEXT)");
                System.out.println("step 1");
                db.execSQL("INSERT INTO backup SELECT xd, link_id, mining_date, mining_difficulty, mining_noose, mining_old_block, mining_new_block, previous_hash_id, hash_id, sig_id, package, package, package FROM mining_db");
                //System.out.println("step 2");
                db.execSQL("DROP TABLE mining_db");
                //db.execSQL("DROP TABLE listings_db2;");
                //System.out.println("step 3");
                db.execSQL("CREATE TABLE mining_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, mining_date LONG, mining_difficulty TEXT, mining_noose TEXT, mining_old_block TEXT, mining_new_block TEXT, previous_hash_id TEXT, hash_id TEXT, sig_id TEXT, package TEXT, mining_pkey_link TEXT, mining_sig TEXT)");
                System.out.println("step 4");
                db.execSQL("INSERT INTO mining_db SELECT xd, link_id, mining_date, mining_difficulty, mining_noose, mining_old_block, mining_new_block, previous_hash_id, hash_id, sig_id, package, mining_pkey_link, mining_sig FROM backup");
                //System.out.println("step 5");
                //db.execSQL("DROP TABLE mining_db2");
                db.execSQL("DROP TABLE backup;");

                db.setTransactionSuccessful();

            } finally {
                db.endTransaction();
            }

        }//*********





        db.close();
        System.out.println("finally block executed");

        network.database_in_use = false;


    }//load



}//load network
