package com.yangproject.embeddedproject.Others;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.yangproject.embeddedproject.R;

/**
 * Created by 우민 on 2016-05-26.
 */
public class SettingDialog extends Dialog implements View.OnClickListener {
    Context context;
    private RadioGroup rotate_num;
    private RadioGroup brightness;
    private RadioGroup speed;
    private Switch inversion;
    private Button okBtn;
    private ConfigureSetting configureSetting = ConfigureSetting.getInstance();

    public SettingDialog(Context context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_for_setting);

        rotate_num = (RadioGroup)findViewById(R.id.rotateRadioGroup);
        int tmp = configureSetting.getConfigureInt("rotate_num");
        switch(tmp) {
            case 3 :
                rotate_num.check(R.id.r1);
                break;
            case 5 :
                rotate_num.check(R.id.r2);
                break;
            case 7 :
                rotate_num.check(R.id.r3);
                break;
            case 9 :
                rotate_num.check(R.id.r4);
                break;
        }

        brightness = (RadioGroup)findViewById(R.id.brightnessRadioGroup);
        tmp = configureSetting.getConfigureInt("brightness");
        switch(tmp) {
            case 3 :
                brightness.check(R.id.b1);
                break;
            case 7 :
                brightness.check(R.id.b2);
                break;
            case 11 :
                brightness.check(R.id.b3);
                break;
            case 15 :
                brightness.check(R.id.b4);
                break;
        }

        speed = (RadioGroup)findViewById(R.id.speedRadioGroup);
        tmp = configureSetting.getConfigureInt("speed");
        switch(tmp) {
            case 1 :
                speed.check(R.id.s1);
                break;
            case 2 :
                speed.check(R.id.s2);
                break;
            case 4:
                speed.check(R.id.s3);
                break;
            default :
                speed.check(R.id.s2);
                break;
        }

        inversion = (Switch)findViewById(R.id.inversionSwitch);
        boolean flag = configureSetting.getConfigureBoolean("inversion");
        inversion.setChecked(flag);

        okBtn = (Button)findViewById(R.id.okBtn);
        okBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.okBtn) {
            int tmp = 10;
            switch(rotate_num.getCheckedRadioButtonId()) {
                case R.id.r1:
                    tmp = 3;
                    break;
                case R.id.r2:
                    tmp = 5;
                    break;
                case R.id.r3:
                    tmp = 7;
                    break;
                case R.id.r4:
                    tmp = 9;
                    break;
            }
            configureSetting.setConfigure("rotate_num", tmp);

            switch(brightness.getCheckedRadioButtonId()) {
                case R.id.b1:
                    tmp = 3;
                    break;
                case R.id.b2:
                    tmp = 7;
                    break;
                case R.id.b3:
                    tmp = 11;
                    break;
                case R.id.b4:
                    tmp = 15;
                    break;
            }
            configureSetting.setConfigure("brightness", tmp);
            switch(speed.getCheckedRadioButtonId()) {
                case R.id.s1:
                    tmp = 1;
                    break;
                case R.id.s2:
                    tmp = 2;
                    break;
                case R.id.s3:
                    tmp = 4;
                    break;
            }
            configureSetting.setConfigure("speed", tmp);
            configureSetting.setConfigure("inversion", inversion.isChecked());
            Toast.makeText(context, "Applied configuration", Toast.LENGTH_SHORT).show();
            this.dismiss();
        }
    }
}
