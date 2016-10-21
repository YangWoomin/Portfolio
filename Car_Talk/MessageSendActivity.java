package com.yangproject.embeddedproject.Activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yangproject.embeddedproject.Others.BluetoothService;
import com.yangproject.embeddedproject.Others.ConfigureSetting;
import com.yangproject.embeddedproject.Others.Constants;
import com.yangproject.embeddedproject.Others.MessageItemAdapter;
import com.yangproject.embeddedproject.Others.SettingDialog;
import com.yangproject.embeddedproject.R;
import com.yangproject.embeddedproject.Utilities.HttpTask;

import net.daum.mf.speech.api.SpeechRecognizeListener;
import net.daum.mf.speech.api.SpeechRecognizerClient;
import net.daum.mf.speech.api.SpeechRecognizerManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 우민 on 2016-03-14.
 */
public class MessageSendActivity extends Activity implements SpeechRecognizeListener, MessageItemAdapter.BtnClickListener {
    private HttpTask httpTask = HttpTask.getInstance();
    private ConfigureSetting configureSetting = ConfigureSetting.getInstance();

    // for Newtone API
    public static final String APIKEY = "24e01c17de4f6b5069f06999e8aed643";
    private SpeechRecognizerClient client;

    // for bluetooth
    private BluetoothService mBluetoothService;

    private String msg;
    private int tempPosition;
    private TextView messageEditText;
    private ListView messageListView;
    private MessageItemAdapter messageItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_message_send);

        // for components
        messageEditText = (EditText)findViewById(R.id.messageEditText);
        messageListView = (ListView)findViewById(R.id.messageListView);
        messageItemAdapter = new MessageItemAdapter(this);
        messageListView.setAdapter(messageItemAdapter);
        // messageListView.setOnItemClickListener(onMessageItemClickListener);

        mBluetoothService = BluetoothService.getInstance(mHandler);

        // for Newtone
        SpeechRecognizerManager.getInstance().initializeLibrary(this);

        IntentFilter filter = new IntentFilter("speech_failed");
        registerReceiver(mReceiver, filter);

        // for messages
        new GetMessage().execute("http://218.150.182.12/get_msg.php", "id=" + configureSetting.getConfigureString("id"));
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("speech_failed")) {
                Toast.makeText(getApplicationContext(), "Speech failed.. Check your LTE/WiFi", Toast.LENGTH_SHORT).show();
                setButtons(true);
            }
        }
    };

    @Override
    public void onBtnClickedListener(boolean flag, int position) {
        if(flag) {
            messageEditText.append(messageItemAdapter.getItem(position));
        }
        else {
            tempPosition = position;
            new PutOrDelMessage().execute("http://218.150.182.12/del_msg.php", "id=" + configureSetting.getConfigureString("id") + "&msg=" + messageItemAdapter.getItem(position));
        }
    }

    public void setButtons(boolean flag) {
        try {
            findViewById(R.id.messageEditText).setEnabled(flag);
            findViewById(R.id.clearBtn).setEnabled(flag);
            findViewById(R.id.addMessageBtn).setEnabled(flag);
            findViewById(R.id.startBtn).setEnabled(flag);
            findViewById(R.id.transmitBtn).setEnabled(flag);
            findViewById(R.id.settingBtn).setEnabled(flag);
        }
        catch (Exception exc) {
            Toast.makeText(MessageSendActivity.this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onBtnClicked(View v) {
        switch(v.getId()) {
            case R.id.clearBtn :
                messageEditText.setText("");
                break;
            case R.id.addMessageBtn :
                if(messageEditText.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Input your message", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(messageEditText.getText().length() > 150) {
                    Toast.makeText(getApplicationContext(), "Reduce the message length(<150)", Toast.LENGTH_SHORT).show();
                    return;
                }
                new PutOrDelMessage().execute("http://218.150.182.12/put_msg.php", "id=" + configureSetting.getConfigureString("id") + "&msg=" + messageEditText.getText().toString());
                break;
            case R.id.settingBtn :
                try {
                    SettingDialog settingDialog = new SettingDialog(this);
                    settingDialog.show();
                }
                catch (Exception exc) {
                    Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.startBtn :
                try {
                    SpeechRecognizerClient.Builder builder = new SpeechRecognizerClient.Builder().setApiKey(APIKEY).setServiceType(null);
                    client = builder.build();
                    client.setSpeechRecognizeListener(this);
                    client.startRecording(true);
                    setButtons(false);
                    Toast.makeText(MessageSendActivity.this, "Recording start", Toast.LENGTH_SHORT).show();
                }
                catch (Exception exc) {
                    Toast.makeText(MessageSendActivity.this, exc.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.transmitBtn :
                try {
                    msg = ((TextView)findViewById(R.id.messageEditText)).getText().toString();
                    if(msg != null && msg != "" && msg.length() != 0) {
                        if(msg.length() > 1000) {
                            Toast.makeText(getApplicationContext(), "String limitation length : 1000 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        configureSetting.setConfigure("message", msg);
                        mBluetoothService.write(configureSetting.getConfigObject().toString().getBytes(), this);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Input your message", Toast.LENGTH_SHORT).show();
                    }

                }
                catch (Exception exc) { }
                break;
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(getApplication(), msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onResults(Bundle results) {
        try {
            ArrayList<String> texts = results.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);
            msg = texts.get(0);
            messageEditText.append(msg);
            client = null;
            setButtons(true);
            Toast.makeText(MessageSendActivity.this, "Finished recording", Toast.LENGTH_SHORT).show();
        }
        catch (Exception exc) {
            Toast.makeText(MessageSendActivity.this, "Failed to speech", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        Intent intent = new Intent("speech_failed");
        sendBroadcast(intent);
    }

    private class GetMessage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return httpTask.getHttpPOSTResult(params[0], params[1]);
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray ja = new JSONArray(result);
                messageItemAdapter.clearMessages();
                for(int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    String message = jo.getString("msg");
                    messageItemAdapter.addMessage(message);
                }
                messageItemAdapter.notifyDataSetChanged();
            }
            catch (JSONException jexc) {
                Toast.makeText(getApplicationContext(), jexc.getMessage(), Toast.LENGTH_SHORT).show();
            }
            catch (Exception exc) {
                Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
            InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(messageEditText.getWindowToken(), 0);
        }
    }

    private class PutOrDelMessage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return httpTask.getHttpPOSTResult(params[0], params[1]);
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jo = new JSONObject(result);
                result = jo.getString("status");
                if(result.equals("INSERT_OK")) {
                    Toast.makeText(getApplicationContext(), "Succeeded in inserting the message", Toast.LENGTH_SHORT).show();
                    messageItemAdapter.addMessage(messageEditText.getText().toString());
                    messageItemAdapter.notifyDataSetChanged();
                    messageEditText.setText("");
                }
                else if(result.equals("DELETE_OK")) {
                    Toast.makeText(getApplicationContext(), "Succeeded in deleting the message", Toast.LENGTH_SHORT).show();
                    messageItemAdapter.delMessage(tempPosition);
                    messageItemAdapter.notifyDataSetChanged();
                }
                else if(result.equals("INSERT_NO")) {
                    Toast.makeText(getApplicationContext(), "Failed to inserting the message", Toast.LENGTH_SHORT).show();
                }
                else if(result.equals("DELETE_NO")) {
                    Toast.makeText(getApplicationContext(), "Failed to deleting the message", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception exc) {
                Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onStart() {
        super.onStart();
        mBluetoothService = BluetoothService.getInstance(mHandler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SpeechRecognizerManager.getInstance().finalizeLibrary();
        mBluetoothService.stop("Try to connect a device");
    }

    @Override
    public void onEndOfSpeech() { }
    @Override
    public void onPartialResult(String text) { }
    @Override
    public void onReady() { }
    @Override
    public void onBeginningOfSpeech() { }
    @Override
    public void onFinished() { }
    @Override
    public void onAudioLevel(float v) { }
}
