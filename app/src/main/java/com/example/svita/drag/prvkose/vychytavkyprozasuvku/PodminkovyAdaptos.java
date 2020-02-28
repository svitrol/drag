package com.example.svita.drag.prvkose.vychytavkyprozasuvku;

import android.content.Context;
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
    private kliklItem hracka;
    private kliklItem DlouheKlik=null;

    public PodminkovyAdaptos(Context mCtx, List<Podminkos> podminksListos, kliklItem hracka, kliklItem dlouheKlik) {
        this.mCtx = mCtx;
        PodminksListos = podminksListos;
        this.hracka = hracka;
        DlouheKlik = dlouheKlik;
    }

    public PodminkovyAdaptos(Context mCtx, List<Podminkos> podminksListos, kliklItem hracka) {
        this.mCtx = mCtx;
        PodminksListos = podminksListos;
        this.hracka=hracka;
    }

    @NonNull
    @Override
    public PodminkossViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.karta_podminky, viewGroup, false);
        if(DlouheKlik!=null)return new PodminkossViewHolder(view,hracka,DlouheKlik);
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

    class PodminkossViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView podminka,vyraz,prikaz;
        kliklItem kliklItem;
        public PodminkossViewHolder(View itemView, kliklItem kliknuti, final kliklItem dlouheKliknuti) {
            super(itemView);
            podminka = itemView.findViewById(R.id.nazevPodminky);
            vyraz = itemView.findViewById(R.id.podminka);
            prikaz = itemView.findViewById(R.id.prikazy);
            kliklItem=kliknuti;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int i=getLayoutPosition();
                    dlouheKliknuti.vowKliknuti(i,v);
                    return true;
                }
            });

        }
        public PodminkossViewHolder(View itemView, kliklItem kliknuti) {
            super(itemView);
            podminka = itemView.findViewById(R.id.nazevPodminky);
            vyraz = itemView.findViewById(R.id.podminka);
            prikaz = itemView.findViewById(R.id.prikazy);
            kliklItem=kliknuti;

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int i=getLayoutPosition();
            kliklItem.vowKliknuti(i,v);

        }

    }

}

