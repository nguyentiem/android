package com.pdfreader.scanner.pdfviewer.utils;

import android.app.Activity;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.pdfreader.scanner.pdfviewer.R;

import java.util.Objects;

public class SnackBarUtils {
    public static Snackbar getSnackbar(Activity context, String message) {
        Snackbar snackBar = Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                message, Snackbar.LENGTH_LONG).setActionTextColor(ColorUtils.getColorFromResource(context, R.color.whiteTotally));
        View snackBarView = snackBar.getView();
        snackBarView.setBackgroundColor(ColorUtils.getThemeColor(context));
        return snackBar;
    }

    public static Snackbar getShortSnackbar(Activity context, String message) {
        Snackbar snackBar = Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                message, Snackbar.LENGTH_SHORT).setActionTextColor(ColorUtils.getColorFromResource(context, R.color.whiteTotally));
        View snackBarView = snackBar.getView();
        snackBarView.setBackgroundColor(ColorUtils.getThemeColor(context));
        return snackBar;
    }

    public static Snackbar getIndefiniteSnackbar(Activity context, String message) {
        Snackbar snackBar = Snackbar.make(Objects.requireNonNull(context).findViewById(android.R.id.content),
                message, Snackbar.LENGTH_INDEFINITE);
        View snackBarView = snackBar.getView();
        snackBarView.setBackgroundColor(ColorUtils.getThemeColor(context));
        return snackBar;
    }
}
