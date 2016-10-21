package com.yangproject.embeddedproject.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yangproject.embeddedproject.Others.ConfigureSetting;
import com.yangproject.embeddedproject.R;
import com.yangproject.embeddedproject.Utilities.HttpTask;

import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
    private ConfigureSetting configureSetting = ConfigureSetting.getInstance();
    private String id;
    private String pw;
    private EditText idEditText;
    private EditText pwEditText;
    private HttpTask httpTask = HttpTask.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        idEditText = (EditText) findViewById(R.id.idEditText);
        pwEditText = (EditText) findViewById(R.id.pwEditText);
        SharedPreferences pref = getSharedPreferences("login_info", MODE_PRIVATE);
        id = pref.getString("id", null);
        pw = pref.getString("pw", null);
        if(id != null && pw != null) {
            idEditText.setText(id);
            pwEditText.setText(pw);
            new Login().execute("http://218.150.182.12/login.php", "id=" + id + "&pw=" + pw);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onLoginBtnClicked(View v) {
        id = idEditText.getText().toString();
        pw = pwEditText.getText().toString();
        new Login().execute("http://218.150.182.12/login.php", "id=" + id + "&pw=" + pw);
    }

    protected class Login extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return httpTask.getHttpPOSTResult(params[0], params[1]);
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jo = new JSONObject(result);
                String res = jo.getString("status");
                if(res.equals("OK")) {
                    SharedPreferences pref = getSharedPreferences("login_info", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("id", id);
                    editor.putString("pw", pw);
                    editor.commit();
                    configureSetting.setConfigure("id", id);

                    Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                else if(res.equals("NO")){
                    Toast.makeText(getApplicationContext(), "Check your account.", Toast.LENGTH_SHORT).show();
                }
                else if(res.equals("FAILURE")) {
                    Toast.makeText(getApplicationContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception exc) {
                Toast.makeText(getApplicationContext(), "Unknown Exception", Toast.LENGTH_SHORT).show();
            }
        }
    }
}