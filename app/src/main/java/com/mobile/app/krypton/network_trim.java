package com.mobile.app.krypton;




public class network_trim{

    String[] token_ssp2 = new String[network.listing_size];

    //The size of the listings is limited here we cut down any string that is too long.

    public String[] trim(String[] token){//**************************************************************************


        token_ssp2 = token;

        if(token_ssp2[0].length() > 160){token_ssp2[0] = token_ssp2[0].substring (0, 10);}//id
        if(token_ssp2[1].length() > 160){token_ssp2[1] = token_ssp2[1].substring (0, 160);}//hash_id varchar(100),
        if(token_ssp2[2].length() > 1160){token_ssp2[2] = token_ssp2[2].substring (0, 1160);}//sig_id varchar(1160),
        if(token_ssp2[3].length() > 160){token_ssp2[3] = token_ssp2[3].substring (0, 160);}//date_id varchar(60),
        if(token_ssp2[4].length() > 560){token_ssp2[4] = token_ssp2[4].substring (0, 560);}//owner_id varchar(560),
        if(token_ssp2[5].length() > 160){token_ssp2[5] = token_ssp2[5].substring (0, 160);}//owner_rating varchar(100),
        if(token_ssp2[6].length() > 160){token_ssp2[6] = token_ssp2[6].substring (0, 160);}//currency varchar(160),
        if(token_ssp2[7].length() > 160){token_ssp2[7] = token_ssp2[7].substring (0, 160);}//custom_template varchar(160),
        if(token_ssp2[8].length() > 160){token_ssp2[8] = token_ssp2[8].substring (0, 160);}//custom_1 varchar(160),
        if(token_ssp2[9].length() > 160){token_ssp2[9] = token_ssp2[9].substring (0, 160);}//custom_2 varchar(160),
        if(token_ssp2[10].length() > 160){token_ssp2[10] = token_ssp2[10].substring(0, 160);}//custom_3 varchar(160),
        if(token_ssp2[11].length() > 160){token_ssp2[11] = token_ssp2[11].substring(0, 160);}//item_errors varchar(160),
        if(token_ssp2[12].length() > 160){token_ssp2[12] = token_ssp2[12].substring(0, 160);}//item_date_listed varchar(160),
        if(token_ssp2[13].length() > 160){token_ssp2[13] = token_ssp2[13].substring(0, 160);}//item_date_listed_day varchar(160),
        if(token_ssp2[14].length() > 160){token_ssp2[14] = token_ssp2[14].substring(0, 160);}//item_date_listed_int varchar(160),
        if(token_ssp2[15].length() > 160){token_ssp2[15] = token_ssp2[15].substring(0, 160);}//item_hits varchar(160),
        if(token_ssp2[16].length() > 160){token_ssp2[16] = token_ssp2[16].substring(0, 160);}//item_confirm_code varchar(160),
        if(token_ssp2[17].length() > 160){token_ssp2[17] = token_ssp2[17].substring(0, 160);}//item_confirmed varchar(160),
        if(token_ssp2[18].length() > 160){token_ssp2[18] = token_ssp2[18].substring(0, 160);}//item_cost varchar(160),
        if(token_ssp2[19].length() > 20000){token_ssp2[19] = token_ssp2[19].substring(0, 20000);}//item_description varchar(3160), 
        if(token_ssp2[20].length() > 160){token_ssp2[20] = token_ssp2[20].substring(0, 160);}//item_id varchar(160),
        if(token_ssp2[21].length() > 160){token_ssp2[21] = token_ssp2[21].substring(0, 160);}//item_price varchar(160),
        if(token_ssp2[22].length() > 160){token_ssp2[22] = token_ssp2[22].substring(0, 160);}//item_weight varchar(160),
        if(token_ssp2[23].length() > 160){token_ssp2[23] = token_ssp2[23].substring(0, 160);}//item_listing_id varchar(160),
        if(token_ssp2[24].length() > 160){token_ssp2[24] = token_ssp2[24].substring(0, 160);}//item_notes varchar(160),
        if(token_ssp2[25].length() > 160){token_ssp2[25] = token_ssp2[25].substring(0, 160);}//item_package_d varchar(160),
        if(token_ssp2[26].length() > 160){token_ssp2[26] = token_ssp2[26].substring(0, 160);}//item_package_l varchar(160),
        if(token_ssp2[27].length() > 160){token_ssp2[27] = token_ssp2[27].substring(0, 160);}//item_package_w varchar(160),
        if(token_ssp2[28].length() > 160){token_ssp2[28] = token_ssp2[28].substring(0, 160);}//item_part_number varchar(160),
        if(token_ssp2[29].length() > 160){token_ssp2[29] = token_ssp2[29].substring(0, 160);}//item_title varchar(160), 
        if(token_ssp2[30].length() > 160){token_ssp2[30] = token_ssp2[30].substring(0, 160);}//item_title_url varchar(160), 
        if(token_ssp2[31].length() > 160){token_ssp2[31] = token_ssp2[31].substring(0, 160);}//item_type varchar(160), 
        if(token_ssp2[32].length() > 160){token_ssp2[32] = token_ssp2[32].substring(0, 160);}//item_search_1 varchar(160), 
        if(token_ssp2[33].length() > 160){token_ssp2[33] = token_ssp2[33].substring(0, 160);}//item_search_2 varchar(160),
        if(token_ssp2[34].length() > 160){token_ssp2[34] = token_ssp2[34].substring(0, 160);}//item_search_3 varchar(160), 
        if(token_ssp2[35].length() > 160){token_ssp2[35] = token_ssp2[35].substring(0, 160);}//item_site_id varchar(160), 
        if(token_ssp2[36].length() > 160){token_ssp2[36] = token_ssp2[36].substring(0, 160);}//item_site_url varchar(160), 
        if(token_ssp2[37].length() > 400){token_ssp2[37] = token_ssp2[37].substring(0, 400);}//item_picture_1 varchar(160),
        if(token_ssp2[38].length() > 160){token_ssp2[38] = token_ssp2[38].substring(0, 160);}//item_total_on_hand varchar(160), 
        if(token_ssp2[39].length() > 160){token_ssp2[39] = token_ssp2[39].substring(0, 160);}//sale_payment_address varchar(160), 
        if(token_ssp2[40].length() > 160){token_ssp2[40] = token_ssp2[40].substring(0, 160);}//sale_payment_type varchar(160), 
        if(token_ssp2[41].length() > 160){token_ssp2[41] = token_ssp2[41].substring(0, 160);}//sale_fees varchar(160), 
        if(token_ssp2[42].length() > 160){token_ssp2[42] = token_ssp2[42].substring(0, 160);}//sale_id varchar(160), 
        if(token_ssp2[43].length() > 160){token_ssp2[43] = token_ssp2[43].substring(0, 160);}//sale_seller_id varchar(160), 
        if(token_ssp2[44].length() > 160){token_ssp2[44] = token_ssp2[44].substring(0, 160);}//sale_status varchar(160), 
        if(token_ssp2[45].length() > 160){token_ssp2[45] = token_ssp2[45].substring(0, 160);}//sale_tax varchar(160), 
        if(token_ssp2[46].length() > 160){token_ssp2[46] = token_ssp2[46].substring(0, 160);}//sale_shipping_company varchar(160), 
        if(token_ssp2[47].length() > 160){token_ssp2[47] = token_ssp2[47].substring(0, 160);}//sale_shipping_in varchar(160), 
        if(token_ssp2[48].length() > 160){token_ssp2[48] = token_ssp2[48].substring(0, 160);}//sale_shipping_out varchar(160), 
        if(token_ssp2[49].length() > 160){token_ssp2[49] = token_ssp2[49].substring(0, 160);}//sale_source_of_sale varchar(160), 
        if(token_ssp2[50].length() > 160){token_ssp2[50] = token_ssp2[50].substring(0, 160);}//sale_total_sale_amount varchar(160), 
        if(token_ssp2[51].length() > 160){token_ssp2[51] = token_ssp2[51].substring(0, 160);}//sale_tracking_number varchar(160), 
        if(token_ssp2[52].length() > 160){token_ssp2[52] = token_ssp2[52].substring(0, 160);}//sale_transaction_id varchar(160), 
        if(token_ssp2[53].length() > 160){token_ssp2[53] = token_ssp2[53].substring(0, 160);}//sale_transaction_info varchar(160), 
        if(token_ssp2[54].length() > 160){token_ssp2[54] = token_ssp2[54].substring(0, 160);}//seller_address_1 varchar(160), 
        if(token_ssp2[55].length() > 160){token_ssp2[55] = token_ssp2[55].substring(0, 160);}//seller_address_2 varchar(160), 
        if(token_ssp2[56].length() > 160){token_ssp2[56] = token_ssp2[56].substring(0, 160);}//seller_address_city varchar(160), 
        if(token_ssp2[57].length() > 160){token_ssp2[57] = token_ssp2[57].substring(0, 160);}//seller_address_state varchar(160), 
        if(token_ssp2[58].length() > 160){token_ssp2[58] = token_ssp2[58].substring(0, 160);}//seller_address_zip varchar(160), 
        if(token_ssp2[59].length() > 160){token_ssp2[59] = token_ssp2[59].substring(0, 160);}//seller_address_country varchar(160), 
        if(token_ssp2[60].length() > 160){token_ssp2[60] = token_ssp2[60].substring(0, 160);}//seller_id varchar(160), 
        if(token_ssp2[61].length() > 160){token_ssp2[61] = token_ssp2[61].substring(0, 160);}//seller_ip varchar(160), 
        if(token_ssp2[62].length() > 160){token_ssp2[62] = token_ssp2[62].substring(0, 160);}//seller_email varchar(160), 
        if(token_ssp2[63].length() > 160){token_ssp2[63] = token_ssp2[63].substring(0, 160);}//seller_first_name varchar(160), 
        if(token_ssp2[64].length() > 160){token_ssp2[64] = token_ssp2[64].substring(0, 160);}//seller_last_name varchar(160), 
        if(token_ssp2[65].length() > 160){token_ssp2[65] = token_ssp2[65].substring(0, 160);}//seller_notes varchar(160), 
        if(token_ssp2[66].length() > 160){token_ssp2[66] = token_ssp2[66].substring(0, 160);}//seller_phone varchar(160), 
        if(token_ssp2[67].length() > 160){token_ssp2[67] = token_ssp2[67].substring(0, 160);}//seller_logo varchar(160), 
        if(token_ssp2[68].length() > 160){token_ssp2[68] = token_ssp2[68].substring(0, 160);}//seller_url varchar(160)



        return token_ssp2;

    }//*********************************************************************************************************






}//class
