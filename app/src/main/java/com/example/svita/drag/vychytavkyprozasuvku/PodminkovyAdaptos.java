package com.example.svita.drag.vychytavkyprozasuvku;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.svita.drag.R;

import java.util.List;

public class PodminkovyAdaptos extends RecyclerView.Adapter<PodminkovyAdaptos.PodminkossViewHolder> {

    private Context mCtx;
    private List<Podminkos> PodminksListos;
    private View.OnClickListener hracka;

    public PodminkovyAdaptos(Context mCtx, List<Podminkos> podminksListos,View.OnClickListener hracka) {
        this.mCtx = mCtx;
        PodminksListos = podminksListos;
        this.hracka=hracka;
    }

    @NonNull
    @Override
    public PodminkossViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.karta_podminky, viewGroup, false);
        return new PodminkossViewHolder(view,hracka);
    }

    @Override
    public void onBindViewHolder(@NonNull PodminkossViewHolder podminkossViewHolder, int i) {
        Podminkos p = PodminksListos.get(i);
        String podminka="";
        for(Vyraz jednaCast:p.getVyrazy()){
            podminka+=jednaCast.getPrvnicast()+" "+jednaCast.getNerovnost()+" "+jednaCast.getDruhacast()+" "+jednaCast.getLogikaNaKonci()+" ";
        }
        podminka+="?";
        podminkossViewHolder.vyraz.setText(podminka);
        podminkossViewHolder.prikaz.setText(p.getKladnyPrikaz()+" - "+p.getZapornyPrikaz());
        podminkossViewHolder.podminka.setText(i+". Podminka");
    }

    @Override
    public int getItemCount() {
        return PodminksListos.size();
    }

    class PodminkossViewHolder extends RecyclerView.ViewHolder {

        TextView podminka,vyraz,prikaz;

        public PodminkossViewHolder(View itemView, View.OnClickListener hracka) {
            super(itemView);
            podminka = itemView.findViewById(R.id.nazevPodminky);
            vyraz = itemView.findViewById(R.id.podminka);
            prikaz = itemView.findViewById(R.id.prikazy);

            itemView.setOnClickListener(hracka);


        }


    }
}
