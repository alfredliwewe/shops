package com.rodz.shops;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.textfield.TextInputLayout;

public class Products extends AppCompatActivity {
    SQLiteDatabase db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.products);

        DbHelper helper = new DbHelper(this);
        db = helper.getWritableDatabase();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        readProducts();
    }

    @SuppressLint("Range")
    public void readProducts(){
        LinearLayout main = (LinearLayout) findViewById(R.id.content);
        main.removeAllViews();

        Cursor c = db.rawQuery("SELECT * FROM products", null);
        while(c.moveToNext()){
            LinearLayout mm = new LinearLayout(this);
            mm.setOrientation(LinearLayout.VERTICAL);
            mm.setPadding(dpToPx(15), dpToPx(10), dpToPx(15), dpToPx(10));

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            //row.setPadding(dpToPx(15), dpToPx(10), dpToPx(15), dpToPx(10));
            mm.addView(row);

            LinearLayout ft = new LinearLayout(this);
            ft.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView category = new TextView(this);
            category.setText(c.getString(c.getColumnIndex("name")));
            category.setTextSize(17.0f);
            category.setTypeface(category.getTypeface(), Typeface.BOLD);
            category.setTextColor(Color.BLACK);
            ft.addView(category);
            row.addView(ft);

            LinearLayout sc = new LinearLayout(this);
            sc.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            sc.setGravity(Gravity.RIGHT);

            ImageView viewall = new ImageView(this);
            viewall.setImageResource(R.drawable.ic_more_horizontal);
            @SuppressLint("Range") final String category_id = c.getString(c.getColumnIndex("id"));
            viewall.setOnClickListener(new View.OnClickListener(){
                public void onClick(View p1){
                    //populate a menu
                    PopupMenu popupMenu = new PopupMenu(Products.this, p1);
                    popupMenu.getMenuInflater().inflate(R.menu.shop_tools, popupMenu.getMenu());
                    popupMenu.show();
                }
            });
            sc.addView(viewall);
            row.addView(sc);

            TextView qty = new TextView(this);
            qty.setText("Buying: "+c.getInt(c.getColumnIndex("buying")));
            TextView subtotal = new TextView(this);
            subtotal.setText("Selling: "+c.getInt(c.getColumnIndex("selling"))+"\n");

            View line = new View(this);
            line.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            line.setBackgroundColor(Color.GRAY);

            mm.addView(qty);
            mm.addView(subtotal);
            mm.addView(line);
            main.addView(mm);
        }
    }

    public int dpToPx(int dp){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public void newProduct(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Product");

        View cont = getLayoutInflater().inflate(R.layout.apply, null);
        builder.setView(cont);

        builder.setPositiveButton((CharSequence) "Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //EditText product = (EditText) dialogInterface.findViewById(R.id.product);
                EditText product = (EditText) ((AlertDialog) dialogInterface).findViewById(R.id.product);
                EditText buying = (EditText) ((AlertDialog) dialogInterface).findViewById(R.id.buying);
                EditText selling = (EditText) ((AlertDialog) dialogInterface).findViewById(R.id.selling);
                /*
                EditText buying = (EditText) builder.findViewById(R.id.buying);
                EditText selling = (EditText) builder.findViewById(R.id.selling); */

                db.execSQL("INSERT INTO products (id, name, buying, selling) VALUES (NULL, ?, ?, ?)", new String[]{product.getText().toString(), buying.getText().toString(), selling.getText().toString()});
                dialogInterface.cancel();
                readProducts();
            }
        });

        builder.setNegativeButton((CharSequence) "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}
