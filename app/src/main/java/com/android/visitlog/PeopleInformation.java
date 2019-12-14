package com.android.visitlog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
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
    TextView days,hours;
    EditText inputYear;
    DBHelper helper;
    String Year,Month,Name;
    ItemCommentAdapter itemCommentAdapter;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_information);

        helper = new DBHelper(this);

        days = findViewById(R.id.valueOfDays);
        hours = findViewById(R.id.valueOfHours);

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

    private void DrawGraph(){
        graphView.getSeries().clear();

        DataPoint [] dataPoint = new DataPoint[datas.size()];

        for(int i = 0;i<dataPoint.length;i++){
            dataPoint[i] = new DataPoint(Double.valueOf(datas.get(i)),helper.getHoursByDay(Name,String.valueOf(datas.get(i)),Month,Year));
            Log.d("day",String.valueOf(datas.get(i)));
            Log.d("hours",String.valueOf(helper.getHoursByDay(Name,String.valueOf(datas.get(i)),Month,Year)));
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoint);
        series.setTitle("Hours/Day");
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graphView.addSeries(series);
        graphView.getViewport().setScalable(true);
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
        days.setText(helper.CameDaysInMonth(0,Name,Year,Month));
        hours.setText(helper.AvgHours(0,Name,Year,Month));
        DrawGraph();
    }
}
