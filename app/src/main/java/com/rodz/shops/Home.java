package com.rodz.shops;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick);
    }

    public void shops(View v){
        startActivity(new Intent(this, Shops.class));
    }

    public void products(View v){
        startActivity(new Intent(this, Products.class));
    }

    public void sales(View v){
        startActivity(new Intent(this, Sales.class));
    }

    public void stock(View v){
        startActivity(new Intent(this, Stock.class));
    }

    public void reports(View v){
        startActivity(new Intent(this, Reports.class));
    }
}
