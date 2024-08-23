package com.rodz.shops;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getSupportActionBar().hide();

        startPage();
    }

    public void startPage() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            startActivity(new Intent(MainActivity.this, Home.class));
                            MainActivity.this.finish();
                        }
                    });
                } catch (Exception ee) {
                }
            }
        });
        thread.start();
    }
}