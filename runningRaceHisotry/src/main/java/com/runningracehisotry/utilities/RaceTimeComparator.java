package com.runningracehisotry.utilities;

import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Race;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by QuyNguyen on 5/16/2015.
 */
public class RaceTimeComparator implements Comparator<Race> {
    @Override
    public int compare(Race lhs, Race rhs) {
        try{
            String[] dateStr1 = lhs.getFinisherTime().split(":");
            String[] dateStr2 = rhs.getFinisherTime().split(":");
            int lFinishTime = Integer.parseInt(dateStr1[0])*60*60
                    + Integer.parseInt(dateStr1[1])*60
                    + Integer.parseInt(dateStr1[2]);

            int rFinishTime = Integer.parseInt(dateStr2[0])*60*60
                    + Integer.parseInt(dateStr2[1])*60
                    + Integer.parseInt(dateStr2[2]);
            //LogUtil.d(Constants.LOG_TAG, "date 1|date 2: " + lFinishTime+"|" + rFinishTime);
            //LogUtil.d(Constants.LOG_TAG, "parse to compare Sort time: "+ (lFinishTime - rFinishTime));
            return (lFinishTime - rFinishTime);
        }
        catch (Exception e){
            LogUtil.e(Constants.LOG_TAG, "parse to compare Sort time error: " + e.getMessage());
        }
        return 0;
    }
}
