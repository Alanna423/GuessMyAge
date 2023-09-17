package com.example.guessmyage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class History extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_page);

        onShow();
    }

    public void onHome(View view) {
        startActivity(new Intent(History.this, MainActivity.class));
    }

    public void onGuess(View view) {
        startActivity(new Intent(History.this, Guess.class));
    }

    // retrieves all previous results from SharedPreferences and displays in a list
    public void onShow() {
        TextView log = findViewById(R.id.history_log);

        String text = "";

        SharedPreferences history = getSharedPreferences("History", MODE_PRIVATE);
        Map<String,?> names = history.getAll();
        for (Map.Entry<String, ?> entry : names.entrySet()) {
            text += (String) entry.getValue() + "\n";
        }

        log.setText(text);

        if (text=="") {
            log.setText("No history, try out the guesser first!");
        }
    }

    public void onClear(View view) {
        SharedPreferences history = getSharedPreferences("History", MODE_PRIVATE);
        SharedPreferences.Editor editor = history.edit();
        editor.clear();
        editor.apply();

        TextView log = findViewById(R.id.history_log);
        log.setText("");
    }
}
