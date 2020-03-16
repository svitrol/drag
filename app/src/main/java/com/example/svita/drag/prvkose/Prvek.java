package com.example.svita.drag.prvkose;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.svita.drag.DbClient;
import com.example.svita.drag.FunkcePrvku;
import com.example.svita.drag.R;


public abstract class Prvek{
    EditText Ejmeno,Eipadresa,Eport;

    public UlozCoPujde getProsteVsecko() {
        return prosteVsecko;
    }

    public void setProsteVsecko(UlozCoPujde prosteVsecko) {
        this.prosteVsecko = prosteVsecko;
        if(convertView!=null){
            tv =  convertView.findViewById(R.id.textView);
            tv.setText(prosteVsecko.getJmeno());
        }
    }

    public UlozCoPujde prosteVsecko;
    public int ikona;
    public boolean edit=false;

    TextView tv ;
    ImageView image;

    public int vlastnostiLayout=R.layout.zakladni_vlastnosti;
    public int funkcniLayout;

    public int getFunkcniLayout() {
        return funkcniLayout;
    }

    public int getVlastnostiLayout() {
        return vlastnostiLayout;
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
    protected void obsluha(){
        Intent funkcni=new Intent(nejakyContext, FunkcePrvku.class);
        funkcni.putExtra("Obsluha",getProsteVsecko());
        nejakyContext.startActivity(funkcni);
    }
    public void nastavSiVlastnosti(Activity KdeToDelam){
        Ejmeno=KdeToDelam.findViewById(R.id.jmeno);
        Eipadresa=KdeToDelam.findViewById(R.id.ipina);
        Eport=KdeToDelam.findViewById(R.id.portak);
        Ejmeno.setText(prosteVsecko.getJmeno());
        Eipadresa.setText(prosteVsecko.getUrl());
        Eport.setText(""+prosteVsecko.getPort());
    }
    public void vemSiCoPotrebujes(Activity kdeToDelam){
        prosteVsecko.setJmeno(Ejmeno.getText().toString());
        prosteVsecko.setUrl(Eipadresa.getText().toString());
        prosteVsecko.setPort(Integer.parseInt(Eport.getText().toString()));
    }
    public Prvek(int ikona, String jmeno) {
        prosteVsecko=new UlozCoPujde(jmeno);
        this.ikona = ikona;
    }
    static public Prvek zalozSpravnyPrvek(UlozCoPujde neco){
        Prvek novy=null;
        switch (neco.getTypPrvku()){
            case "Teplomer":{
                novy=new Teplomer(neco);
                break;
            }
            case "Zarovka":{
                novy=new Zarovka(neco);
                break;
            }
            case "Kamera":{
                novy=new Kamera(neco);
                break;
            }
            case "Zasuvka":{
                novy=new Zasuvka(neco);
                break;
            }
            case "SensorPS":{
                novy=new SensorPS(neco);
                break;
            }
        }
        return novy;
    }

    public void setIkona(int ikona) {
        this.ikona = ikona;
        image.setImageResource(ikona);
    }
    public void nastavPozici(float x,float y){
        convertView.setX(x);
        convertView.setY(y);
        prosteVsecko.soradniceX=x;
        prosteVsecko.soradniceY=y;

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
    private Context nejakyContext;
    public View dejmiNovyNahled(Context context ) {
        saveTask();

        return getView(context);

    }

    public View getView(Context context ) {
        nejakyContext=context;

        LayoutInflater inflater= (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        convertView = inflater.inflate(R.layout.prvke, null);
        tv =  convertView.findViewById(R.id.textView);
        image =  convertView.findViewById(R.id.imageView2);
        tv.setText(prosteVsecko.getJmeno());
        image.setImageResource(ikona);
        setEdit(true);
        dlouhyKlik(convertView);
        nastavPozici(prosteVsecko.soradniceX,prosteVsecko.soradniceY);
        return convertView;

    }

    public void deleteTask(final UlozCoPujde task) {
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
        updateTask(prosteVsecko);
    }
    public void fachej(Activity kdeToDelam){}

    public void uzNeFachej(Activity kdeToDelam){}


}
