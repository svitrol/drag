package com.example.svita.drag.prvkose;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.text.format.DateFormat;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.svita.drag.R;
import com.example.svita.drag.prvkose.nactiDataDoGrafu.vykresliGraf;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import android.os.Handler;

public class Teplomer extends Prvek {

    private EditText Edb,Edbjmeno,Edbheslo;
    private CheckBox MaDatabazi,zobrazHeslo;
    public Teplomer() {
        super(R.drawable.thermometer, "teplota");
        prosteVsecko.setTypPrvku("Teplomer");
        vlastnostiLayout = R.layout.activity_vlastnosti;
        funkcniLayout = R.layout.activity_funkce_prvku;
    }
    public Teplomer(UlozCoPujde prostevsecko){
        super(R.drawable.thermometer, "teplota");
        vlastnostiLayout = R.layout.activity_vlastnosti;
        funkcniLayout = R.layout.activity_funkce_prvku;
        setProsteVsecko(prostevsecko);
    }
    @Override
    public boolean isPrvekRidici(){
        return false;
    }
    @Override
    public String coMaPrvekPodSebou(){
        return "teplota:vlhkost";
    }
    @Override
    public String dejMiPrikazivo(String coTimChcesDokazat){
        String prikazNaMiru="";
        if(coTimChcesDokazat.equals("teplota:vlhkost"))prikazNaMiru="t:v";
        else if(coTimChcesDokazat.equals("teplota"))prikazNaMiru="t";
        else if(coTimChcesDokazat.equals("vlhkost"))prikazNaMiru="v";

        return prikazNaMiru;
    }

    @Override
    public void nastavSiVlastnosti(Activity kdeToDelam){

        super.nastavSiVlastnosti(kdeToDelam);
        Edb=kdeToDelam.findViewById(R.id.databazislav);
        Edbjmeno=kdeToDelam.findViewById(R.id.jmenodbcka);
        Edbheslo=kdeToDelam.findViewById(R.id.heslodbcka);
        MaDatabazi=kdeToDelam.findViewById(R.id.checkBox);
        zobrazHeslo=kdeToDelam.findViewById(R.id.checkBox2);
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
        updatePrvek();

    }
    private funkcnost facha=null;
    @Override
    public void fachej(Activity kdeToDelam){
        facha=new funkcnost(kdeToDelam);
    }
    class funkcnost {
        private TextView conectly,teplotka,vlhkostik,Od,Do;
        private Button obnov,pripij,vyberOd,vyberDo,VypracujGraf;
        private RadioButton teplotaMod;
        private LinearLayout grafovaCast;
        Thread Thread2 = null;
        String SERVER_IP=prosteVsecko.getUrl();
        int SERVER_PORT=prosteVsecko.getPort();
        Calendar odDatum=null,DoDadtum=null;
        private Activity kdeToDelam;
        private Boolean pripojen=false;
        private Boolean muzejetSmycka=false;
        public  funkcnost(final Activity kdeToDelam){
            this.kdeToDelam=kdeToDelam;
            conectly=kdeToDelam.findViewById(R.id.textView11);
            teplotka=kdeToDelam.findViewById(R.id.textView10);
            vlhkostik=kdeToDelam.findViewById(R.id.textView9);
            obnov=kdeToDelam.findViewById(R.id.obnov);
            pripij=kdeToDelam.findViewById(R.id.pripij);

            muzejetSmycka=true;
            Handler handeler=new Handler();
            handeler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    obnov.performClick();
                }
            },1000);
            new Thread(new Thread1()).start();
            obnov.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refresh(v);
                }
            });
            pripij.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pripjenisa(v);
                }
            });


            vyberOd=kdeToDelam.findViewById(R.id.vyberOd);
            vyberDo=kdeToDelam.findViewById(R.id.vyberDo);
            Od=kdeToDelam.findViewById(R.id.od);
            Do=kdeToDelam.findViewById(R.id.Do);
            grafovaCast=kdeToDelam.findViewById(R.id.grafy);
            teplotaMod=kdeToDelam.findViewById(R.id.radioButton);
            VypracujGraf=kdeToDelam.findViewById(R.id.zpracuj);
            if(prosteVsecko.isMamDatabazi()){
                grafovaCast.setVisibility(View.VISIBLE);
                VypracujGraf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(odDatum!=null){
                            if(DoDadtum!=null){
                                String hodnota="vlhkost";
                                if(teplotaMod.isChecked())hodnota="teplota";

                                ArrayList<CharSequence> lisovniTajemstvi=new ArrayList<>();
                                lisovniTajemstvi.add(prosteVsecko.getDb());
                                lisovniTajemstvi.add(prosteVsecko.getDbjmeno());
                                lisovniTajemstvi.add(prosteVsecko.getDbheslo());
                                lisovniTajemstvi.add(hodnota);
                                lisovniTajemstvi.add(DateFormat.format("yyyy-MM-dd HH:mm:ss", odDatum));
                                lisovniTajemstvi.add(DateFormat.format("yyyy-MM-dd HH:mm:ss", DoDadtum));
                                Intent grafek=new Intent(kdeToDelam, vykresliGraf.class);
                                grafek.putExtra("Graf",lisovniTajemstvi);
                                kdeToDelam.startActivity(grafek);
                            }
                            else {
                                Toast.makeText(kdeToDelam,"Ale ale nejak si nenastavil do datum",Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(kdeToDelam,"Ale ale nejak si nenastavil od datum",Toast.LENGTH_LONG).show();
                        }


                    }
                });
                vyberOd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        odDatum=Calendar.getInstance();
                        handleDateButton(Od,kdeToDelam,odDatum,true);
                    }
                });
                vyberDo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DoDadtum=Calendar.getInstance();
                        handleDateButton(Do,kdeToDelam,DoDadtum,false);
                    }
                });
            }
            else {
                grafovaCast.setVisibility(View.INVISIBLE);
            }
        }
        public void refresh(View v) {
                new Thread(new Thread3("t:v")).start();

        }
        public void pripjenisa(View v){
            new Thread(new Thread1()).start();

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
                            conectly.setText("Connected");
                        }
                    });
                    pripojen=true;
                    Thread2=new Thread(new Thread2());
                    Thread2.start();
                } catch (IOException e) {
                    e.printStackTrace();
                    pripojen=false;
                    kdeToDelam.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            conectly.setText("Disconnected");
                        }
                    });

                }
            }
        }
        class Thread2 implements Runnable {
            @Override
            public void run() {
                while (muzejetSmycka) {
                    try {
                        final String message = input.readLine();
                        if(message==null){}
                        else if (!message.isEmpty()) {
                            kdeToDelam.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Toast.makeText(kdeToDelam," prisla "+message,Toast.LENGTH_LONG).show();
                                    try{
                                        String []polak=message.split(":");
                                        teplotka.setText("teplota: "+polak[0]+"°C");
                                        vlhkostik.setText("vlhkost: "+polak[1]+"%");
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        else {
                            new Thread(new Thread1()).start();
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
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
                if(pripojen){
                    output.write(message);
                    output.flush();
                    kdeToDelam.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(kdeToDelam,"poslána teplota",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }
        public void SkonciTo(){
            muzejetSmycka=false;
        }

        private void handleDateButton(final TextView dateTextView, final Activity kdeToDelam, final Calendar calendar1, final boolean mod) {//mod true =od
            String neco="";
            final Calendar calendar = Calendar.getInstance();
            int YEAR = calendar.get(Calendar.YEAR);
            int MONTH = calendar.get(Calendar.MONTH);
            int DATE = calendar.get(Calendar.DATE);
            //calendar1 = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(kdeToDelam, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                    calendar1.set(Calendar.YEAR, year);
                    calendar1.set(Calendar.MONTH, month);
                    calendar1.set(Calendar.DATE, date);
                    int HOUR = calendar.get(Calendar.HOUR);
                    int MINUTE = calendar.get(Calendar.MINUTE);
                    boolean is24HourFormat = DateFormat.is24HourFormat(kdeToDelam);

                    final TimePickerDialog timePickerDialog = new TimePickerDialog(kdeToDelam, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                            calendar1.set(Calendar.HOUR, hour);
                            calendar1.set(Calendar.MINUTE, minute);
                            String dateText = DateFormat.format("HH:mm EEEE, dd.MMM yyyy", calendar1).toString();//MM-dd-yyyy HH:mm
                            dateTextView.setText(dateText);
                            if(mod){
                                if(DoDadtum!=null){
                                    if(DoDadtum.getTimeInMillis()<odDatum.getTimeInMillis()){
                                        Toast.makeText(kdeToDelam,"od datum je vetší než do datum",Toast.LENGTH_LONG).show();
                                        handleDateButton(dateTextView,kdeToDelam,calendar1,mod);
                                    }

                                }
                            }
                            else {
                                if(odDatum!=null){
                                    if(DoDadtum.getTimeInMillis()<odDatum.getTimeInMillis()){
                                        Toast.makeText(kdeToDelam,"od datum je vetší než do datum",Toast.LENGTH_LONG).show();
                                        handleDateButton(dateTextView,kdeToDelam,calendar1,mod);
                                    }

                                }
                            }
                        }
                    }, HOUR, MINUTE,is24HourFormat);

                    timePickerDialog.show();
                }
            }, YEAR, MONTH, DATE);
            if(mod){
                if(DoDadtum!=null){
                    datePickerDialog.getDatePicker().setMaxDate(DoDadtum.getTimeInMillis());
                }
                else {
                    datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                }
            }
            else {
                datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                if(odDatum!=null){
                    datePickerDialog.getDatePicker().setMinDate(odDatum.getTimeInMillis());
                }
            }
            datePickerDialog.show();


        }
    }

    @Override
    public void uzNeFachej(Activity kdeToDelam) {
        facha.SkonciTo();
    }
}
