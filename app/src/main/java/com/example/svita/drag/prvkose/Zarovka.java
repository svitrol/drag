package com.example.svita.drag.prvkose;

import com.example.svita.drag.R;

public class Zarovka extends Prvek {
    public Zarovka() {
        super(R.drawable.bulb, "zarovka");
        prosteVsecko.setTypPrvku("Zarovka");
    }
    public Zarovka(UlozCoPujde prostevsecko){
        super(R.drawable.bulb, "zarovka");
        setProsteVsecko(prostevsecko);
    }
}
