package com.example.svita.drag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

