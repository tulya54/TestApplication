package com.example.testapplication;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private Button mBtnOpen = null;
    private Button mBtnSend = null;
    private Button mBtnClose = null;
    private EditText mEdit = null;
    private Connection mConnect = null;
    private String HOST = "145.249.246.125";
    private int PORT = 5959;
    private String LOG_TAG = "SOCKET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnOpen = (Button) findViewById(R.id.btn_open);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnClose = (Button) findViewById(R.id.btn_close);
        mEdit = (EditText) findViewById(R.id.edText);

        mBtnSend.setEnabled(false);
        mBtnClose.setEnabled(false);

        mBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenClick();
            }
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendClick();
            }
        });

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCloseClick();
            }
        });
    }


    private void onOpenClick() {
        // Создание подключения
        mConnect = new Connection(HOST, PORT);
        // Открытие сокета в отдельном потоке
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mConnect.openConnection();
                    // Разблокирование кнопок в UI потоке
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBtnSend.setEnabled(true);
                            mBtnClose.setEnabled(true);
                        }
                    });
                    Log.d(LOG_TAG, "Соединение установлено");
                    Log.d(LOG_TAG, "(mConnect != null) = "
                            + (mConnect != null));
                } catch (Exception e) {
                    Log.e(LOG_TAG, e.getMessage());
                    mConnect = null;
                }
            }
        }).start();
    }
    private void onSendClick() {
        if (mConnect == null) {
            Log.d(LOG_TAG, "Соединение не установлено");
        }  else {
            Log.d(LOG_TAG, "Отправка сообщения");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String text;
                        text = mEdit.getText().toString();
                        if (text.trim().length() == 0)
                            text = "Test message";
                        // отправляем сообщение
                        mConnect.sendData(text.getBytes());
                    } catch (Exception e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }
            }).start();
        }
    }
    private void onCloseClick() {
        // Закрытие соединения
        mConnect.closeConnection();
        // Блокирование кнопок
        mBtnSend .setEnabled(false);
        mBtnClose.setEnabled(false);
        Log.d(LOG_TAG, "Соединение закрыто");
    }
}