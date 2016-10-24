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
public abstract class NewLensDialog extends Dialog implements View.OnClickListener {
    private int index;
    private Button okBtn;
    private EditText lensTitle;
    private EditText expirationDate;

    public NewLensDialog(Context context, int index) {
        super(context);
        this.index = index;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_lens_dialog_layout);
        okBtn = (Button)findViewById(R.id.okBtn);
        okBtn.setOnClickListener(this);
        lensTitle = (EditText)findViewById(R.id.lensTitleEditText);
        expirationDate = (EditText)findViewById(R.id.expirationDateEditText);
    }

    public String[] getResult() { return new String[] {lensTitle.getText().toString(), expirationDate.getText().toString() }; }
    public int getIndex() { return index; }
}
