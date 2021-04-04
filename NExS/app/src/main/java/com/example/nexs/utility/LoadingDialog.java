package com.example.nexs.utility;

import android.app.AlertDialog;
import android.content.Context;

import com.example.nexs.R;

public class LoadingDialog {

    Context context;
    AlertDialog dialog;

    public LoadingDialog(Context context) {
        this.context = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setView(R.layout.loading_dialog_view);
        dialog = builder.create();
    }

    public void showDialog() {
        dialog.show();
    }

    public void stopDialog() {
        dialog.dismiss();
    }
}
