package com.example.svita.drag.vychytavkyprozasuvku;

import java.io.Serializable;

public class Vyraz implements Serializable {
    private String Prvnicast,Druhacast,nerovnost,LogikaNaKonci;

    public Vyraz(String prvnicast, String druhacast, String nerovnost, String logikaNaKonci) {
        Prvnicast = prvnicast;
        Druhacast = druhacast;
        this.nerovnost = nerovnost;
        LogikaNaKonci = logikaNaKonci;
    }

    public String getPrvnicast() {
        return Prvnicast;
    }

    public void setPrvnicast(String prvnicast) {
        Prvnicast = prvnicast;
    }

    public String getDruhacast() {
        return Druhacast;
    }

    public void setDruhacast(String druhacast) {
        Druhacast = druhacast;
    }

    public String getNerovnost() {
        return nerovnost;
    }

    public void setNerovnost(String nerovnost) {
        this.nerovnost = nerovnost;
    }

    public String getLogikaNaKonci() {
        return LogikaNaKonci;
    }

    public void setLogikaNaKonci(String logikaNaKonci) {
        LogikaNaKonci = logikaNaKonci;
    }
}
