package com.example.svita.drag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.svita.drag.prvkose.Prvek;
import com.example.svita.drag.prvkose.UlozCoPujde;

public class FunkcePrvku extends AppCompatActivity {
    Prvek kteryLeti=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent jeTamNejaky=this.getIntent();
        UlozCoPujde nejaka=(UlozCoPujde) jeTamNejaky.getSerializableExtra("Obsluha");
        if(nejaka!=null){
            kteryLeti=Prvek.zalozSpravnyPrvek(nejaka);
            setContentView(kteryLeti.getFunkcniLayout());
        }
        else {
            Intent kolobezka=new Intent(this,MainActivity.class);
            startActivity(kolobezka);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        kteryLeti.fachej(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        kteryLeti.uzNeFachej(this);
    }
}

