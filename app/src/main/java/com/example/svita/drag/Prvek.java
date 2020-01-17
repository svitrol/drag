package com.example.svita.drag;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.system.ErrnoException;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.svita.drag.R;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;


public class Prvek{
    EditText Ejmeno,Eipadresa,Eport;

    public UlozCoPujde getProsteVsecko() {
        return prosteVsecko;
    }

    public void setProsteVsecko(UlozCoPujde prosteVsecko) {
        this.prosteVsecko = prosteVsecko;
        VemSiToZpatky();
    }

    UlozCoPujde prosteVsecko;
    public int ikona;
    public boolean edit=false;
    TextView tv ;
    ImageView image;
    public String popis="";
    public Uri url=Uri.parse("1.1.1.1");
    public String jmeno="";
    public int port=80;
    public int vlastnostiLayout=R.layout.zakladni_vlastnosti;
    public int funkcniLayout;

    public int getFunkcniLayout() {
        return funkcniLayout;
    }

    public int getVlastnostiLayout() {
        return vlastnostiLayout;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPopis() {
        return popis;
    }

    public Uri getUrl() {
        return url;
    }

    public String getJmeno() {
        return jmeno;
    }

    public int getPort() {
        return port;
    }

    public void setUrl(String urlcko) {
        url = Uri.parse(urlcko);
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
        //tv.setText(jmeno);
    }



    private KliknutiPrvku KlikNaVytvoreniStinu=null;
    private KliknutiPrvku VolejNastaveniPrvku=null;

    public void setKlikNaVytvoreniStinu(KliknutiPrvku klikNaVytvoreniStinu) {
        KlikNaVytvoreniStinu = klikNaVytvoreniStinu;
    }

    public void setVolejNastaveniPrvku(KliknutiPrvku volejNastaveniPrvku) {
        VolejNastaveniPrvku = volejNastaveniPrvku;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
        if(edit){
            convertView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(KlikNaVytvoreniStinu!=null){
                        KlikNaVytvoreniStinu.klikAkce(Prvek.this);
                    }
                    return true;
                }
            });
        }
        else{
            convertView.setOnTouchListener(null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    obsluha();
                }
            });
        }
    }
    void dlouhyKlik(View ikona){
        ikona.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(VolejNastaveniPrvku!=null){
                    VolejNastaveniPrvku.klikAkce(Prvek.this);
                }
                return true;
            }
        });
    }
    protected   void obsluha(){
        Intent funkcni=new Intent(nejakyContext,FunkcePrvku.class);
        funkcni.putExtra("Obsluha",getProsteVsecko());
        nejakyContext.startActivity(funkcni);
    }
    public void nastavSiVlastnosti(Activity KdeToDelam){
        Ejmeno=KdeToDelam.findViewById(R.id.jmeno);
        Eipadresa=KdeToDelam.findViewById(R.id.ipina);
        Eport=KdeToDelam.findViewById(R.id.portak);
        Ejmeno.setText(getJmeno());
        Eipadresa.setText(getUrl().toString());
        Eport.setText(""+getPort());
    }
    public void vemSiCoPotrebujes(Activity kdeToDelam){
        setJmeno(Ejmeno.getText().toString());
        setUrl(Eipadresa.getText().toString());
        setPort(Integer.parseInt(Eport.getText().toString()));
    }
    public Prvek(int ikona, String jmeno) {
        this.ikona = ikona;
        this.jmeno = jmeno;
    }
    static public Prvek zalozSpravnyPrvek(UlozCoPujde neco){
        Prvek novy=null;
        switch (neco.getTypPrvku()){
            case "Teplomer":{
                novy=new Teplomer();
                novy.setProsteVsecko(neco);
                break;
            }
            case "Zarovka":{
                novy=new Zarovka();
                novy.setProsteVsecko(neco);
                break;
            }
            case "Kamera":{
                novy=new Kamera();
                novy.setProsteVsecko(neco);
                break;
            }
            case "Zasuvka":{
                novy=new Zasuvka();
                novy.setProsteVsecko(neco);
                break;
            }
            case "SensorPS":{
                novy=new SensorPS();
                novy.setProsteVsecko(neco);
                break;
            }
        }
        return novy;
    }

    public void setIkona(int ikona) {
        this.ikona = ikona;
        image.setImageResource(ikona);
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }
    protected float soradniceX=0;
    protected float soradniceY=0;
    void nastavPozici(float x,float y){
        convertView.setX(x);
        convertView.setY(y);
        soradniceX=x;
        soradniceY=y;
    }
    public boolean isPrvekRidici(){
        return true;
    }
    public String coMaPrvekPodSebou(){
        return "nic";
    }
    public String dejMiPrikazivo(String coTimChcesDokazat){
        return "nic";
    }



    public View convertView;
    Context nejakyContext;
    public View dejmiNovyNahled(Context context ) {
        nejakyContext=context;
        //NaplnProsteVsecko();
        saveTask();

        LayoutInflater inflater= (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        convertView = inflater.inflate(R.layout.prvke, null);
        tv =  convertView.findViewById(R.id.textView);
        image =  convertView.findViewById(R.id.imageView2);
        tv.setText(jmeno);
        image.setImageResource(ikona);
        setEdit(true);
        dlouhyKlik(convertView);
        nastavPozici(soradniceX,soradniceY);
        return convertView;

    }

    public View getView(Context context ) {
        nejakyContext=context;

        LayoutInflater inflater= (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        convertView = inflater.inflate(R.layout.prvke, null);
        tv =  convertView.findViewById(R.id.textView);
        image =  convertView.findViewById(R.id.imageView2);
        tv.setText(jmeno);
        image.setImageResource(ikona);
        setEdit(true);
        dlouhyKlik(convertView);
        nastavPozici(soradniceX,soradniceY);
        return convertView;

    }

    protected void deleteTask(final UlozCoPujde task) {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DbClient.getInstance(nejakyContext).getAppDatabase()
                        .taskDao()
                        .delete(task);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(nejakyContext, "Deleted", Toast.LENGTH_LONG).show();
            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();

    }
    protected void updateTask(final UlozCoPujde task) {


        class UpdateTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DbClient.getInstance(nejakyContext).getAppDatabase()
                        .taskDao()
                        .update(task);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //Toast.makeText(nejakyContext, "Updated", Toast.LENGTH_LONG).show();
            }
        }

        UpdateTask ut = new UpdateTask();
        ut.execute();
    }
    protected void saveTask() {

        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    //creating item
                    UlozCoPujde prostredni=new UlozCoPujde(prosteVsecko);
                    //adding to database
                    DbClient.getInstance(nejakyContext).getAppDatabase()
                            .taskDao()
                            .insert(prostredni);
                }catch(Exception e){
                    prosteVsecko.setId(prosteVsecko.getId()+1);
                    saveTask();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(nejakyContext, "Saved s ideckem: "+prosteVsecko.getId(), Toast.LENGTH_LONG).show();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }
    public void updatePrvek(){
        NaplnProsteVsecko();
        updateTask(prosteVsecko);
    }
    public void NaplnProsteVsecko(){

        prosteVsecko.setPopis(popis) ;

        prosteVsecko.setUrl(url.toString()) ;

        prosteVsecko.setJmeno(jmeno) ;

        prosteVsecko.setPort(port);

        prosteVsecko.setSoradniceX(soradniceX);

        prosteVsecko.setSoradniceY(soradniceY) ;

    }
    public void VemSiToZpatky(){

        setPopis(prosteVsecko.getPopis()) ;

        setUrl(prosteVsecko.getUrl());

        jmeno=prosteVsecko.getJmeno() ;

        port=prosteVsecko.getPort();

        soradniceX=prosteVsecko.getSoradniceX();

        soradniceY=prosteVsecko.getSoradniceY() ;

    }
    protected void fachej(Activity kdeToDelam){    }
    protected void uzNeFachej(Activity kdeToDelam){};



}
