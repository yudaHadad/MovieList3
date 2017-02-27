package com.example.yuda.movielist;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class addEditActivity extends AppCompatActivity
{
    mySQLHelper mySQLHelper;
    Button saveBtn;
    Button cancelBtn;
    EditText titleEt;
    EditText descET;
    EditText urlPictureET;
    int id;
    ImageView downlodedImgeIV;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        downlodedImgeIV= (ImageView)findViewById(R.id.movieImageIV);

        // Get title, desc edit_text
        titleEt = (EditText) findViewById(R.id.nameET);
        descET = (EditText) findViewById(R.id.descET);
        urlPictureET = (EditText) findViewById(R.id.urlPictureET);

        // Get save, canceled Button
        saveBtn = (Button) findViewById(R.id.saveBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);

        // Get all item details from intent
        String title = getIntent().getStringExtra("Title");
        String desc = getIntent().getStringExtra("desc");

        // Put the details into edit text
        titleEt.setText(title);
        descET.setText(desc);

        // Get information from internet activity
        String imdbID = getIntent().getStringExtra("imdbID");
        id = getIntent().getIntExtra("id", -1);

        urlPictureET.setText(getIntent().getStringExtra("Poster"));

        //create mySQLHelper
        mySQLHelper = new mySQLHelper(this);

        // if save clicked
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // get the new details
                String name = titleEt.getText().toString();
                name = name.trim();
                String desc = descET.getText().toString();
                desc = desc.trim();
                String poster = urlPictureET.getText().toString();
                poster = poster.trim();

                if (name.equals("")  | desc.equals(""))
                {
                    Toast.makeText(addEditActivity.this, "You can not insert an empty field", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try
                    {
                        // put all column in one content values
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBconstants.titleColumn, name);
                        contentValues.put(DBconstants.descColumn, desc);
                        contentValues.put(DBconstants.posterColumn, poster);

                        // if this row is new - insert this row to db
                        if (id == -1) {
                            mySQLHelper.getWritableDatabase().insert(DBconstants.tableName, null, contentValues);
                        }
                        // if this row is not new - update this row
                        else {
                            mySQLHelper.getWritableDatabase().update(DBconstants.tableName, contentValues, "_id=?", new String[]{"" + id});
                        }
//                        Intent intent = new Intent(addEditActivity.this, MainActivity.class);
//                        startActivity(intent);
                        finish();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        // if cancel clicked
        cancelBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        DownloadDescraption Download_descraption = new addEditActivity.DownloadDescraption();
        Download_descraption.execute("http://www.omdbapi.com/?i=" + imdbID);


        ((Button) findViewById(R.id.goURLBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String imageUrl= ((EditText) findViewById(R.id.urlPictureET)).getText().toString();
                DownloadImgeTask downloadImgeTask= new DownloadImgeTask();
                downloadImgeTask.execute(imageUrl);

            }
        });
    }

    class DownloadImgeTask extends AsyncTask<String, Void, Bitmap> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(addEditActivity.this);
            dialog.setTitle("connecting");
            dialog.setMessage("please wait...");
            dialog.setCancelable(true);
            dialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap= null;

            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                // open a connection
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = (InputStream) url.getContent();
                bitmap = BitmapFactory.decodeStream(in);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap downloadedImage) {

            downlodedImgeIV.setImageBitmap(downloadedImage);

            dialog.dismiss();
        }
    }


    // Create an inher class
    public class DownloadDescraption extends AsyncTask<String, Long, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            // make a new String Builder
            StringBuilder response = null;

            // Surround with 'try and catch' to get execption if we get one
            try {
                //getting infermation from web site
                URL webSite = new URL(params[0]);
                URLConnection connection = webSite.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } catch (Exception e)
            {

            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String resultJSON)
        {
            try
            {
                JSONObject mainObject = new JSONObject(resultJSON);
                titleEt.setText(mainObject.getString("Title"));
                descET.setText(mainObject.getString("Plot"));
                urlPictureET.setText(mainObject.getString("Poster"));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }



}
