package com.rodz.shops;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

public class Shops extends AppCompatActivity {
    SQLiteDatabase db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shops);
        DbHelper helper = new DbHelper(this);
        db = helper.getWritableDatabase();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readShops();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    public void saveShop(View v){
        EditText shopname = (EditText) findViewById(R.id.shopname);
        if (shopname.getText().toString().isEmpty()){
            Toast.makeText(this, "Write shop name", Toast.LENGTH_SHORT).show();
        }
        else{
            db.execSQL("INSERT INTO shops (id, name) VALUES (NULL, ?)", new String[]{shopname.getText().toString()});
            readShops();
            shopname.getText().clear();
        }
    }

    public int dpToPx(int dp){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @SuppressLint("Range")
    public void readShops(){
        LinearLayout main = (LinearLayout) findViewById(R.id.content);
        main.removeAllViews();

        Cursor c = db.rawQuery("SELECT * FROM shops", null);
        while(c.moveToNext()){
            int shop_id = c.getInt(c.getColumnIndex("id"));
            LinearLayout mm = new LinearLayout(this);
            mm.setOrientation(LinearLayout.VERTICAL);
            mm.setPadding(dpToPx(15), dpToPx(10), dpToPx(15), dpToPx(10));

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            mm.addView(row);

            LinearLayout ft = new LinearLayout(this);
            ft.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            TextView category = new TextView(this);
            category.setText(c.getString(c.getColumnIndex("name")));
            category.setTextSize(17.0f);
            category.setTypeface(category.getTypeface(), Typeface.BOLD);
            category.setTextColor(Color.BLACK);
            ft.addView(category);
            row.addView(ft);

            LinearLayout sc = new LinearLayout(this);
            sc.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            sc.setGravity(Gravity.RIGHT);

            ImageView viewall = new ImageView(this);
            viewall.setImageResource(R.drawable.ic_more_horizontal);
            final String category_id = c.getString(c.getColumnIndex("id"));
            viewall.setOnClickListener(new View.OnClickListener(){
                public void onClick(View p1){
                    //populate a menu
                    PopupMenu popupMenu = new PopupMenu(Shops.this, p1);
                    popupMenu.getMenuInflater().inflate(R.menu.shop_tools, popupMenu.getMenu());
                    popupMenu.show();
                }
            });
            sc.addView(viewall);
            row.addView(sc);

            TextView total = new TextView(this);
            Cursor d = db.rawQuery("SELECT SUM(qty * selling) AS total FROM stock JOIN products ON stock.product = products.id WHERE stock.shop = '"+shop_id+"'", null);
            d.moveToFirst();
            total.setText("K"+d.getInt(d.getColumnIndex("total"))+", in stock \n");
            mm.addView(total);

            View line = new View(this);
            line.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            line.setBackgroundColor(Color.GRAY);
            mm.addView(line);
            main.addView(mm);
        }
    }
}
