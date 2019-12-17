package com.example.svita.drag.vychytavkyprozasuvku;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class tvorbaPodminky extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    AutoCompleteTextView hodnota1,hodnota2;
    Spinner nerovnoxti,logika;
    LinearLayout zaprnavetevCoOvladnout,kladnavetevCoOvladnout;
    CheckBox kladnaVtevevicerosPinos,zapornaVetevVicerosPinos;
    ArrayAdapter<String> napovednyAdapter;
    LinearLayout rozhodovaciCast;
    List<View> listakPodminkos=new ArrayList<>();
    List<View> listakofPinosKladnos=new ArrayList<>();
    List<View> listakoPinosZaporos=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvorba_podminky);

        rozhodovaciCast=findViewById(R.id.castRozhodovaci);
        LayoutInflater inflater= (LayoutInflater) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View convertView = inflater.inflate(R.layout.podminkova_jednotka, null);
        rozhodovaciCast.addView(convertView);

        hodnota1=convertView.findViewById(R.id.actv);
        hodnota2=convertView.findViewById(R.id.actv2);

        nerovnoxti=convertView.findViewById(R.id.spinner);
        logika=convertView.findViewById(R.id.spinner2);

        zaprnavetevCoOvladnout=findViewById(R.id.zaporenavetev);
        kladnavetevCoOvladnout=findViewById(R.id.kladackavetev);

        kladnaVtevevicerosPinos=findViewById(R.id.kladnycheckBox);
        zapornaVetevVicerosPinos=findViewById(R.id.zapornycheckBox);

        kladnaVtevevicerosPinos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                kladnavetevCoOvladnout.removeAllViews();
                listakofPinosKladnos=NahodTamTenVyberos(kladnavetevCoOvladnout,isChecked);
            }
        });

        zapornaVetevVicerosPinos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                zaprnavetevCoOvladnout.removeAllViews();
                listakoPinosZaporos=NahodTamTenVyberos(zaprnavetevCoOvladnout,isChecked);
            }
        });



        listakPodminkos.add(convertView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.znaminka, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nerovnoxti.setAdapter(adapter);

        nerovnoxti.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> pracka = ArrayAdapter.createFromResource(this,R.array.logickeOperatory, android.R.layout.simple_spinner_item);

        pracka.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        logika.setAdapter(pracka);

        logika.setOnItemSelectedListener(new PridejRadek(this));

        dostanPrvky();

        listakoPinosZaporos=NahodTamTenVyberos(zaprnavetevCoOvladnout,false);
        listakofPinosKladnos=NahodTamTenVyberos(kladnavetevCoOvladnout,false);


    }
    private String DostanZtohoPiny(List<View> listonos, EditText coasovac){
        String vysledek="";
        if(listonos.size()==1){
            Spinner vyberovkaPinu=listonos.get(0).findViewById(R.id.rele);
            RadioGroup hodnota=listonos.get(0).findViewById(R.id.stavy);
            vysledek=((TextView)vyberovkaPinu.getSelectedView()).getText().toString()+":";
            switch(hodnota.getCheckedRadioButtonId()){
                case R.id.on:{
                    vysledek+="1";
                    break;
                }
                case R.id.off:{
                    vysledek+="0";
                    break;
                }
            }

        }
        else {
            vysledek="P";
            for(View pinator:listonos){
                RadioGroup hodnota=pinator.findViewById(R.id.stavy);
                switch(hodnota.getCheckedRadioButtonId()){
                    case R.id.on:{
                        vysledek+="1";
                        break;
                    }
                    case R.id.off:{
                        vysledek+="0";
                        break;
                    }
                    case R.id.nedifnovan:{
                        vysledek+="X";
                        break;
                    }
                }
            }

        }
        vysledek+=":"+coasovac.getText().toString();
        return vysledek;
    }
    private Podminkos prectiTvarPodminky(){
        List <Vyraz> vyrazivoNaVyhodnoceni=new ArrayList<>();
        for(View podminkovaJednotka:listakPodminkos){
            Spinner znaminkaNerovnosti,LogickyeOperatory;
            AutoCompleteTextView prvniCast,DruhaCast;
            znaminkaNerovnosti=podminkovaJednotka.findViewById(R.id.spinner);
            LogickyeOperatory=podminkovaJednotka.findViewById(R.id.spinner2);
            prvniCast=podminkovaJednotka.findViewById(R.id.actv);
            DruhaCast=podminkovaJednotka.findViewById(R.id.actv2);
            Vyraz hlavicka=new Vyraz(prvniCast.getText().toString(),DruhaCast.getText().toString(),((TextView)znaminkaNerovnosti.getSelectedView()).getText().toString(),((TextView)LogickyeOperatory.getSelectedView()).getText().toString());
            vyrazivoNaVyhodnoceni.add(hlavicka);
        }
        EditText kladnycas=findViewById(R.id.kladnyeditText);
        EditText zapornycas=findViewById(R.id.zapornyeditText);

        Podminkos chobotnicka=new Podminkos(DostanZtohoPiny(listakofPinosKladnos,kladnycas),DostanZtohoPiny(listakoPinosZaporos,zapornycas),vyrazivoNaVyhodnoceni);
        return chobotnicka;
    }
    public void podminkaJeHotova(View v){
        Intent intent=new Intent();
        intent.putExtra("Podminkos",prectiTvarPodminky());

        setResult(Activity.RESULT_OK,intent);
        finish();
    }
    private List<View> NahodTamTenVyberos(LinearLayout kdeJsem,boolean viceroPinos){
        List<View> veslednyListak=new ArrayList<>();
        if(viceroPinos){
            for (String pinos:MainActivity.aktivni.coMaPrvekPodSebou().split(":")) {
                LayoutInflater inflater= (LayoutInflater)this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                View convertView = inflater.inflate(R.layout.nastacenivicenezjednoho_pinu, null);
                ((TextView)convertView.findViewById(R.id.rele)).setText(pinos);
                kdeJsem.addView(convertView);
                veslednyListak.add(convertView);

            }
        }
        else {
            ArrayAdapter<String> OvaldanyRele = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,MainActivity.aktivni.coMaPrvekPodSebou().split(":"));
            OvaldanyRele.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            LayoutInflater inflater= (LayoutInflater)this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View convertView = inflater.inflate(R.layout.nastavenipinu_zasuvky, null);
            Spinner vyberovkaPinu=convertView.findViewById(R.id.rele);
            vyberovkaPinu.setAdapter(OvaldanyRele);
            kdeJsem.addView(convertView);
            veslednyListak.add(convertView);

        }
        return veslednyListak;
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
                VrazTamTenListNapoved(tasks);
            }
        }

        dostanPrvky gt = new dostanPrvky();
        gt.execute();
    }
    private void VrazTamTenListNapoved(final List<UlozCoPujde> prvky){
        //String[] countries =

        napovednyAdapter = new ArrayAdapter<String>(this,R.layout.napovedna_polozka, R.id.text_view_list_item);
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
                String[] coMaPodPalcem=novy.coMaPrvekPodSebou().split(":");
                for (String vec:coMaPodPalcem) {
                    napovednyAdapter.add(""+jdnotka.getId()+":"+jdnotka.getJmeno()+":"+vec);

                }
            }


        }
        //System.out.println("hovado konec ");
        String[] veci=MainActivity.aktivni.coMaPrvekPodSebou().split(":");
        for (String vec:veci) {
            napovednyAdapter.add("TentoPrvek:"+vec);
        }
        hodnota1.setAdapter(napovednyAdapter);
        hodnota2.setAdapter(napovednyAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Spinner vyberovka=(Spinner)view;
        //vyberovka
        //((TextView) parent.getChildAt(0)).setTextSize(15);
        String[]znaminka={">","<","=","!=",">=","<="};
        ((TextView) parent.getChildAt(0)).setText(znaminka[position]);
        ((TextView) parent.getChildAt(0)).setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    class PridejRadek implements AdapterView.OnItemSelectedListener {
        Context CoSeDeje;
        public PridejRadek(Context CoSeDeje){
            this.CoSeDeje=CoSeDeje;
        }
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(position==1||position==2){
                String[]znaminka={"","&","|"};
                ((TextView) parent.getChildAt(0)).setText(znaminka[position]);
                ((TextView) parent.getChildAt(0)).setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                LayoutInflater inflater= (LayoutInflater)CoSeDeje.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                View convertView = inflater.inflate(R.layout.podminkova_jednotka, null);
                rozhodovaciCast.addView(convertView);
                hodnota1=convertView.findViewById(R.id.actv);
                hodnota2=convertView.findViewById(R.id.actv2);
                hodnota1.setAdapter(napovednyAdapter);
                hodnota2.setAdapter(napovednyAdapter);
                nerovnoxti=convertView.findViewById(R.id.spinner);
                logika=convertView.findViewById(R.id.spinner2);
                listakPodminkos.add(convertView);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(CoSeDeje,
                        R.array.znaminka, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                nerovnoxti.setAdapter(adapter);
                nerovnoxti.setOnItemSelectedListener(tvorbaPodminky.this);
                ArrayAdapter<CharSequence> pracka = ArrayAdapter.createFromResource(CoSeDeje,
                        R.array.logickeOperatory, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                logika.setAdapter(pracka);
                logika.setOnItemSelectedListener(this);
            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}

