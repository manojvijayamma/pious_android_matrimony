package com.mega.usnazrani.Utility;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;


public class FontsOverride {

        public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {

            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Map<String, Typeface> newMap = new HashMap<>();
                newMap.put("serif", customFontTypeface);
                try {
                    final Field staticField = Typeface.class
                            .getDeclaredField("sSystemFontMap");
                    staticField.setAccessible(true);
                    staticField.set(null, newMap);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
                    defaultFontTypefaceField.setAccessible(true);
                    defaultFontTypefaceField.set(null, customFontTypeface);
                } catch (Exception e) {
                    Log.e(FontsOverride.class.getSimpleName(), "Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
                }
            }
        }
    }

