package com.mobile.app.krypton;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.msopentech.thali.toronionproxy.Utilities;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.spongycastle.util.encoders.Base64;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Timer;
import java.util.TimerTask;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.util.Arrays;




public class ViewActivity extends AppCompatActivity {

    private ProgressBar spinner;

    String tokenx[] = new String[network.listing_size];

    Timer xtimerx;//class loop.

    int showid = 0;

    String pic_url = "";

    ImageView picture_1;

    EditText show_idx;

    TextView title;
    TextView price;
    TextView part_number;
    TextView on_hand;
    TextView weight;
    TextView dimensions;

    WebView htmlx;

    TextView name;
    TextView note;
    TextView seller_id;
    TextView email;
    TextView phone;
    TextView website;
    TextView location;
    TextView hash;

    Button back;
    Button load_id;
    Button next;



    //This is the android View screen were users can view tokens on the network.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);


        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        //spinner.set(getResources().getColor(R.color.black));
        spinner.setVisibility(View.VISIBLE);


        back = (Button) findViewById(R.id.buttonPrev);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showid--;
                get_token();

            }//*****************************

        });


        load_id = (Button) findViewById(R.id.buttonLoad);
        load_id.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showid = Integer.parseInt(show_idx.getText().toString());
                get_token();

            }

        });

        next = (Button) findViewById(R.id.buttonNext);
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showid++;
                get_token();

            }

        });


        show_idx = (EditText) findViewById(R.id.editTextID);

        picture_1 = (ImageView) findViewById(R.id.picture1);

        title = (TextView) findViewById(R.id.textViewTitle);
        price = (TextView) findViewById(R.id.textViewPrice);
        part_number = (TextView) findViewById(R.id.textViewPartNumber);
        on_hand = (TextView) findViewById(R.id.textViewOnHand);
        weight = (TextView) findViewById(R.id.textViewWeight);
        dimensions = (TextView) findViewById(R.id.textViewDimensions);

        htmlx = (WebView) findViewById(R.id.htmlx);
        //htmlx.getSettings().setWebViewClient(new WebViewClient());
        htmlx.getSettings().setSupportMultipleWindows(false);
        htmlx.getSettings().setUseWideViewPort(false);
        htmlx.getSettings().setLoadWithOverviewMode(false);
        htmlx.getSettings().setPluginState(WebSettings.PluginState.OFF);
        htmlx.getSettings().setJavaScriptEnabled(false);
        htmlx.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        //htmlx.getSettings().setPluginsEnabled(true);
        htmlx.getSettings().setSupportMultipleWindows(false);
        htmlx.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        htmlx.loadUrl("https://www.yahoo.com");//this should fail! we don't want URLs being loaded.

        name = (TextView) findViewById(R.id.textViewNameX);
        note = (TextView) findViewById(R.id.textViewNoteX);
        seller_id = (TextView) findViewById(R.id.textViewSIDX);
        email = (TextView) findViewById(R.id.textViewEmailX);
        phone = (TextView) findViewById(R.id.textViewPhoneX);
        website = (TextView) findViewById(R.id.textViewWebsiteX);
        location = (TextView) findViewById(R.id.textViewLocationX);
        hash = (TextView) findViewById(R.id.textViewHashX);


        int value = -1;//Or other values
        Bundle b = getIntent().getExtras();
        if(b != null){

            value = b.getInt("ID");
            showid = value;

            System.out.println("value " + showid);

            get_token();

        }//************



    }//*************************





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





    //Decided which item to get, from our db or from the server.

    public void get_token(){

        spinner.setVisibility(View.VISIBLE);

        try {


            if(showid <= 100000){back.setEnabled(false);}
            else{back.setEnabled(true);}

            if(showid >= (100000 + (network.hard_token_limit -1))){next.setEnabled(false);}
            else{next.setEnabled(true);}

            show_idx.setText(Integer.toString(showid));


            if(network.full_node) {

                //Here we get the item from our local database because we are using a full node and we have the item.

                System.out.println("Next");

                krypton_database_get_token token = new krypton_database_get_token();
                tokenx = token.getToken2(Integer.toString(showid));

                verify(tokenx);

                network_convert convertx = new network_convert();
                tokenx = convertx.hex_to_string(tokenx);

                show_token();

            }//********************
            else{

                //Here we have to get the item from the server because we don't have it.

                System.out.println("Next2");

                if(network.active_peers > 0) {

                    xtimerx = new Timer();
                    xtimerx.schedule(new RemindTask_tor_get_token(), 0);

                }//*********************
                else{

                    Toast.makeText(getApplicationContext(), (String) "Peer is not ready!", Toast.LENGTH_LONG).show();
                    spinner.setVisibility(View.GONE);

                }//**


            }//**

        }catch(Exception e){e.printStackTrace();}


    }//******************************************






    //Android won't allow us to get internet content on the main tread.

    class RemindTask_tor_get_token extends TimerTask {

        Runtime rxrunti = Runtime.getRuntime();

        public void run(){//************************************************************************************

            try {


                System.out.println("Start TOR search...");

                tokenx = request_item_remote(Integer.toString(showid));

                verify(tokenx);

                network_convert convertx = new network_convert();
                tokenx = convertx.hex_to_string(tokenx);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        show_token();

                    }//@Override

                });


            }catch(Exception e){e.printStackTrace();}

        }//runx*************************************************************************************************

    }//remindtask








    //If this user is using the lite "SPV" non full node version then we have to get items from the server because we don't have them ourselves.

    public String[] request_item_remote(String id){//********************************************************


        String jsonText = "";
        String[] jsonText2 = null;


        try{

            JSONObject obj = new JSONObject();
            obj.put("request","get_token_id");
            obj.put("id", id);

            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            jsonText = out.toString();
            System.out.println(jsonText);

        }catch(Exception e){System.out.println("JSON ERROR");}


        try{

            String onionAddress = "";

            if(network.use_one_peer){onionAddress = network.onionAddress;}
            else if(network.peersx0){onionAddress = network.peerid0;}//network.settingsx[9];
            else if(network.peersx1){onionAddress = network.peerid1;}//network.settingsx[10];
            else if(network.peersx2){onionAddress = network.peerid2;}//network.settingsx[11];
            else if(network.peersx3){onionAddress = network.peerid3;}//network.settingsx[12];

            //System.out.println("address: " + krypton_net_client.client_port_connect);
            System.out.println("onionAddress: " + onionAddress);

            System.out.println("socket");

            Socket socket = Utilities.socks4aSocketConnection(onionAddress, krypton_net_client.hiddenServicePort, krypton_net_client.local_host_connect, krypton_net_client.localPort);//127.0.0.1
            //socket.setSoTimeout(20000);

            System.out.println("socketg");

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter outx = new PrintWriter(outputStream);
            outx.print(jsonText + "\r\n\r\n");
            outx.flush();
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader in = new BufferedReader(inputStreamReader);

            System.out.println("socketw");

            String modifiedSentence = "";

            String line;
            while ((line = in.readLine()) != null) {

                System.out.println(line);
                modifiedSentence = line;

            }//*************************************

            outputStream.close();
            outx.close();
            in.close();
            socket.close();


            JSONParser parser = new JSONParser();
            Object obj = parser.parse(modifiedSentence);
            JSONObject jsonObject = (JSONObject) obj;

            String response = (String) jsonObject.get("response");
            String message = (String) jsonObject.get("message");
            System.out.println("JSON " + response);

            if(response.equals("1")){

                Object obj2 = parser.parse(message);
                JSONObject jsonObject2 = (JSONObject) obj2;

                jsonText2 = new String[jsonObject2.size()];

                for(int loop = 0; loop < network.listing_size; loop++) {//******************************

                    jsonText2[loop] = (String) jsonObject2.get("l" + Integer.toString(loop));
                    System.out.println("convert " + jsonText2[loop]);

                }//*************************************************************************************

            }//**********************
            else{}


        }catch(Exception e){

            e.printStackTrace();
            System.out.println("Cannot find node!");

        }//*****************


        return jsonText2;

    }//*********************************************************************************************







    public void show_token(){

        //Display the info for the user.

        title.setText(tokenx[29]);
        price.setText("Price: " + tokenx[21] + " " + tokenx[6]);
        part_number.setText("Part Number: " + tokenx[28]);
        on_hand.setText("(" + tokenx[38] + ") Available");
        dimensions.setText("Dimensions: " + tokenx[25] + "x" + tokenx[26] + "x" + tokenx[27] + " cm");
        weight.setText("Weight: " + tokenx[22] + " kg");

        int sizes = 0;

        try{

            //htmlx = (WebView) findViewById(R.id.webView1);
            htmlx.getSettings().setJavaScriptEnabled(true);
            htmlx.loadData(tokenx[19], "text/html", "UTF-8");

        }catch(Exception e){e.printStackTrace(); sizes = 650;}



        name.setText("" + tokenx[63] + " " + tokenx[64]);
        note.setText("" + tokenx[65]);
        seller_id.setText("" + tokenx[60]);
        email.setText("" + tokenx[62]);
        phone.setText("" + tokenx[66]);
        website.setText("" + tokenx[68]);
        location.setText("" + tokenx[59]);
        hash.setText("" + tokenx[1]);



        pic_url = tokenx[37];

        if(network.tor_active) {

            xtimerx = new Timer();
            xtimerx.schedule(new RemindTask_pics(), 0);

        }//********************
        else{

            Toast.makeText(getApplicationContext(), (String) "Tor is not ready!", Toast.LENGTH_LONG).show();
            spinner.setVisibility(View.GONE);

        }//**

    }//*****************************






    public void verify(String[] itemx){


        boolean testx1 = false;
        boolean testx2 = false;

        String build_hash = "";
        build_hash = build_hash + itemx[0];
        for(int loop1 = 3; loop1 < network.listing_size; loop1++){//***********

            build_hash = build_hash + itemx[loop1];

        }//********************************************************************

        System.out.println("ID " + itemx[0]);
        System.out.println("build_hash " + build_hash);



        //test item
        try{

            byte[] sha256_1w = MessageDigest.getInstance("SHA-256").digest(build_hash.getBytes());

            System.out.println("TESTX " + Base64.toBase64String(sha256_1w));
            System.out.println("GIVEN " + itemx[1]);

            if(Base64.toBase64String(sha256_1w).equals(itemx[1])){testx1 = true;}
            else{System.out.println("Bad HASH");}


            byte[] keyxb3 = Base64.decode(itemx[4]);

            X509EncodedKeySpec keySpecx3 = new X509EncodedKeySpec(keyxb3);
            KeyFactory factx3 = KeyFactory.getInstance("RSA");
            PublicKey pubx3 = factx3.generatePublic(keySpecx3);
            Arrays.fill(keyxb3, (byte) 0);

            Signature sigpk3 = Signature.getInstance("SHA1WithRSA");//MD5WithRSA
            byte[] messagex3 = Base64.toBase64String(sha256_1w).getBytes("UTF8");

            byte[] signatureBytesx3 = Base64.decode(itemx[2]);

            sigpk3.initVerify(pubx3);
            sigpk3.update(messagex3);

            if(sigpk3.verify(signatureBytesx3)){testx2 = true;}
            else{System.out.println("Bad SIG");}

        }catch(Exception e){e.printStackTrace(); spinner.setVisibility(View.GONE);}



    }//*************************






    class RemindTask_pics extends TimerTask {

        Runtime rxrunti = Runtime.getRuntime();

        public void run(){//************************************************************************************


            //Load image from the web using tor so the user is protected from people tracking image downloads.
            try{


                //URL url = new URL(pic_url);

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

                trimx = trimx.substring(0, trimfx);
                System.out.println("trimx2 " + trimx);

                String address = trimx;

                //This should already be running from the net_client class we are just calling it from here.
                //It has to be started first. If it's not ready the user will have to wait.

                Socket socket = Utilities.socks4aSocketConnection(address, krypton_net_client.hiddenServicePort, krypton_net_client.local_host_connect, krypton_net_client.localPort);//127.0.0.1

                //Proxy instance, proxy ip = 10.0.0.1 with port 8080
                //Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(krypton_net_client.local_host_connect, krypton_net_client.localPort));
                //SocketAddress sockAddr = socketx.getLocalSocketAddress();
                //Proxy proxy = new Proxy(Proxy.Type.SOCKS, socketx.get);

                //URL socket = new URL("https://check.torproject.org/");

                //Socket socket = new Socket("https://check.torproject.org/",80);

                //socket.openConnection(proxy);

                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
                //out.println("GET " + pic_url + " HTTP/1.0");// \r\n\r\n
                out.println("GET " + pic_url + " HTTP/1.0\r\n\r\n");
                out.flush();

                System.out.println("socketg");

                BitmapFactory.Options options = new BitmapFactory.Options();

                InputStream is = socket.getInputStream();
                //InputStream is = socket.openConnection(proxy).getInputStream();


                //String line;
                //String line2 = "";
                //BufferedReader br = new BufferedReader(new InputStreamReader(is));
                //while ((line = br.readLine()) != null) {
                    //System.out.print("" + line);
                    //line2 = line2 + line;
                //}

                byte[] bytes = IOUtils.toByteArray(is);


                out.close();
                socket.close();

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

                System.out.println("socketw");


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                       picture_1.setImageBitmap(imgview);
                       spinner.setVisibility(View.GONE);

                    }

                });


            }catch(Exception e){

                e.printStackTrace();
                System.out.println("Error Cannot download image!");

                //picture_1.setImageResource(R.drawable.download);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        picture_1.setImageResource(R.drawable.placeholder);
                        spinner.setVisibility(View.GONE);

                    }

                });

            }//catch*************

        }//runx***************************************************************************************************

    }//remindtask







}//last
