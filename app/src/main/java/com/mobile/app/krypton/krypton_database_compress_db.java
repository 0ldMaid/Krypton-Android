package com.mobile.app.krypton;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class krypton_database_compress_db extends SQLiteOpenHelper {



    @Override
    public void onCreate(SQLiteDatabase db) {
        // this will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted


    }


    //To prevent the database from getting too long on phones we delete the old stuff.
    //This creates a buffer of about 3 months time when nodes need to stay up to date. If a node drops off for more then that time they will need to start over.
    //And they will no longer be able to verify the chain.

    krypton_database_compress_db() {//*************

        super(MainActivity.context2, krypton_database_driver.DATABASE_NAME, null, krypton_database_driver.DATABASE_VERSION);

    }//**************************************************



    public boolean compress_mining_db(){


        //Is this work a success?
        boolean deleted = false;

        try {

            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int numberx = 0;


            try {

                //Get the number of blocks to save
                //We know that there are 25k blocks in the system so if we ask for distinct values from the mining blockchain then the block at 25k should be our oldest block.
                //Everything before that isn't needed.

                String query = ("SELECT xd,link_id FROM mining_db GROUP BY link_id ORDER BY xd DESC LIMIT " + network.hard_token_limit);
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToLast();

                numberx = cursor.getInt(cursor.getColumnIndex("xd"));

                cursor.close();

            } catch (Exception e) {e.printStackTrace();}




            try {

                //Here we are deleting the old blocks that are not needed anymore.
                //If we wanted to make a system like bitcoin we could remove this code and the blockchain would grow forever like bitcoin.

                System.out.println("Last Block ID " + numberx);

                numberx = numberx - network.confirm_before_delete;

                System.out.println("Delete ID     " + numberx);

                //Delete old blocks
                System.out.println("Load ITEM... And delete");

                if(numberx > 0){

                    db.execSQL("DELETE FROM mining_db where xd < " + numberx);
                    db.execSQL("DELETE FROM backup_db where xd < " + numberx);

                    deleted = true;

                }//if***********


            } catch(Exception e) {e.printStackTrace(); deleted = false;}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return deleted;

    }



    //This isn't needed because we can do the same thing in the class above.

    public boolean compress_backup_db2(){//**********************************************************

        //Is this work a success?
        boolean deleted = false;

        try {

            //We are working.
            network.database_in_use = true;

            SQLiteDatabase db = this.getWritableDatabase();

            int numberx = 0;


            try {

                //Get the number of blocks to save.

                String query = ("SELECT xd,link_id FROM mining_db GROUP BY link_id ORDER BY xd DESC LIMIT " + network.hard_token_limit);
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToLast();

                numberx = cursor.getInt(cursor.getColumnIndex("xd"));

                cursor.close();


            } catch (Exception e) {e.printStackTrace();}




            try {

                //Here we are deleting the old blocks that are not needed anymore.
                //If we wanted to make a system like Bitcoin we could remove this code and the Blockchain would grow forever.

                System.out.println("Last Block ID " + numberx);

                numberx = numberx - network.confirm_before_delete;

                System.out.println("Delete ID     " + numberx);

                //Delete old blocks.
                System.out.println("Load ITEM... And delete");

                if(numberx > 0){

                    db.execSQL("DELETE FROM backup_db where xd < " + numberx);

                    deleted = true;

                }//if***********


            } catch (Exception e) {e.printStackTrace(); deleted = false;}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}
        finally {

            network.database_in_use = false;

        }

        return deleted;

    }//load






}//class
