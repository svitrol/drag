package com.example.svita.drag;

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
    Prvek kteryLeti=MainActivity.aktivni;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(kteryLeti.getFunkcniLayout());
        kteryLeti.fachej(this);
    }

}

