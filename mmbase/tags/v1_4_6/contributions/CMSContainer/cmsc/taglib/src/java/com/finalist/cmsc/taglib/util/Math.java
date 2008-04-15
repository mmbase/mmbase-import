package com.finalist.cmsc.taglib.util;

public class Math {

    public static double ceil(Object obj) {
        if (null != obj && obj instanceof Double) {
            return java.lang.Math.ceil(((Double) obj));
        }
        return 0;
    }
}
