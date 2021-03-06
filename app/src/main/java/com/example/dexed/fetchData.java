package com.example.dexed;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class fetchData extends AsyncTask<Void, Void, Void> {

    protected String data = "";
    protected String name = "";
    protected String height = "";
    protected String weight = "";
    protected String id = "";
    protected int simpleID = 0;
    protected String strType1;// Create an ArrayList object
    protected String strType2;
    protected String pokSearch;
    @SuppressLint("StaticFieldLeak")
    protected Context context;

    public fetchData(String pokSearch, Context context) {
        this.pokSearch = pokSearch;
        strType1 = "";
        strType2 = "";
        this.context =context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            //Make API connection

            URL urlPokemon = new URL("https://pokeapi.co/api/v2/pokemon/" + pokSearch);
            Log.i("logtest", "https://pokeapi.co/api/v2/pokemon/" + pokSearch);

            HttpURLConnection httpURLConnection = (HttpURLConnection) urlPokemon.openConnection();

            // Read API results
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sBuilder = new StringBuilder();

            // Build JSON String
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sBuilder.append(line).append("\n");
            }
            inputStream.close();
            data = sBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        JSONObject jObject;
        String img = "";
        String icon = "";


        try {
            jObject = new JSONObject(data);

            // Get JSON name, height, weight
            name += "Name: " + jObject.getString("name");
            height += "Height: " + jObject.getString("height");
            weight += "Weight: " + jObject.getString("weight");
            id += "ID:" + jObject.getString("id");
            simpleID = jObject.getInt("id");

            // Get type/types
            JSONArray types = new JSONArray(jObject.getString("types"));
            for (int i = 0; i < types.length(); i++) {
                JSONObject type = new JSONObject(types.getString(i));
                JSONObject type2 = new JSONObject(type.getString("type"));
                if (i == 0) {
                    strType1 = type2.getString("name");
                    String strimgtype1 = "https://raw.githubusercontent.com/msikma/pokesprite/master/misc/types/gen8/" + strType1 + ".png";
                    Glide.with(context).load(strimgtype1).apply(new RequestOptions().override(80, 80)).into(MainActivity.imgType1);
                }

                MainActivity.imgType2.setVisibility(View.INVISIBLE);

                if (i == 1) {
                    strType2 = type2.getString("name");
                    String strimgtype2 = "https://raw.githubusercontent.com/msikma/pokesprite/master/misc/types/gen8/" + strType2 + ".png";
                    Glide.with(context).load(strimgtype2).apply(new RequestOptions().override(80, 80)).into(MainActivity.imgType2);
                    MainActivity.imgType2.setVisibility(View.VISIBLE);
                }
            }

            Log.d("TAG", strType1);
            Log.d("TAG", strType2);

            // Get img SVG

            JSONObject sprites = new JSONObject(jObject.getString("sprites"));
            JSONObject versions = new JSONObject(sprites.getString("versions"));

            if (simpleID > 649){
                img = sprites.getString("front_default");
            } else {

                JSONObject generation = new JSONObject(versions.getString("generation-v"));
                JSONObject blackWhite = new JSONObject(generation.getString("black-white"));
                JSONObject animated = new JSONObject(blackWhite.getString("animated"));

                img = animated.getString("front_default");

            }

            JSONObject generation1 = new JSONObject(versions.getString("generation-viii"));
            JSONObject icons = new JSONObject(generation1.getString("icons"));

            icon = icons.getString("front_default");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        fetchData1 process1 = new fetchData1(simpleID, context);
        process1.execute();

        // Set info
        MainActivity.txtName.setText(this.name);
        MainActivity.txtHeight.setText(this.height);
        MainActivity.txtWeight.setText(this.weight);
        MainActivity.txtID.setText(this.id);
        MainActivity.getID = this.id;
        MainActivity.simpleID = this.simpleID;
        Glide.with(context).load(img).transition(withCrossFade()).into(MainActivity.imgPok);
        Glide.with(context).load(icon).transition(withCrossFade()).into(MainActivity.icon);
        Log.d("TAG", this.name);
        Log.d("TAG", this.height);
        Log.d("TAG", this.weight);
    }
}
