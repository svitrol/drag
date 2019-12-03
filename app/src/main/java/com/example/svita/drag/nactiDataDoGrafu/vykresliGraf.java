package com.example.svita.drag.nactiDataDoGrafu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;

import com.example.svita.drag.R;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;

public class vykresliGraf extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vykresli_graf);
        Intent cil=getIntent();
        ArrayList<CharSequence> lisovniTajemstvi=cil.getCharSequenceArrayListExtra("Graf");
        LineChart Graf=findViewById(R.id.graf);
        HttpHandler nactiGraf=new HttpHandler(this,Graf);
        nactiGraf.execute(lisovniTajemstvi.get(0).toString(),lisovniTajemstvi.get(1).toString(),lisovniTajemstvi.get(2).toString(),lisovniTajemstvi.get(3).toString(),lisovniTajemstvi.get(4).toString(),lisovniTajemstvi.get(5).toString());
    }
}
