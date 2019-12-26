package com.example.svita.drag.vychytavkyprozasuvku;

import android.arch.persistence.room.RoomDatabase;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.svita.drag.DbClient;
import com.example.svita.drag.Kamera;
import com.example.svita.drag.MainActivity;
import com.example.svita.drag.Prvek;
import com.example.svita.drag.R;
import com.example.svita.drag.Teplomer;
import com.example.svita.drag.UlozCoPujde;
import com.example.svita.drag.Zarovka;
import com.example.svita.drag.Zasuvka;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class makraZasuvky extends AppCompatActivity {
    private static final int PODMINKOS_SPLNENOS = 1000;
    List<Podminkos> listakos=new ArrayList<>();
    RecyclerView recyklac;
    int kliklaPodminka;
    List<Prvek> prvkySeKterymiBychMohlPracovat;

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
                dostanPrvky();
                String zprava=DostanFormatovanyTvarZpravyProModul();
                odesliMakro noOdesliJe=new odesliMakro(zprava,view);
                noOdesliJe.execute();
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        kliklaPodminka=-1;
        dostanPrvky();
        new dostanMakroZmodulu().execute();
    }
    private void dostanPrvky( ){
        class dostanPrvky extends AsyncTask<Void, Void, List<UlozCoPujde>> {

            @Override
            protected List<UlozCoPujde> doInBackground(Void... voids) {
                List<UlozCoPujde> komponenty = DbClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .getAll();
                return komponenty;
            }

            @Override
            protected void onPostExecute(List<UlozCoPujde> tasks) {
                super.onPostExecute(tasks);
                //vraž tam ten list do layoutu
                VrazTamTenListPrvku(tasks);
            }
        }

        dostanPrvky gt = new dostanPrvky();
        gt.execute();
    }
    private void VrazTamTenListPrvku(final List<UlozCoPujde> prvky){
        //String[] countries =

        prvkySeKterymiBychMohlPracovat=new ArrayList<>();
        //System.out.println("hovado ");
        for (UlozCoPujde jdnotka:prvky) {;
            Prvek novy=null;
            switch (jdnotka.getTypPrvku()){
                case "Teplomer":{
                    novy=new Teplomer();
                    novy.setProsteVsecko(jdnotka);
                    break;
                }
                case "Zarovka":{
                    novy=new Zarovka();
                    novy.setProsteVsecko(jdnotka);
                    break;
                }
                case "Kamera":{
                    novy=new Kamera();
                    novy.setProsteVsecko(jdnotka);
                    break;
                }
                case "Zasuvka":{
                    novy=new Zasuvka();
                    novy.setProsteVsecko(jdnotka);
                    break;
                }
            }
            if(!novy.isPrvekRidici()){
                prvkySeKterymiBychMohlPracovat.add(novy);
            }


        }

    }
    private String DostanFormatovanyTvarZpravyProModul(){
        String konecnaZprva="S";
        List<Prvek> potrebnyPrvky=new ArrayList<>();
        List<String> hodnotyCoBuduPotrebovat=new ArrayList<>();
        //pridej co kdyz nebude zadna hodnota cizi
        for(Podminkos podminka:listakos){
            for(Vyraz vyraz:podminka.getVyrazy()){
                vkladejNovyPrvky(vyraz.getPrvnicast(),potrebnyPrvky,hodnotyCoBuduPotrebovat);
                vkladejNovyPrvky(vyraz.getDruhacast(),potrebnyPrvky,hodnotyCoBuduPotrebovat);
            }
        }
        for(int i=0;i<potrebnyPrvky.size();i++){
            Prvek zlaticko=potrebnyPrvky.get(i);
            konecnaZprva+=String.format("%s:%d[%s];",
                    zlaticko.getUrl().toString()
                    ,zlaticko.getPort()
                    ,zlaticko.dejMiPrikazivo(hodnotyCoBuduPotrebovat.get(i)));
        }

        //Toast.makeText(makraZasuvky.this,"celkem cizich prvku: "+potrebnyPrvky.size()+" celkem hodnot: "+hodnotyCoBuduPotrebovat.size(),Toast.LENGTH_LONG).show();
        if(potrebnyPrvky.size()==0)konecnaZprva+="t";
        else konecnaZprva=konecnaZprva.substring(0,konecnaZprva.length()-1);
        konecnaZprva+="{";
        List<String> vlastniHodnoty= Arrays.asList(MainActivity.aktivni.coMaPrvekPodSebou().split(":"));

        for(int i=0;i<listakos.size();i++){
            Podminkos podminka=listakos.get(i);
            for(Vyraz vyraz:podminka.getVyrazy()){
                konecnaZprva+=String.format("%s%s%s%s",
                        formatovanaStranaVyrazu(vyraz.getPrvnicast(),potrebnyPrvky,hodnotyCoBuduPotrebovat,vlastniHodnoty),
                        vyraz.getNerovnost(),
                        formatovanaStranaVyrazu(vyraz.getDruhacast(),potrebnyPrvky,hodnotyCoBuduPotrebovat,vlastniHodnoty),
                        vyraz.getLogikaNaKonci().equals("X")?"":vyraz.getLogikaNaKonci()
                        );
            }
            konecnaZprva+=String.format("?%s-%s;",podminka.getKladnyPrikaz(),podminka.getZapornyPrikaz());
        }
        konecnaZprva=konecnaZprva.substring(0,konecnaZprva.length()-1);
        konecnaZprva+="}";
        return konecnaZprva;

    }
    private void vkladejNovyPrvky(String castVyrazu,List<Prvek> uzite,List<String> prikazUzity){
        String [] policko=castVyrazu.split(":");
        if(policko.length==3){
            //length=1 takze nejaka konstanta
            //nic nevlozi
            //length 2 promena na modulu jako stavy relatek
            final int idPrvku=Integer.parseInt(policko[0]);
            Prvek sIdeckem= Iterables.tryFind(prvkySeKterymiBychMohlPracovat, new Predicate<Prvek>() {
                @Override
                public boolean apply(@NullableDecl Prvek input) {
                    return input.getProsteVsecko().getId()==idPrvku;
                }
            }).orNull();
            if(!uzite.contains(sIdeckem)){
                uzite.add(sIdeckem);
                prikazUzity.add(policko[2]);

            }
            else {
                int pozice=uzite.indexOf(sIdeckem);
                if(!prikazUzity.get(pozice).contains(policko[2])){
                    String prikaznik=prikazUzity.get(pozice);
                    prikazUzity.remove(pozice);
                    if(prikaznik.charAt(0)>policko[2].charAt(0)){
                        prikaznik=policko[2]+":"+prikaznik;
                    }
                    else {
                        prikaznik=prikaznik+":"+policko[2];
                    }
                    prikazUzity.add(pozice,prikaznik);
                }
            }

        }
    }
    private String formatovanaStranaVyrazu(String stranaVyrazu,List<Prvek> potrebnyPrvky,List<String> HodnotZCizichZdroju,List<String> VlastniHodnoty ){
        final String[] policko=stranaVyrazu.split(":");
        String formatlej="";
        switch (policko.length){
            case 2:{
                formatlej=Iterables.tryFind(VlastniHodnoty, new Predicate<String>() {
                    @Override
                    public boolean apply(@NullableDecl String input) {
                        return policko[1].equals(input);
                    }
                }).orNull();
                if(formatlej==null){
                    formatlej=policko[1];
                }
                break;
            }
            case 3:{
                final int idPrvku=Integer.parseInt(policko[0]);
                int pozice=Iterables.indexOf(potrebnyPrvky, new Predicate<Prvek>() {
                    @Override
                    public boolean apply(@NullableDecl Prvek input) {
                        return input.getProsteVsecko().getId()==idPrvku;
                    }
                });
                formatlej=HodnotZCizichZdroju.get(pozice);
                String[] moznosti=formatlej.split(":");
                if(moznosti[0].equals(policko[2])){
                    formatlej=String.format("v%d%d",pozice,0);
                }
                else if(moznosti[1].equals(policko[2])){
                    formatlej=String.format("v%d%d",pozice,1);
                }
                else {
                    formatlej="errr";
                    Toast.makeText(makraZasuvky.this,"Sorry kámo ale tahle strana výrazu: "+stranaVyrazu+" je nějak špatně",Toast.LENGTH_LONG).show();
                }
                break;

            }
            default:{
                formatlej=stranaVyrazu;
                break;
            }
        }
        return formatlej;
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

    class odesliMakro  extends AsyncTask<Void, Void, Boolean>{
        List<String> RozsekanaZprava=new ArrayList<>();
        //int aktualniPozice;
        Socket socket;
        private PrintWriter output;
        private BufferedReader input;
       // private boolean pripojen;
        int SERVER_PORT;
        String SERVER_IP;
        View view;
        //Thread Thread1 = null;

        public odesliMakro(String zprava,View view){
            SERVER_IP=MainActivity.aktivni.getUrl().toString();
            SERVER_PORT=MainActivity.aktivni.getPort();
            this.view=view;
            if(zprava.length()>49){
                RozsekanaZprava.add(zprava.substring(0,49));
                int i=49;
                for(;i<zprava.length();i+=45){
                    if(i+45<zprava.length())RozsekanaZprava.add("SP"+zprava.substring(i,i+45));
                    else RozsekanaZprava.add("SP"+zprava.substring(i));
                }
            }
            else {
                RozsekanaZprava.add(zprava);
            }
            String coVlastneOdesilam="";
            for (String radkos:RozsekanaZprava) {
                coVlastneOdesilam+=radkos+"\n";

            }
            Toast.makeText(makraZasuvky.this,coVlastneOdesilam,Toast.LENGTH_LONG).show();



        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                return false;

            }
            for(int i=0;i<RozsekanaZprava.size();i++){
                output.write(RozsekanaZprava.get(i));
                output.flush();
                while (true) {
                    try {
                        final String message = input.readLine();
                        if(message==null){}
                        else if (!message.isEmpty()) {
                            if(message.contains("ok"))break;

                        }
                        else {
                            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Snackbar.make(view, "makro bylo odeslano", Snackbar.LENGTH_LONG).show();
        }

    }
    class dostanMakroZmodulu  extends AsyncTask<Void, Void, String>{
        //int aktualniPozice;
        Socket socket;
        private PrintWriter output;
        private BufferedReader input;
        // private boolean pripojen;
        int SERVER_PORT;
        String SERVER_IP;
        //Thread Thread1 = null;

        public dostanMakroZmodulu(){
            SERVER_IP=MainActivity.aktivni.getUrl().toString();
            SERVER_PORT=MainActivity.aktivni.getPort();
        }
        String dostanCitelnyTvarpromene(String Necitelny,List<Prvek> zCehoVybiram){
            String vysledkos="";
            List<String> pinos= Arrays.asList(MainActivity.aktivni.coMaPrvekPodSebou().split(":"));
            if(Necitelny.charAt(0)=='v'){
                Prvek kterySePouzil=zCehoVybiram.get(Necitelny.charAt(1)-48);
                String coByToMhloByt=kterySePouzil.coMaPrvekPodSebou();
                vysledkos+=kterySePouzil.getProsteVsecko().getId()+": "+kterySePouzil.getJmeno()+" :"+coByToMhloByt.split(":")[Necitelny.charAt(2)-48];
            }
            else if(pinos.indexOf(Necitelny)!=-1) {
                vysledkos="TentoPrvek:"+Necitelny;
            }
            else {
                vysledkos=Necitelny;
            }
            return vysledkos;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                return "";

            }
            output.write("S?");
            output.flush();
            String zprava;
            while (true) {
                try {
                    final String message = input.readLine();
                    if(message==null){}
                    else if (!message.isEmpty()) {
                        zprava=message;
                        break;
                    }
                    else {
                        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return zprava;
        }

        @Override
        protected void onPostExecute(String zpravicka) {
            Toast.makeText(makraZasuvky.this,zpravicka,Toast.LENGTH_LONG).show();
            if(!zpravicka.isEmpty()){
                String[] hlavniCasti=zpravicka.split("\\{");
                hlavniCasti[1]=hlavniCasti[1].substring(0,hlavniCasti[1].length()-1);
                String[] Senzory=hlavniCasti[0].split(";");
                Senzory[0]=Senzory[0].substring(1);
                List<Prvek> coJeTamPouzito=new ArrayList<>();
                if(Senzory[0].charAt(1)!='t'){
                    for(String senzornik:Senzory){
                        final String uzFaktNevimJakToPojmenovat=senzornik.split("\\[")[0];
                        final Prvek pouzity= Iterables.tryFind(prvkySeKterymiBychMohlPracovat, new Predicate<Prvek>() {
                            @Override
                            public boolean apply(@NullableDecl Prvek input) {
                                return (input.getUrl().toString()+":"+input.getPort()).equals(uzFaktNevimJakToPojmenovat);
                            }
                        }).orNull();
                        coJeTamPouzito.add(pouzity);
                    }
                }

                String[] podminkose=hlavniCasti[1].split(";");
                for(String podminak:podminkose){
                    String[] castiPodminky=podminak.split("\\?");
                    List<Vyraz> vyrazivo=new ArrayList<>();
                    Vyraz vyraznik=null;
                    for(int i=0;i<castiPodminky[0].length();i++){
                        String prvniCast="";
                        for(;i<castiPodminky[0].length()&&castiPodminky[0].charAt(i)!='<'&&castiPodminky[0].charAt(i)!='>'&&castiPodminky[0].charAt(i)!='!'&&castiPodminky[0].charAt(i)!='=';i++){
                            prvniCast+=castiPodminky[0].charAt(i);
                        }
                        String znaminka=""+castiPodminky[0].charAt(i);
                        //if(i+1<castiPodminky[0].length())
                        if(castiPodminky[0].charAt(++i)=='='){
                            znaminka+=castiPodminky[0].charAt(i);
                            i++;
                        }
                        String druhaCast="";
                        for(;i<castiPodminky[0].length()&&castiPodminky[0].charAt(i)!='&'&&castiPodminky[0].charAt(i)!='|';i++){
                            druhaCast+=castiPodminky[0].charAt(i);
                        }
                        String logika="";
                        if(i<castiPodminky[0].length()){
                            logika+=castiPodminky[0].charAt(i);
                        }
                        else logika+="X";
                        prvniCast=dostanCitelnyTvarpromene(prvniCast,coJeTamPouzito);
                        druhaCast=dostanCitelnyTvarpromene(druhaCast,coJeTamPouzito);
                        vyraznik=new Vyraz(prvniCast,druhaCast,znaminka,logika);
                        vyrazivo.add(vyraznik);
                    }
                    String[] prikazy=castiPodminky[1].split("-");
                    listakos.add(new Podminkos(prikazy[0],prikazy[1],vyrazivo));
                }
            }
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
