package com.mobile.app.krypton;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;
import java.util.Arrays;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.spongycastle.util.encoders.Base64;
import java.security.MessageDigest;


public class TransferActivity extends AppCompatActivity{

    String[] tokenx_buffer = new String[network.listing_size];
    String[] tokenx = new String[network.listing_size];

    int showid = 0;

    EditText token_id;
    EditText account_id;

    Spinner mspin;

    Button buttonGetID;
    Button buttonGetQR;
    Button buttonTransfer;

    TextView textViewTokens;


    //This is the android transfer screen were users can send their tokens to someone else.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);


        //At the top we shop the user how many listings they have.

        textViewTokens = (TextView) findViewById(R.id.textViewTokens);
        textViewTokens.setText("You have " + Integer.toString(network.database_listings_owner) + " token(s)");



        //The user can choose a listing ID to send or they can just choose one at random from get ID

        token_id = (EditText) findViewById(R.id.editTextTokenID);
        token_id.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {testid();}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

        });


        //This is the address the user is sending the token to.

        account_id = (EditText) findViewById(R.id.editTextAccountID);


        //To speed things up we just have a spinner here rather then a textfield.
        //Because there are no transfer fees and updates only happen one by one putting them together doesn't matter.

        mspin = (Spinner) findViewById(R.id.spinnerx);
        Integer[] items = new Integer[]{1,2,3,4,5,10,50,100};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, items);
        mspin.setAdapter(adapter);


        //Unlike Bitcoin each token has a unique ID here we allow the user to choose what ID they want to send.
        //Other IDs might be being used by the user as their listings so they won't want to transfer those.

        buttonGetID = (Button) findViewById(R.id.buttonGetID);
        buttonGetID.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                show_id_test();

            }

        });


        //Here the user is starting the QR code scanner.

        buttonGetQR = (Button) findViewById(R.id.buttonGetQR);
        buttonGetQR.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                IntentIntegrator integrator = new IntentIntegrator(TransferActivity.this);
                integrator.setPrompt("Scan a barcode");
                //integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setOrientationLocked(false);
                integrator.setBeepEnabled(true);
                //integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
                integrator.initiateScan();

                //new IntentIntegrator(TransferActivity.this).initiateScan(); // `this` is the current Activity

            }

        });


        //Here the user is wanting to transfer tokens to another account.

        buttonTransfer = (Button) findViewById(R.id.buttonTransfer);
        buttonTransfer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                update_yes();

            }

        });


        //If the user has no tokens they don't need access to the buttons.

        if(network.database_listings_owner == 0){

            buttonGetID.setEnabled(false);
            buttonTransfer.setEnabled(false);

        }//**************************************
        else{

            buttonGetID.setEnabled(true);
            buttonTransfer.setEnabled(true);

            showid = 0;
            show_token();
            testid();

        }//**



    }//****






    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                account_id.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }




    //Testing the ID the user wants to transfer to make sure it's ours. If it is we show it in green, if not red.

    public void testid(){

        System.out.println("TEST ID");

        boolean found = false;

        //Search though our listings to see if what the user entered is something they have.

        for (int loop = 0; loop < network.my_listings.size(); loop++){

            if(token_id.getText().toString().equals(network.my_listings.get(loop).toString())){

                showid = loop;
                found = true;
                break;

            }//if******************************************************************************

        }//for********************************************************


        //Now show them if it's good (green) or not their token (red)...

        if(found){

            System.out.println("FOUND!");
            token_id.setTextColor(getResources().getColor(R.color.jblue2));

        }//*******
        else{

            System.out.println("ERROR!");
            token_id.setTextColor(getResources().getColor(R.color.red));

        }//***


    }//******************







    public void show_token(){

        System.out.println("Next");

        try{

            if(showid > -1){

                String req_id = network.my_listings.get(showid).toString();
                tokenx = get_token(req_id);
                token_id.setText(req_id);

            }//if
            else{}

        }catch(Exception e){e.printStackTrace();}

    }//*****************






    public String[] get_token(String x){

        String[] token1 = null;

        //if this is a full node then we load from listings if not we load from the lite db
        if(network.full_node) {


            //If the system is busy updating the blockchain this will fail retrieving tokens.
            //We want to show the user that it's not an error they just have to wait.
            try {

                krypton_database_get_token tokenx = new krypton_database_get_token();
                token1 = tokenx.getToken2(x);

                //We try to print it so if it fails we can show the error.
                System.out.println(token1[0]);

            } catch(Exception e) {

                Toast.makeText(getApplicationContext(), "System is busy...", Toast.LENGTH_LONG).show();

            }

        }//********************
        else {


            //If the system is busy updating the blockchain this will fail retrieving tokens.
            //We want to show the user that it's not an error they just have to wait.
            try {

                krypton_update_listings_lite tokenx = new krypton_update_listings_lite();
                token1 = tokenx.getToken(x);

                //We try to print it so if it fails we can show the error.
                System.out.println(token1[0]);

            } catch(Exception e) {

                Toast.makeText(getApplicationContext(), "System is busy...", Toast.LENGTH_LONG).show();

            }

        }//**

        return token1;

    }//****************************************







    public void update_yes(){

        //Get the value first.

        System.out.println("Amount: " + mspin.getSelectedItem().toString());

        final int value = (Integer) Integer.parseInt(mspin.getSelectedItem().toString());

        //Continue.
        final String[] token_array = get_token(token_id.getText().toString());

        System.out.println("token_array[60] " + token_array[60]);
        System.out.println("base58_id       " + network.base58_id);


        //Make sure the item is ours.
        if(token_array[60].equals(network.base58_id)){

            //Make sure the address we are sending to is valid.
            if(account_id.getText().length() != network.base_58_id_size || !account_id.getText().toString().substring(0,1).equals("K")){

                System.out.println("Account ID is not valid!");

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("'To Address' is not valid!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }//*************************************************************************************************************************
            else{


                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to transfer " + Integer.toString(value) + " token(s) to another account?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {


                        System.out.println("Transfer token(s)");

                        int successx = 0;
                        int updatingx = 0;
                        int errorsx = 0;

                        for (int loop = showid; loop < (value + showid); loop++){

                            //3 tries.
                            for (int loop2 = 0; loop2 < 3; loop2++){

                                //This can fail if someone tries to send more tokens then they have.
                                try {


                                    System.out.println("Send ID         " + network.my_listings.get(loop).toString());
                                    System.out.println("token_array[60] " + token_array[60]);
                                    System.out.println("base58_id       " + network.base58_id);

                                    //krypton_database_get_token tokenx4b = new krypton_database_get_token();
                                    String[] token_array2 = get_token(network.my_listings.get(loop).toString());

                                    tokenx = token_array2;

                                    krypton_database_get_unconfirmed_test test1 = new krypton_database_get_unconfirmed_test();
                                    int test = test1.testx(tokenx[0]);

                                    //If the item is not currently updating then it can be sent.
                                    if(test == 0){updatet(); successx++;}
                                    else{updatingx++;}

                                    //If we succeed or fail we still need to break because we are done with this try.
                                    //We only try another loop if there is an error.
                                    break;


                                } catch (Exception e) {

                                    e.printStackTrace();

                                    if(loop2 == 2){errorsx++;}

                                    //Wait a second and try again.
                                    try{Thread.sleep(1000);} catch (InterruptedException ex){}

                                }//********************

                            }//for**********************************

                        }//for****************************************************

                        //krypton_database_load reload = new krypton_database_load();
                        //mining.mining3 = true;
                        account_id.setText("");


                        dialog.dismiss();

                        //A user pointed out it was not clear that the network was working.
                        Toast.makeText(getApplicationContext(), (String) "Updates are being sent to the network.\n" + Integer.toString(successx) + " Success.\n" + Integer.toString(updatingx) + " In use.\n" + Integer.toString(errorsx) + " Errors.", Toast.LENGTH_LONG).show();


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


            }//else


        }//
        else{

            System.out.println("This is not your token!");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Invalid token ID, You cannot send a token that is not yours!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();


        }//


    }//**********************








    public void updatet(){

        System.out.println("Update");


        tokenx[3] = Long.toString( System.currentTimeMillis() );

        tokenx[4] = network.pub_key_id;//network.settingsx[5];

        //tokenx[9] = tokenx[9];

        tokenx[60] = account_id.getText().toString();


        //Sign.

        try{


            String build_hash = "";

            build_hash = build_hash + tokenx[0];

            for (int loop = 3; loop < tokenx.length; loop++){

                build_hash = build_hash + tokenx[loop];//save everything else

            }//**********************************************


            String hashx = build_hash;
            byte[] sha256_1x = MessageDigest.getInstance("SHA-256").digest(hashx.getBytes());
            System.out.println(Base64.toBase64String(sha256_1x));

            tokenx[1] = Base64.toBase64String(sha256_1x);



            byte[] message = Base64.toBase64String(sha256_1x).getBytes("UTF8");

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



            byte[] keyxb3 = Base64.decode(tokenx[4]);

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


            if (tokenx[60].length() == network.base_58_id_size && tokenx[60].substring(0,1).equals("K")) {

                //Save the update.
                tokenx_buffer = tokenx;

                //Send the update.
                krypton_update_token update = new krypton_update_token(tokenx_buffer);

                //Update buffer count.
                network.send_buffer_size++;

                System.out.println("Sent!");

            }//******************************************************************************************
            else {System.out.println("Transfer item error does not pass tests.");}


        } catch (Exception e) {e.printStackTrace();}



    }//*****************








    public void show_id_test(){

        //If the user clicks the show token button we get the next one on our list.
        if(token_id.getText().toString().length() == 0){showid = 0;}
        else{showid++;}

        //If we don't have any tokens then we show an error.
        if(network.my_listings.size() == 0){showid = -1;}

        //Display the token.
        show_token();

    }//*************************





}//last
