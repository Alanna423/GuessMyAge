package com.example.guessmyage;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Guess extends AppCompatActivity {

    private RequestQueue queue;
    private String recentResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guess_page);
    }

    public void onHome(View view) {
        startActivity(new Intent(Guess.this, MainActivity.class));
    }

    public void onHistory(View view) {
        startActivity(new Intent(Guess.this, History.class));
    }


    //API call to Agify on the user inputted name and country
    public void onSubmit(View view) {
        // textview where results of the API call will be shown
        TextView resultText = findViewById(R.id.result);

        // construct API request string
        String url = "https://api.agify.io?name=";
        String name = ((EditText) findViewById(R.id.name)).getText().toString();
        String country = ((EditText) findViewById(R.id.country)).getText().toString();
        if (name.length()<1) {
            resultText.setText("Please enter a name first!");
            return;
        } else {
            url += name;
            if (country.length()>0)
                url += "&country_id=" + country;
        }

        // Volley library for API calls in Java
        queue = Volley.newRequestQueue(this);


        // GET request information and how to handle response; response is a String returned from the API
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {

            // wonky method of finding the location of age
            int index = response.indexOf("\"age\":") + 6;
            int responseAge = 0;
            String age = "";
            while (!(response.substring(index,index+1).equals("}") || response.substring(index,index+1).equals(","))) {
                age += response.substring(index,index+1);
                index++;
            }

            // SharedPreferences for storing the results for the history page
            SharedPreferences history = getSharedPreferences("History", MODE_PRIVATE);
            SharedPreferences.Editor editor = history.edit();

            // rare names that the API can't predict for
            if (age.equals("null")) {
                recentResult = "For the name: " + name + ". Wow, we can't predict your age at all. Your name is a rarity!";
                resultText.setText(recentResult);
            // all other cases
            } else {
                responseAge = Integer.valueOf(age);
                recentResult = "For the name: " + name + ". We think you might be " + responseAge + " years old.";
                resultText.setText(recentResult);
            }

            // save and apply to SharedPreferences
            editor.putString(name, recentResult);
            editor.apply();

        // error trapping
        }, error -> {
            Log.i(TAG, "Error :" + error.toString());
            resultText.setText("Error logged. Did you enter a name?");
        });

        queue.add(request);
    }

    //share results method
    public void onShare(View view) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, recentResult);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}
