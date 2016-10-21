package com.yangproject.embeddedproject.Others;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.yangproject.embeddedproject.R;

/**
 * Created by 우민 on 2016-05-26.
 */
public class Test extends Dialog {
    public Test(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.test);
    }
}
