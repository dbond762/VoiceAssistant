package com.example.voiceassistant;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.util.Consumer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    protected RecyclerView chatMessageList;
    protected EditText questionText;
    protected Button sendButton;

    protected TextToSpeech tts;

    protected MessageListAddapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatMessageList = findViewById(R.id.chatMessageList);
        questionText = findViewById(R.id.questionField);
        sendButton = findViewById(R.id.sendButton);

        adapter = new MessageListAddapter();
        chatMessageList.setLayoutManager(new LinearLayoutManager(this));
        chatMessageList.setAdapter(adapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                onClickSendButton();
            }
        });

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(new Locale("ru"));
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onClickSendButton() {
        final String text = questionText.getText().toString();

        adapter.messageList.add(new Message(text, true));
        adapter.notifyDataSetChanged();
        int lastMessageIndex = adapter.messageList.size() - 1;
        chatMessageList.scrollToPosition(lastMessageIndex);

        AI.getAnswer(text, new Consumer<String>() {
            @Override
            public void accept(String answer) {
                adapter.messageList.add(new Message(answer, false));
                adapter.notifyDataSetChanged();
                int lastMessageIndex = adapter.messageList.size() - 1;
                chatMessageList.scrollToPosition(lastMessageIndex);
                // tts.speak(answer, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        questionText.setText("");
    }
}
