package com.runningracehisotry.utilities;

import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Race;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by QuyNguyen on 5/16/2015.
 */
public class RaceDateComparator implements Comparator<Race> {
    @Override
    public int compare(Race lhs, Race rhs) {
        String raceDate1 = lhs.getRaceDate().substring(0,10);
        String raceDate2 = rhs.getRaceDate().substring(0,10);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date lDate, rDate;
        try {
            //LogUtil.e(Constants.LOG_TAG, "parse to compare Sort date: " + raceDate1 +"|" +raceDate2);
            lDate = format.parse(raceDate1);
            rDate = format.parse(raceDate2);

            LogUtil.e(Constants.LOG_TAG, "parse to compare Sort date: "+ (rDate.compareTo(lDate)));
            return -rDate.compareTo(lDate);
        } catch (java.text.ParseException e) {
            LogUtil.e(Constants.LOG_TAG, "parse to compare sort date error: " + e.getMessage());
        }
        return 0;
        //return lhs.getRaceDate();
    }
}
