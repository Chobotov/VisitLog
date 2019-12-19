package com.android.visitlog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PeopleInformation extends AppCompatActivity {

    Toolbar toolbar;
    Spinner spinnerMonth;
    GraphView graphView;
    ArrayList<Integer>datas;
    TextView days,hours,groupNames;
    EditText inputYear;
    DBHelper helper;
    String Year,Month,Name;
    ItemCommentAdapter itemCommentAdapter;
    RecyclerView recyclerView;

    MenuItem remove;
    MenuItem rename;
    MenuItem search;
    MenuItem edit;
    MenuItem itemSelectedMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_information);

        helper = new DBHelper(this);

        days = findViewById(R.id.valueOfDays);
        hours = findViewById(R.id.valueOfHours);
        groupNames = findViewById(R.id.groupNames);
        //groupNames.setMovementMethod(new ScrollingMovementMethod());

        spinnerMonth = findViewById(R.id.spinnerMonth);
        inputYear = findViewById(R.id.inputYear);

        inputYear.setText(Year);

        Intent intent = getIntent();

        Year = intent.getStringExtra("YEAR");
        Month = intent.getStringExtra("MONTH");
        Name = intent.getStringExtra("NAME");

        toolbar = findViewById(R.id.infoToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        datas = new ArrayList<>();

        datas = helper.GetAllDatas(0,Name,Month,Year);

        Collections.sort(datas);

        itemCommentAdapter = new ItemCommentAdapter(this,Name,Year,Month,datas);

        recyclerView = findViewById(R.id.recyclerViewInfo);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemCommentAdapter);

        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(true);

        graphView = findViewById(R.id.graph);

        UpdateData();

        String [] month ={"Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь",};

        ArrayAdapter<String>adapterMonth = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,month);

        spinnerMonth.setAdapter(adapterMonth);
        spinnerMonth.setPrompt("Месяц");
        spinnerMonth.setSelection(Integer.valueOf(Month)-1);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Month = String.valueOf(position+1);
                update();
                UpdateData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        inputYear.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Year = inputYear.getText().toString();
                update();
                UpdateData();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groups_activity_menu, menu);
        remove = menu.findItem(R.id.removeItem);
        rename = menu.findItem(R.id.renameItem);

        search = menu.findItem(R.id.app_bar_search);
        edit = menu.findItem(R.id.editMod);
        itemSelectedMode = menu.findItem(R.id.itemCheckBox);

        search.setEnabled(false);
        search.setVisible(false);
        edit.setEnabled(false);
        edit.setVisible(false);
        itemSelectedMode.setEnabled(false);
        itemSelectedMode.setVisible(false);

        menu.findItem(R.id.save_data).setVisible(false);
        menu.findItem(R.id.open_data).setVisible(false);

        remove.setOnMenuItemClickListener(menuItem -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setCancelable(true);
            builder.setMessage("Удалить человека " +'"' + Name + '"' + " ?");
            builder.setPositiveButton("Да", (dialogInterface, i) -> {
                helper.removePeople(Name);
                finish();
            });
            builder.setNegativeButton("Нет", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        });

        rename.setOnMenuItemClickListener(menuItem -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setCancelable(true);

            View view1 = LayoutInflater.from(this).inflate(R.layout.add_people_alert, null);

            builder.setView(view1);

            EditText editText = view1.findViewById(R.id.text_edit_alertview);
            editText.setText(Name);

            builder.setPositiveButton(R.string.Add, (dialogInterface, i) -> {

                RenamePeople(editText.getText().toString());

            });
            AlertDialog alertDialog = builder.create();

            alertDialog.show();

            return false;
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    private void RenamePeople(String name) {

        if (!name.equals("")) {

            if (!helper.containsPeople(new People(name))) {

                helper.RenamePeople(Name,name);
                Name = name;

            } else {

                int counter = 2;

                while (helper.containsPeople(new People(name + counter))) {
                    counter++;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setCancelable(true);

                String newName = name + counter;

                builder.setMessage(getResources().getString(R.string.RepeatAlert) + " " + '"' + newName + '"' + " ?");
                builder.setPositiveButton("Да", (dialogInterface, i) -> {
                    helper.RenamePeople(Name, newName);
                    Name = newName;

                });
                builder.setNegativeButton("Нет", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            getSupportActionBar().setTitle(Name);
        } else {
            Toast.makeText(this,
                    getResources().getString(R.string.AlertEmptyGroupName),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void DrawGraph(){
        graphView.getSeries().clear();

        DataPoint [] dataPoint = new DataPoint[datas.size()];

        for(int i = 0;i<dataPoint.length;i++){
            dataPoint[i] = new DataPoint(Double.valueOf(datas.get(i)),helper.getHoursByDay(Name,String.valueOf(datas.get(i)),Month,Year));
            Log.d("day",String.valueOf(datas.get(i)));
            Log.d("hours",String.valueOf(helper.getHoursByDay(Name,String.valueOf(datas.get(i)),Month,Year)));
        }
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(dataPoint);
        series.setTitle(getResources().getString(R.string.hours)+"/"+getResources().getString(R.string.day));
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graphView.addSeries(series);
        graphView.getViewport().setScalable(true);

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series.setSpacing(50);

// draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
    }

    public void update() {
        if (itemCommentAdapter != null)
        {
            ItemCommentAdapter.Month = Month;
            ItemCommentAdapter.Year = Year;
            datas.clear();
            datas.addAll(helper.GetAllDatas(0,Name,Month,Year));
            Collections.sort(datas);
            itemCommentAdapter.notifyDataSetChanged();
        }
    }

    private void UpdateData(){
        days.setText(helper.CameDaysInMonth(0,Name,Year,Month) +" "+ getResources().getString(R.string.informationDay));
        hours.setText(helper.AvgHours(1,Name,Year,Month) + " " + getResources().getString(R.string.informationHours) + "/"  + (helper.AvgHours(0,Name,Year,Month)+" " + getResources().getString(R.string.informationHours)));
        groupNames.setText(helper.FindGroupThisPeople(new People(Name)));
        DrawGraph();
    }
}
