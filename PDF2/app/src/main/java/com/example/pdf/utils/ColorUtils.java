package com.example.pdf.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.pdfreader.scanner.pdfviewer.R;
import com.pdfreader.scanner.pdfviewer.data.DataManager;

public class ColorUtils {
    public static GradientDrawable getShapeFromColor(String color) {
        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius( 50 );
        shape.setColor(Color.parseColor(color));
        return shape;
    }

    public static int getColorFromCode(String color) {
        return Color.parseColor(color);
    }

    public static int getColorFromResource(Context context, int colorId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return context.getResources().getColor(colorId, context.getTheme());
            } else {
                return ContextCompat.getColor(context, colorId);
            }
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    public static int getThemeColor(Context context) {
        int selectedTheme = DataManager.getInstance(context).getTheme();
        int[] COLOR_LIST = {R.color.red_theme_color, R.color.blue_theme_color, R.color.jade_theme_color, R.color.violet_theme_color, R.color.orange_theme_color, R.color.green_theme_color, R.color.yellow_theme_color};
        return getColorFromResource(context, COLOR_LIST[selectedTheme]);
    }
}
