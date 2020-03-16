package com.example.svita.drag.prvkose;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.svita.drag.R;
import com.example.svita.drag.prvkose.vychytavkyprozasuvku.RelePresDb;
import com.example.svita.drag.prvkose.vychytavkyprozasuvku.makraZasuvky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.os.Handler;

public class Zasuvka extends Prvek {
    public Zasuvka() {
        super(R.drawable.plugwall,"Zásuvka");
        prosteVsecko.setTypPrvku("Zasuvka");
        funkcniLayout=R.layout.funkce_zasuvky;
        vlastnostiLayout=R.layout.vlastnosti_zasuvka;
        setKolikZasuvek(1);

    }
    public Zasuvka(UlozCoPujde prostevsecko) {
        super(R.drawable.plugwall,"Zásuvka");
        setProsteVsecko(prostevsecko);
        funkcniLayout=R.layout.funkce_zasuvky;
        vlastnostiLayout=R.layout.vlastnosti_zasuvka;
    }
    public int kolikZasuvek=1;

    public void setKolikZasuvek(int kolikZasuvek) {
        this.kolikZasuvek = kolikZasuvek;
        prosteVsecko.setPopis("pocetZasuvek="+kolikZasuvek);

    }

    @Override
    public void setProsteVsecko(UlozCoPujde prosteVsecko) {
        super.setProsteVsecko(prosteVsecko);
        kolikZasuvek=Integer.parseInt(prosteVsecko.getPopis().split("=")[1]);
    }

    EditText Edb,Edbjmeno,Edbheslo;
    CheckBox MaDatabazi,zobrazHeslo;
    RadioGroup skupnika;

    @Override
    public void nastavSiVlastnosti(Activity kdeToDelam){

        super.nastavSiVlastnosti(kdeToDelam);
        Edb=kdeToDelam.findViewById(R.id.databazislav);
        Edbjmeno=kdeToDelam.findViewById(R.id.jmenodbcka);
        Edbheslo=kdeToDelam.findViewById(R.id.heslodbcka);
        MaDatabazi=kdeToDelam.findViewById(R.id.checkBox);
        zobrazHeslo=kdeToDelam.findViewById(R.id.checkBox2);
        skupnika=kdeToDelam.findViewById(R.id.skipnator9000);
        RadioButton aktivni=kdeToDelam.findViewById(R.id.Z1);
        switch (kolikZasuvek){
            case 2:{
                aktivni=kdeToDelam.findViewById(R.id.Z2);
                break;
            }
            case 4:{
                aktivni=kdeToDelam.findViewById(R.id.Z4);
                break;
            }
            case 8:{
                aktivni=kdeToDelam.findViewById(R.id.Z8);
                break;
            }
        }
        aktivni.setChecked(true);
        Edb.setText(prosteVsecko.getDb());
        Edbjmeno.setText(prosteVsecko.getDbjmeno());
        Edbheslo.setText(prosteVsecko.getDbheslo());
        if(!prosteVsecko.isMamDatabazi()){
            Edb.setVisibility(View.INVISIBLE);
            Edbjmeno.setVisibility(View.INVISIBLE);
            Edbheslo.setVisibility(View.INVISIBLE);
        }
        MaDatabazi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    prosteVsecko.MamDatabazi=true;
                    Edb.setVisibility(View.VISIBLE);
                    Edbjmeno.setVisibility(View.VISIBLE);
                    Edbheslo.setVisibility(View.VISIBLE);
                }
                else{
                    prosteVsecko.MamDatabazi=false;
                    Edb.setVisibility(View.INVISIBLE);
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
            prosteVsecko.setDb(Edb.getText().toString());
            prosteVsecko.setDbjmeno(Edbjmeno.getText().toString());
            prosteVsecko.setDbheslo(Edbheslo.getText().toString());
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
        setKolikZasuvek(kolikZasuvek);
        updatePrvek();

    }

    //"pocetZasuvek="+kolikZasuvek
    @Override
    public String coMaPrvekPodSebou(){
        String mojeOvecky="";
        switch (kolikZasuvek){
            case 1:{
                mojeOvecky="R1";
                break;
            }
            case 2:{
                mojeOvecky="R1:R2";
                break;
            }
            case 4:{
                mojeOvecky="R1:R2:R3:R4";
                break;
            }
            case 8:{
                mojeOvecky="R1:R2:R3:R4:R5:R6:R7:R8";
                break;
            }
        }
        return mojeOvecky;
    }
    private funkcnost facha=null;
    @Override
    public void fachej(Activity kdeToDelam){
        facha=new funkcnost(kdeToDelam);
    }
    class funkcnost{
        private Button infosaurus;
        private Thread Thread1 = null;
        private boolean pripojen=false;
        private String SERVER_IP=prosteVsecko.getUrl();
        private int SERVER_PORT=prosteVsecko.getPort();
        private Activity kdeToDelam;
        private boolean poslalJsem=false,dosloOk=false;
        private Button aktivni=null,Makra;
        private Button[]pole=new Button[8];
        private Handler handeler=new Handler();
        private RelePresDb jenKdyzToNejdePrimo;
        private boolean muzejetSmycka=false;
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
                    if(pripojen){
                        new Thread(new Thread3("RX:?")).start();
                    }
                    else {
                        jenKdyzToNejdePrimo=new RelePresDb(Zasuvka.this,pole,kolikZasuvek);
                        jenKdyzToNejdePrimo.execute("stav");
                    }

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
            muzejetSmycka=true;
            Thread1 = new Thread(new Thread1());
            Thread1.start();
            //handeler.postDelayed(new Thread3("RX:?"), 1000);
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
            handeler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(pripojen){
                        new Thread(new Thread3("RX:?")).start();
                    }
                    else {
                        jenKdyzToNejdePrimo=new RelePresDb(Zasuvka.this,pole,kolikZasuvek);
                        jenKdyzToNejdePrimo.execute("stav");
                    }
                }
            },1000);
        }
        private void zacniAktivituProNoveMakro(final Activity kdeToDelam){
            Intent grafek=new Intent(kdeToDelam, makraZasuvky.class);
            grafek.putExtra("Obsluha",getProsteVsecko());
            kdeToDelam.startActivity(grafek);
        }
        private String stav(boolean stav){
            if(stav)return"1";
            return "0";
        }
        public void posli(View v) {
            boolean stavator=true;
            if(((Button)v).getHint().toString().equals("ON")){
                stavator=false;
            }
            if(pripojen){
                if(poslalJsem&&dosloOk||!poslalJsem){
                    String zprava="R"+((Button)v).getText().charAt(1)+":"+stav(stavator);
                    new Thread(new Thread3(zprava)).start();
                    poslalJsem=true;
                    dosloOk=false;
                    aktivni=(Button)v;
                }
            }
            else {
                String[] polish={"update","N","N","N","N","N","N","N","N",((Button)v).getText().toString()};
                int i=((Button)v).getText().charAt(1)-48;
                polish[i]=stav(stavator);
                jenKdyzToNejdePrimo=new RelePresDb(Zasuvka.this,pole,kolikZasuvek);
                jenKdyzToNejdePrimo.execute(polish);

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
                while (muzejetSmycka) {
                    try {
                        final String message = input.readLine();
                        if (!message.isEmpty()) {
                            kdeToDelam.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Toast.makeText(kdeToDelam," prisla "+message,Toast.LENGTH_LONG).show();
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
        public void SkonciTo(){
            muzejetSmycka=false;
        }
    }
    @Override
    public void uzNeFachej(Activity kdeToDelam) {
        facha.SkonciTo();
    }
}
