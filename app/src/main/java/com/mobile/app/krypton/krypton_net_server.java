package com.mobile.app.krypton;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.*;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONValue;




public class krypton_net_server{

    JSONParser parser = new JSONParser();
    krypton_database_load loadx = new krypton_database_load();

    static boolean start_once = true;

	//This is the server it can handel requests from other nodes and also from TOR browsers.
	//The TOR system is started and run from the client class. We just connect to the port from this class.

	krypton_net_server(){//*********************************************************


		//Build the server.

        if (start_once) {

            Timer xtimerx = new Timer();
            xtimerx.schedule(new RemindTask_server(), 0);

            //cannot use again.
            start_once = false;

        }

	}//*****************************************************************************



	//This is the server port manager to handel multiple requests.

	class RemindTask_server extends TimerTask{

        Runtime rxrunti = Runtime.getRuntime();

        public void run(){//************************************************************************************

            //always on
            while (true) {

                //server loop
                while (network.server) {//network.server == 1

                    ServerSocket serverSocket = null;

                    try {

                        serverSocket = new ServerSocket(network.p2p_port);
                        //serverSocket.setSoTimeout(60000);

                        Socket clientSocket = serverSocket.accept();
                        //Delegate to new thread
                        new Thread(new serverInput(clientSocket)).start();

                        serverSocket.close();

                    } catch (SocketTimeoutException e) {

                        System.out.println("Server timeout 60 seconds try again...");

                    } catch (Exception e) {

                        //e.printStackTrace();

                        try {serverSocket.close();} catch (Exception ex){}

                        try {Thread.sleep(1000);} catch (InterruptedException ex){}

                    }//*****************

                }//*****while

                System.out.println("Server is off...");
                try {Thread.sleep(1000);} catch (InterruptedException e){}

            }//*****while

        }//runx***************************************************************************************************

    }//remindtask





    //Server thread that allows multiple incoming connections.

	public class serverInput implements Runnable {

	   private final Socket clientSocket; //initialize in const'r


		public serverInput(Socket clientSocketx){

			clientSocket = clientSocketx;

		}//**************************************


	    public void run() {


            String get_list = "";
            String clientSentence = "";
			String jsonText = "";
			String statex = "";
			String responsex = "";


            try {

                boolean send_html = false;
                boolean send_image = false;
                //test_statex = false;

                if (clientSocket != null) {System.out.println("Connected");}

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                while (!(inputLine = in.readLine()).equals("")) {

                    System.out.println(inputLine);
                    clientSentence = inputLine;

                    if (inputLine.contains("/item/") && !inputLine.contains("Referer"))   {get_list = inputLine;}
                    if (inputLine.contains("/search/") && !inputLine.contains("Referer")) {get_list = inputLine;}
                    if (inputLine.contains("/item/") && !inputLine.contains("Referer"))   {get_list = inputLine;}
                    if (inputLine.contains("/images/") && !inputLine.contains("Referer")) {get_list = inputLine; send_image = true;}

                    if (inputLine.equals("\r") || inputLine.equals("\n")) {System.out.println("BREAK>>>"); break;}
                    else {System.out.println("xx");}

                }//**********************************************


                //in.close();

                System.out.println(">>>");

                //Make sure the client is using tor and does not have an IP address.
                String client_ip = clientSocket.getRemoteSocketAddress().toString();
                System.out.println("CLIENT ADDRESS " + clientSocket.getRemoteSocketAddress().toString());


                try {


                    statex = "0";
                    responsex = "e000";

                    Object obj = null;

                    //This sometimes throws an error if we get a response that is corrupted.
                    //This will shutdown the app.
                    //java.lang.Error: Error: could not match input
                    try {

                         obj = parser.parse(clientSentence);

                    } catch (Error e) {System.out.println("Response is unreadable..");}

                    JSONObject jsonObject = (JSONObject) obj;

                    String request = (String) jsonObject.get("request");
                    System.out.println("GET: " + request);

                    //Get the token or information.
                    String token = "";
                    String objectx = "";
                    String block_id = "";
                    String packagex = "";
                    String unconfirmed_id = "";
                    String user_key = "";
                    String hash_id = "";
                    String text = "";
                    String id = "";

                    //Populate the info.
                    try {token =          (String) jsonObject.get("token").toString();}               catch (Exception e) {}//System.out.println("extra info no token...");
                    try {objectx =        (String) jsonObject.get("unconfirmed_package").toString();} catch (Exception e) {}//System.out.println("extra info no token...");
                    try {block_id =       (String) jsonObject.get("block_id").toString();}            catch (Exception e) {}//System.out.println("extra info no block_id...");
                    try {packagex =       (String) jsonObject.get("package").toString();}             catch (Exception e) {}//System.out.println("extra info no package...");
                    try {unconfirmed_id = (String) jsonObject.get("unconfirmed_id").toString();}      catch (Exception e) {}//System.out.println("extra info no unconfirmed_id...");
                    try {user_key =       (String) jsonObject.get("key_id").toString();}              catch (Exception e) {}//System.out.println("extra info no user_key...");
                    try {hash_id =        (String) jsonObject.get("hash_id").toString();}             catch (Exception e) {}//System.out.println("extra info no user_key...");
                    try {text =           (String) jsonObject.get("text").toString();}                catch (Exception e) {}//System.out.println("extra info no user_key...");
                    try {id =             (String) jsonObject.get("id").toString();}                  catch (Exception e) {}//System.out.println("extra info no user_key...");

                    try {

                        //These are the possible server requests we can get from the user.
                        //If it's not a node and just a browser then we send them HTML.
                        //These are only for nodes to use.

                        //We need 2 variables from each method so we have to package them into an array before we return them.
                        String[] test_statex_json = new String[2];


                        //If the user is requesting status that's OK but if they want database info and we are updating then they get nothing.
                        if(!request.equals("status") && network.database_in_use)  {responsex = "System is busy."; statex = "2";}

                        //The user is requesting our status.
                        else if(request.equals("status"))                         {responsex = get_status(); statex = "1";}

                        //The user has updated soemthing and is sending us the update.
                        else if(request.equals("add_new_unconfirmed"))            {test_statex_json = add_task(objectx); statex = test_statex_json[0]; responsex = test_statex_json[1];}

                        //The user found a new block and is sending it to us.
                        else if(request.equals("add_new_block"))                  {test_statex_json = add_block(token); statex = test_statex_json[0]; responsex = test_statex_json[1];}

                        //The user found a package and is sending it to us.
                        else if(request.equals("add_new_package"))                {test_statex_json = add_package(packagex); statex = test_statex_json[0]; responsex = test_statex_json[1];}

                        //The user can request the difficulty but this isn't used in normal opperation.
                        else if(request.equals("get_difficulty"))                 {responsex = network.difficultyx.toString(); statex = "1"; }

                        //The user wants our peer list so they can connect to our peers.
                        else if(request.equals("get_network"))                    {responsex = get_network_list(); statex = "1";}

                        //The user is using the lite "SPV" wallet and wants to get their listings.
                        else if(request.equals("get_user_listings"))              {test_statex_json = get_user_listings_nx(user_key); statex = test_statex_json[0]; responsex = test_statex_json[1];}

                        //The user wants our list of unconfirmed listings so they can mine for them with us.
                        else if(request.equals("unconfirmed_block_update"))       {test_statex_json = get_unconfirmed_id_nx(unconfirmed_id); statex = test_statex_json[0]; responsex = test_statex_json[1];}

                        //Get block from mining hash.
                        else if(request.equals("block_update"))                   {test_statex_json = get_block_id_nx(block_id); statex = test_statex_json[0]; responsex = test_statex_json[1];}

                        //Normal block update opperation. The user needs our list.
                        else if(request.equals("blocks_x_update"))                {test_statex_json = get_blocks_x_id_nx(block_id); statex = test_statex_json[0]; responsex = test_statex_json[1];}

                        //The user DB has forked and they need to go back in time to find where it forked from.
                        else if(request.equals("blocks_n_update"))                {test_statex_json = get_blocks_xn_id_nx2(block_id); statex = test_statex_json[0]; responsex = test_statex_json[1];}

                        //The user just started their app and wants an early block from us to start their DB from.
                        else if(request.equals("blocks_get_first"))               {test_statex_json = get_blocks_first(); statex = test_statex_json[0]; responsex = test_statex_json[1];}

                        //The user wants to search our listings for something.
                        else if(request.equals("search_listings"))                {test_statex_json = get_search_list(text); statex = test_statex_json[0]; responsex = test_statex_json[1];}

                        //The user wants to search our listings for something.
                        else if(request.equals("search_listings_new"))            {test_statex_json = get_search_list_new(text); statex = test_statex_json[0]; responsex = test_statex_json[1];}

                        //Get a token listing usings it's ID
                        else if(request.equals("get_token_id"))                   {test_statex_json = get_token_id(id); statex = test_statex_json[0]; responsex = test_statex_json[1];}

                        //Get a toke listings using it's HASH
                        else if(request.equals("get_token_idx"))                  {test_statex_json = get_token_idx(hash_id); statex = test_statex_json[0]; responsex = test_statex_json[1];}

                        //A list of commands our server can perform.
                        else if(request.equals("help"))                           {responsex = ""; statex = "1";}//responsex = get_help_list();

                        //The user is requesting something we don't provide.
                        else {statex = "0"; responsex = "Not a valid request!";}


                    } catch (Exception e) {e.printStackTrace(); statex = "0"; responsex = "Server error!";}


                } catch (Exception e) {e.printStackTrace(); System.out.println("JSON ERORR SEND HTML"); responsex = build_html_file(get_list); send_html = true;}


                //If the client using the TOR browser is requesting an image or the HTML code.
                if (!send_image) {

                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

                    JSONObject obj = new JSONObject();
                    obj.put("response", statex);
                    try {obj.put("message", responsex);} catch (Exception e) {e.printStackTrace();}

                    StringWriter outs = new StringWriter();
                    obj.writeJSONString(outs);
                    jsonText = outs.toString();

                    if (!send_html) {out.print(jsonText);}
                    else {out.print(responsex);}

                    out.flush();
                    out.close();

                }//**************
                else {

                    String image_test = get_list.replace("/images/","");

                    System.out.println("Image test: " + image_test);

                    OutputStream outputStream = clientSocket.getOutputStream();

                    //We are only going to allow our picture from this server.
                    InputStream is = MainActivity.context2.getAssets().open("hex100.jpg");
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArray);

                    outputStream.write(byteArray.toByteArray());
                    outputStream.flush();
                    outputStream.close();

                }//**

                clientSocket.close();
                //serverSocket.close();

                if (network.programst.equals("Port blocked (" + Integer.toString(network.p2p_port) + ")")) {network.programst = "";}


            }//try
            catch (Exception e) {

                e.printStackTrace();

                //System.err.println("Could not listen on port: " + network.p2p_port);
                network.programst = "Port blocked (" + Integer.toString(network.p2p_port) + ")";

                try {Thread.sleep(100);} catch (InterruptedException ex) {System.out.println("Cannot sleep!");}

            }//******************

            //try{Thread.sleep(1000);} catch (InterruptedException e){}


	    }//run

	}//****************************************************







    //The connection has a header so it's a browser we send them our info in HTML.

    public String build_html_file(String get_list){

        //System.out.println("Build HTML file");

        network.website_hits++;

        String buffer = "";
        String html = "";

        System.out.println("get_list " + get_list);

        try {

            if (get_list.contains("/item/")) {

                System.out.println("Send Item...");

                String search_for = "";
                int xp = 0;
                System.out.println("get_list " + get_list);

                xp = get_list.indexOf("/item/");
                search_for = get_list.substring(xp + 6, get_list.length());
                System.out.println("search_for " + search_for);

                xp = search_for.lastIndexOf(" HTTP");
                try{search_for = search_for.substring(0, xp);}catch(Exception e){}
                System.out.println("search_for " + search_for);

                try {

                    BufferedReader in = new BufferedReader(new InputStreamReader(MainActivity.context2.getAssets().open("item.html")));
                    while ((buffer = in.readLine()) != null){html = html + buffer;}
                    in.close();

                } catch (IOException e) {e.printStackTrace();}

                String[] load_token;

                krypton_database_get_token tokenx = new krypton_database_get_token();
                load_token = tokenx.getToken(search_for);

                network_convert convertx = new network_convert();
                load_token = convertx.hex_to_string(load_token);

                for (int loop = 0; loop < network.item_layout.length; loop++){//*******

                    html = html.replace("<||" + network.item_layout[loop] + "||>", load_token[loop]);

                }//********************************************************************

                //if template 0 only send description
                if(load_token[7].equals("0")){html = load_token[19];}


            }//if****************************
            else if (get_list.contains("/search/")) {

                System.out.println("Send Search...");

                network.website_searches++;

                String search_for = "";
                int xp = 0;
                System.out.println("get_list " + get_list);

                xp = get_list.indexOf("/search/?search=");
                search_for = get_list.substring(xp + 16, get_list.length());
                System.out.println("search_for " + search_for);

                xp = search_for.lastIndexOf(" HTTP");
                try{search_for = search_for.substring(0, xp);}catch(Exception e){}
                search_for = search_for.replace("+"," ");
                System.out.println("search_for " + search_for);

                krypton_database_search searchx = new krypton_database_search();
                String[][] search_list = searchx.search(search_for);

                try {

                    BufferedReader in = new BufferedReader(new InputStreamReader(MainActivity.context2.getAssets().open("search.html")));
                    while ((buffer = in.readLine()) != null){html = html + buffer;}
                    in.close();

                } catch (IOException e) {e.printStackTrace();}

                try {

                    for (int loop = 0; loop < search_list[0].length; loop++) {//********************

                        if (search_list[0][loop].contains("-")) {}
                        else {

                            html = html + "<br />";
                            html = html + "<br />";
                            html = html + "<a href='/item/" + search_list[0][loop] + "'>" + search_list[0][loop] + "</a> ";
                            html = html + "<a href='/item/" + search_list[0][loop] + "'>" + search_list[1][loop] + "</a>";
                            html = html + "<br />";
                            html = html + search_list[2][loop] + "";
                            html = html + search_list[3][loop] + "";
                            html = html + "&nbsp;&nbsp;&nbsp;&nbsp;Location: " + search_list[4][loop] + "";
                            html = html + "<br />";

                        }//else

                    }//*****************************************************************************

                } catch (Exception e) {e.printStackTrace();}

                html = html + "</div>";
                html = html + "</center>";
                html = html + "</div>";
                html = html + "</body>";
                html = html + "</html>";


            }//************************************
            else if (get_list.contains("/images/")) {

                //This is done above closer to the outputstream.

                System.out.println("Send Images...");

            }//************************************
            else {

                System.out.println("Send HTML Text...");

                try {

                    BufferedReader in = new BufferedReader(new InputStreamReader(MainActivity.context2.getAssets().open("home.html")));
                    while ((buffer = in.readLine()) != null){html = html + buffer;}
                    in.close();

                } catch (IOException e) {e.printStackTrace();}

                for (int loop = 0; loop < network.item_layout.length; loop++) {//******

                    html = html.replace("<||" + network.item_layout[loop] + "||>", network.html_block_ql[loop]);

                }//********************************************************************

            }//else

        } catch (Exception e) {e.printStackTrace(); html = "Error loading tokens! Your database may not be ready.";}

        return html;

    }//status****************







    //If the node is a lite "SPV" client it searches from here.

    public String[] get_search_list_new(String text){

        System.out.println("Get search list");

        String test_statex = "0";
        String jsonText = "";

        try {


            System.out.println("GET SEARCH NEW...");
            krypton_database_search_new searchx = new krypton_database_search_new();
            String[][] search_list = searchx.search();


            JSONObject obj = new JSONObject();
            for (int loop = 0; loop < search_list[0].length; loop++) {//*************

                JSONObject obj1 = new JSONObject();

                for (int loop2 = 0; loop2 < search_list.length; loop2++) {//************************

                    obj1.put(Integer.toString(loop2), search_list[loop2][loop]);
                    System.out.println(search_list[loop2][loop]);

                }//*********************************************************************************

                System.out.println(JSONValue.toJSONString(obj1));

                obj.put(Integer.toString(loop), JSONValue.toJSONString(obj1));


            }//*********************************************************************

            if (search_list[0].length > 0) {test_statex = "1";}
            else {test_statex = "0";}

            System.out.println(JSONValue.toJSONString(obj));

            jsonText = JSONValue.toJSONString(obj);


        } catch (Exception e) {e.printStackTrace(); test_statex = "0"; jsonText = "error";}


        String[] jsonText2 = new String[2];

        jsonText2[0] = test_statex;
        jsonText2[1] = jsonText;

        return jsonText2;

    }//******************************







    //If the node is a lite "SPV" client it searches from here.

    public String[] get_search_list(String text){

        System.out.println("Get search list");

        String test_statex = "0";
        String jsonText = "";

        try {


            System.out.println("GET SEARCH...");
            krypton_database_search searchx = new krypton_database_search();
            String[][] search_list = searchx.search(text);


            JSONObject obj = new JSONObject();
            for (int loop = 0; loop < search_list[0].length; loop++) {//*************

                JSONObject obj1 = new JSONObject();

                for (int loop2 = 0; loop2 < search_list.length; loop2++) {//************************

                    obj1.put(Integer.toString(loop2), search_list[loop2][loop]);
                    System.out.println(search_list[loop2][loop]);

                }//*********************************************************************************

                System.out.println(JSONValue.toJSONString(obj1));

                obj.put(Integer.toString(loop), JSONValue.toJSONString(obj1));


            }//*********************************************************************

            if (search_list[0].length > 0) {test_statex = "1";}
            else {test_statex = "0";}

            System.out.println(JSONValue.toJSONString(obj));

            jsonText = JSONValue.toJSONString(obj);


        } catch (Exception e) {e.printStackTrace(); test_statex = "0"; jsonText = "error";}


        String[] jsonText2 = new String[2];

        jsonText2[0] = test_statex;
        jsonText2[1] = jsonText;

        return jsonText2;

    }//******************************






    //If the client is a lite "SPV" client it needs to download listings from us.

    public String[] get_token_id(String id){

        System.out.println("Get token id");

        String test_statex = "0";
        String jsonText = "";

        try {


            System.out.println("GET TOKEN FROM ID...");
            krypton_database_get_token tokenx = new krypton_database_get_token();
            String[] token_array = tokenx.getToken2(id);


            JSONObject obj = new JSONObject();
            int xxp1 = 0;
            for (int loop = 0; loop < token_array.length; loop++) {//***********

                obj.put("l" + Integer.toString(xxp1), token_array[loop]);
                System.out.println("l" + token_array[loop]);
                xxp1++;

            }//*****************************************************************


            if (token_array[0].length() > 3){test_statex = "1";}
            else {test_statex = "0";}

            System.out.println(JSONValue.toJSONString(obj));

            jsonText = JSONValue.toJSONString(obj);


        } catch (Exception e) {e.printStackTrace(); test_statex = "0"; jsonText = "error";}


        String[] jsonText2 = new String[2];

        jsonText2[0] = test_statex;
        jsonText2[1] = jsonText;

        return jsonText2;

    }//******************************







    //If the client is a lite "SPV" client it needs to download listings from us.

    public String[] get_token_idx(String hash_id){

        System.out.println("Get token idx");

        String test_statex = "0";
        String jsonText = "";

        try {


            System.out.println("GET TOKEN FROM HASH...");
            krypton_database_get_token tokenx = new krypton_database_get_token();
            String[] token_array = tokenx.getTokenFH(hash_id);


            JSONObject obj = new JSONObject();
            int xxp1 = 0;
            for (int loop = 0; loop < token_array.length; loop++) {//************

                obj.put("l" + Integer.toString(xxp1), token_array[loop]);
                System.out.println("l" + token_array[loop]);
                xxp1++;

            }//******************************************************************


            if (token_array[0].length() > 3) {test_statex = "1";}
            else {test_statex = "0";}

            System.out.println(JSONValue.toJSONString(obj));

            jsonText = JSONValue.toJSONString(obj);


        } catch (Exception e) {e.printStackTrace(); test_statex = "0"; jsonText = "error";}


        String[] jsonText2 = new String[2];

        jsonText2[0] = test_statex;
        jsonText2[1] = jsonText;

        return jsonText2;

    }//******************************







    //If the peer is using a lite "SPV" wallet then they will request the tokens they have from their public base 58 key.
    //Here we have their public key and we are getting the listing they own for them.

    public String[] get_user_listings_nx(String key){

        System.out.println("Get user listings nx");

        String test_statex = "0";
        String jsonText = "";

        try {

            System.out.println("GET USER LISTINGS...");

            krypton_database_get_user_token_list listx = new krypton_database_get_user_token_list();
            String[][] list = listx.getTokenList(key);

            JSONObject obj1 = new JSONObject();
            for (int loop1 = 0; loop1 < list[0].length; loop1++) {//**************

                JSONObject obj2 = new JSONObject();

                obj2.put("id", list[0][loop1]);
                obj2.put("hash", list[1][loop1]);

                System.out.println(JSONValue.toJSONString(obj2));

                String jxsonarry = JSONValue.toJSONString(obj2);

                obj1.put(Integer.toString(loop1), jxsonarry);

            }//for****************************************************************

            System.out.println(JSONValue.toJSONString(obj1));

            jsonText = JSONValue.toJSONString(obj1);

            test_statex = "1";

        } catch (Exception e ){e.printStackTrace(); test_statex = "0"; jsonText = "error";}


        String[] jsonText2 = new String[2];

        jsonText2[0] = test_statex;
        jsonText2[1] = jsonText;

        return jsonText2;

    }//******************************









    public String[] get_blocks_first(){

        System.out.println("Get blocks first");

        String test_statex = "0";
    	String jsonText = "";

	    try {


			System.out.println("GET FIRST MINING TOKEN...");
			krypton_database_get_token_first first = new krypton_database_get_token_first();
			String[] token_array = first.getToken();


			JSONObject obj = new JSONObject();
			int xxp1 = 0;
			int xxp2 = 0;
		
			for (int loop = 0; loop < network.miningx_size; loop++) {//*****************************
				
				obj.put("m" + Integer.toString(xxp1), token_array[loop]);
				System.out.println("m" + token_array[loop]);
				xxp1++;

			}//*************************************************************************************

	    	for (int loop = network.miningx_size; loop < token_array.length; loop++) {//************

				obj.put("l" + Integer.toString(xxp2), token_array[loop]);
				System.out.println("l" + token_array[loop]);
				xxp2++;

			}//*************************************************************************************


			if (token_array[0].length() > 3) {test_statex = "1";}
			else {test_statex = "0";}

			System.out.println(JSONValue.toJSONString(obj));

			jsonText = JSONValue.toJSONString(obj);


		} catch (Exception e) {e.printStackTrace(); test_statex = "0"; jsonText = "error";}


        String[] jsonText2 = new String[2];

        jsonText2[0] = test_statex;
        jsonText2[1] = jsonText;

        return jsonText2;

    }//******************************








    public String[] add_block(String token){

        System.out.println("Add block");

        String test_statex = "0";
		String jsonText = "";


		try {

			//Get the array.

            Object obj = null;

            //This sometimes throws an error if we get a response that is corrupted.
            //This will shutdown the app.
            //java.lang.Error: Error: could not match input
            try {

                obj = parser.parse(token);

            } catch (Error e) {System.out.println("Response is unreadable..");}

			JSONObject jsonObjectx = (JSONObject) obj;


			String update_token[] = new String[network.listing_size];
			String mining_token[] = new String[network.miningx_size];

	    	for (int loop = 0; loop < network.listing_size; loop++){//************

				update_token[loop] = (String) jsonObjectx.get("l" + Integer.toString(loop));
				System.out.println("convert " + update_token[loop]);

			}//*******************************************************************

	    	for (int loop = 0; loop < mining_token.length; loop++){//*************

				mining_token[loop] = (String) jsonObjectx.get("m" + Integer.toString(loop));
				System.out.println("convert " + update_token[loop]);

			}//*******************************************************************



	        //get the last token
	        krypton_database_get_token getxt = new krypton_database_get_token();

	        String req_id = update_token[0];
	        String old_token[];
	        old_token = getxt.getToken(req_id);


            //try to add the new token
            krypton_update_new_block_remote remotex = new krypton_update_new_block_remote();
            boolean test = remotex.update(update_token, mining_token, old_token);

			System.out.println("test " + test);

			if (test) {

				jsonText = "Block added.";

				//delete from the pending list
				krypton_database_delete_buffer bufferd = new krypton_database_delete_buffer();
				bufferd.deleteID(update_token[0]);

				System.out.println("Send my new update to network.");

				Timer xtimerx = new Timer();
				xtimerx.schedule(new RemindTask_server_updaten(update_token, mining_token, old_token), 0);

				test_statex = "1";

			}//*********************
			else {

				jsonText = "Block could not be added.";

				test_statex = "0";

			}//**

            //krypton_database_load loadx = new krypton_database_load();
            loadx.load();

		} catch (Exception e) {e.printStackTrace(); test_statex = "0"; jsonText = "error";}


        String[] jsonText2 = new String[2];

        jsonText2[0] = test_statex;
        jsonText2[1] = jsonText;

        return jsonText2;

    }//status****************








    //The client is sending us a package and we are going to try to add it.

    public String[] add_package(String packagex) {

        System.out.println("Add package");

        String test_statex = "0";
        String jsonText = "";

        jsonText = "Package block submitted.";

        //Maybe some simple tests on the block before we say it's added?

        if (!network.installing_package) {

            //We have to use a thread because the package could be huge and it could take a long long time to finish.
            Timer xtimerx = new Timer();
            xtimerx.schedule(new RemindTask_add_package_background(packagex), 0);

            test_statex = "1";

        }//*******************************
		else {test_statex = "0";}


        String[] jsonText2 = new String[2];

        jsonText2[0] = test_statex;
        jsonText2[1] = jsonText;

        return jsonText2;

    }//status****************







    //The package takes a long time to add so we do it as a background process so the server can help other users.

	class RemindTask_add_package_background extends TimerTask{

    	String packagex;
    	boolean package_installed = false;

		RemindTask_add_package_background(String packagex1){

			packagex = packagex1;

		}//*************************************************


		public void run(){//************************************************************************************

			System.out.println("_add package_");

            network.installing_package = true;

            try {

                Object obj = null;

                //This sometimes throws an error if we get a response that is corrupted.
                //This will shutdown the app.
                //java.lang.Error: Error: could not match input
                try {

                    obj = parser.parse(packagex);

                } catch (Error e) {System.out.println("Response is unreadable..");}

				JSONObject jsonObjectx = (JSONObject) obj;

                System.out.println("Packagex: " + packagex);
                System.out.println("Size: " + jsonObjectx.size());


				for (int loop = 0; loop < jsonObjectx.size(); loop++) {//*****************************

					String update_token[] = new String[network.listing_size];
					String mining_token[] = new String[network.miningx_size];

                    //System.out.println(jsonObjectx.get(loop));
                    System.out.println(jsonObjectx.get(Integer.toString(loop)));

                    Object objx = null;

                    //This sometimes throws an error if we get a response that is corrupted.
                    //This will shutdown the app.
                    //java.lang.Error: Error: could not match input
                    try {

                        objx = parser.parse(jsonObjectx.get(Integer.toString(loop)).toString());

                    } catch (Error e) {System.out.println("Response is unreadable..");}

					//Object obj = parser.parse(jsonObjectx.get(Integer.toString(loop)).toString());
					JSONObject jsonObject = (JSONObject) objx;

					for (int loopx = 0; loopx < network.listing_size; loopx++) {//********

                        //System.out.println("convert " + update_token[loopx]);
						update_token[loopx] = (String) jsonObject.get("l" + Integer.toString(loopx));

					}//*******************************************************************

					for (int loopx = 0; loopx < mining_token.length; loopx++) {//*********

                        //System.out.println("convert " + update_token[loopx]);
						mining_token[loopx] = (String) jsonObject.get("m" + Integer.toString(loopx));

					}//*******************************************************************

					//Get the last token.
					krypton_database_get_token getxt = new krypton_database_get_token();

					String req_id = update_token[0];
					String[] old_token = getxt.getToken(req_id);


                    //Try to add the new token.
                    krypton_update_new_block_remote remotex = new krypton_update_new_block_remote();
                    boolean test = remotex.update(update_token, mining_token, old_token);

                    System.out.println("test " + test);

					if (test) {

						//Delete from the pending list.
						krypton_database_delete_buffer bufferd = new krypton_database_delete_buffer();
						bufferd.deleteID(update_token[0]);

                        package_installed = true;

					}//******
					else {

						System.out.println("Package could not be added.");

                        package_installed = false;
						break;

					}//**

                    //krypton_database_load loadx = new krypton_database_load();
                    loadx.load_lite();

				}//*********************************************************************************


                //krypton_database_load loadx = new krypton_database_load();
                loadx.load();

				//Package is used or broken get a new one.
				network.mining_package_ready = false;

                //If the package was installed then we send the package on to our peers.
				if (package_installed) {

					System.out.println("Send my new updates to network.");

                    krypton_net_client.resend_new_block_package(packagex);

				}//*******************

			} catch (Exception e) {e.printStackTrace();}

            network.installing_package = false;

		}//runx*************************************************************************************************

	}//remindtask






    //After we get a new block we try to send it to all of our peers to update the network.

	class RemindTask_server_updaten extends TimerTask{

		Runtime rxrunti = Runtime.getRuntime();

		String[] move_itemt;
		String[] mining_itemt;
		String[] old_tokent;

		RemindTask_server_updaten(String[] move_itemx, String[] mining_itemx, String[] old_tokenx){

			move_itemt = move_itemx;
			mining_itemt = mining_itemx;
			old_tokent = old_tokenx;

		}//****************************************************************************************

		public void run(){//************************************************************************************

			System.out.println("Update all my peers with the new block.");

            if (!krypton_update_new_block_remote.update_in_use) {


                krypton_net_client.send_new_block_update(mining_itemt, move_itemt);

                //krypton_database_load loadx = new krypton_database_load();
                loadx.load();

                //System.out.println("test2 " + test2);

            }//**************************************************

			System.out.println("Update all my peers with the new block. Done...");

		}//runx*************************************************************************************************

    }//remindtask









    public String[] add_task(String token){

        System.out.println("Add task");

        String test_statex = "0";
		String jsonText = "0";
		int success = 0;

		LinkedList<String> list = new LinkedList<String>();


		try {


			//here we are limiting the amount of tasks we can get from the client we don't want them spamming the network.
			//otherwise we would loop though the array amount.
			for (int loop1 = 0; loop1 < network.package_block_size; loop1++) {//************


                Object obj = null;

                //This sometimes throws an error if we get a response that is corrupted.
                //This will shutdown the app.
                //java.lang.Error: Error: could not match input
                try {

                    obj = parser.parse(token);

                } catch (Error e) {System.out.println("Response is unreadable..");}

				JSONObject jsonObject = (JSONObject) obj;

				String splitx = (String) jsonObject.get(Integer.toString(loop1));
				if (splitx == null) {break;}

				System.out.println(splitx);

                Object objx = null;

                try {

                    objx = parser.parse(splitx);

                } catch (Error e) {System.out.println("Response is unreadable..");}

				//Object objx = parser.parse(splitx);
				JSONObject jsonObjectx = (JSONObject) objx;


				String update_token[] = new String[network.listing_size];

				for (int loopx = 0; loopx < network.listing_size; loopx++) {//********

					update_token[loopx] = (String) jsonObjectx.get(Integer.toString(loopx));
					System.out.println("convert " + update_token[loopx]);

				}//*******************************************************************


				//Test for this item in the task list already.
				krypton_database_get_unconfirmed_test test_token = new krypton_database_get_unconfirmed_test();
				String req_id1 = update_token[0];
				int int_token = test_token.testx(req_id1);

				if (int_token == 0) {

					//get the old token
					krypton_database_get_token getxt = new krypton_database_get_token();

					String req_id2 = update_token[0];
					String old_token[] = new String[network.listing_size];
					old_token = getxt.getToken(req_id2);

					//try to add the new token
					krypton_update_token_remote remotex = new krypton_update_token_remote();
					boolean test = remotex.update(update_token, old_token);


					if (test) {

						list.add(update_token[0]);
						success++;

					}//********


				}//if**************


				if (success > 0) {test_statex = "1";}
				else {test_statex = "0";}


				//delete from the pending list
				krypton_database_delete_buffer bufferd = new krypton_database_delete_buffer();
				bufferd.deleteID(update_token[0]);


			}//for


			jsonText = JSONValue.toJSONString(list);;

			System.out.println("jsonText return: " + jsonText);

			//krypton_database_load loadx = new krypton_database_load();
            loadx.load();


		} catch (Exception e) {e.printStackTrace(); test_statex = "0"; jsonText = "error";}


        String[] jsonText2 = new String[2];

        jsonText2[0] = test_statex;
        jsonText2[1] = jsonText;

        return jsonText2;

    }//status****************









    public String[] get_block_id_nx(String idx){

        System.out.println("Get block id nx");

        String test_statex = "0";
		String jsonText = "";

		try {


			String[] token_array;

			System.out.println("idx " + idx);
			System.out.println("last_block_mining_idx " + network.last_block_mining_idx);


			System.out.println("SLOW LOAD");
			krypton_database_get_token_fmh fmh = new krypton_database_get_token_fmh();
			token_array = fmh.getToken(idx);


			JSONObject obj = new JSONObject();
			int xxp1 = 0;
			int xxp2 = 0;
		
			for (int loop = 0; loop < network.miningx_size; loop++) {//*****************************
				
				obj.put("m" + Integer.toString(xxp1), token_array[loop]);
				System.out.println("m" + token_array[loop]);
				xxp1++;

			}//*************************************************************************************

	    	for (int loop = network.miningx_size; loop < token_array.length; loop++) {//************

				obj.put("l" + Integer.toString(xxp2), token_array[loop]);
				System.out.println("l" + token_array[loop]);
				xxp2++;

			}//*************************************************************************************


			if(token_array[0].length() > 3){test_statex = "1";}
			else{test_statex = "0";}

			System.out.println(JSONValue.toJSONString(obj));

			jsonText = JSONValue.toJSONString(obj);


		} catch (Exception e) {e.printStackTrace(); test_statex = "0"; jsonText = "Cannot find block!";}


        String[] jsonText2 = new String[2];

        jsonText2[0] = test_statex;
        jsonText2[1] = jsonText;

        return jsonText2;

    }//status****************










    public String[] get_blocks_x_id_nx(String idx){

        System.out.println("Get blocks x id nx");

        String test_statex = "0";
		String jsonText = "";

		try {


			String[][] token_array;

			System.out.println("idx " + idx);
			System.out.println("last_block_mining_idx " + network.last_block_mining_idx);

			//idx = "00002D476806AD5C56DEA0BB487E54AEA27FF60DEB1D2833010788B6C0C2C8F0";


			System.out.println("SLOW LOAD");
			krypton_database_get_token_fmh_x fmhx = new krypton_database_get_token_fmh_x();
			token_array = fmhx.get_tokens(idx, network.package_block_size);

			System.out.println("token_array[0].length " + token_array[0].length);

			if (token_array[0].length != 0 && !token_array[0][0].equals("error")) {

				test_statex = "1";

				JSONObject obj1 = new JSONObject();
	    		for (int loop1 = 0; loop1 < token_array[0].length; loop1++){//************

	    			String jxsonarry = "";

					JSONObject obj2 = new JSONObject();
					int xxp1 = 0;
					int xxp2 = 0;
		
					for (int loop = 0; loop < network.miningx_size; loop++){//*************

						obj2.put("m" + Integer.toString(xxp1), token_array[loop][loop1]);
						//System.out.println("m" + token_array[loop][loop1]);
						xxp1++;

					}//********************************************************************

					for (int loop = network.miningx_size; loop < token_array.length; loop++){//*************

						obj2.put("l" + Integer.toString(xxp2), token_array[loop][loop1]);
						//System.out.println("l" + token_array[loop][loop1]);
						xxp2++;

					}//*************************************************************************************

					//System.out.println(JSONValue.toJSONString(obj2));

					jxsonarry = JSONValue.toJSONString(obj2);

					obj1.put(Integer.toString(loop1), jxsonarry);


				}//***********************************************************************

                //System.out.println(JSONValue.toJSONString(obj1));

				jsonText = JSONValue.toJSONString(obj1);


			}//if**********************************
			else {test_statex = "0";}


		} catch (Exception e) {e.printStackTrace(); test_statex = "0"; jsonText = "Cannot find block!";}


        String[] jsonText2 = new String[2];

        jsonText2[0] = test_statex;
        jsonText2[1] = jsonText;

        return jsonText2;

    }//status****************









    public String[] get_blocks_xn_id_nx2(String idx){

        System.out.println("Get blocks xn id nx2");

        String test_statex = "0";
		String jsonText = "";

		try {


			String[][] token_array;

			System.out.println("idx " + idx);
			System.out.println("last_block_mining_idx " + network.last_block_mining_idx);

			//idx = "00002D476806AD5C56DEA0BB487E54AEA27FF60DEB1D2833010788B6C0C2C8F0";


			System.out.println("SLOW LOAD");
			krypton_database_get_token_fmh_n fmhn = new krypton_database_get_token_fmh_n();
			token_array = fmhn.getTokens(idx, network.package_block_size);

			System.out.println("token_array[0].length " + token_array[0].length);

			if (token_array[0].length != 0 && !token_array[0][0].equals("error")) {

				test_statex = "1";

				JSONObject obj1 = new JSONObject();
	    		for (int loop1 = 0; loop1 < token_array[0].length; loop1++) {//************

	    		    String jxsonarry = "";

					JSONObject obj2 = new JSONObject();
					int xxp1 = 0;
					int xxp2 = 0;
		
					for (int loop = 0; loop < network.miningx_size; loop++){//*************

						obj2.put("m" + Integer.toString(xxp1), token_array[loop][loop1]);
						//System.out.println("m" + token_array[loop][loop1]);
						xxp1++;

					}//********************************************************************

					for (int loop = network.miningx_size; loop < token_array.length; loop++){//*************

						obj2.put("l" + Integer.toString(xxp2), token_array[loop][loop1]);
						//System.out.println("l" + token_array[loop][loop1]);
						xxp2++;

					}//*************************************************************************************

					jxsonarry = JSONValue.toJSONString(obj2);

					obj1.put(Integer.toString(loop1), jxsonarry);

				}//***********************************************************************


				jsonText = JSONValue.toJSONString(obj1);


			}//if**********************************
			else {test_statex = "0";}


		} catch (Exception e) {e.printStackTrace(); test_statex = "0"; jsonText = "Cannot find block!";}


        String[] jsonText2 = new String[2];

        jsonText2[0] = test_statex;
        jsonText2[1] = jsonText;

        return jsonText2;

    }//status****************










    public String[] get_unconfirmed_id_nx(String idx){

        System.out.println("Get unconfirmed id nx");

        String test_statex = "0";
		String jsonText = "";

		try {

			//krypton_database_get_unconfirmed_fts fts = new krypton_database_get_unconfirmed_fts();
			krypton_database_get_unconfirmed_package fts = new krypton_database_get_unconfirmed_package();
			String[][] token_array = fts.getTokensNewest(network.package_block_size);

			JSONObject obj2 = new JSONObject();
			for (int loop1 = 0; loop1 < token_array[0].length; loop1++) {//*************************

				JSONObject obj1 = new JSONObject();

				for (int loopx = 0; loopx < token_array.length; loopx++) {//*********

					obj1.put(Integer.toString(loopx), token_array[loopx][loop1]);
					System.out.println("BUILD " + token_array[loopx][loop1]);

				}//******************************************************************

				String jxsonarry = JSONValue.toJSONString(obj1);

				obj2.put(Integer.toString(loop1), jxsonarry);

			}//*************************************************************************************


			//System.out.println(JSONValue.toJSONString(obj2));

			jsonText = JSONValue.toJSONString(obj2);

			test_statex = "1";


		} catch (Exception e) {e.printStackTrace(); test_statex = "0"; jsonText = "error";}


		String[] jsonText2 = new String[2];

        jsonText2[0] = test_statex;
        jsonText2[1] = jsonText;

        return jsonText2;

    }//status****************









    public String get_network_list(){

        System.out.println("Get network list");

		String jsonText = "";
		//LinkedList<String> list = new LinkedList<String>();
        JSONObject obj1 = new JSONObject();

		int array_size = network.network_size;

		if(array_size > 100){array_size = 100;}

		try {

			for (int xloop = 0; xloop < array_size; xloop++) {//****

                obj1.put(Integer.toString(xloop),network.network_list.get(xloop).toString());

			}//for**************************************************

            jsonText = JSONValue.toJSONString(obj1);

		} catch (Exception e) {e.printStackTrace();}

		return jsonText;

    }//status****************







    public String get_status(){

        System.out.println("Get status");

		String jsonText = "";

		try {

			JSONObject obj = new JSONObject();

			obj.put("active","1");
			obj.put("network", network.idx);
			obj.put("version", network.versionx);
			obj.put("difficulty", network.difficultyx.toString());
			obj.put("last_block_idx", network.last_block_mining_idx);
            obj.put("last_block_id", network.last_block_id);
			obj.put("last_block_timestamp", network.last_block_timestamp);
			obj.put("prev_block_idx", network.prev_block_mining_idx);
			obj.put("last_unconfirmed_id", network.last_unconfirmed_idx);
			obj.put("node_list", Integer.toString(network.network_size));
            obj.put("block_speed", Long.toString(network.blocktimesx));
            obj.put("unconfirmed", Integer.toString(network.database_unconfirmed_total));
            obj.put("peer0", network.peerid0);

            System.out.println(JSONValue.toJSONString(obj));

			jsonText = JSONValue.toJSONString(obj);

		} catch (Exception e) {e.printStackTrace();}

		return jsonText;

    }//status****************






}//last
