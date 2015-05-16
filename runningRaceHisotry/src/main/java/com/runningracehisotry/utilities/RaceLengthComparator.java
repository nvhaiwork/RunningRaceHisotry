package com.runningracehisotry.utilities;

import com.runningracehisotry.constants.Constants;
import com.runningracehisotry.models.Race;

import java.util.Comparator;

/**
 * Created by QuyNguyen on 5/16/2015.
 */
public class RaceLengthComparator implements Comparator<Race> {
    @Override
    public int compare(Race lhs, Race rhs) {
        try{
            String mileStr1 = lhs.getRaceMiles();
            String mileStr2 = rhs.getRaceMiles();
            float lFinishTime = Float.parseFloat(mileStr1);
            float rFinishTime = Float.parseFloat(mileStr2);
            //LogUtil.e(Constants.LOG_TAG, "parse to compare Sort length: "+ (lFinishTime - rFinishTime));

            if((lFinishTime - rFinishTime) > 0){
                return 1;
            }
            else if((lFinishTime - rFinishTime) == 0){
                return 0;
            }
            else{
                return -1;
            }

        }
        catch (Exception e){
            LogUtil.e(Constants.LOG_TAG, "parse to compare Sort length error: " + e.getMessage());
        }
        return 0;
    }
}
