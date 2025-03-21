package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.ui.tv.QRCodeGen;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class QRCodeDialog extends BaseDialog {

    private final ImageView ivQRCode;
    private final TextView tvAddress;


    public QRCodeDialog(@NonNull Context context) {
        super(context, R.style.CustomDialogStyleDim);
        setContentView(R.layout.dialog_qrcode);

        ivQRCode = findViewById(R.id.ivQRCode);
        tvAddress = findViewById(R.id.tvAddress);
        refreshQRCode();
    }

    private void refreshQRCode() {
        String address = ControlManager.get().getAddress(false);
        tvAddress.setText(String.format("请扫描二维码或输入ip地址进行连接\n%s", address));
        ivQRCode.setImageBitmap(QRCodeGen.generateBitmap(address, AutoSizeUtils.mm2px(getContext(), 300), AutoSizeUtils.mm2px(getContext(), 300), 4));
    }
}
