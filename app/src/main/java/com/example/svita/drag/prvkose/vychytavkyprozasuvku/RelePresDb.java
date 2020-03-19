package com.example.svita.drag.prvkose.vychytavkyprozasuvku;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Button;

import com.example.svita.drag.prvkose.Prvek;
import com.example.svita.drag.prvkose.Zasuvka;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class RelePresDb extends AsyncTask<String,Void,String> {
    String login_url ;
    String login_name ;
    String login_pass ;
    Button[] pole;
    int kolikZasuvek;
    Prvek aktivni;

    public RelePresDb(Zasuvka aktivni, Button[] pole, int kolikZasuvek) {
        this.login_url = aktivni.prosteVsecko.getDb();
        this.login_name = aktivni.prosteVsecko.getDbjmeno();
        this.login_pass = aktivni.prosteVsecko.getDbheslo();
        this.pole=pole;
        this.kolikZasuvek=kolikZasuvek;
        this.aktivni=aktivni;
    }
    @Override
    protected String doInBackground(String... strings) {
        if(strings[0].equals("update")){
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String data = URLEncoder.encode("jmeno","UTF-8")+
                        "="+URLEncoder.encode(login_name,"UTF-8")+"&"+
                        URLEncoder.encode("heslo","UTF-8")+
                        "="+URLEncoder.encode(login_pass,"UTF-8")+"&"+
                        URLEncoder.encode("akce","UTF-8")+
                        "="+URLEncoder.encode(strings[0],"UTF-8");
                String[] coBudeOvladat= aktivni.coMaPrvekPodSebou().split(":");
                for(int i=0;i<coBudeOvladat.length;i++){
                    String jdenotka=coBudeOvladat[i];
                    String hodnota=strings[i+1];
                    data+="&"+URLEncoder.encode(jdenotka,"UTF-8")+"="+URLEncoder.encode(hodnota,"UTF-8");
                }
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                httpURLConnection.getInputStream().close();
                httpURLConnection.disconnect();
                return strings[9];

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        else if(strings[0].equals("stav")){
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String data = URLEncoder.encode("jmeno","UTF-8")+"="+URLEncoder.encode(login_name,"UTF-8")+"&"+
                        URLEncoder.encode("heslo","UTF-8")+"="+URLEncoder.encode(login_pass,"UTF-8")+"&"+
                        URLEncoder.encode("akce","UTF-8")+"="+URLEncoder.encode(strings[0],"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                //httpURLConnection.setReadTimeout(10000);
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine())!=null)
                {
                    response+= line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;

    }




    @Override
    protected void onPostExecute(String s) {
        if(s!= null){
            try {
                //Toast.makeText(pole[0].getContext().getApplicationContext(),s,Toast.LENGTH_LONG).show();
                if(s.length()>2){
                    String[]polak= s.split(";");
                    //RX:0
                    for(int i=0;i<kolikZasuvek;i++){
                        if(polak[i].split(":")[1].equals("1")){
                            pole[i].setHint("ON");
                            pole[i].setBackgroundColor(Color.rgb(152, 240, 53));
                        }
                        else {
                            pole[i].setHint("OFF");
                            pole[i].setBackgroundColor(Color.rgb(237, 94, 69));
                        }
                    }
                }
                else {
                    Button aktivni=pole[s.charAt(1)-48-1];
                    if(aktivni.getHint().toString().equals("ON")){
                        aktivni.setHint("OFF");
                        aktivni.setBackgroundColor(Color.rgb(237, 94, 69));
                    }
                    else {
                        aktivni.setHint("ON");
                        aktivni.setBackgroundColor(Color.rgb(152, 240, 53));
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
