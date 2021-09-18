package com.mobile.app.krypton;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.msopentech.thali.toronionproxy.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.io.IOUtils;
import org.spongycastle.util.encoders.Base64;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;


public class EditActivity extends AppCompatActivity {

String tokenx[] = new String[network.listing_size];
String[] tokenx_buffer = new String[network.listing_size];

Timer xtimerx;//class loop.

int showid = 0;

Button back;
Button load_id;
Button next;
Button test;
Button submit;

//This is just for testing it won't show.
ImageView test_image = new ImageView(MainActivity.context2);
TextView test_load_text_view;

EditText id;

EditText currency;
EditText custom_template;
EditText custom_1;
EditText custom_2;
EditText custom_3;
EditText item_errors;
EditText item_date_listed;
EditText item_date_listed_day;
EditText item_date_listed_int;
EditText hits;
EditText item_confirm_code;
EditText item_confirmed;
EditText cost;
EditText item_description;
EditText item_id;
EditText sale_price;
EditText weight;
EditText item_listing_id;
EditText item_notes;
EditText item_package_d;
EditText item_package_l;
EditText item_package_w;
EditText item_part_number;
EditText title;
EditText item_title_url;
EditText item_type;
EditText item_search_1;
EditText item_search_2;
EditText item_search_3;
EditText item_seller_id;
EditText item_site_url;
EditText picture_1;
EditText item_total_on_hand;


    //This is the android Edit screen were the user can edit their tokens.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        //At the top of the app the user can scroll through their listings here they can go back one in their list.

        back = (Button) findViewById(R.id.buttonPrev);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showid--; show_token();

            }

        });


        //If they have a lot of tokens they may want to load a specific token ID

        load_id = (Button) findViewById(R.id.buttonLoad);
        load_id.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                System.out.println("Load...");

                load_yes();

            }

        });


        //Here the user can go to the next token in their list.

        next = (Button) findViewById(R.id.buttonNext);
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showid++; show_token();

            }

        });



        //Here we get all the textfields from the XML file.

        id = (EditText) findViewById(R.id.editTextID);

        title = (EditText) findViewById(R.id.editTextTitle);
        item_part_number = (EditText) findViewById(R.id.editTextPart);
        picture_1 = (EditText) findViewById(R.id.editTextPicURL);
        currency = (EditText) findViewById(R.id.editTextCurrency);
        sale_price = (EditText) findViewById(R.id.editTextPrice);
        item_total_on_hand = (EditText) findViewById(R.id.editTextQuantity);
        weight = (EditText) findViewById(R.id.editTextWeight);
        item_package_d = (EditText) findViewById(R.id.editTextHeight);
        item_package_l = (EditText) findViewById(R.id.editTextLength);
        item_package_w = (EditText) findViewById(R.id.editTextWidth);
        item_description = (EditText) findViewById(R.id.editTextDescription);
        item_notes = (EditText) findViewById(R.id.editTextNotes);
        item_site_url = (EditText) findViewById(R.id.editTextSiteURL);
        item_search_1 = (EditText) findViewById(R.id.editTextSearch1);
        item_search_2 = (EditText) findViewById(R.id.editTextSearch2);
        item_search_3 = (EditText) findViewById(R.id.editTextSearch3);
        custom_1 = (EditText) findViewById(R.id.editTextCustom1);
        custom_2 = (EditText) findViewById(R.id.editTextCustom2);
        custom_3 = (EditText) findViewById(R.id.editTextCustom3);


        test_load_text_view = (TextView) findViewById(R.id.textViewTestPicture);


        //The Tor system that we use doesn't do DNS lookups very well or perhaps there are other network problems but images are not always available.
        //Here we give the user the ability to test their image before they waste time uploading it.
        //The user on the other end will use Tor to look up the image to prevent snoopers from uploading a picture and then seeing what IPs look it up.
        //This APP has no naked internet connections it only connects outside using Tor.

        test = (Button) findViewById(R.id.buttonTestPicture);
        test.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

            test_load_picture();

            }

        });



        //Here the user wants to submit their listing that they edited.
        //We check first to make sure it isn't already being updated because the network will reject it.
        //Then we sign it and send it off.

        submit = (Button) findViewById(R.id.buttonSubmit);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                System.out.println("showid " + showid);

                krypton_database_get_unconfirmed_test test1 = new krypton_database_get_unconfirmed_test();
                int test = test1.testx(network.my_listings.get(showid).toString());

                if(test == 0){update_yes();}
                else{

                    System.out.println("This item is updating!");
                    Toast.makeText(getApplicationContext(), (String) "This item is updating!", Toast.LENGTH_LONG).show();

                }//**

            }

        });



        //Here we show the first token the user has, or if they have none then we show them a message.

        if(network.database_listings_owner > 0){show_token();}
        else{

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You do not have any tokens or they haven't updated yet!\n\nHow to get tokens?\nSometimes people sell their tokens just like they would sell an item. Or you can go to token ID 100000 for more information.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }//**


        //If the user has no tokens they don't need access to the buttons.

        if(network.database_listings_owner == 0){

            back.setEnabled(false);
            load_id.setEnabled(false);
            next.setEnabled(false);
            test.setEnabled(false);
            submit.setEnabled(false);

        }//**************************************
        else{

            back.setEnabled(true);
            load_id.setEnabled(true);
            next.setEnabled(true);
            test.setEnabled(true);
            submit.setEnabled(true);

        }//**


    }//




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle action bar item clicks here. The action bar will.
        //Automatically handle clicks on the Home/Up button, so long.
        //As you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);

    }






    public static String[] get_token(String x){

        String[] token1;

        //If this is a full node then we load from listings if not we load from the lite db.
        if(network.full_node) {

            krypton_database_get_token tokenx = new krypton_database_get_token();
            token1 = tokenx.getToken2(x);

        }//********************
        else{

            krypton_update_listings_lite tokenx = new krypton_update_listings_lite();
            token1 = tokenx.getToken(x);

        }//**

        network_convert convertx = new network_convert();
        token1 = convertx.hex_to_string(token1);

        return token1;

    }//****************************************








    public void show_token(){

        System.out.println("Next");



        if(showid <= 0){back.setEnabled(false);}
        else{back.setEnabled(true);}

        if(showid >= (network.my_listings.size() -1)){next.setEnabled(false);}
        else{next.setEnabled(true);}

        System.out.println("showid " + showid);
        System.out.println("network.my_listings.size() " + network.my_listings.size());




        if(showid > -1 && showid < network.my_listings.size()){

            String req_id = network.my_listings.get(showid).toString();

            //Show id of the token we are editing.
            id.setText(req_id);

            //Get the token.
            tokenx = get_token(req_id);

            currency.setEnabled(true);
            //custom_template.setEnabled(true);
            custom_1.setEnabled(true);
            custom_2.setEnabled(true);
            custom_3.setEnabled(true);
            //item_errors.setEnabled(true);
            //item_date_listed.setEnabled(true);
            //item_date_listed_day.setEnabled(true);
            //item_date_listed_int.setEnabled(true);
            //hits.setEnabled(true);
            //item_confirm_code.setEnabled(true);
            //item_confirmed.setEnabled(true);
            //cost.setEnabled(true);
            item_description.setEnabled(true);
            //item_id.setEditable(true);
            sale_price.setEnabled(true);
            weight.setEnabled(true);
            //item_listing_id.setEnabled(true);
            item_notes.setEnabled(true);
            item_package_d.setEnabled(true);
            item_package_l.setEnabled(true);
            item_package_w.setEnabled(true);
            item_part_number.setEnabled(true);
            title.setEnabled(true);
            //item_title_url.setEnabled(false);
            //item_type.setEnabled(true);
            item_search_1.setEnabled(true);
            item_search_2.setEnabled(true);
            item_search_3.setEnabled(true);
            //item_seller_id.setEditable(true);
            item_site_url.setEnabled(true);
            picture_1.setEnabled(true);
            item_total_on_hand.setEnabled(true);

            currency.setText(tokenx[6]);
            //custom_template.setText(tokenx[7]);
            custom_1.setText(tokenx[8]);
            custom_2.setText(tokenx[9]);
            custom_3.setText(tokenx[10]);
            //item_errors.setText(tokenx[11]);
            //item_date_listed.setText(tokenx[12]);
            //item_date_listed_day.setText(tokenx[13]);
            //item_date_listed_int.setText(tokenx[14]);
            //hits.setText(tokenx[15]);
            //item_confirm_code.setText(tokenx[16]);
            //item_confirmed.setText(tokenx[17]);
            //cost.setText(tokenx[18]);
            item_description.setText(tokenx[19]);
            //item_id.setText(tokenx[0]);
            sale_price.setText(tokenx[21]);
            weight.setText(tokenx[22]);
            //item_listing_id.setText(tokenx[30]);
            item_notes.setText(tokenx[24]);
            item_package_d.setText(tokenx[25]);
            item_package_l.setText(tokenx[26]);
            item_package_w.setText(tokenx[27]);
            item_part_number.setText(tokenx[28]);
            title.setText(tokenx[29]);
            //item_title_url.setText(tokenx[30]);
            //item_type.setText(tokenx[31]);
            item_search_1.setText(tokenx[32]);
            item_search_2.setText(tokenx[33]);
            item_search_3.setText(tokenx[34]);
            //item_seller_id.setText(tokenx[60]);
            item_site_url.setText(tokenx[36]);
            picture_1.setText(tokenx[37]);
            item_total_on_hand.setText(tokenx[38]);

        }//if
        else{

            currency.setEnabled(false);
            //custom_template.setEnabled(false);
            custom_1.setEnabled(false);
            custom_2.setEnabled(false);
            custom_3.setEnabled(false);
            //item_errors.setFocusable(false);
            //item_date_listed.setFocusable(false);
            //item_date_listed_day.setEnabled(false);
            //item_date_listed_int.setEnabled(false);
            //hits.setEnabled(false);
            //item_confirm_code.setEnabled(false);
            //item_confirmed.setEnabled(false);
            //cost.setEnabled(false);
            item_description.setEnabled(false);
            //item_id.setEnabled(false);
            sale_price.setEnabled(false);
            weight.setEnabled(false);
            //item_listing_id.setEnabled(false);
            item_notes.setEnabled(false);
            item_package_d.setEnabled(false);
            item_package_l.setEnabled(false);
            item_package_w.setEnabled(false);
            item_part_number.setEnabled(false);
            title.setEnabled(false);
            //item_title_url.setEnabled(false);
            //item_type.setEnabled(false);
            item_search_1.setEnabled(false);
            item_search_2.setEnabled(false);
            item_search_3.setEnabled(false);
            //item_seller_id.setEnabled(false);
            item_site_url.setEnabled(false);
            picture_1.setEnabled(false);
            item_total_on_hand.setEnabled(false);

        }//**

    }//*****************






    public void load_yes(){

        try{

            //String req_id = "";

            int showid2 = Integer.parseInt(id.getText().toString());

            System.out.println("showid2 " + showid2);

            if(showid2 >= 100000 && showid2 <= 125000){


                String req_id = Integer.toString(showid2);

                //network.my_listings.get(showid).toString();


                for (int loop = 0; loop < network.my_listings.size(); loop++){

                    if(req_id.equals(network.my_listings.get(loop).toString())){

                        showid = loop;

                        if(showid <= 0){back.setEnabled(false);}
                        else{back.setEnabled(true);}

                        if(showid >= (network.my_listings.size() -1)){next.setEnabled(false);}
                        else{next.setEnabled(true);}

                    }//if

                }//for********************************************************

                System.out.println("showid " + showid);

                id.setText(req_id);

                //get the token
                tokenx = get_token(req_id);


                currency.setEnabled(true);
                //custom_template.setEnabled(true);
                custom_1.setEnabled(true);
                custom_2.setEnabled(true);
                custom_3.setEnabled(true);
                //item_errors.setEnabled(true);
                //item_date_listed.setEnabled(true);
                //item_date_listed_day.setEnabled(true);
                //item_date_listed_int.setEnabled(true);
                //hits.setEnabled(true);
                //item_confirm_code.setEnabled(true);
                //item_confirmed.setEnabled(true);
                //cost.setEnabled(true);
                item_description.setEnabled(true);
                //item_id.setEditable(true);
                sale_price.setEnabled(true);
                weight.setEnabled(true);
                //item_listing_id.setEnabled(true);
                item_notes.setEnabled(true);
                item_package_d.setEnabled(true);
                item_package_l.setEnabled(true);
                item_package_w.setEnabled(true);
                item_part_number.setEnabled(true);
                title.setEnabled(true);
                //item_title_url.setEnabled(false);
                //item_type.setEnabled(true);
                item_search_1.setEnabled(true);
                item_search_2.setEnabled(true);
                item_search_3.setEnabled(true);
                //item_seller_id.setEditable(true);
                item_site_url.setEnabled(true);
                picture_1.setEnabled(true);
                item_total_on_hand.setEnabled(true);

                currency.setText(tokenx[6]);
                //custom_template.setText(tokenx[7]);
                custom_1.setText(tokenx[8]);
                custom_2.setText(tokenx[9]);
                custom_3.setText(tokenx[10]);
                //item_errors.setText(tokenx[11]);
                //item_date_listed.setText(tokenx[12]);
                //item_date_listed_day.setText(tokenx[13]);
                //item_date_listed_int.setText(tokenx[14]);
                //hits.setText(tokenx[15]);
                //item_confirm_code.setText(tokenx[16]);
                //item_confirmed.setText(tokenx[17]);
                //cost.setText(tokenx[18]);
                item_description.setText(tokenx[19]);
                //item_id.setText(tokenx[0]);
                sale_price.setText(tokenx[21]);
                weight.setText(tokenx[22]);
                //item_listing_id.setText(tokenx[30]);
                item_notes.setText(tokenx[24]);
                item_package_d.setText(tokenx[25]);
                item_package_l.setText(tokenx[26]);
                item_package_w.setText(tokenx[27]);
                item_part_number.setText(tokenx[28]);
                title.setText(tokenx[29]);
                //item_title_url.setText(tokenx[30]);
                //item_type.setText(tokenx[31]);
                item_search_1.setText(tokenx[32]);
                item_search_2.setText(tokenx[33]);
                item_search_3.setText(tokenx[34]);
                //item_seller_id.setText(tokenx[60]);
                item_site_url.setText(tokenx[36]);
                picture_1.setText(tokenx[37]);
                item_total_on_hand.setText(tokenx[38]);

            }//if
            else{

                currency.setEnabled(false);
                //custom_template.setEnabled(false);
                custom_1.setEnabled(false);
                custom_2.setEnabled(false);
                custom_3.setEnabled(false);
                //item_errors.setEnabled(false);
                //item_date_listed.setEnabled(false);
                //item_date_listed_day.setEnabled(false);
                //item_date_listed_int.setEnabled(false);
                //hits.setEnabled(false);
                //item_confirm_code.setEnabled(false);
                //item_confirmed.setEnabled(false);
                //cost.setEnabled(false);
                item_description.setEnabled(false);
                //item_id.setEnabled(false);
                sale_price.setEnabled(false);
                weight.setEnabled(false);
                //item_listing_id.setEnabled(false);
                item_notes.setEnabled(false);
                item_package_d.setEnabled(false);
                item_package_l.setEnabled(false);
                item_package_w.setEnabled(false);
                item_part_number.setEnabled(false);
                title.setEnabled(false);
                //item_title_url.setEnabled(false);
                //item_type.setEnabled(false);
                item_search_1.setEnabled(false);
                item_search_2.setEnabled(false);
                item_search_3.setEnabled(false);
                //item_seller_id.setEnabled(false);
                item_site_url.setEnabled(false);
                picture_1.setEnabled(false);
                item_total_on_hand.setEnabled(false);

            }//**

        }catch(Exception e){

            e.printStackTrace();

            Toast.makeText(getApplicationContext(), (String) "You cannot edit this item!", Toast.LENGTH_LONG).show();

            currency.setEnabled(false);
            //custom_template.setEnabled(false);
            custom_1.setEnabled(false);
            custom_2.setEnabled(false);
            custom_3.setEnabled(false);
            //item_errors.setEnabled(false);
            //item_date_listed.setEnabled(false);
            //item_date_listed_day.setEnabled(false);
            //item_date_listed_int.setEnabled(false);
            //hits.setEnabled(false);
            //item_confirm_code.setEnabled(false);
            //item_confirmed.setEnabled(false);
            //cost.setEnabled(false);
            item_description.setEnabled(false);
            //item_id.setEnabled(false);
            sale_price.setEnabled(false);
            weight.setEnabled(false);
            //item_listing_id.setEnabled(false);
            item_notes.setEnabled(false);
            item_package_d.setEnabled(false);
            item_package_l.setEnabled(false);
            item_package_w.setEnabled(false);
            item_part_number.setEnabled(false);
            title.setEnabled(false);
            //item_title_url.setEnabled(false);
            //item_type.setEnabled(false);
            item_search_1.setEnabled(false);
            item_search_2.setEnabled(false);
            item_search_3.setEnabled(false);
            //item_seller_id.setEnabled(false);
            item_site_url.setEnabled(false);
            picture_1.setEnabled(false);
            item_total_on_hand.setEnabled(false);

        }//catch



    }//*******************







    public void test_load_picture(){

        System.out.println("Test Load Picture...");


        if(network.tor_active) {

            xtimerx = new Timer();
            xtimerx.schedule(new RemindTask_pics(), 0);

        }//********************
        else{

            Toast.makeText(getApplicationContext(), (String) "Peer is not ready!", Toast.LENGTH_LONG).show();

        }//**


    }//*****************************



    class RemindTask_pics extends TimerTask {

        Runtime rxrunti = Runtime.getRuntime();

        public void run(){//************************************************************************************

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    test_load_text_view.setText("Testing...");
                    test.setEnabled(true);

                }

            });

            //Load image from the web using tor so the user is protected from people tracking image downloads.
            try{


                String pic_url = picture_1.getText().toString();

                System.out.println("pic_url " + pic_url);

                String trimx = pic_url;

                trimx = trimx.replace("http://www.","");
                trimx = trimx.replace("http://","");
                trimx = trimx.replace("https://www.","");
                trimx = trimx.replace("https://","");

                System.out.println("trimx1 " + trimx);

                int trimfx = trimx.indexOf("/");

                String pic_url2 = trimx.substring(trimfx,trimx.length());
                System.out.println("pic_url2 " + pic_url2);

                trimx = trimx.substring(0,trimfx);
                System.out.println("trimx2 " + trimx);

                String address = trimx;

                //This should already be running from the net_client class we are just calling it from here.
                //It has to be started first. If it's not ready the user will have to wait.
                Socket socket = Utilities.socks4aSocketConnection(address, krypton_net_client.hiddenServicePort, krypton_net_client.local_host_connect, krypton_net_client.localPort);//127.0.0.1

                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                out.println("GET " + pic_url + " HTTP/1.0\r\n\r\n");
                out.flush();

                System.out.println("socketg");

                BitmapFactory.Options options = new BitmapFactory.Options();

                InputStream is = socket.getInputStream();

                byte[] bytes = IOUtils.toByteArray(is);

                int headerEnded = 0;

                for (int i = 0; i < bytes.length; i++) {

                    if (bytes[i] == 13 && bytes[i + 1] == 10 && bytes[i + 2] == 13 && bytes[i + 3] == 10) {

                        headerEnded = i;
                        //fileOutputStream.write(bytes, i+4 , 2048-i-4);
                        System.out.println("BREAK!");
                        break;

                    }//***********************************************************************************

                }//**************************************

                System.out.println("headerEnded: " + headerEnded);

                byte[] subArray = Arrays.copyOfRange(bytes,headerEnded + 4,bytes.length);

                System.out.println("Image Array Size: " + subArray.length);
                System.out.println(new String(subArray, "UTF-8"));

                final Bitmap imgview = BitmapFactory.decodeByteArray(subArray, 0, subArray.length);
                //final Bitmap imgview = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                System.out.println("Height: " + imgview.getHeight());

                System.out.println("socketw");

                out.close();
                socket.close();

                final int showSubArray = subArray.length;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //This will test if the picture can be loaded. We don't actually show it.
                        //test_image.setImageBitmap(imgview);

                        //If the picture can load then we never see this.
                        test_load_text_view.setText("Success! " + showSubArray + " Bytes.");
                        test.setEnabled(true);

                    }

                });


            }catch(Exception e){

                e.printStackTrace();
                System.out.println("Error Cannot download image!");

                final String message = e.getMessage();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        test_load_text_view.setText("Error: Image cannot be reached!");
                        test.setEnabled(true);

                    }

                });

            }//catch*************



        }//runx***************************************************************************************************

    }//remindtask









    public void update_yes(){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to update your listing?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog

                updatet();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }//**********************








    public void updatet(){

        System.out.println("Update");

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE");

        //update the noose
        tokenx[3] = Long.toString( System.currentTimeMillis() );

        tokenx[4] = network.pub_key_id;//network.settingsx[5];

        tokenx[6] = currency.getText().toString();
        tokenx[7] = "1";//custom_template.getText().toString();
        tokenx[8] = custom_1.getText().toString();
        tokenx[9] = custom_2.getText().toString();
        tokenx[10] = custom_3.getText().toString();
        tokenx[11] = "0";//item_errors.getText().toString();
        tokenx[12] = sdf.format( cal.getTime() );//item_date_listed.getText();
        tokenx[13] = sdf2.format( cal.getTime() );//item_date_listed_day.getText();
        tokenx[14] = Long.toString( System.currentTimeMillis() );//item_date_listed_int.getText();
        tokenx[15] = "0";//hits.getText().toString();
        tokenx[16] = "0";//item_confirm_code.getText().toString();
        tokenx[17] = "0";//item_confirmed.getText().toString();
        tokenx[18] = "0";//cost.getText().toString();
        tokenx[19] = item_description.getText().toString();
        tokenx[20] = "0";//item_id.getText().toString();
        tokenx[21] = sale_price.getText().toString();
        tokenx[22] = weight.getText().toString();

        tokenx[24] = item_notes.getText().toString();
        tokenx[25] = item_package_d.getText().toString();
        tokenx[26] = item_package_l.getText().toString();
        tokenx[27] = item_package_w.getText().toString();
        tokenx[28] = item_part_number.getText().toString();
        tokenx[29] = title.getText().toString();
        tokenx[30] = "0";//item_title_url.getText().toString();
        tokenx[31] = "0";//item_type.getText().toString();
        tokenx[32] = item_search_1.getText().toString();
        tokenx[33] = item_search_2.getText().toString();
        tokenx[34] = item_search_3.getText().toString();

        tokenx[36] = item_site_url.getText().toString();
        tokenx[37] = picture_1.getText().toString();
        tokenx[38] = item_total_on_hand.getText().toString();



        //To help with search
        tokenx[30] = tokenx[29].toLowerCase();

        //Base 58
        tokenx[60] = network.base58_id;

        //Add our .onion address into our listing so others can use it to connect to our server.
        //This confirms that the addresses people use are owned by people who have tokens.
        if(network.server && network.add_node_onion){tokenx[61] = krypton_net_client.serverOnionAddress;}//Seller IP
        else{tokenx[61] = "";}//Seller IP

        System.out.println("Seller IP tokenx[61]: " + tokenx[61]);

        //Seller info
        tokenx[63] = MainActivity.settings.getString("contactname1", "");//name
        tokenx[64] = MainActivity.settings.getString("contactname2", "");//last
        tokenx[54] = MainActivity.settings.getString("contactaddress", "");//address
        tokenx[55] = MainActivity.settings.getString("contactaddress2", "");//address2
        tokenx[56] = MainActivity.settings.getString("contactcity", "");//city
        tokenx[57] = MainActivity.settings.getString("contactprovince", "");//state
        tokenx[58] = MainActivity.settings.getString("contactzip", "");//zip
        tokenx[59] = MainActivity.settings.getString("contactcountry", "");//country

        tokenx[39] = MainActivity.settings.getString("contactbitcoin", "");;//btc
        tokenx[62] = MainActivity.settings.getString("contactemail", "");//email
        tokenx[65] = MainActivity.settings.getString("contactnotes", "");//notes
        tokenx[66] = MainActivity.settings.getString("contactphone", "");//phone
        tokenx[68] = MainActivity.settings.getString("contactwebsite", "");//website

        tokenx[65] = MainActivity.settings.getString("contactnotes", "");//Seller note


        //Sign

        try{


            //Convert to HEX format so we don't have symbol errors.
            //Different systems will display some symbols differently and the hash will be different for example the british pound sign.
            //If we don't HEX first the hash on Android will be different then the hash on other systems.
            network_convert convertx = new network_convert();
            tokenx = convertx.string_to_hex(tokenx);

            //Cut down the token if it's too big for the network
            network_trim trimx = new network_trim();
            tokenx = trimx.trim(tokenx);


            String build_hash = "";

            build_hash = build_hash + tokenx[0];

            for (int loop = 3; loop < tokenx.length; loop++){

                build_hash = build_hash + tokenx[loop];//Save everything else.

            }//**********************************************


            String hashx = build_hash;
            byte[] sha256_1x = MessageDigest.getInstance("SHA-256").digest(hashx.getBytes());
            System.out.println(Base64.toBase64String(sha256_1x));
            
            tokenx[1] = Base64.toBase64String(sha256_1x);



            byte[] message = Base64.toBase64String(sha256_1x).getBytes("UTF8");

            //Build private key and test.
            byte[] clear = Base64.decode(network.prv_key_id);//network.settingsx[4]
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey priv = fact.generatePrivate(keySpec);
            Arrays.fill(clear, (byte) 0);

            Signature sigx = Signature.getInstance("SHA1WithRSA");//MD5WithRSA
            sigx.initSign(priv);
            sigx.update(message);
            byte[] signatureBytesx = sigx.sign();
            //System.out.println("Public: " + Base64.toBase64String(pub.getEncoded()));
            System.out.println("Singature: " + Base64.toBase64String(signatureBytesx));

            String signxx = Base64.toBase64String(signatureBytesx);

            tokenx[2] = signxx;



            //Test the signature with our public key.
            byte[] keyxb3 = Base64.decode(network.pub_key_id);//settingsx[5]
            System.out.println("settingsx[5] " + network.pub_key_id);//settingsx[5]

            X509EncodedKeySpec keySpecx3 = new X509EncodedKeySpec(keyxb3);
            KeyFactory factx3 = KeyFactory.getInstance("RSA");
            PublicKey pubx3 = factx3.generatePublic(keySpecx3);
            Arrays.fill(keyxb3, (byte) 0);

            Signature sigpk3 = Signature.getInstance("SHA1WithRSA");//MD5WithRSA
            byte[] messagex3 = Base64.toBase64String(sha256_1x).getBytes("UTF8");

            byte[] signatureBytesx3 = Base64.decode(signxx);

            sigpk3.initVerify(pubx3);
            sigpk3.update(messagex3);

            System.out.println(sigpk3.verify(signatureBytesx3));

            System.out.println("tokenx[1] " + tokenx[1]);



            //Test to make sure nothing is wrong with the address before we send.
            if(tokenx[60].length() == network.base_58_id_size && tokenx[60].substring(0,1).equals("K")) {

                tokenx_buffer = tokenx;
                //Send the update.
                krypton_update_token update = new krypton_update_token(tokenx_buffer);

                network.send_buffer_size++;

                System.out.println("Sent!");

                Toast.makeText(getApplicationContext(), (String) "Success! Updates are being sent to the network.", Toast.LENGTH_LONG).show();

            }//*****************************************************************************************


        }catch(Exception e){e.printStackTrace();}



    }//*****************





}//last
