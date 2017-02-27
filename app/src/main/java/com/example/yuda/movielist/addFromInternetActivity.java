package com.example.yuda.movielist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class addFromInternetActivity extends AppCompatActivity {

    // Making the objects to be global
    EditText titleET;
    ArrayList<myMovie> allmovie;
    ListView moviesLV;
    ArrayAdapter<myMovie> adapter;
    Cursor cursor;
    mySQLHelper mySQLHelper;
    String link;
    String title;
    String imdb;
    String poster;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_from_internet);

        // initializing the Array List
        allmovie = new ArrayList();

        // initializing the Adapter
        adapter = new ArrayAdapter<myMovie>(this, android.R.layout.simple_list_item_1, allmovie);

        // get List View
        moviesLV = (ListView) findViewById(R.id.movieFromInterntLV);
        moviesLV.setAdapter(adapter);

        // get Text View
        titleET = (EditText) findViewById(R.id.titleET);

        // create 'mySQLHelper'
        mySQLHelper = new mySQLHelper(this);

        // get table with name 'Recpie'
        cursor = mySQLHelper.getReadableDatabase().query(DBconstants.tableName, null, null, null, null, null, null);

        Button goSearchBtn = (Button) findViewById(R.id.goSearchBtn);
        goSearchBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                DownloadWebSite downloadWebSite = new DownloadWebSite();
                downloadWebSite.execute("http://www.omdbapi.com/?s=" + titleET.getText().toString());
            }
        });

        moviesLV.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // get the chosen item from list view
            myMovie currentMovie = allmovie.get(position);

                // Enter all details to intent
                Intent intent = new Intent(addFromInternetActivity.this, addEditActivity.class);
                intent.putExtra("imdbID", currentMovie.descreption);

                // Get editRecpie activity
                startActivity(intent);
            }
        });

        ((Button)findViewById(R.id.cancelSearchBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Create an inher class
    public class DownloadWebSite extends AsyncTask<String, Long, String>
    {
        @Override
        protected void onPreExecute()
        {
            dialog = ProgressDialog.show(addFromInternetActivity.this, "", "Loading. Please wait...", true);
        }

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
                e.printStackTrace();
            }
            if (response.toString().contains("Movie not found"))
            {
                return null;
            }
            else
            {
                return response.toString();
            }
        }

        @Override
        protected void onPostExecute(String resultJSON) {
            if (resultJSON == null) {
                Toast.makeText(addFromInternetActivity.this, "Your search did not match any movie", Toast.LENGTH_SHORT).show();

            }
            else
            {
                allmovie.clear();
                try
                {
                    JSONObject mainObject = new JSONObject(resultJSON);

                    JSONArray resultArray = mainObject.getJSONArray("Search");

                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject currentObject = resultArray.getJSONObject(i);

                        title = currentObject.getString("Title");
                        link = currentObject.getString("Poster");
                        imdb = currentObject.getString("imdbID");
                        poster = currentObject.getString("Poster");


                        allmovie.add(new myMovie(title, imdb, link, poster));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
                dialog.dismiss();

        }
    }

}


