package com.mobile.app.krypton;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import java.io.*;
import java.security.*;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyPair;
import org.spongycastle.util.encoders.Base64;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;



public class krypton_database_driver extends SQLiteOpenHelper{

    String base58 = "";

    KeyPair keyPair;
    
    //saving directory
    //static File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

    //All Static variables
    //Database Version
    static final int DATABASE_VERSION = 1;

    //Database Name
    static final String DATABASE_NAME = network.idx;



    //This is the database drive start up class. Also if the app is just installed it will install the database tables.

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        //This is the mining database that has to be in order like bitcoin.
        String t1 = "CREATE TABLE mining_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, mining_date LONG, mining_difficulty TEXT, mining_noose TEXT, mining_old_block TEXT, mining_new_block TEXT, previous_hash_id TEXT, hash_id TEXT, sig_id TEXT, package TEXT, mining_pkey_link TEXT, mining_sig TEXT)";
        db.execSQL(t1);

        //This is the listings database that stores the info for each listing.
        String t2 = "CREATE TABLE listings_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, hash_id TEXT, sig_id TEXT, date_id TEXT, owner_id TEXT, owner_rating TEXT, currency TEXT, custom_template TEXT, custom_1 TEXT, custom_2 TEXT, custom_3 TEXT, item_errors TEXT, item_date_listed TEXT, item_date_listed_day TEXT, item_date_listed_int TEXT, item_hits TEXT, item_confirm_code TEXT, item_confirmed TEXT, item_cost TEXT, item_description TEXT, item_id TEXT, item_price TEXT, item_weight TEXT, item_listing_id TEXT, item_notes TEXT, item_package_d TEXT, item_package_l TEXT, item_package_w TEXT, item_part_number TEXT, item_title TEXT, item_title_url TEXT, item_type TEXT, item_search_1 TEXT, item_search_2 TEXT, item_search_3 TEXT, item_site_id TEXT, item_site_url TEXT, item_picture_1 TEXT, item_total_on_hand TEXT, sale_payment_address TEXT, sale_payment_type TEXT, sale_fees TEXT, sale_id TEXT, sale_seller_id TEXT, sale_status TEXT, sale_tax TEXT, sale_shipping_company TEXT, sale_shipping_in TEXT, sale_shipping_out TEXT, sale_source_of_sale TEXT, sale_total_sale_amount TEXT, sale_tracking_number TEXT, sale_transaction_id TEXT, sale_transaction_info TEXT, seller_address_1 TEXT, seller_address_2 TEXT, seller_address_city TEXT, seller_address_state TEXT, seller_address_zip TEXT, seller_address_country TEXT, seller_id TEXT, seller_ip TEXT, seller_email TEXT, seller_first_name TEXT, seller_last_name TEXT, seller_notes TEXT, seller_phone TEXT, seller_logo TEXT, seller_url TEXT)";
        db.execSQL(t2);

        //This is the database that we use when we are NOT a full node.
        String t3 = "CREATE TABLE listings_lite_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, hash_id TEXT, sig_id TEXT, date_id TEXT, owner_id TEXT, owner_rating TEXT, currency TEXT, custom_template TEXT, custom_1 TEXT, custom_2 TEXT, custom_3 TEXT, item_errors TEXT, item_date_listed TEXT, item_date_listed_day TEXT, item_date_listed_int TEXT, item_hits TEXT, item_confirm_code TEXT, item_confirmed TEXT, item_cost TEXT, item_description TEXT, item_id TEXT, item_price TEXT, item_weight TEXT, item_listing_id TEXT, item_notes TEXT, item_package_d TEXT, item_package_l TEXT, item_package_w TEXT, item_part_number TEXT, item_title TEXT, item_title_url TEXT, item_type TEXT, item_search_1 TEXT, item_search_2 TEXT, item_search_3 TEXT, item_site_id TEXT, item_site_url TEXT, item_picture_1 TEXT, item_total_on_hand TEXT, sale_payment_address TEXT, sale_payment_type TEXT, sale_fees TEXT, sale_id TEXT, sale_seller_id TEXT, sale_status TEXT, sale_tax TEXT, sale_shipping_company TEXT, sale_shipping_in TEXT, sale_shipping_out TEXT, sale_source_of_sale TEXT, sale_total_sale_amount TEXT, sale_tracking_number TEXT, sale_transaction_id TEXT, sale_transaction_info TEXT, seller_address_1 TEXT, seller_address_2 TEXT, seller_address_city TEXT, seller_address_state TEXT, seller_address_zip TEXT, seller_address_country TEXT, seller_id TEXT, seller_ip TEXT, seller_email TEXT, seller_first_name TEXT, seller_last_name TEXT, seller_notes TEXT, seller_phone TEXT, seller_logo TEXT, seller_url TEXT)";
        db.execSQL(t3);

        //This is where we store listings when we are testing for a fork in the chian.
        String t4 = "CREATE TABLE test_listings_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, mining_date TEXT, mining_difficulty TEXT, mining_noose TEXT, mining_old_block TEXT, mining_new_block TEXT, previous_hash_id TEXT, mining_hash_id TEXT, mining_sig_id TEXT, package TEXT, mining_pkey_link TEXT, mining_sig TEXT, id TEXT, hash_id TEXT, sig_id TEXT, date_id TEXT, owner_id TEXT, owner_rating TEXT, currency TEXT, custom_template TEXT, custom_1 TEXT, custom_2 TEXT, custom_3 TEXT, item_errors TEXT, item_date_listed TEXT, item_date_listed_day TEXT, item_date_listed_int TEXT, item_hits TEXT, item_confirm_code TEXT, item_confirmed TEXT, item_cost TEXT, item_description TEXT, item_id TEXT, item_price TEXT, item_weight TEXT, item_listing_id TEXT, item_notes TEXT, item_package_d TEXT, item_package_l TEXT, item_package_w TEXT, item_part_number TEXT, item_title TEXT, item_title_url TEXT, item_type TEXT, item_search_1 TEXT, item_search_2 TEXT, item_search_3 TEXT, item_site_id TEXT, item_site_url TEXT, item_picture_1 TEXT, item_total_on_hand TEXT, sale_payment_address TEXT, sale_payment_type TEXT, sale_fees TEXT, sale_id TEXT, sale_seller_id TEXT, sale_status TEXT, sale_tax TEXT, sale_shipping_company TEXT, sale_shipping_in TEXT, sale_shipping_out TEXT, sale_source_of_sale TEXT, sale_total_sale_amount TEXT, sale_tracking_number TEXT, sale_transaction_id TEXT, sale_transaction_info TEXT, seller_address_1 TEXT, seller_address_2 TEXT, seller_address_city TEXT, seller_address_state TEXT, seller_address_zip TEXT, seller_address_country TEXT, seller_id TEXT, seller_ip TEXT, seller_email TEXT, seller_first_name TEXT, seller_last_name TEXT, seller_notes TEXT, seller_phone TEXT, seller_logo TEXT, seller_url TEXT)";
        db.execSQL(t4);

        //This the database of listings that are waiting to be added to the chain.
        String t5 = "CREATE TABLE unconfirmed_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, hash_id TEXT, sig_id TEXT, date_id TEXT, owner_id TEXT, owner_rating TEXT, currency TEXT, custom_template TEXT, custom_1 TEXT, custom_2 TEXT, custom_3 TEXT, item_errors TEXT, item_date_listed TEXT, item_date_listed_day TEXT, item_date_listed_int TEXT, item_hits TEXT, item_confirm_code TEXT, item_confirmed TEXT, item_cost TEXT, item_description TEXT, item_id TEXT, item_price TEXT, item_weight TEXT, item_listing_id TEXT, item_notes TEXT, item_package_d TEXT, item_package_l TEXT, item_package_w TEXT, item_part_number TEXT, item_title TEXT, item_title_url TEXT, item_type TEXT, item_search_1 TEXT, item_search_2 TEXT, item_search_3 TEXT, item_site_id TEXT, item_site_url TEXT, item_picture_1 TEXT, item_total_on_hand TEXT, sale_payment_address TEXT, sale_payment_type TEXT, sale_fees TEXT, sale_id TEXT, sale_seller_id TEXT, sale_status TEXT, sale_tax TEXT, sale_shipping_company TEXT, sale_shipping_in TEXT, sale_shipping_out TEXT, sale_source_of_sale TEXT, sale_total_sale_amount TEXT, sale_tracking_number TEXT, sale_transaction_id TEXT, sale_transaction_info TEXT, seller_address_1 TEXT, seller_address_2 TEXT, seller_address_city TEXT, seller_address_state TEXT, seller_address_zip TEXT, seller_address_country TEXT, seller_id TEXT, seller_ip TEXT, seller_email TEXT, seller_first_name TEXT, seller_last_name TEXT, seller_notes TEXT, seller_phone TEXT, seller_logo TEXT, seller_url TEXT)";
        db.execSQL(t5);

        //This is where we save our updates before they are sent to the network.
        String t6 = "CREATE TABLE send_buffer (xd INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, hash_id TEXT, sig_id TEXT, date_id TEXT, owner_id TEXT, owner_rating TEXT, currency TEXT, custom_template TEXT, custom_1 TEXT, custom_2 TEXT, custom_3 TEXT, item_errors TEXT, item_date_listed TEXT, item_date_listed_day TEXT, item_date_listed_int TEXT, item_hits TEXT, item_confirm_code TEXT, item_confirmed TEXT, item_cost TEXT, item_description TEXT, item_id TEXT, item_price TEXT, item_weight TEXT, item_listing_id TEXT, item_notes TEXT, item_package_d TEXT, item_package_l TEXT, item_package_w TEXT, item_part_number TEXT, item_title TEXT, item_title_url TEXT, item_type TEXT, item_search_1 TEXT, item_search_2 TEXT, item_search_3 TEXT, item_site_id TEXT, item_site_url TEXT, item_picture_1 TEXT, item_total_on_hand TEXT, sale_payment_address TEXT, sale_payment_type TEXT, sale_fees TEXT, sale_id TEXT, sale_seller_id TEXT, sale_status TEXT, sale_tax TEXT, sale_shipping_company TEXT, sale_shipping_in TEXT, sale_shipping_out TEXT, sale_source_of_sale TEXT, sale_total_sale_amount TEXT, sale_tracking_number TEXT, sale_transaction_id TEXT, sale_transaction_info TEXT, seller_address_1 TEXT, seller_address_2 TEXT, seller_address_city TEXT, seller_address_state TEXT, seller_address_zip TEXT, seller_address_country TEXT, seller_id TEXT, seller_ip TEXT, seller_email TEXT, seller_first_name TEXT, seller_last_name TEXT, seller_notes TEXT, seller_phone TEXT, seller_logo TEXT, seller_url TEXT)";
        db.execSQL(t6);

        //This is the backup of listings in case we have to do a rollback.
        String t7 = "CREATE TABLE backup_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, hash_id TEXT, sig_id TEXT, date_id TEXT, owner_id TEXT, owner_rating TEXT, currency TEXT, custom_template TEXT, custom_1 TEXT, custom_2 TEXT, custom_3 TEXT, item_errors TEXT, item_date_listed TEXT, item_date_listed_day TEXT, item_date_listed_int TEXT, item_hits TEXT, item_confirm_code TEXT, item_confirmed TEXT, item_cost TEXT, item_description TEXT, item_id TEXT, item_price TEXT, item_weight TEXT, item_listing_id TEXT, item_notes TEXT, item_package_d TEXT, item_package_l TEXT, item_package_w TEXT, item_part_number TEXT, item_title TEXT, item_title_url TEXT, item_type TEXT, item_search_1 TEXT, item_search_2 TEXT, item_search_3 TEXT, item_site_id TEXT, item_site_url TEXT, item_picture_1 TEXT, item_total_on_hand TEXT, sale_payment_address TEXT, sale_payment_type TEXT, sale_fees TEXT, sale_id TEXT, sale_seller_id TEXT, sale_status TEXT, sale_tax TEXT, sale_shipping_company TEXT, sale_shipping_in TEXT, sale_shipping_out TEXT, sale_source_of_sale TEXT, sale_total_sale_amount TEXT, sale_tracking_number TEXT, sale_transaction_id TEXT, sale_transaction_info TEXT, seller_address_1 TEXT, seller_address_2 TEXT, seller_address_city TEXT, seller_address_state TEXT, seller_address_zip TEXT, seller_address_country TEXT, seller_id TEXT, seller_ip TEXT, seller_email TEXT, seller_first_name TEXT, seller_last_name TEXT, seller_notes TEXT, seller_phone TEXT, seller_logo TEXT, seller_url TEXT)";
        db.execSQL(t7);

        //This is the database of .onion nodes we use to conned to our peers.
        String t8 = "CREATE TABLE network (address TEXT)";
        db.execSQL(t8);

        //Each listing is saved in HEX format to be able to search though the titles we have to save them in another database.
        String t9 = "CREATE TABLE searchx (xd INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, title TEXT, price TEXT, currency TEXT, seller_address_country TEXT, item_search_1 TEXT, item_search_2 TEXT, item_search_3 TEXT)";
        db.execSQL(t9);

        //Listings that have just been updated are saved here to display a "what's new" page.
        String t10 = "CREATE TABLE new_update (xd INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, title TEXT, price TEXT, currency TEXT, seller_address_country TEXT, item_search_1 TEXT, item_search_2 TEXT, item_search_3 TEXT)";
        db.execSQL(t10);


        DateFormat dateFormatx = new SimpleDateFormat("yyyyMMddHHmmss");
        Date datex = new Date();
        System.out.println(dateFormatx.format(datex));


        //Here we get a new set of keys for the user. If they have their own they can enter them later.
        try {

            //We run this until we get a good key.
            while (true) {


                //RSA keys are easy for web developers to use.
                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                //after non intensive search 2048 seems to be ok for now.
                kpg.initialize(2048);
                keyPair = kpg.genKeyPair();

                System.out.println("");
                System.out.println("privateKey Base 64: " + Base64.toBase64String(keyPair.getPrivate().getEncoded()));
                System.out.println("");
                System.out.println("Public Base 64:     " + Base64.toBase64String(keyPair.getPublic().getEncoded()));
                System.out.println("");


                byte[] clear = Base64.decode(Base64.toBase64String(keyPair.getPrivate().getEncoded()));
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
                KeyFactory fact = KeyFactory.getInstance("RSA");
                PrivateKey privateKey = fact.generatePrivate(keySpec);
                Arrays.fill(clear, (byte) 0);

                KeyFactory kf = KeyFactory.getInstance("RSA");
                RSAPrivateKeySpec priv = kf.getKeySpec(privateKey, RSAPrivateKeySpec.class);
                RSAPublicKeySpec keySpecx = new RSAPublicKeySpec(priv.getModulus(), BigInteger.valueOf(65537));
                PublicKey publicKey = kf.generatePublic(keySpecx);

                String base58x = Base64.toBase64String(publicKey.getEncoded());
                System.out.println("base58x " + base58x);


                base58 = Base64.toBase64String(keyPair.getPublic().getEncoded());


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

                if (network.use_old_key) {base58 = Base58Encode.encode(sha256_1); break;}
                else {base58 = Base58Encode.encode(result);}

                System.out.println("base58 " + MainActivity.path + "/" + base58);

                if (base58.length() == network.base_58_id_size && base58.substring(0,1).equals("K")) {break;}


            }//while



            //Here we are creating the QR code picture for the app.
            ByteArrayOutputStream out = QRCode.from(base58).to(ImageType.JPG).withSize(156, 156).stream();//

            try {

                FileOutputStream fout = new FileOutputStream(new File(MainActivity.path + "/" + base58 + ".png"));

                fout.write(out.toByteArray());

                fout.flush();
                fout.close();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }


            System.out.println("");

            network.prv_key_id = Base64.toBase64String(keyPair.getPrivate().getEncoded());
            network.pub_key_id = Base64.toBase64String(keyPair.getPublic().getEncoded());

            SharedPreferences.Editor editor = MainActivity.settings.edit();
            editor.putString("prv_key_id", network.prv_key_id);
            editor.putString("pub_key_id", network.pub_key_id);
            editor.commit();


            //Add a few nodes to get the system started.
            //These are the server addresses that are already working.
            ContentValues values = new ContentValues();
            values.put("address", "4s67jvslfe2rb6jy.onion");//Onion address
            db.insert("network", null, values);
            values.put("address", "kk2llsw2pbaykgi3.onion");//Onion address
            db.insert("network", null, values);
            values.put("address", "takamnrdd7cu637f.onion");//Onion address
            db.insert("network", null, values);
            values.put("address", "qt6d5prx2rh6q2hj.onion");//Onion address
            db.insert("network", null, values);


        } catch (Exception e) {e.printStackTrace();}

        System.out.println("DBsx");

    }//***************************************



    //Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Upgrade system.

        System.out.println("Delete old network data...");

        //We want to delete all the users nodes and replace them with the correct ones.
        //In case their system has become corrupt.
        db.execSQL("DELETE FROM network");

        System.out.println("Install new network data...");

        //Add a few nodes to get the system started.
        //These are the server addresses that are already working.
        ContentValues values = new ContentValues();
        values.put("address", "4s67jvslfe2rb6jy.onion");//Onion address
        db.insert("network", null, values);
        values.put("address", "kk2llsw2pbaykgi3.onion");//Onion address
        db.insert("network", null, values);
        values.put("address", "takamnrdd7cu637f.onion");//Onion address
        db.insert("network", null, values);
        values.put("address", "qt6d5prx2rh6q2hj.onion");//Onion address
        db.insert("network", null, values);



    }//***********************************************************************





    public krypton_database_driver(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            System.out.println("KRYPTON DATABASE START");


            try {


                db.execSQL("DELETE FROM test_listings_db");

                //db.execSQL("DROP TABLE settings");

                //String query = ("SELECT xd FROM mining_db WHERE mining_new_block LIKE ? LIMIT 1");
                //String[] selection = new String[]{"%33%"};
                //Cursor cursor = db.rawQuery(query, selection);

                //System.out.println("Testing ??? " + cursor.getCount());


            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }//*****


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }


    }//Database driver





}//Database driver
