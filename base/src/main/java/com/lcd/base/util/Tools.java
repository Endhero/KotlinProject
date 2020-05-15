package com.lcd.base.util;

import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by Chen on 2019/11/29.
 */
public class Tools {

    public static int getStatusBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        } else {
            return TypedValue.complexToDimensionPixelSize(24, activity.getResources().getDisplayMetrics());
        }
    }
}
