package org.cn.explorer.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import org.cn.explorer.listener.AppDialogListener;

/**
 * Created by chenning on 2015/10/21.
 */
public class DialogUtil {

    public static void alertDialog(Activity ctx, String title, String message, String negative, String positive) {
        alertDialog(ctx, title, message, null, negative, positive, null, null);
    }

    public static void alertDialog(Activity ctx, String title, String message, String neutral, String negative, String positive, View view) {
        alertDialog(ctx, title, message, neutral, negative, positive, view, null);
    }

    public static void alertDialog(Activity ctx, String title, String message, String negative, String positive, View view, final AppDialogListener listener) {
        alertDialog(ctx, title, message, null, negative, positive, view, listener);
    }

    public static void alertDialog(Activity ctx, String title, String message, String neutral, String negative, String positive, View view, final AppDialogListener listener) {
        if (TextUtils.isEmpty(message)) {
            message = "";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        if (view != null) {
            builder.setView(view);
        }
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (dialog != null) {
                    dialog.cancel();
                }
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        if (!TextUtils.isEmpty(neutral)) {
            // neutral
            builder.setNeutralButton(neutral, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onNeutralButtonClickListener();
                    }
                }
            });
        }
        if (!TextUtils.isEmpty(negative)) {
            // cancel
            builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onNegativeButtonClickListener();
                    }
                }
            });
        }
        if (!TextUtils.isEmpty(positive)) {
            // confirm
            builder.setPositiveButton(positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (listener != null) {
                        listener.onPositiveButtonClickListener();
                    }
                }
            });
        }
        builder.create().show();
    }

}
