package com.example.svita.drag.vychytavkyprozasuvku;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.svita.drag.R;

import java.util.ArrayList;
import java.util.List;

public class makraZasuvky extends AppCompatActivity {
    private static final int PODMINKOS_SPLNENOS = 1000;
    List<Podminkos> listakos=new ArrayList<>();
    RecyclerView recyklac;
    int kliklaPodminka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makra_zasuvky);
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyklac=findViewById(R.id.NejhorsiAnglictinarCoJeVeTride);
        recyklac.setLayoutManager(new LinearLayoutManager(this));
        FloatingActionButton hystyTlacidlo=findViewById(R.id.floatingActionButton);
        hystyTlacidlo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(makraZasuvky.this,tvorbaPodminky.class);
                startActivityForResult(intent, PODMINKOS_SPLNENOS);
            }
        });

        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        kliklaPodminka=-1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==PODMINKOS_SPLNENOS&&resultCode==RESULT_OK)
        {
            Podminkos vysledkos=(Podminkos)data.getSerializableExtra("Podminkos");
            if(kliklaPodminka!=-1){
                listakos.remove(kliklaPodminka);
                if(vysledkos!=null){
                    listakos.add(kliklaPodminka,vysledkos);
                }
                kliklaPodminka=-1;
                recyklac.getAdapter().notifyDataSetChanged();
            }
            else if(vysledkos!=null){
                listakos.add(vysledkos);
                PodminkovyAdaptos adapter = new PodminkovyAdaptos(makraZasuvky.this, listakos, new kliklItem(){
                    @Override
                    public void vowKliknuti(int pozice, View view){
                        kliklaPodminka=pozice;
                        Intent intent=new Intent(makraZasuvky.this,tvorbaPodminky.class);
                        intent.putExtra("Podminkos",listakos.get(pozice));
                        startActivityForResult(intent, PODMINKOS_SPLNENOS);
                    }
                });
                recyklac.setAdapter(adapter);
                recyklac.getAdapter().notifyDataSetChanged();
            }

        }
    }
}
