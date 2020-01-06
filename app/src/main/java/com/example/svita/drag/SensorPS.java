package com.example.svita.drag;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Handler;
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

import com.example.svita.drag.nactiDataDoGrafu.vykresliGraf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;

public class SensorPS extends Prvek {
    public String getDb() {
        return db;
    }

    public void setDb(String ipdbcka) {
        db = ipdbcka;
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
    EditText Edb,Edbport,Edbjmeno,Edbheslo;
    CheckBox MaDatabazi,zobrazHeslo;
    public SensorPS() {
        super(R.drawable.sensorpohybu, "sensor pohybu");
        prosteVsecko=new UlozCoPujde("SensorPS","sensor pohybu");
        vlastnostiLayout=R.layout.activity_vlastnosti;
        funkcniLayout=R.layout.funkce_ps;
    }
    @Override
    public boolean isPrvekRidici(){
        return false;
    }
    @Override
    public String coMaPrvekPodSebou(){
        return "pohyb:svetlo";
    }
    @Override
    public String dejMiPrikazivo(String coTimChcesDokazat){
        String prikazNaMiru="";
        if(coTimChcesDokazat.equals("pohyb:svetlo"))prikazNaMiru="p:s";
        else if(coTimChcesDokazat.equals("pohyb"))prikazNaMiru="p";
        else if(coTimChcesDokazat.equals("svetlo"))prikazNaMiru="s";

        return prikazNaMiru;
    }

    @Override
    public void nastavSiVlastnosti(Activity kdeToDelam){

        super.nastavSiVlastnosti(kdeToDelam);
        Edb=kdeToDelam.findViewById(R.id.databazislav);
        Edbport=kdeToDelam.findViewById(R.id.portakdbcka);
        Edbjmeno=kdeToDelam.findViewById(R.id.jmenodbcka);
        Edbheslo=kdeToDelam.findViewById(R.id.heslodbcka);
        MaDatabazi=kdeToDelam.findViewById(R.id.checkBox);
        zobrazHeslo=kdeToDelam.findViewById(R.id.checkBox2);
        Edb.setText(getDb());
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
        updatePrvek();

    }

    @Override
    public void NaplnProsteVsecko(){

        super.NaplnProsteVsecko();

        prosteVsecko.setDb(db) ;

        prosteVsecko.setDbheslo(dbheslo) ;

        prosteVsecko.setDbjmeno(dbjmeno) ;

        prosteVsecko.setDbport(dbport) ;

        prosteVsecko.setMamDatabazi(MamDatabazi) ;
    }
    @Override
    public void VemSiToZpatky(){
        super.VemSiToZpatky();
        setDb(prosteVsecko.getDb());

        dbheslo=prosteVsecko.getDbheslo() ;

        dbjmeno=prosteVsecko.getDbjmeno() ;

        dbport=prosteVsecko.getDbport() ;

        MamDatabazi=prosteVsecko.isMamDatabazi() ;

    }
    private funkcnost facha=null;
    @Override
    protected void fachej(Activity kdeToDelam){
        facha=new funkcnost(kdeToDelam);
    }
    class funkcnost {
        private TextView conectly, pohybovnik, svetlik,Od,Do;
        private Button obnov,pripij,vyberOd,vyberDo,VypracujGraf;
        private RadioButton pohybMod;
        private LinearLayout grafovaCast;
        Thread Thread2 = null;
        String SERVER_IP=getUrl().toString();
        int SERVER_PORT=getPort();
        Calendar odDatum=null,DoDadtum=null;
        private Activity kdeToDelam;
        private Boolean pripojen=false;
        private Boolean muzejetSmycka=false;
        public  funkcnost(final Activity kdeToDelam){
            this.kdeToDelam=kdeToDelam;
            conectly=kdeToDelam.findViewById(R.id.textView11);
            pohybovnik =kdeToDelam.findViewById(R.id.textView10);
            svetlik =kdeToDelam.findViewById(R.id.textView9);
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
            new Thread(new funkcnost.Thread1()).start();
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
            pohybMod =kdeToDelam.findViewById(R.id.radioButton);
            VypracujGraf=kdeToDelam.findViewById(R.id.zpracuj);
            if(MamDatabazi){
                grafovaCast.setVisibility(View.VISIBLE);
                VypracujGraf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(odDatum!=null){
                            if(DoDadtum!=null){
                                String hodnota="svetlo";
                                if(pohybMod.isChecked())hodnota="pohyb";

                                ArrayList<CharSequence> lisovniTajemstvi=new ArrayList<>();
                                lisovniTajemstvi.add(db);
                                lisovniTajemstvi.add(dbjmeno);
                                lisovniTajemstvi.add(dbheslo);
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
            new Thread(new funkcnost.Thread3("p:s")).start();

        }
        public void pripjenisa(View v){
            new Thread(new funkcnost.Thread1()).start();

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
                    Thread2=new Thread(new funkcnost.Thread2());
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
                                        pohybovnik.setText("Pohyb: "+polak[0]);
                                        svetlik.setText("Intenzita svetla: "+polak[1]+"lux");
                                    }catch(Exception e){

                                    }


                                }
                            });
                        }
                        else {
                            new Thread(new funkcnost.Thread1()).start();
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
                            Toast.makeText(kdeToDelam,"poslána žádost",Toast.LENGTH_SHORT).show();
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
    protected void uzNeFachej(Activity kdeToDelam) {
        facha.SkonciTo();
    }

}
