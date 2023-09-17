package com.example.guessmyage;

import static android.content.ContentValues.TAG;

import android.content.Intent;
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

    public void onSubmit(View view) {
        TextView resultText = findViewById(R.id.result);

        String url = "https://api.agify.io?name=";
        String name = ((EditText) findViewById(R.id.name)).getText().toString();
        String country = ((EditText) findViewById(R.id.country)).getText().toString();
        if (name==null)
            return;
        else {
            url += name;
            if (country!=null) {
                url += country;
            }
        }

        queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            int index = response.indexOf("\"age\":") + 6;
            int responseAge = 0;
            String age = "";
            while (!response.substring(index,index+1).equals("}")) {
                age += response.substring(index,index+1);
                index++;
            }

            if (age.equals("null")) {
                recentResult = "For the name: " + name + ". Wow, we can't predict your age at all. Your name is a rarity!";
                resultText.setText(recentResult);
            } else {
                responseAge = Integer.valueOf(age);
                recentResult = "For the name: " + name + ". We think you might be " + responseAge + " years old.";
                resultText.setText(recentResult);
            }

        }, error -> {
            Log.i(TAG, "Error :" + error.toString());
            resultText.setText("Error logged. Did you enter a name?");
        });

        queue.add(request);
    }

    public void onShare(View view) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, recentResult);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}
