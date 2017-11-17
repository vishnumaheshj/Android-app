package com.example.dotslash.dotslashhome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class DisplayDash extends AppCompatActivity {
    private String user, pass;
    private OkHttpClient client;
    private LinearLayout linearLayout;

    private final class ServerListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        private String sockId = "";

        @Override
        public void onOpen(WebSocket webSocket, Response response) {

        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            try {
                JSONObject cMsg = new JSONObject();
                JsonParser parser = new JsonParser();
                JsonObject sMsg = parser.parse(text).getAsJsonObject();

                String type = sMsg.get("type").getAsString();
                switch (type) {
                    case "init":
                        sockId = sMsg.get("appSocketID").getAsString();
                        cMsg.put("appSocketID", sockId);
                        cMsg.put("type", "auth");
                        cMsg.put("user", user);
                        cMsg.put("passcode", pass);
                        webSocket.send(cMsg.toString());
                        break;

                    case "error":
                        String reason = sMsg.get("reason").getAsString();
                        if (reason.equals("auth_fail"))
                            createTextView("Log In failed");
                        else
                            createTextView(reason);
                        break;

                    case "stateChange":
                        long totalNodes = sMsg.get("totalNodes").getAsLong();
                        long hubAddr = sMsg.get("hubAddr").getAsLong();
                        createTextView("Hub address : " + hubAddr + "\n\n");

                        for (long nodeNum = 1; nodeNum <= totalNodes; nodeNum++) {
                            JsonObject board = sMsg.getAsJsonObject("board" + nodeNum);
                            createTextView("Board " + nodeNum);
                            switch (board.get("type").getAsInt()) {
                                case 2:
                                    for (long switchNum = 1; switchNum <= 4; switchNum++) {
                                        long value = board.get("switch" + switchNum).getAsLong();
                                        if (value == 1)
                                            createSwitch(switchNum, 1);
                                        else
                                            createSwitch(switchNum, 0);
                                    }
                                    break;
                                default:
                                    createTextView("Switchboard type of switchboard " + nodeNum + " is unknown");
                            }
                        }
                        break;

                    default:
                        createTextView("Default case Receiving: " + text);
                }
            } catch (Exception e) {
                createTextView("Server message is invalid");
                createTextView(text);
                e.printStackTrace();
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            createTextView("Error: " + t.getMessage());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_dash);
        linearLayout = (LinearLayout) findViewById(R.id.container);

        client = new OkHttpClient();
        Intent intent = getIntent();
        user = intent.getStringExtra("username");
        pass = intent.getStringExtra("password");

        createTextView(user.toUpperCase() + "'s Dashboard\n");

        Request request = new Request.Builder().url("http://192.168.50.110:8888/app/websocket").build();
        ServerListener serverListener = new ServerListener();
        WebSocket webSocket = client.newWebSocket(request, serverListener);
        client.dispatcher().executorService().shutdown();

    }

    private void createSwitch(final long num, final long state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Switch aSwitch = new Switch(DisplayDash.this);
                aSwitch.setText("Switch " + num);
                aSwitch.setGravity(Gravity.LEFT);
                aSwitch.setId(View.generateViewId());
                if (state == 1)
                    aSwitch.setChecked(true);
                else
                    aSwitch.setChecked(false);
                linearLayout.addView(aSwitch);
            }
        });
    }

    private void createTextView(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = new TextView(DisplayDash.this);
                textView.setText(txt);
                textView.setId(View.generateViewId());
                linearLayout.addView(textView);
            }
        });
    }
}
/*
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("Receiving bytes : " + bytes.hex());
        }
*/