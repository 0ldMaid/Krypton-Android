package com.mobile.app.krypton;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class krypton_database_search extends SQLiteOpenHelper {




    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //This is called from the server class when a outside user of TOR wants to see an item in their browser.

    public krypton_database_search(){

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }



    public String[][] search(String id){//**************************************************************************

        String[][] tokens_ssp2 = null;

        try {


            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int ix0 = 0;


            try {


                System.out.println("Load listings_db... get search items" );

                id = id.toLowerCase();

                boolean found_items = false;

                System.out.println("Search: " + id);

                //we are getting the oldest items first this incentivises people not to update their tokens too often.
                String query = ("SELECT id,title,price,currency,seller_address_country,item_search_1,item_search_2,item_search_3 FROM searchx WHERE title LIKE ? ORDER BY xd ASC LIMIT 100");
                Cursor cursor = db.rawQuery(query, new String[]{"%" + id + "%"});

                int rowCountx = cursor.getCount();
                tokens_ssp2 = new String[8][rowCountx];

                System.out.println("rowCountx " + rowCountx);

                cursor.moveToFirst();

                ix0 = 0;
                if (rowCountx > 0) {

                    tokens_ssp2[0][ix0] = cursor.getString(cursor.getColumnIndex("id"));
                    tokens_ssp2[1][ix0] = cursor.getString(cursor.getColumnIndex("title"));
                    tokens_ssp2[2][ix0] = cursor.getString(cursor.getColumnIndex("price"));
                    tokens_ssp2[3][ix0] = cursor.getString(cursor.getColumnIndex("currency"));
                    tokens_ssp2[4][ix0] = cursor.getString(cursor.getColumnIndex("seller_address_country"));
                    tokens_ssp2[5][ix0] = cursor.getString(cursor.getColumnIndex("item_search_1"));
                    tokens_ssp2[6][ix0] = cursor.getString(cursor.getColumnIndex("item_search_2"));
                    tokens_ssp2[7][ix0] = cursor.getString(cursor.getColumnIndex("item_search_3"));

                    ix0++;
                    found_items = true;

                }//*****************

                //db starts before first here we have to make up for that move
                cursor.moveToNext();

                while (!cursor.isAfterLast()) {

                    tokens_ssp2[0][ix0] = cursor.getString(cursor.getColumnIndex("id"));
                    tokens_ssp2[1][ix0] = cursor.getString(cursor.getColumnIndex("title"));
                    tokens_ssp2[2][ix0] = cursor.getString(cursor.getColumnIndex("price"));
                    tokens_ssp2[3][ix0] = cursor.getString(cursor.getColumnIndex("currency"));
                    tokens_ssp2[4][ix0] = cursor.getString(cursor.getColumnIndex("seller_address_country"));
                    tokens_ssp2[5][ix0] = cursor.getString(cursor.getColumnIndex("item_search_1"));
                    tokens_ssp2[6][ix0] = cursor.getString(cursor.getColumnIndex("item_search_2"));
                    tokens_ssp2[7][ix0] = cursor.getString(cursor.getColumnIndex("item_search_3"));

                    ix0++;
                    found_items = true;

                    cursor.moveToNext();

                }//while


                System.out.println("found_items " + found_items);

                cursor.close();


            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return tokens_ssp2;

    }//load





}//class
