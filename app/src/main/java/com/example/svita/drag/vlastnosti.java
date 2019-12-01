package com.example.svita.drag;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

public class vlastnosti extends AppCompatActivity {

    Prvek coNastavuji=MainActivity.nastavuju;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(coNastavuji.getVlastnostiLayout());
        coNastavuji.nastavSiVlastnosti(this);
    }
    public void nacpyToTam(View v){
        coNastavuji.vemSiCoPotrebujes(this);
        finish();
    }
}
