package com.mobile.app.krypton;




public class network_convert{

    final protected static char[] hexArray = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    String[] token_ssp2 = new String[network.listing_size];

    //The android Java system is not the same as the Java system on computers some characters such as the british pound sign will hash differently on a PC then on android.
    //This creates a problem when we try to verify the hash. So in this class we convert all text to HEX so that it is hashed correctly.

    public String[] string_to_hex(String[] token){//**************************************************************************

        token_ssp2 = token;

        token_ssp2[0] =  token_ssp2[0];//id
        token_ssp2[1] =  token_ssp2[1];//hash_id varchar(100),
        token_ssp2[2] =  token_ssp2[2];//sig_id varchar(1160),
        token_ssp2[3] =  token_ssp2[3];//date_id varchar(60),
        token_ssp2[4] =  token_ssp2[4];//owner_id varchar(560),
        token_ssp2[5] =  bytesToHex(token_ssp2[5].getBytes());//owner_rating varchar(100), 
        token_ssp2[6] =  bytesToHex(token_ssp2[6].getBytes());//currency varchar(160), 
        token_ssp2[7] =  bytesToHex(token_ssp2[7].getBytes());//custom_template varchar(160), 
        token_ssp2[8] =  bytesToHex(token_ssp2[8].getBytes());//custom_1 varchar(160), 
        token_ssp2[9] =  bytesToHex(token_ssp2[9].getBytes());//custom_2 varchar(160), 
        token_ssp2[10] = bytesToHex(token_ssp2[10].getBytes());//custom_3 varchar(160), 
        token_ssp2[11] = bytesToHex(token_ssp2[11].getBytes());//item_errors varchar(160), 
        token_ssp2[12] = bytesToHex(token_ssp2[12].getBytes());//item_date_listed varchar(160), 
        token_ssp2[13] = bytesToHex(token_ssp2[13].getBytes());//item_date_listed_day varchar(160), 
        token_ssp2[14] = bytesToHex(token_ssp2[14].getBytes());//item_date_listed_int varchar(160), 
        token_ssp2[15] = bytesToHex(token_ssp2[15].getBytes());//item_hits varchar(160), 
        token_ssp2[16] = bytesToHex(token_ssp2[16].getBytes());//item_confirm_code varchar(160), 
        token_ssp2[17] = bytesToHex(token_ssp2[17].getBytes());//item_confirmed varchar(160), 
        token_ssp2[18] = bytesToHex(token_ssp2[18].getBytes());//item_cost varchar(160), 
        token_ssp2[19] = bytesToHex(token_ssp2[19].getBytes());//item_description varchar(3160), 
        token_ssp2[20] = bytesToHex(token_ssp2[20].getBytes());//item_id varchar(160), 
        token_ssp2[21] = bytesToHex(token_ssp2[21].getBytes());//item_price varchar(160), 
        token_ssp2[22] = bytesToHex(token_ssp2[22].getBytes());//item_weight varchar(160), 
        token_ssp2[23] = bytesToHex(token_ssp2[23].getBytes());//item_listing_id varchar(160), 
        token_ssp2[24] = bytesToHex(token_ssp2[24].getBytes());//item_notes varchar(160), 
        token_ssp2[25] = bytesToHex(token_ssp2[25].getBytes());//item_package_d varchar(160), 
        token_ssp2[26] = bytesToHex(token_ssp2[26].getBytes());//item_package_l varchar(160), 
        token_ssp2[27] = bytesToHex(token_ssp2[27].getBytes());//item_package_w varchar(160), 
        token_ssp2[28] = bytesToHex(token_ssp2[28].getBytes());//item_part_number varchar(160), 
        token_ssp2[29] = bytesToHex(token_ssp2[29].getBytes());//item_title varchar(160), 
        token_ssp2[30] = bytesToHex(token_ssp2[30].getBytes());//item_title_url varchar(160), 
        token_ssp2[31] = bytesToHex(token_ssp2[31].getBytes());//item_type varchar(160), 
        token_ssp2[32] = bytesToHex(token_ssp2[32].getBytes());//item_search_1 varchar(160), 
        token_ssp2[33] = bytesToHex(token_ssp2[33].getBytes());//item_search_2 varchar(160),
        token_ssp2[34] = bytesToHex(token_ssp2[34].getBytes());//item_search_3 varchar(160), 
        token_ssp2[35] = bytesToHex(token_ssp2[35].getBytes());//item_site_id varchar(160), 
        token_ssp2[36] = bytesToHex(token_ssp2[36].getBytes());//item_site_url varchar(160), 
        token_ssp2[37] = bytesToHex(token_ssp2[37].getBytes());//item_picture_1 varchar(160), 
        token_ssp2[38] = bytesToHex(token_ssp2[38].getBytes());//item_total_on_hand varchar(160), 
        token_ssp2[39] = bytesToHex(token_ssp2[39].getBytes());//sale_payment_address varchar(160), 
        token_ssp2[40] = bytesToHex(token_ssp2[40].getBytes());//sale_payment_type varchar(160), 
        token_ssp2[41] = bytesToHex(token_ssp2[41].getBytes());//sale_fees varchar(160), 
        token_ssp2[42] = bytesToHex(token_ssp2[42].getBytes());//sale_id varchar(160), 
        token_ssp2[43] = bytesToHex(token_ssp2[43].getBytes());//sale_seller_id varchar(160), 
        token_ssp2[44] = bytesToHex(token_ssp2[44].getBytes());//sale_status varchar(160), 
        token_ssp2[45] = bytesToHex(token_ssp2[45].getBytes());//sale_tax varchar(160), 
        token_ssp2[46] = bytesToHex(token_ssp2[46].getBytes());//sale_shipping_company varchar(160), 
        token_ssp2[47] = bytesToHex(token_ssp2[47].getBytes());//sale_shipping_in varchar(160), 
        token_ssp2[48] = bytesToHex(token_ssp2[48].getBytes());//sale_shipping_out varchar(160), 
        token_ssp2[49] = bytesToHex(token_ssp2[49].getBytes());//sale_source_of_sale varchar(160), 
        token_ssp2[50] = bytesToHex(token_ssp2[50].getBytes());//sale_total_sale_amount varchar(160), 
        token_ssp2[51] = bytesToHex(token_ssp2[51].getBytes());//sale_tracking_number varchar(160), 
        token_ssp2[52] = bytesToHex(token_ssp2[52].getBytes());//sale_transaction_id varchar(160), 
        token_ssp2[53] = bytesToHex(token_ssp2[53].getBytes());//sale_transaction_info varchar(160), 
        token_ssp2[54] = bytesToHex(token_ssp2[54].getBytes());//seller_address_1 varchar(160), 
        token_ssp2[55] = bytesToHex(token_ssp2[55].getBytes());//seller_address_2 varchar(160), 
        token_ssp2[56] = bytesToHex(token_ssp2[56].getBytes());//seller_address_city varchar(160), 
        token_ssp2[57] = bytesToHex(token_ssp2[57].getBytes());//seller_address_state varchar(160), 
        token_ssp2[58] = bytesToHex(token_ssp2[58].getBytes());//seller_address_zip varchar(160), 
        token_ssp2[59] = bytesToHex(token_ssp2[59].getBytes());//seller_address_country varchar(160), 
        token_ssp2[60] = token_ssp2[60];//seller_id varchar(160),
        token_ssp2[61] = bytesToHex(token_ssp2[61].getBytes());//seller_ip varchar(160), 
        token_ssp2[62] = bytesToHex(token_ssp2[62].getBytes());//seller_email varchar(160), 
        token_ssp2[63] = bytesToHex(token_ssp2[63].getBytes());//seller_first_name varchar(160), 
        token_ssp2[64] = bytesToHex(token_ssp2[64].getBytes());//seller_last_name varchar(160), 
        token_ssp2[65] = bytesToHex(token_ssp2[65].getBytes());//seller_notes varchar(160), 
        token_ssp2[66] = bytesToHex(token_ssp2[66].getBytes());//seller_phone varchar(160), 
        token_ssp2[67] = bytesToHex(token_ssp2[67].getBytes());//seller_logo varchar(160), 
        token_ssp2[68] = bytesToHex(token_ssp2[68].getBytes());//seller_url varchar(160)

        return token_ssp2;

    }//*********************************************************************************************************







    public String[] hex_to_string(String[] token){//**************************************************************************


        token_ssp2 = token;

        token_ssp2[0] =  token_ssp2[0];//id
        token_ssp2[1] =  token_ssp2[1];//hash_id varchar(100),
        token_ssp2[2] =  token_ssp2[2];//sig_id varchar(1160),
        token_ssp2[3] =  token_ssp2[3];//date_id varchar(60),
        token_ssp2[4] =  token_ssp2[4];//owner_id varchar(560),
        token_ssp2[5] =  new String(hexToBytes(token_ssp2[5]));//owner_rating varchar(100), 
        token_ssp2[6] =  new String(hexToBytes(token_ssp2[6]));//currency varchar(160), 
        token_ssp2[7] =  new String(hexToBytes(token_ssp2[7]));//custom_template varchar(160), 
        token_ssp2[8] =  new String(hexToBytes(token_ssp2[8]));//custom_1 varchar(160), 
        token_ssp2[9] =  new String(hexToBytes(token_ssp2[9]));//custom_2 varchar(160), 
        token_ssp2[10] = new String(hexToBytes(token_ssp2[10]));//custom_3 varchar(160), 
        token_ssp2[11] = new String(hexToBytes(token_ssp2[11]));//item_errors varchar(160), 
        token_ssp2[12] = new String(hexToBytes(token_ssp2[12]));//item_date_listed varchar(160), 
        token_ssp2[13] = new String(hexToBytes(token_ssp2[13]));//item_date_listed_day varchar(160), 
        token_ssp2[14] = new String(hexToBytes(token_ssp2[14]));//item_date_listed_int varchar(160), 
        token_ssp2[15] = new String(hexToBytes(token_ssp2[15]));//item_hits varchar(160), 
        token_ssp2[16] = new String(hexToBytes(token_ssp2[16]));//item_confirm_code varchar(160), 
        token_ssp2[17] = new String(hexToBytes(token_ssp2[17]));//item_confirmed varchar(160), 
        token_ssp2[18] = new String(hexToBytes(token_ssp2[18]));//item_cost varchar(160), 
        token_ssp2[19] = new String(hexToBytes(token_ssp2[19]));//item_description varchar(3160), 
        token_ssp2[20] = new String(hexToBytes(token_ssp2[20]));//item_id varchar(160), 
        token_ssp2[21] = new String(hexToBytes(token_ssp2[21]));//item_price varchar(160), 
        token_ssp2[22] = new String(hexToBytes(token_ssp2[22]));//item_weight varchar(160), 
        token_ssp2[23] = new String(hexToBytes(token_ssp2[23]));//item_listing_id varchar(160), 
        token_ssp2[24] = new String(hexToBytes(token_ssp2[24]));//item_notes varchar(160), 
        token_ssp2[25] = new String(hexToBytes(token_ssp2[25]));//item_package_d varchar(160), 
        token_ssp2[26] = new String(hexToBytes(token_ssp2[26]));//item_package_l varchar(160), 
        token_ssp2[27] = new String(hexToBytes(token_ssp2[27]));//item_package_w varchar(160), 
        token_ssp2[28] = new String(hexToBytes(token_ssp2[28]));//item_part_number varchar(160), 
        token_ssp2[29] = new String(hexToBytes(token_ssp2[29]));//item_title varchar(160), 
        token_ssp2[30] = new String(hexToBytes(token_ssp2[30]));//item_title_url varchar(160), 
        token_ssp2[31] = new String(hexToBytes(token_ssp2[31]));//item_type varchar(160), 
        token_ssp2[32] = new String(hexToBytes(token_ssp2[32]));//item_search_1 varchar(160), 
        token_ssp2[33] = new String(hexToBytes(token_ssp2[33]));//item_search_2 varchar(160),
        token_ssp2[34] = new String(hexToBytes(token_ssp2[34]));//item_search_3 varchar(160), 
        token_ssp2[35] = new String(hexToBytes(token_ssp2[35]));//item_site_id varchar(160), 
        token_ssp2[36] = new String(hexToBytes(token_ssp2[36]));//item_site_url varchar(160), 
        token_ssp2[37] = new String(hexToBytes(token_ssp2[37]));//item_picture_1 varchar(160), 
        token_ssp2[38] = new String(hexToBytes(token_ssp2[38]));//item_total_on_hand varchar(160), 
        token_ssp2[39] = new String(hexToBytes(token_ssp2[39]));//sale_payment_address varchar(160), 
        token_ssp2[40] = new String(hexToBytes(token_ssp2[40]));//sale_payment_type varchar(160), 
        token_ssp2[41] = new String(hexToBytes(token_ssp2[41]));//sale_fees varchar(160), 
        token_ssp2[42] = new String(hexToBytes(token_ssp2[42]));//sale_id varchar(160), 
        token_ssp2[43] = new String(hexToBytes(token_ssp2[43]));//sale_seller_id varchar(160), 
        token_ssp2[44] = new String(hexToBytes(token_ssp2[44]));//sale_status varchar(160), 
        token_ssp2[45] = new String(hexToBytes(token_ssp2[45]));//sale_tax varchar(160), 
        token_ssp2[46] = new String(hexToBytes(token_ssp2[46]));//sale_shipping_company varchar(160), 
        token_ssp2[47] = new String(hexToBytes(token_ssp2[47]));//sale_shipping_in varchar(160), 
        token_ssp2[48] = new String(hexToBytes(token_ssp2[48]));//sale_shipping_out varchar(160), 
        token_ssp2[49] = new String(hexToBytes(token_ssp2[49]));//sale_source_of_sale varchar(160), 
        token_ssp2[50] = new String(hexToBytes(token_ssp2[50]));//sale_total_sale_amount varchar(160), 
        token_ssp2[51] = new String(hexToBytes(token_ssp2[51]));//sale_tracking_number varchar(160), 
        token_ssp2[52] = new String(hexToBytes(token_ssp2[52]));//sale_transaction_id varchar(160), 
        token_ssp2[53] = new String(hexToBytes(token_ssp2[53]));//sale_transaction_info varchar(160), 
        token_ssp2[54] = new String(hexToBytes(token_ssp2[54]));//seller_address_1 varchar(160), 
        token_ssp2[55] = new String(hexToBytes(token_ssp2[55]));//seller_address_2 varchar(160), 
        token_ssp2[56] = new String(hexToBytes(token_ssp2[56]));//seller_address_city varchar(160), 
        token_ssp2[57] = new String(hexToBytes(token_ssp2[57]));//seller_address_state varchar(160), 
        token_ssp2[58] = new String(hexToBytes(token_ssp2[58]));//seller_address_zip varchar(160), 
        token_ssp2[59] = new String(hexToBytes(token_ssp2[59]));//seller_address_country varchar(160), 
        token_ssp2[60] = token_ssp2[60];//seller_id varchar(160),
        token_ssp2[61] = new String(hexToBytes(token_ssp2[61]));//seller_ip varchar(160), 
        token_ssp2[62] = new String(hexToBytes(token_ssp2[62]));//seller_email varchar(160), 
        token_ssp2[63] = new String(hexToBytes(token_ssp2[63]));//seller_first_name varchar(160), 
        token_ssp2[64] = new String(hexToBytes(token_ssp2[64]));//seller_last_name varchar(160), 
        token_ssp2[65] = new String(hexToBytes(token_ssp2[65]));//seller_notes varchar(160), 
        token_ssp2[66] = new String(hexToBytes(token_ssp2[66]));//seller_phone varchar(160), 
        token_ssp2[67] = new String(hexToBytes(token_ssp2[67]));//seller_logo varchar(160), 
        token_ssp2[68] = new String(hexToBytes(token_ssp2[68]));//seller_url varchar(160)

        return token_ssp2;

    }//*********************************************************************************************************










    public static byte[] hexToBytes(String s) {

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;

    }



    public static String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for ( int j = 0; j < bytes.length; j++ ) {

            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];

        }//***************************************

        return new String(hexChars);

    }//********************************************







}//class
