package com.example.svita.drag.prvkose;

import android.app.Activity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.svita.drag.R;

public class Kamera extends Prvek {
    public Kamera() {
        super(R.drawable.camera, "Kamera");
        prosteVsecko=new UlozCoPujde("Kamera","Kamera");
        funkcniLayout=R.layout.funkce_kamery;
    }
    @Override
    public void nastavSiVlastnosti(Activity kdeToDelam){
        super.nastavSiVlastnosti(kdeToDelam);
    }
    @Override
    public void vemSiCoPotrebujes(Activity kdeToDelam){
        super.vemSiCoPotrebujes(kdeToDelam);
        updatePrvek();
    }


    @Override
    public void NaplnProsteVsecko(){
        super.NaplnProsteVsecko();
    }
    @Override
    public void VemSiToZpatky(){
        super.VemSiToZpatky();
    }
    @Override
    public void fachej(Activity kdeToDelam){

        WebView prohlizec=kdeToDelam.findViewById(R.id.prohlizec);
        prohlizec.setWebViewClient(new WebViewClient());
        prohlizec.loadUrl("http://"+url.toString()+"/");

        WebSettings webSettings = prohlizec.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
}
