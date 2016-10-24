package com.yangproject.iot.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.yangproject.iot.R;

/**
 * Created by 우민 on 2016-06-03.
 */
public abstract class ModifySleepTimeDialog extends Dialog implements View.OnClickListener {
    private Button okBtn;
    private EditText sleepTimeEditText;

    public ModifySleepTimeDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.modify_sleep_time_dialog_layout);
        okBtn = (Button)findViewById(R.id.okBtn);
        okBtn.setOnClickListener(this);
        sleepTimeEditText = (EditText)findViewById(R.id.sleepTimeEditText);
    }

    public String getResult() { return sleepTimeEditText.getText().toString(); }
}
