package com.mobile.app.krypton;

import android.content.SharedPreferences;

import java.io.*;
import java.security.KeyPairGenerator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.KeyPair;
import org.spongycastle.util.encoders.Base64;
import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;







public class krypton_rebuild_keys{


    //Here we build the RSA 2048 keys for the system. Each user has a set of keys like Bitcoin but here they are RSA not elliptic.

    krypton_rebuild_keys(){//************************************************************************

        //We can't use this here.
        //network.database_in_use = true;

        String base58 = "";
        KeyPair keyPair;


        try {


            System.out.println("KRYPTON KEYS BUILD.");

            //delete old pics
            try {
            
                File file = new File(network.base58_id + ".png");
            
                if (file.delete()) {System.out.println(file.getName() + " is deleted!");}
                else {System.out.println("Delete operation failed.");}
           
            } catch (Exception e) {e.printStackTrace();}


            //We run this until we get a good key.
            while (true) {


                KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                kpg.initialize(2048);
                keyPair = kpg.genKeyPair();

                System.out.println("");
                System.out.println("privateKey Base 64: " + Base64.toBase64String(keyPair.getPrivate().getEncoded()));
                System.out.println("");
                System.out.println("Public Base 64:     " + Base64.toBase64String(keyPair.getPublic().getEncoded()));
                System.out.println("");

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

                if(network.use_old_key){base58 = Base58Encode.encode(sha256_1); break;}
                else{base58 = Base58Encode.encode(result);}

                System.out.println("base58 " + base58);

                if(base58.length() == network.base_58_id_size && base58.substring(0,1).equals("K")){break;}


            }//while



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

            }



            System.out.println("");

            network.base58_id = base58;

            network.prv_key_id = Base64.toBase64String(keyPair.getPrivate().getEncoded());
            network.pub_key_id = Base64.toBase64String(keyPair.getPublic().getEncoded());

            SharedPreferences.Editor editor = MainActivity.settings.edit();
            editor.putString("prv_key_id", network.prv_key_id);
            editor.putString("pub_key_id", network.pub_key_id);
            editor.commit();

            System.out.println("DBsx");



        } catch (Exception e) {e.printStackTrace();}


        //We can't use this here.
        //network.database_in_use = false;

    }//rebuild






}//class
