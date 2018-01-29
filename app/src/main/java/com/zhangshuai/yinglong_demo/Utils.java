package com.zhangshuai.yinglong_demo;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.ArrayList;

/**
 * Created by fly on 16/10/21.
 */

public class Utils {
    public static String checkNull(String s) {
        return s == null ? "" : s;
    }

    /**
     * dp和px的转换
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /*public static int sp2px(float value) {
        DisplayMetrics metric = DimidiateCore.getInstance().getContext().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, metric);
    }

    public static int px2sp(float pxValue) {
        final float fontScale = DimidiateCore.getInstance().getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }*/

    public static ArrayList<String> range2List(String range){
        ArrayList<String> list = new ArrayList<>();
        if (!TextUtil.isNull(range)){
            String[] split = range.split("-");
            int min = Integer.parseInt(split[1]);
            int max = Integer.parseInt(split[2]);
            for (int i = min; i <= max; i++) {
                list.add(String.valueOf(i));
            }
        }
        return list;

    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Returns true if a and b are equal, including if they are both null.
     * <p><i>Note: In platform versions 1.1 and earlier, this method only worked well if
     * both the arguments were instances of String.</i></p>
     *
     * @param a first CharSequence to check
     * @param b second CharSequence to check
     * @return true if a and b are equal
     * <p>
     * NOTE: Logic slightly change due to strict policy on CI -
     * "Inner assignments should be avoided"
     */
    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        if (a != null && b != null) {
            int length = a.length();
            if (length == b.length()) {
                if (a instanceof String && b instanceof String) {
                    return a.equals(b);
                } else {
                    for (int i = 0; i < length; i++) {
                        if (a.charAt(i) != b.charAt(i)) return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }


}
