package com.example.svita.drag;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.svita.drag.prvkose.Kamera;
import com.example.svita.drag.prvkose.KliknutiPrvku;
import com.example.svita.drag.prvkose.Prvek;
import com.example.svita.drag.prvkose.SensorPS;
import com.example.svita.drag.prvkose.Teplomer;
import com.example.svita.drag.prvkose.UlozCoPujde;
import com.example.svita.drag.prvkose.Zarovka;
import com.example.svita.drag.prvkose.Zasuvka;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public RelativeLayout plocha;
    ListView listak;
    Button nabidkovy;
    private  Prvek zrovnaPresouvam;

    String[] names = { "Teploměr", "Žárovka", "Kamera", "Zásuvka","Sensor Pohybu" };
    int[] images = { R.drawable.thermometer, R.drawable.bulb, R.drawable.camera,
            R.drawable.plugwall,R.drawable.sensorpohybu };
    List<Prvek> prvke=new ArrayList<>();

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
                        view.startDragAndDrop(data,shadow,null,0);
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
                        dalsi.getProsteVsecko().setId(prvke.size());
                        prvke.add(dalsi);
                        //pridání polozky do layoutu
                        plocha.addView(dalsi.dejmiNovyNahled(MainActivity.this));
                        zrovnaPresouvam=prvke.get(prvke.size()-1);
                        nahodtamAkce(dalsi);

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
                nahodtamAkce(novy);
            }

        }
    }
    private static final int NASTAVENI_DOPLNENOS = 1001;
    private void nahodtamAkce(Prvek neco){
        neco.setKlikNaVytvoreniStinu(new KliknutiPrvku() {
            @Override
            public void klikAkce(Prvek coKlik) {
                zrovnaPresouvam=coKlik;
                ClipData data=ClipData.newPlainText("","");

                View.DragShadowBuilder shadow=new View.DragShadowBuilder(coKlik.convertView){
                    @Override
                    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
                        int width, height;
                        width = getView().getWidth();
                        height = getView().getHeight();
                        outShadowSize.set(width, height);

                        // Sets the touch point's position to be in the middle of the drag shadow
                        outShadowTouchPoint.set(width / 2, height / 2);
                    }
                };
                coKlik.convertView.startDragAndDrop(data,shadow,null,0);
            }
        });
        neco.setVolejNastaveniPrvku(new KliknutiPrvku() {
            @Override
            public void klikAkce(final Prvek coKlik) {
                final View v=coKlik.convertView;
                PopupMenu popup=new PopupMenu(v.getContext(),v);
                MenuInflater inflater =popup.getMenuInflater();
                inflater.inflate(R.menu.munu,popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id=item.getItemId();
                        switch (id){
                            case R.id.vlastnosti:{
                                Intent funkcni=new Intent(MainActivity.this,vlastnosti.class);
                                funkcni.putExtra("Vlastnosti",coKlik.getProsteVsecko());
                                MainActivity.this.startActivityForResult(funkcni,NASTAVENI_DOPLNENOS);
                                break;

                            }
                            case R.id.odstranit:{
                                plocha.removeView(v);
                                prvke.remove(coKlik);
                                coKlik.deleteTask(coKlik.getProsteVsecko());

                                break;

                            }
                        }
                        return true;
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==NASTAVENI_DOPLNENOS&&resultCode==RESULT_OK)
        {
            final UlozCoPujde vysledkos=(UlozCoPujde) data.getSerializableExtra("Vysledkos");
            int index=Iterables.indexOf(prvke, new Predicate<Prvek>() {
                @Override
                public boolean apply(@NullableDecl Prvek input) {
                    return input.getProsteVsecko().getId()==vysledkos.getId();

                }
            });
            prvke.get(index).setProsteVsecko(vysledkos);
            prvke.get(index).updatePrvek();
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
