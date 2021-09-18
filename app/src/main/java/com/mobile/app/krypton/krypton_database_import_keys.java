package com.mobile.app.krypton;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.io.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import org.spongycastle.util.encoders.Base64;
import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;







public class krypton_database_import_keys extends SQLiteOpenHelper {


    String base58 = "";



    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }



    //If the user has a private key they have saved in another location they can add it though this class.

    krypton_database_import_keys(String keyx){//************************************************************************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();


            try {


                System.out.println("KRYPTON IMPORT KEY.");

                network.pub_key_id = keyx;//network.settingsx[5]


                try {


                    byte[] clear = Base64.decode(keyx);
                    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
                    KeyFactory fact = KeyFactory.getInstance("RSA");
                    PrivateKey privateKey = fact.generatePrivate(keySpec);
                    Arrays.fill(clear, (byte) 0);



                    KeyFactory kf = KeyFactory.getInstance("RSA");
                    RSAPrivateKeySpec priv = kf.getKeySpec(privateKey, RSAPrivateKeySpec.class);

                    RSAPublicKeySpec keySpecx = new RSAPublicKeySpec(priv.getModulus(), BigInteger.valueOf(65537));

                    PublicKey publicKey = kf.generatePublic(keySpecx);



                    base58 = Base64.toBase64String(publicKey.getEncoded());


                    int len = base58.length();
                    byte[] data = new byte[len / 2];
                    for (int i = 0; i < len; i += 2) {

                        data[i / 2] = (byte) ((Character.digit(base58.charAt(i), 16) << 4) + Character.digit(base58.charAt(i+1), 16));

                    }//*******************************


                    byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(data);

                    byte[] a = "=".getBytes();
                    byte[] b = sha256_1;

                    byte[] result = new byte[a.length + b.length];
                    System.arraycopy(a, 0, result, 0, a.length);
                    System.arraycopy(b, 0, result, a.length, b.length);

                    if(network.use_old_key){base58 = Base58Encode.encode(sha256_1);}
                    else{base58 = Base58Encode.encode(result);}

                    System.out.println("base58 " + base58);


                    //After we save the keys we need a new QR icon.
                    ByteArrayOutputStream out = QRCode.from(base58).to(ImageType.PNG).withSize(156, 156).stream();

                    try {

                        FileOutputStream fout = new FileOutputStream(new File(MainActivity.path + "/" + base58 + ".png"));

                        fout.write(out.toByteArray());

                        fout.flush();
                        fout.close();

                    } catch (FileNotFoundException e) {

                        System.out.println(e.getMessage());

                    } catch (IOException e) {

                        System.out.println(e.getMessage());

                    }//**********************



                    System.out.println("");

                    network.base58_id = base58;

                    network.prv_key_id = Base64.toBase64String(privateKey.getEncoded());
                    network.pub_key_id = Base64.toBase64String(publicKey.getEncoded());

                    SharedPreferences.Editor editor = MainActivity.settings.edit();
                    editor.putString("prv_key_id", network.prv_key_id);
                    editor.putString("pub_key_id", network.pub_key_id);
                    editor.commit();


                    System.out.println("DBsx");


                } catch(Exception e) {e.printStackTrace();}




            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
            finally {

            network.database_in_use = false;

        }



    }//




}//class
