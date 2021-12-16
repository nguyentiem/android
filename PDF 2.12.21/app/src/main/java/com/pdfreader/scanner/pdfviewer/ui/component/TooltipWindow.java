package com.pdfreader.scanner.pdfviewer.ui.component;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.pdfreader.scanner.pdfviewer.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class TooltipWindow {

    private static final int MSG_DISMISS_TOOLTIP = 100;
    private Context ctx;
    private PopupWindow tipWindow;
    private View contentView;
    private LayoutInflater inflater;
    private Disposable disposable;

    public TooltipWindow(Context ctx) {
        this.ctx = ctx;
        tipWindow = new PopupWindow(ctx);

        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.tooltip_layout, null);
    }

    public void showToolTip(View anchor) {

        tipWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        tipWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

        tipWindow.setOutsideTouchable(true);
        tipWindow.setTouchable(true);
        tipWindow.setBackgroundDrawable(null);
        tipWindow.setContentView(contentView);

        int screen_pos[] = new int[2];
        // Get location of anchor view on screen
        anchor.getLocationOnScreen(screen_pos);

        // Get rect for anchor view
        Rect anchor_rect = new Rect(screen_pos[0], screen_pos[1], screen_pos[0]
                + anchor.getWidth(), screen_pos[1] + anchor.getHeight());

        // Call view measure to calculate how big your view should be.
        contentView.measure(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        int contentViewHeight = contentView.getMeasuredHeight();
        int contentViewWidth = contentView.getMeasuredWidth();
        // In this case , i dont need much calculation for x and y position of
        // tooltip
        // For cases if anchor is near screen border, you need to take care of
        // direction as well
        // to show left, right, above or below of anchor view
        int position_x = anchor_rect.centerX() - (contentViewWidth / 2);
        int position_y = anchor_rect.top - contentViewHeight - 40;

        tipWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, position_x, position_y);

        // send message to handler to dismiss tipWindow after X milliseconds
        disposable = Completable.timer(100, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(this::hide);

    }

    boolean isTooltipShown() {
        if (tipWindow != null && tipWindow.isShowing())
            return true;
        return false;
    }

    void dismissTooltip() {
        if (tipWindow != null && tipWindow.isShowing()) {
            tipWindow.dismiss();
        }
    }

    private void hide() {
        if (tipWindow != null && tipWindow.isShowing()) {
            tipWindow.dismiss();
        }
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
