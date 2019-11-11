package com.example.gmap_v_01_2.reader;

public class FindStringAfter {

    public String after(String value, String a) {

        int posA = value.indexOf(a);
        if(posA == -1) {
            return "";
        }
        int adJustedposA = posA + a.length();
        if(adJustedposA >= value.length()){
            return "";
        }
        return value.substring(adJustedposA);
    }

}
