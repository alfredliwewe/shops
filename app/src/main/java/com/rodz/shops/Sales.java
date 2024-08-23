package com.rodz.shops;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class Sales extends AppCompatActivity {
    SQLiteDatabase db;
    AutoCompleteTextView shops;
    String shop_id;
    int screenWidth;
    int mYear;
    int mMonth;
    int mDay;
    int mHour, mMinute;
    @SuppressLint("Range")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock);

        DbHelper helper = new DbHelper(this);
        db = helper.getWritableDatabase();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

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
            refill.setText("Add record");
            buttonCont.addView(refill);
            main.addView(buttonCont);

            refill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showRefill();
                }
            });

            Cursor c = db.rawQuery("SELECT *,progress.id AS progress_id FROM progress JOIN products ON progress.product = products.id WHERE progress.shop = '"+shop_id+"' AND progress.status = 'saved'", null);
            int total_sold = 0;
            while (c.moveToNext()) {
                Cursor data = db.rawQuery("SELECT * FROM stock WHERE product = '"+c.getInt(c.getColumnIndex("product"))+"' AND shop = '"+shop_id+"'", null);
                data.moveToFirst();

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
                        PopupMenu popupMenu = new PopupMenu(Sales.this, p1);
                        popupMenu.getMenuInflater().inflate(R.menu.shop_tools, popupMenu.getMenu());
                        popupMenu.show();
                    }
                });
                sc.addView(viewall);
                row.addView(sc);

                LinearLayout bottomRow = new LinearLayout(this);
                bottomRow.setOrientation(LinearLayout.HORIZONTAL);
                bottomRow.setPadding(0, 0, 0, dpToPx(15));
                mm.addView(bottomRow);

                LinearLayout left = new LinearLayout(this);
                left.setOrientation(LinearLayout.VERTICAL);
                left.setLayoutParams(new ViewGroup.LayoutParams(screenWidth/2, ViewGroup.LayoutParams.WRAP_CONTENT));
                bottomRow.addView(left);

                TextView qty = new TextView(this);
                qty.setText("New Qty: "+c.getInt(c.getColumnIndex("qty")));
                TextView subtotal = new TextView(this);
                subtotal.setText("Old Qty: "+data.getInt(data.getColumnIndex("qty")));
                left.addView(qty);
                left.addView(subtotal);

                LinearLayout right = new LinearLayout(this);
                right.setOrientation(LinearLayout.VERTICAL);
                right.setLayoutParams(new ViewGroup.LayoutParams(screenWidth/2, ViewGroup.LayoutParams.WRAP_CONTENT));
                bottomRow.addView(right);

                TextView sold = new TextView(this);
                int num_sold = data.getInt(data.getColumnIndex("qty")) - c.getInt(c.getColumnIndex("qty"));
                sold.setText("Sold: "+num_sold);
                TextView amount = new TextView(this);
                int amount_sold  = num_sold * c.getInt(c.getColumnIndex("selling"));
                amount.setText("Amount: K"+amount_sold);
                right.addView(sold);
                right.addView(amount);


                total_sold += amount_sold;

                View line = new View(this);
                line.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                line.setBackgroundColor(Color.GRAY);


                mm.addView(line);
                main.addView(mm);
            }

            TextView total = new TextView(this);
            total.setText("Total: K"+total_sold);
            total.setTypeface(total.getTypeface(), Typeface.BOLD);
            main.addView(total);

            //add the confirm button
            MaterialButton button = new MaterialButton(this);
            button.setText("Confirm sale");
            button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Sales.this);
                    builder.setTitle("Confirm");
                    builder.setMessage("Are you sure you want to save these sales records?");

                    builder.setPositiveButton((CharSequence) "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //update
                            Cursor g = db.rawQuery("SELECT * FROM progress WHERE shop = '"+shop_id+"' AND status = 'saved'", null);
                            while (g.moveToNext()){
                                ContentValues cv = new ContentValues();
                                cv.put("qty", g.getString(g.getColumnIndex("qty")));
                                db.update("stock", cv, "shop = ? AND product = ?", new String[]{shop_id, g.getString(g.getColumnIndex("product"))});
                            }
                            ContentValues cv = new ContentValues();
                            cv.put("status", "verified");
                            db.update("progress", cv, "shop = ?", new String[]{shop_id});
                            Toast.makeText(Sales.this, "Successfully added sales record", Toast.LENGTH_SHORT).show();
                            dialogInterface.cancel();
                            Sales.this.finish();
                        }
                    });

                    builder.setNegativeButton((CharSequence) "No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });

                    builder.show();
                }
            });
            main.addView(button);
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
        builder.setTitle("Add Record");

        View cont = getLayoutInflater().inflate(R.layout.refill, null);
        builder.setView(cont);

        builder.setPositiveButton((CharSequence) "Add", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText amount = (EditText) ((AlertDialog) dialogInterface).findViewById(R.id.amount);
                AutoCompleteTextView products = (AutoCompleteTextView) ((AlertDialog) dialogInterface).findViewById(R.id.products);
                Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                String z = c.get(Calendar.DAY_OF_YEAR)+"";

                String date_time = mDay + "-" + (mMonth + 1) + "-" + mYear+" "+mHour + ":" + mMinute;

                Cursor f = db.rawQuery("SELECT * FROM products WHERE name = '"+products.getText().toString()+"' ", null);
                f.moveToFirst();
                String id = f.getString(f.getColumnIndex("id"));

                //check first
                Cursor check = db.rawQuery("SELECT * FROM stock WHERE product = '"+id+"' AND shop = '"+shop_id+"'", null);
                if(check.getCount() > 0) {
                    check.moveToFirst();
                    int av_qty = check.getInt(check.getColumnIndex("qty"));
                    if (av_qty >= Integer.parseInt(amount.getText().toString())) {
                        db.delete("progress", "product = ? AND shop = ? AND status = ?", new String[]{id, shop_id, "saved"});
                        db.execSQL("INSERT INTO progress (id, shop, product, qty, status, date_str, day) VALUES (NULL, ?, ?, ?, ?, ?, ?)", new String[]{shop_id, id, amount.getText().toString(), "saved", date_time, z});

                        Toast.makeText(Sales.this, "Successfully saved ", Toast.LENGTH_SHORT).show();
                        dialogInterface.cancel();
                        //readProducts();
                        show(shops.getText().toString());
                    }
                    else{
                        Toast.makeText(Sales.this, "Qty cannot be greater than previous recorded", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(Sales.this, "Product is not available in stock ", Toast.LENGTH_SHORT).show();
                }
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
        TextInputLayout layout = dialog.findViewById(R.id.qty_layout);
        layout.setHint("Current qty");

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
