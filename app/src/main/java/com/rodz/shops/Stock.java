package com.rodz.shops;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import java.util.ArrayList;

public class Stock extends AppCompatActivity {
    SQLiteDatabase db;
    AutoCompleteTextView shops;
    String shop_id;
    @SuppressLint("Range")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock);

        DbHelper helper = new DbHelper(this);
        db = helper.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS stock (id INTEGER PRIMARY KEY AUTOINCREMENT, shop VARCHAR, product VARCHAR, qty VARCHAR)");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shops = (AutoCompleteTextView) findViewById(R.id.shops);
        String first = null;
        ArrayList<String> av = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM shops", null);
        while (c.moveToNext()){
            if (first == null){
                first = c.getString(c.getColumnIndex("name"));
            }
            av.add(c.getString(c.getColumnIndex("name")));
        }
        ArrayAdapter aa1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, av);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shops.setAdapter(aa1);

        if (first != null){
            show(first);
        }

        shops.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                show(shops.getText().toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("Range")
    public void show(String name){
        Cursor d = db.rawQuery("SELECT * FROM shops WHERE name = '"+name+"'", null);
        if (d.getCount() > 0) {
            d.moveToFirst();
            shop_id = d.getString(d.getColumnIndex("id"));

            LinearLayout main = (LinearLayout) findViewById(R.id.content);
            main.removeAllViews();

            LinearLayout buttonCont = new LinearLayout(this);
            buttonCont.setOrientation(LinearLayout.VERTICAL);
            buttonCont.setPadding(dpToPx(15), dpToPx(10), dpToPx(15), dpToPx(10));

            Button refill = new Button(this);
            refill.setText("Refill");
            buttonCont.addView(refill);
            main.addView(buttonCont);

            refill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showRefill();
                }
            });

            Cursor c = db.rawQuery("SELECT *,stock.id AS stock_id FROM stock JOIN products ON stock.product = products.id WHERE stock.shop = '"+shop_id+"'", null);
            int num_total = 0;
            while (c.moveToNext()) {
                LinearLayout mm = new LinearLayout(this);
                mm.setOrientation(LinearLayout.VERTICAL);
                mm.setPadding(dpToPx(15), dpToPx(10), dpToPx(15), dpToPx(10));

                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
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
                viewall.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View p1) {
                        //populate a menu
                        PopupMenu popupMenu = new PopupMenu(Stock.this, p1);
                        popupMenu.getMenuInflater().inflate(R.menu.shop_tools, popupMenu.getMenu());
                        popupMenu.show();
                    }
                });
                sc.addView(viewall);
                row.addView(sc);
                TextView qty = new TextView(this);
                qty.setText("Qty: "+c.getInt(c.getColumnIndex("qty")));
                TextView subtotal = new TextView(this);
                int subtotal2 = c.getInt(c.getColumnIndex("qty")) * c.getInt(c.getColumnIndex("selling"));
                subtotal.setText("Subtotal: K"+subtotal2+"\n");
                num_total += subtotal2;

                View line = new View(this);
                line.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                line.setBackgroundColor(Color.GRAY);

                mm.addView(qty);
                mm.addView(subtotal);
                mm.addView(line);
                main.addView(mm);
            }

            TextView total = new TextView(this);
            total.setText("Total: K"+num_total);
            total.setTypeface(total.getTypeface(), Typeface.BOLD);
            main.addView(total);
        }
        else{
            Toast.makeText(this, name+" not found", Toast.LENGTH_SHORT).show();
        }
    }

    public int dpToPx(int dp){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @SuppressLint("Range")
    public void showRefill(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Refill");

        View cont = getLayoutInflater().inflate(R.layout.refill, null);
        builder.setView(cont);

        builder.setPositiveButton((CharSequence) "Refill", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText amount = (EditText) ((AlertDialog) dialogInterface).findViewById(R.id.amount);
                AutoCompleteTextView products = (AutoCompleteTextView) ((AlertDialog) dialogInterface).findViewById(R.id.products);

                Cursor f = db.rawQuery("SELECT * FROM products WHERE name = '"+products.getText().toString()+"' ", null);
                f.moveToFirst();
                String id = f.getString(f.getColumnIndex("id"));

                //check first
                Cursor check = db.rawQuery("SELECT * FROM stock WHERE product = '"+id+"' AND shop = '"+shop_id+"'", null);
                if (check.getCount() > 0){
                    check.moveToFirst();
                    String rowid = check.getString(check.getColumnIndex("id"));
                    int qty = Integer.parseInt(amount.getText().toString()) + check.getInt(check.getColumnIndex("qty"));
                    //update
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("qty", qty);
                    db.update("stock", contentValues, "id = ?", new String[]{rowid});
                }
                else{
                    //insert
                    db.execSQL("INSERT INTO stock (id, shop, product, qty) VALUES (NULL, ?, ?, ?)", new String[]{shop_id, id, amount.getText().toString()});
                }
                Toast.makeText(Stock.this, "Successfully saved ", Toast.LENGTH_SHORT).show();
                dialogInterface.cancel();
                //readProducts();
                show(shops.getText().toString());
            }
        });

        builder.setNegativeButton((CharSequence) "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        //builder.show();
        AlertDialog dialog = builder.show();
        AutoCompleteTextView products = dialog.findViewById(R.id.products);

        ArrayList<String> ps = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM products", null);
        while (c.moveToNext()){
            ps.add(c.getString(c.getColumnIndex("name")));
        }
        ArrayAdapter aa1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ps);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        products.setAdapter(aa1);
    }
}
