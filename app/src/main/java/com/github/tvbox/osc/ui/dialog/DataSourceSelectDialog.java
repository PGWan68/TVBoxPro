package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.R;

public class DataSourceSelectDialog extends BaseDialog {
    private OnClickBtnListener listener;

    public DataSourceSelectDialog(@NonNull Context context, String title) {
        super(context, R.style.CustomDialogStyleDim);

        setContentView(R.layout.dialog_data_source_select);
        ((TextView) findViewById(R.id.tvTitle)).setText(title);

        findViewById(R.id.btnSetCurrent).setOnClickListener(v -> {
            if (listener != null) {
                dismiss();
                listener.onClickBtnSetCurrent();
            }
        });

        findViewById(R.id.btnSetDelete).setOnClickListener(v -> {
            if (listener != null) {
                dismiss();
                listener.onClickBtnSetDelete();
            }
        });
    }

    public void setOnClickBtnListener(OnClickBtnListener listener) {
        this.listener = listener;
    }

    public interface OnClickBtnListener {
        void onClickBtnSetCurrent();

        void onClickBtnSetDelete();
    }


}
