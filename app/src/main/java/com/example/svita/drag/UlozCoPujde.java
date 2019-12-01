package com.example.svita.drag;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
@Entity
public class UlozCoPujde {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "popis")
    public String popis="volovina";
    @ColumnInfo(name = "typPrvku")
    public String typPrvku= "Teplomer";
    @ColumnInfo(name = "url")
    public String url="1.1.1.1";
    @ColumnInfo(name = "jmeno")
    public String jmeno="Teplomer";
    @ColumnInfo(name = "portak")
    public int port=80;
    @ColumnInfo(name = "souradniceX")
    protected float soradniceX=0;
    @ColumnInfo(name = "souradniceY")
    protected float soradniceY=0;
    @ColumnInfo(name = "uriDatabaze")
    public String db="1.1.1.1";
    @ColumnInfo(name = "dbHeslo")
    protected String dbheslo="Teplomer";
    @ColumnInfo(name = "dbJmeno")
    public String dbjmeno="Teplomer";
    @ColumnInfo(name = "dbPort")
    public int dbport=80;
    @ColumnInfo(name = "mamDb")
    public boolean MamDatabazi=true;
    @Ignore//@ColumnInfo(name = "kolikZasuvek")
    public int kolikzasuvek=8;
    @Ignore
    public String mistnost="obyvak";



    public UlozCoPujde(UlozCoPujde dalsi){
        setId(dalsi.getId());
        setPopis(dalsi.getPopis());
        setTypPrvku(dalsi.getTypPrvku());
        setUrl(dalsi.getUrl());
        setJmeno(dalsi.getJmeno());
        setPort(dalsi.getPort());
        setSoradniceX(dalsi.getSoradniceX());
        setSoradniceY(dalsi.getSoradniceY());
        setDb(dalsi.getDb());
        setDbheslo(dalsi.getDbheslo());
        setDbjmeno(dalsi.getDbjmeno());
        setDbport(dalsi.getDbport());
        setMamDatabazi(dalsi.isMamDatabazi());
        setKolikzasuvek(dalsi.getKolikzasuvek());
    }
    public UlozCoPujde(String typPrvku, String jmeno) {
        this.typPrvku = typPrvku;
        this.jmeno = jmeno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }

    public String getTypPrvku() {
        return typPrvku;
    }

    public void setTypPrvku(String typPrvku) {
        this.typPrvku = typPrvku;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public float getSoradniceX() {
        return soradniceX;
    }

    public void setSoradniceX(float soradniceX) {
        this.soradniceX = soradniceX;
    }

    public float getSoradniceY() {
        return soradniceY;
    }

    public void setSoradniceY(float soradniceY) {
        this.soradniceY = soradniceY;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getDbheslo() {
        return dbheslo;
    }

    public void setDbheslo(String dbheslo) {
        this.dbheslo = dbheslo;
    }

    public String getDbjmeno() {
        return dbjmeno;
    }

    public void setDbjmeno(String dbjmeno) {
        this.dbjmeno = dbjmeno;
    }

    public int getDbport() {
        return dbport;
    }

    public void setDbport(int dbport) {
        this.dbport = dbport;
    }

    public boolean isMamDatabazi() {
        return MamDatabazi;
    }

    public void setMamDatabazi(boolean mamDatabazi) {
        MamDatabazi = mamDatabazi;
    }
    public int getKolikzasuvek() {
        return kolikzasuvek;
    }

    public void setKolikzasuvek(int kolikzasuvek) {
        this.kolikzasuvek = kolikzasuvek;
    }
    public String getMistnost() {
        return mistnost;
    }

    public void setMistnost(String mistnost) {
        this.mistnost = mistnost;
    }
}
