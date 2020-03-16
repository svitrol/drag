package com.example.svita.drag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.svita.drag.prvkose.Prvek;
import com.example.svita.drag.prvkose.UlozCoPujde;

public class Vlastnosti extends AppCompatActivity {

    Prvek coNastavuji=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent jeTamNejaky=this.getIntent();
        UlozCoPujde nejaka=(UlozCoPujde) jeTamNejaky.getSerializableExtra("Vlastnosti");
        if(nejaka==null){
            Intent zpatky=new Intent(this,MainActivity.class);
            startActivity(zpatky);
        }
        coNastavuji=Prvek.zalozSpravnyPrvek(nejaka);
        setContentView(coNastavuji.getVlastnostiLayout());
        coNastavuji.nastavSiVlastnosti(this);
    }
    public void nacpyToTam(View v){
        coNastavuji.vemSiCoPotrebujes(this);
        Intent intent=new Intent();
        intent.putExtra("Vysledkos",coNastavuji.getProsteVsecko());
        setResult(RESULT_OK,intent);
        finish();
    }
}
