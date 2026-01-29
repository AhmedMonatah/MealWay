package com.example.mealway.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mealway.R;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.button.MaterialButton;

public class AlertUtils {

    public interface AlertCallback {
        void onConfirmed();
    }

    public static void showConfirmation(Context context, String title, String message, String positiveAction, AlertCallback callback) {
        showCustomAlert(context, title, message, android.R.drawable.ic_dialog_alert, "#FF9800", positiveAction, true, callback);
    }

    public static void showError(Context context, String message) {
        showCustomAlert(context, context.getString(R.string.error_title), message, android.R.drawable.stat_notify_error, "#F44336", context.getString(R.string.try_again), false, null);
    }
    
    public static void showSuccess(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private static void showCustomAlert(Context context, String title, String message, int iconRes, String colorHex, String positiveBtnText, boolean showNegative, AlertCallback callback) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_custom_alert, null);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        FrameLayout header = view.findViewById(R.id.alert_header);
        ImageView ivIcon = view.findViewById(R.id.iv_alert_icon);
        TextView tvTitle = view.findViewById(R.id.tv_alert_title);
        TextView tvMessage = view.findViewById(R.id.tv_alert_message);
        MaterialButton btnPositive = view.findViewById(R.id.btn_alert_positive);
        MaterialButton btnNegative = view.findViewById(R.id.btn_alert_negative);

        header.setBackgroundColor(Color.parseColor(colorHex));
        ivIcon.setImageResource(iconRes);
        ivIcon.setColorFilter(Color.WHITE);
        tvTitle.setText(title);
        tvMessage.setText(message);
        btnPositive.setText(positiveBtnText);
        btnPositive.setBackgroundColor(Color.parseColor(colorHex));

        btnPositive.setOnClickListener(v -> {
            dialog.dismiss();
            if (callback != null) callback.onConfirmed();
        });

        if (showNegative) {
            btnNegative.setVisibility(View.VISIBLE);
            btnNegative.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }
}
