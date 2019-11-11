package com.example.gmap_v_01_2.reader;

public class FindStringBefore {

    public String before(String value, String a){

        int posA = value.indexOf(a);
        if(posA == -1) {
            return "";
        }
        return value.substring(0, posA);
    }

}
