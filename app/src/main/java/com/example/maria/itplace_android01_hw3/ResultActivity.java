package com.example.maria.itplace_android01_hw3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by maria on 17.08.2017.
 */

public class ResultActivity extends AppCompatActivity {

    private static final String KEY_STRING = "KEY_STRING";

    public static final Intent start(Context context) {
        Intent starter = new Intent(context, ResultActivity.class);
        return starter;
    }

    public static String getExtraString(Intent data) {
        return "#" + data.getStringExtra(KEY_STRING);
    }

    Button buttonView;
    EditText editTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        initViews();
    }

    private void initViews() {
        buttonView = (Button) findViewById(R.id.button_ok);
        editTextView = (EditText) findViewById(R.id.hex_text);

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int len = editTextView.getText().toString().length();
                if ((len == 6) || (len == 8)) {
                    Intent result = new Intent();
                    result.putExtra(KEY_STRING, editTextView.getText().toString());
                    setResult(RESULT_OK, result);
                    finish();
                } else {
                    Toast toast = Toast.makeText(ResultActivity.this,"Неверный HEX-код!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}
