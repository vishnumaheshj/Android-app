package com.example.dotslash.dotslashhome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class DisplayDash extends AppCompatActivity {
    private String user, pass;
    private OkHttpClient client;
    private TextView msgT;

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
                updateViewText(type);
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
                        if(reason.equals("auth_fail")){
                            updateViewText("Log In failed");
                        }
                        else
                            updateViewText(reason);
                        break;
                    default:
                        updateViewText("Default case Receiving: " + text);
                }
            } catch (Exception e) {
                updateViewText("Server message is invalid");
                updateViewText(text);
                e.printStackTrace();
            }
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            updateViewText("Error: " + t.getMessage());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_dash);

        TextView userT = findViewById(R.id.textView2);
        TextView passT = findViewById(R.id.textView3);
        msgT = findViewById(R.id.textView);

        client = new OkHttpClient();
        Intent intent = getIntent();
        user = intent.getStringExtra("username");
        pass = intent.getStringExtra("password");

        userT.setText(user);
        passT.setText(pass);

        Request request = new Request.Builder().url("http://192.168.50.110:8888/app/websocket").build();
        ServerListener serverListener = new ServerListener();
        WebSocket webSocket = client.newWebSocket(request, serverListener);
        client.dispatcher().executorService().shutdown();

    }

    private void updateViewText(final String txt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgT.setText(msgT.getText().toString() + "\n\n" + txt);
            }
        });
    }
}
//    compile 'com.google.code.gson:gson:2.8.2'

//private TextView output;
//private TextView msgT  = findViewById(R.id.textView);


    /*

    */

//output(user + pass);


        /*
        Request request = new Request.Builder().url("http://192.168.50.110:8888/app/websocket").build();
        ServerListener serverListener = new ServerListener();
        WebSocket webSocket = client.newWebSocket(request, serverListener);
        client.dispatcher().executorService().shutdown();
        */


/*
    private void output(final String txt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText(output.getText().toString() + "\n\n" + txt);
            }
        });
    }*/
/*
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("Receiving bytes : " + bytes.hex());
        }
*/