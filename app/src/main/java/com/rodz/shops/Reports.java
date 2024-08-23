package com.rodz.shops;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class Reports extends AppCompatActivity {
    AutoCompleteTextView type;
    AutoCompleteTextView shops;
    EditText startdate;
    EditText enddate;
    EditText currentEdit;
    SQLiteDatabase db;
    int mYear;
    int mMonth;
    int mDay;
    int mHour, mMinute;
    @SuppressLint("Range")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reports);

        DbHelper helper = new DbHelper(this);
        db = helper.getWritableDatabase();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        type = (AutoCompleteTextView) findViewById(R.id.type);
        ArrayAdapter aa1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, new String[]{"Sales", "Stock"});
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(aa1);

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
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, av);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shops.setAdapter(aa);

        startdate = (EditText) findViewById(R.id.startdate);
        startdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    currentEdit = startdate;
                    //date_mode = "departure";
                    setDate();
                }
            }
        });
        enddate = (EditText) findViewById(R.id.enddate);
        enddate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    currentEdit = enddate;
                    //date_mode = "departure";
                    setDate();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setDate(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                String date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                currentEdit.setText(date_time);

            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void viewReport(View v){
        try{
            //
            String dtStart = enddate.getText().toString();
            SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");
            try {
                Date date = format.parse(dtStart);
                Toast.makeText(this, date.getMonth(), Toast.LENGTH_SHORT).show();
                System.out.println(date);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception ee){
            Toast.makeText(this, ee.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
