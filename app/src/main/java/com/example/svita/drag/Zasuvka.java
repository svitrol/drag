package com.example.svita.drag;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.svita.drag.nactiDataDoGrafu.vykresliGraf;
import com.example.svita.drag.vychytavkyprozasuvku.makraZasuvky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Zasuvka extends Prvek {
    public Zasuvka() {
        super(R.drawable.plugwall,"Zásuvka");
        prosteVsecko=new UlozCoPujde("Zasuvka","Zásuvka");
        funkcniLayout=R.layout.funkce_zasuvky;
        vlastnostiLayout=R.layout.vlastnosti_zasuvka;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String url) {
        db = url;
    }

    public String getDbjmeno() {
        return dbjmeno;
    }

    public void setDbjmeno(String dbjmeno) {
        this.dbjmeno = dbjmeno;
    }

    public int getDbport() {
        return dbport;
    }

    public void setDbport(int dbport) {
        this.dbport = dbport;
    }
    public void setDbheslo(String dbheslo) {
        this.dbheslo = dbheslo;
    }

    public String getDbheslo() {
        return dbheslo;
    }

    public String db="1.1.1.1";
    protected String dbheslo="";
    public String dbjmeno="";
    public int dbport=80;
    public boolean MamDatabazi=true;
    public int kolikZasuvek=1;

    EditText Edb,Edbport,Edbjmeno,Edbheslo;
    CheckBox MaDatabazi,zobrazHeslo;
    RadioGroup skupnika;

    @Override
    public void nastavSiVlastnosti(Activity kdeToDelam){

        super.nastavSiVlastnosti(kdeToDelam);
        Edb=kdeToDelam.findViewById(R.id.databazislav);
        Edbport=kdeToDelam.findViewById(R.id.portakdbcka);
        Edbjmeno=kdeToDelam.findViewById(R.id.jmenodbcka);
        Edbheslo=kdeToDelam.findViewById(R.id.heslodbcka);
        MaDatabazi=kdeToDelam.findViewById(R.id.checkBox);
        zobrazHeslo=kdeToDelam.findViewById(R.id.checkBox2);
        skupnika=kdeToDelam.findViewById(R.id.skipnator9000);
        Edb.setText(getDb().toString());
        Edbport.setText(""+getDbport());
        Edbjmeno.setText(getDbjmeno());
        Edbheslo.setText(getDbheslo());
        if(!MamDatabazi){
            Edb.setVisibility(View.INVISIBLE);
            Edbport.setVisibility(View.INVISIBLE);
            Edbjmeno.setVisibility(View.INVISIBLE);
            Edbheslo.setVisibility(View.INVISIBLE);
        }
        MaDatabazi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    MamDatabazi=true;
                    Edb.setVisibility(View.VISIBLE);
                    Edbport.setVisibility(View.VISIBLE);
                    Edbjmeno.setVisibility(View.VISIBLE);
                    Edbheslo.setVisibility(View.VISIBLE);
                }
                else{
                    MamDatabazi=false;
                    Edb.setVisibility(View.INVISIBLE);
                    Edbport.setVisibility(View.INVISIBLE);
                    Edbjmeno.setVisibility(View.INVISIBLE);
                    Edbheslo.setVisibility(View.INVISIBLE);
                }
            }
        });
        zobrazHeslo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    Edbheslo.setTransformationMethod(null);
                }
                else{

                    Edbheslo.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
    }
    @Override
    public void vemSiCoPotrebujes(Activity kdeToDelam){
        super.vemSiCoPotrebujes(kdeToDelam);
        if(MaDatabazi.isChecked()){
            setDb(Edb.getText().toString());
            setDbport(Integer.parseInt(Edbport.getText().toString()));
            setDbjmeno(Edbjmeno.getText().toString());
            setDbheslo(Edbheslo.getText().toString());
        }
        switch (skupnika.getCheckedRadioButtonId()){
            case R.id.Z1:{
                kolikZasuvek=1;
                break;
            }
            case R.id.Z2:{
                kolikZasuvek=2;
                break;
            }
            case R.id.Z4:{
                kolikZasuvek=4;
                break;
            }
            case R.id.Z8:{
                kolikZasuvek=8;
                break;
            }
        }
        updatePrvek();

    }

    @Override
    public void NaplnProsteVsecko(){

        super.NaplnProsteVsecko();

        prosteVsecko.setDb(db.toString()) ;

        prosteVsecko.setDbheslo(dbheslo) ;

        prosteVsecko.setDbjmeno(dbjmeno) ;

        prosteVsecko.setDbport(dbport) ;

        prosteVsecko.setMamDatabazi(MamDatabazi) ;

        prosteVsecko.setKolikzasuvek(kolikZasuvek);
    }
    @Override
    public void VemSiToZpatky(){
        super.VemSiToZpatky();
        setDb(prosteVsecko.getDb());

        dbheslo=prosteVsecko.getDbheslo() ;

        dbjmeno=prosteVsecko.getDbjmeno() ;

        dbport=prosteVsecko.getDbport() ;

        MamDatabazi=prosteVsecko.isMamDatabazi() ;

        kolikZasuvek=prosteVsecko.getKolikzasuvek();

    }
    @Override
    protected void fachej(Activity kdeToDelam){
        funkcnost facha=new funkcnost(kdeToDelam);
    }
    class funkcnost{
        Button infosaurus;
        Thread Thread1 = null;
        boolean pripojen=false;
        String SERVER_IP=getUrl().toString();
        int SERVER_PORT=getPort();
        Activity kdeToDelam;
        boolean poslalJsem=false,dosloOk=false;
        Button aktivni=null,Makra;
        Button[]pole=new Button[8];
        public  funkcnost(final Activity kdeToDelam){
            this.kdeToDelam=kdeToDelam;
            pole[0]=kdeToDelam.findViewById(R.id.Z1);
            pole[1]=kdeToDelam.findViewById(R.id.Z2);
            pole[2]=kdeToDelam.findViewById(R.id.Z3);
            pole[3]=kdeToDelam.findViewById(R.id.Z4);
            pole[4]=kdeToDelam.findViewById(R.id.Z5);
            pole[5]=kdeToDelam.findViewById(R.id.Z6);
            pole[6]=kdeToDelam.findViewById(R.id.Z7);
            pole[7]=kdeToDelam.findViewById(R.id.Z8);
            infosaurus=kdeToDelam.findViewById(R.id.info);
            infosaurus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Thread3("RX:?")).start();
                }
            });

            switch (kolikZasuvek){
                case 1:{
                    pole[1].setVisibility(View.INVISIBLE);
                    pole[2].setVisibility(View.INVISIBLE);
                    pole[3].setVisibility(View.INVISIBLE);
                    pole[4].setVisibility(View.INVISIBLE);
                    pole[5].setVisibility(View.INVISIBLE);
                    pole[6].setVisibility(View.INVISIBLE);
                    pole[7].setVisibility(View.INVISIBLE);
                    break;
                }
                case 2:{
                    pole[2].setVisibility(View.INVISIBLE);
                    pole[3].setVisibility(View.INVISIBLE);
                    pole[4].setVisibility(View.INVISIBLE);
                    pole[5].setVisibility(View.INVISIBLE);
                    pole[6].setVisibility(View.INVISIBLE);
                    pole[7].setVisibility(View.INVISIBLE);
                    break;
                }
                case 4:{
                    pole[4].setVisibility(View.INVISIBLE);
                    pole[5].setVisibility(View.INVISIBLE);
                    pole[6].setVisibility(View.INVISIBLE);
                    pole[7].setVisibility(View.INVISIBLE);
                    break;
                }
            }
            Thread1 = new Thread(new Thread1());
            Thread1.start();
            for(int i=0;i<kolikZasuvek;i++){
                pole[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        posli(v);
                    }
                });
            }
            Makra=kdeToDelam.findViewById(R.id.makra);
            Makra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //začni aktivitu s makrem
                    zacniAktivituProNoveMakro(kdeToDelam);
                }
            });



        }
        private void zacniAktivituProNoveMakro(final Activity kdeToDelam){
            ArrayList<CharSequence> lisovniTajemstvi=new ArrayList<>();
            lisovniTajemstvi.add(db);
            lisovniTajemstvi.add(dbjmeno);
            lisovniTajemstvi.add(dbheslo);
            Intent grafek=new Intent(kdeToDelam, makraZasuvky.class);
            grafek.putExtra("Graf",lisovniTajemstvi);
            kdeToDelam.startActivity(grafek);
        }
        private String stav(boolean stav){
            if(stav)return"1";
            return "0";
        }
        public void posli(View v) {
            if(pripojen){
                boolean stavator=true;
                if(((Button)v).getHint().toString().equals("ON")){
                    stavator=false;
                }
                if(poslalJsem&&dosloOk||!poslalJsem){
                    String zprava="R"+((Button)v).getText().charAt(1)+":"+stav(stavator);
                    new Thread(new Thread3(zprava)).start();
                    poslalJsem=true;
                    dosloOk=false;
                    aktivni=(Button)v;
                }
            }


        }
        public void pripjenisa(View v){
            Toast.makeText(kdeToDelam,"Snažím se",Toast.LENGTH_LONG).show();
            Thread1 = new Thread(new Thread1());
            Thread1.start();

        }
        private PrintWriter output;
        private BufferedReader input;
        class Thread1 implements Runnable {
            public void run() {
                Socket socket;
                try {
                    socket = new Socket(SERVER_IP, SERVER_PORT);
                    output = new PrintWriter(socket.getOutputStream());
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    kdeToDelam.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pripojen=true;
                            Toast.makeText(kdeToDelam,"connected",Toast.LENGTH_LONG).show();
                        }
                    });
                    new Thread(new Thread2()).start();
                } catch (IOException e) {
                    e.printStackTrace();
                    pripojen=false;
                }
            }
        }
        class Thread2 implements Runnable {
            @Override
            public void run() {
                while (true) {
                    try {
                        final String message = input.readLine();
                        if (!message.isEmpty()) {
                            kdeToDelam.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(kdeToDelam," prisla "+message,Toast.LENGTH_LONG).show();
                                    if(message.contains("ok")){
                                        dosloOk=true;
                                        if(aktivni.getHint().toString().equals("ON")){
                                            aktivni.setHint("OFF");
                                            aktivni.setBackgroundColor(Color.rgb(237, 94, 69));
                                        }
                                        else {
                                            aktivni.setHint("ON");
                                            aktivni.setBackgroundColor(Color.rgb(152, 240, 53));
                                        }

                                    }
                                    else{
                                        try {
                                            String[]polak= message.split(";");
                                            //RX:ON
                                            for(int i=0;i<kolikZasuvek;i++){
                                                if(polak[i].split(":")[1].equals("1")){
                                                    pole[i].setHint("ON");
                                                    pole[i].setBackgroundColor(Color.rgb(152, 240, 53));
                                                }
                                                else {
                                                    pole[i].setHint("OFF");
                                                    pole[i].setBackgroundColor(Color.rgb(237, 94, 69));
                                                }
                                            }
                                        }catch (Exception e){

                                        }
                                    }


                                }
                            });
                        }
                        else {
                            Thread1 = new Thread(new Thread1());
                            Thread1.start();
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch (NullPointerException e){
                        kdeToDelam.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(kdeToDelam," neprisla ",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        }
        class Thread3 implements Runnable {
            private String message;
            Thread3(String message) {
                this.message = message;
            }
            @Override
            public void run() {
                output.write(message);
                output.flush();
                //Toast.makeText(kdeToDelam,message,Toast.LENGTH_SHORT).show();
                kdeToDelam.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(kdeToDelam,message,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
