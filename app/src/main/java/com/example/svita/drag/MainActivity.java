package com.example.svita.drag;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.svita.drag.Prvek;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static RelativeLayout plocha;
    ListView listak;
    Button nabidkovy;
    public static Prvek zrovnaPresouvam;

    String[] names = { "Teploměr", "Žárovka", "Kamera", "Zásuvka","Sensor Pohybu" };
    int[] images = { R.drawable.thermometer, R.drawable.bulb, R.drawable.camera,
            R.drawable.plugwall,R.drawable.sensorpohybu };
    static List<Prvek> prvke=new ArrayList<>();

    boolean edit=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plocha=findViewById(R.id.relativeLayout);
        listak=findViewById(R.id.listView);
        nabidkovy=findViewById(R.id.button);
        CustomAdapter adaptak=new CustomAdapter();
        listak.setAdapter(adaptak);
        dostanPrvky();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menus){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menus);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item){



        if(edit==false){
            //přesouvačka
            nabidkovy.setVisibility(View.VISIBLE);
            item.setTitle("end edit");
            plocha.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    if(event.getAction()==DragEvent.ACTION_DROP){
                        float ex=event.getX()-62;
                        float ey=event.getY()-53;

                        zrovnaPresouvam.nastavPozici(ex,ey);
                    }
                    return true;
                }
            });
            for (Prvek neco:prvke) {
                neco.setEdit(true);
            }

            edit=true;
        }
        else{
            item.setTitle("edit");
            for (Prvek neco:prvke) {
                neco.setEdit(false);
                neco.updatePrvek();
            }
            plocha.setOnDragListener(null);
            edit=false;
            nabidkovy.setVisibility(View.INVISIBLE);
            listak.setVisibility(View.INVISIBLE);
            jdeVidetListak=false;
        }
        return true;
    }
    boolean jdeVidetListak=false;
    public void ukazNabidku(View v){
        if(!jdeVidetListak){
            try{
                jdeVidetListak=true;
                listak.setHovered(false);
                listak.setVisibility(View.VISIBLE);
                listak.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        ClipData data=ClipData.newPlainText("","");
                        View.DragShadowBuilder shadow=new View.DragShadowBuilder(view);
                        view.startDrag(data,shadow,null,0);
                        listak.setVisibility(View.INVISIBLE);
                        jdeVidetListak=false;
                        Prvek dalsi=null;
                        switch(position){
                            case 0:{
                                dalsi=new Teplomer();
                                break;
                            }
                            case 1:{
                                dalsi=new Zarovka();
                                break;
                            }
                            case 2:{
                                dalsi=new Kamera();
                                break;
                            }
                            case 3:{
                                dalsi=new Zasuvka();
                                break;
                            }
                            case 4:{
                                dalsi=new SensorPS();
                                break;
                            }

                        }
                        dalsi.prosteVsecko.setId(prvke.size());
                        prvke.add(dalsi);
                        //pridání polozky do layoutu
                        dalsi.prosteVsecko.setId(prvke.size()-1);
                        plocha.addView(prvke.get(prvke.size()-1).dejmiNovyNahled(MainActivity.this));
                        zrovnaPresouvam=prvke.get(prvke.size()-1);

                        return false;
                    }
                });
            }catch(Exception e){
                Toast.makeText(MainActivity.this, "Nějak to nevyšlo asi pro vyjimku: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
        else{
            jdeVidetListak=false;
            listak.setVisibility(View.INVISIBLE);
        }


    }

    private void dostanPrvky(){
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
                VrazTamTenListDoLayoutu(tasks);
            }
        }

        dostanPrvky gt = new dostanPrvky();
        gt.execute();
    }
    public void VrazTamTenListDoLayoutu(List<UlozCoPujde> listak){
        for (UlozCoPujde neco:listak ) {
            Prvek novy=Prvek.zalozSpravnyPrvek(neco);
            if(novy!=null){
                prvke.add(novy);
                //pridání polozky do layoutu
                plocha.addView(prvke.get(prvke.size()-1).getView(MainActivity.this));
                novy.setEdit(false);
            }

        }
    }

    class CustomAdapter extends BaseAdapter{
        //fajne seznam s obrázky
        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            convertView = inflater.inflate(R.layout.custom, null);
            TextView tv =  convertView.findViewById(R.id.textView1);
            ImageView image =  convertView.findViewById(R.id.imageView1);
            tv.setText(names[position]);
            image.setImageResource(images[position]);
            return convertView;
        }
    }

}
