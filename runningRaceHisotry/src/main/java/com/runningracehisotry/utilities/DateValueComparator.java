package com.runningracehisotry.utilities;

import com.runningracehisotry.models.Race;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by QuyNguyen on 5/16/2015.
 */
public class DateValueComparator implements Comparator<String> {

    public int compare(String a, String b) {
        String [] s1= a.substring(0,10).split("-");
        String [] s2= b.substring(0,10).split("-");
        if(s1[0].equalsIgnoreCase(s2[0])){
            return (Integer.parseInt(s1[0]) - Integer.parseInt(s2[0]));
        }
        else{
            return (Integer.parseInt(s1[1]) - Integer.parseInt(s2[1]));
        }
        /*if (map.get(a) >= map.get(b)) {
            return -1;
        } else {
            return 1;
        }*/
         // returning 0 would merge keys*/
    }
}
