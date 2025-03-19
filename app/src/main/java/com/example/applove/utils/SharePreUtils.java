package com.example.applove.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePreUtils {
    private static SharedPreferences mSharePref;

    public static void init(Context context) {
        if (mSharePref == null) {
            mSharePref = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }


    public static void forceRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("rated", true);
        editor.commit();
    }

    public static boolean isRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("rated", false);
    }

    public static int getClickStartWelCome(Context context) {
        SharedPreferences pre = context.getSharedPreferences("start_welcome", Context.MODE_PRIVATE);
        return pre.getInt("counts", 1);
    }

    public static void setClickStartWelCome(Context context) {
        SharedPreferences pre = context.getSharedPreferences("start_welcome", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("counts", pre.getInt("counts", 1) + 1);
        editor.apply();
    }
    public static void setStyleMap(Context context,int style){
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("STYLE_MAP", style);
        editor.apply();
    }
    public static int getStyleMap(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getInt("STYLE_MAP", 0);
    }

    public static void setTimeShowAds(Context context, long timer) {
        SharedPreferences pre = context.getSharedPreferences("show_ads", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putLong("time", timer);
        editor.apply();
    }

    public static long getTimeShowAds(Context context) {
        SharedPreferences pre = context.getSharedPreferences("show_ads", Context.MODE_PRIVATE);
        return pre.getLong("time", System.currentTimeMillis());
    }

    public static void setConfigAds(Context context, String name_config, boolean config) {
        SharedPreferences pre = context.getSharedPreferences("remote_fill", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean(name_config, config);
        editor.apply();
    }

    public static boolean getConfigAds(Context context, String name_config) {
        SharedPreferences pre = context.getSharedPreferences("remote_fill", Context.MODE_PRIVATE);
        return pre.getBoolean(name_config, true);

    }
}
