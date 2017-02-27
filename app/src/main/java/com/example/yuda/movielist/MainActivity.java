package com.example.yuda.movielist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {

    // Making the objects to be global
    int currentPosition = 0;
    mySQLHelper mySQLHelper;
    Cursor cursor;
    myCursor adapter;
    ListView myMovieLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the list view
        myMovieLv = (ListView) findViewById(R.id.movieLV);

        // create 'mySQLHelper'
        mySQLHelper = new mySQLHelper(this);

        // get table with name 'Recpie'
        cursor = mySQLHelper.getReadableDatabase().query(DBconstants.tableName, null, null, null, null, null, null);

        //geting the column from DB that you want
        String[] fromColumn = new String[]{DBconstants.titleColumn};

        //put the info to item in list view
        int[] toTV = new int[]{R.id.oneItenTV};

        // initializing the adapter
        adapter = new myCursor(this, cursor);

        // make contact between list view to adapter
        myMovieLv.setAdapter(adapter);

        // Create context Menu
        registerForContextMenu(myMovieLv);

        //get the plus 'Button'
        final Button addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, addBtn);

                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //  Toast.makeText(MainActivity.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        if (item.getItemId() == R.id.addMenualItem) {
                            // open 'add_edit_activity'
                            Intent intent = new Intent(MainActivity.this, addEditActivity.class);
                            startActivity(intent);
                        } else {
                            //open 'add_from_internet_activity'
                            Intent intent = new Intent(MainActivity.this, addFromInternetActivity.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                });
                popup.show(); //showing popup menu
            }
        });
        myMovieLv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // get the chosen item from list view
                cursor.moveToPosition(position);

                // Get the details of this row
                String movieName = cursor.getString(cursor.getColumnIndex(DBconstants.titleColumn));
                String movieDesc = cursor.getString(cursor.getColumnIndex(DBconstants.descColumn));
                String moviePoster = cursor.getString(cursor.getColumnIndex(DBconstants.posterColumn));
                int movieID = cursor.getInt(cursor.getColumnIndex(DBconstants.idColumn));

                // Enter all details to intent
                Intent intent = new Intent(MainActivity.this, addEditActivity.class);

                intent.putExtra("Title", movieName);
                intent.putExtra("desc", movieDesc);
                intent.putExtra("Poster", moviePoster);
                intent.putExtra("id", movieID);

                // Get editRecpie activity
                startActivity(intent);
            }
        });
    }

     /**
     * this method is refresh the list view
     */
    public void initListViewFromDb() {
        // get table with name 'Movies'
        cursor = mySQLHelper.getReadableDatabase().query(DBconstants.tableName, null, null, null, null, null, null);

        // updating the cursor
        adapter.swapCursor(cursor);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        // get the position of chosen item from list view
        currentPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // get the chosen item from list view
        cursor.moveToPosition(currentPosition);

        // Get the details of this row
        String recpieTitle = cursor.getString(cursor.getColumnIndex(DBconstants.titleColumn));
        String descMovie = cursor.getString(cursor.getColumnIndex(DBconstants.descColumn));
        String moviePoster = cursor.getString(cursor.getColumnIndex(DBconstants.posterColumn));
        final int movieID = cursor.getInt(cursor.getColumnIndex(DBconstants.idColumn));

        // if edit clicked
        if (item.getItemId() == R.id.editItemMenu) {
            // Enter all details to intent
            Intent intent = new Intent(this, addEditActivity.class);
            intent.putExtra("Title", recpieTitle);
            intent.putExtra("desc", descMovie);
            intent.putExtra("id", movieID);
            intent.putExtra("Poster", moviePoster);

            // Get editRecpie activity
            startActivity(intent);
        }
        // if delete clicked
        else if (item.getItemId() == R.id.deleteItemMenu)
        {
            // Show message - if you want to delete this row
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("are you sure you want to delete");
            alertDialogBuilder
                    .setMessage("Click yes to delete!")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        // if clicked yes
                        public void onClick(DialogInterface dialog, int id) {
                            // Delete this row from db - by id
                            mySQLHelper.getWritableDatabase().delete(DBconstants.tableName, "_id=?", new String[]{"" + movieID});
                            MainActivity.this.initListViewFromDb();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        // if clicked no
                        public void onClick(DialogInterface dialog, int id) {
                            // close this dialog
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else if (item.getItemId() == R.id.rankingItemMenu)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            final RatingBar ratingBar = new RatingBar(getApplicationContext());

            ratingBar.setRating(0);
            ratingBar.setStepSize(1);
            ratingBar.setNumStars(2);

            builder.setTitle("rank the movie")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(ratingBar)
                    .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // TODO make the item background change by the .
                        }
                    })
                    .setNegativeButton("Cancel", null);
            builder.show();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // if 'delete all' item was clicked delete any details Data Base
        if (item.getItemId() == R.id.deleteAllItem)
        {
            // Show message - if you want to delete this row
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("are you sure you want to delete all details");
            alertDialogBuilder
                    .setMessage("Click yes to delete!")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        // if clicked yes
                        public void onClick(DialogInterface dialog, int id) {
                            // Delete this row from db - by id
                            mySQLHelper.getWritableDatabase().delete(DBconstants.tableName, null, null);
                            MainActivity.this.initListViewFromDb();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        // if clicked no
                        public void onClick(DialogInterface dialog, int id) {
                            // close this dialog
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else if (item.getItemId() == R.id.closeAppItem)
        {
            finish();
        }
        return true;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.initListViewFromDb();
    }



}
