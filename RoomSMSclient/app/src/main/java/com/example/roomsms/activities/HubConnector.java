package com.example.roomsms.activities;

import android.util.Log;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

public class HubConnector {
    private HubConnection hubConnection;
    public HubConnector(String add) {
        hubConnection = HubConnectionBuilder.create("http://10.0.2.2:5190" + add).build();
    }

    public boolean start() {
        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            try {
                hubConnection.start().doOnError(throwable -> {
                            Log.e("Connection:Start", "doInBackground > doOnError: ", throwable);
                        })
                        .doOnComplete(() -> {
                            Log.i("Connection:Start", "doInBackground > doOnComplete.");
                        })
                        .blockingAwait();
            } catch (Exception e) {
                Log.e("Connection:Start", "Connection Timeout");
                return false;
            }
            return true;
        }
        return true;
    }

    public boolean isConnected() {
        return hubConnection.getConnectionState() == HubConnectionState.CONNECTED;
    }

    public boolean isDisconnected() {
        return hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED;
    }

    public void stop() {
        hubConnection.stop();
    }

    public HubConnection getHubConnection() {
        return hubConnection;
    }
}
