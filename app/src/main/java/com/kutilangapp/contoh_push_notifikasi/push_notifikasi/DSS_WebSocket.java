package com.kutilangapp.contoh_push_notifikasi.push_notifikasi;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class DSS_WebSocket {
    DSS_NotificationListener dss_notificationListener;

    WebSocketListener wsListener;
    String ClientID = "";
    String TAG = "CoinWeb";
    WebSocket WS;
    JSONObject _REQUEST;

    public DSS_WebSocket(DSS_NotificationListener listener, String wsURL) {
        this.dss_notificationListener = listener;
        OkHttpClient clientCoinPrice = new OkHttpClient();
        Request requestCoinPrice = new Request.Builder().url(wsURL).build();
        wsListener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                /*webSocket.send("{\n" +
                        "    \"type\": \"subscribe\",\n" +
                        "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"" + product + "\"] }]\n" +
                        "}");*/
                webSocket.send("{\n" +
                        "    \"req\": \"initID\"\n" +
                        "}");
                /*    webSocket.send("{\n" +
                        "    \"req\": \"subcribe2\",\n" +
                        "    \"channel\": \"notification\"\n" +
                        "}");*/
                Log.e(TAG, "onOpen");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.e(TAG, "MESSAGE: " + text);
                try {
                    JSONObject obj = new JSONObject(text);
                    if (obj.getString("req").equals("initID")) {
                        ClientID = obj.getString("value");
                        /*webSocket.send("{\n" +
                                "    \"req\": \"subcribe\",\n" +
                                "    \"channel\": \"notification\",\n" +
                                "    \"id\": \"" + ClientID + "\"\n" +
                                "}");*/
                    } else if (obj.getString("req").equals("subcribe")) {
                        //CB_Subcribe(text);
                        dss_notificationListener.pushNotification(text);
                    } else if (obj.getString("req").equals("sendToChannel")) {
                        //CB_sendToChannel(text);
                        dss_notificationListener.pushNotification(text);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                Log.e(TAG, "MESSAGE: " + bytes.hex());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                webSocket.cancel();
                Log.e(TAG, "CLOSE: " + code + " " + reason);

            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                //TODO: stuff
                Log.e(TAG, "CLOSED: ");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                //TODO: stuff
                Log.e(TAG, "FAIL: " + response + " " + t.getMessage());

            }


        };
        WS = clientCoinPrice.newWebSocket(requestCoinPrice, wsListener);
        clientCoinPrice.dispatcher().executorService().shutdown();
        _REQUEST = new JSONObject();
    }

    void sendMSG(String msg) {
        WS.send(msg);
    }

    public DSS_WebSocket req(String req) {
        try {
            this._REQUEST.put("req", req);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public DSS_WebSocket channel(String channel) {
        try {
            this._REQUEST.put("channel", channel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public DSS_WebSocket email(String email) {
        try {
            this._REQUEST.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public DSS_WebSocket msg(String msg) {
        try {
            this._REQUEST.put("msg", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void commit() {
        WS.send(this._REQUEST.toString());
        this._REQUEST = new JSONObject();
    }

    public void close(int code, @Nullable String reason) {
        WS.close(code, reason);
    }
}
