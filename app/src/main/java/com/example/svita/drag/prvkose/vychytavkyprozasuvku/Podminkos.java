package com.example.svita.drag.prvkose.vychytavkyprozasuvku;

import java.io.Serializable;
import java.util.List;

public class Podminkos implements Serializable,Cloneable {
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private String ZapornyPrikaz,KladnyPrikaz;
    private List<Vyraz> vyrazy;

    public Podminkos( String kladnyPrikaz,String zapornyPrikaz, List<Vyraz> vyrazy) {
        ZapornyPrikaz = zapornyPrikaz;
        KladnyPrikaz = kladnyPrikaz;
        this.vyrazy = vyrazy;
    }

    public String getZapornyPrikaz() {
        return ZapornyPrikaz;
    }

    public void setZapornyPrikaz(String zapornyPrikaz) {
        ZapornyPrikaz = zapornyPrikaz;
    }

    public String getKladnyPrikaz() {
        return KladnyPrikaz;
    }

    public void setKladnyPrikaz(String kladnyPrikaz) {
        KladnyPrikaz = kladnyPrikaz;
    }

    public List<Vyraz> getVyrazy() {
        return vyrazy;
    }

    public void setVyrazy(List<Vyraz> vyrazy) {
        this.vyrazy = vyrazy;
    }
}
