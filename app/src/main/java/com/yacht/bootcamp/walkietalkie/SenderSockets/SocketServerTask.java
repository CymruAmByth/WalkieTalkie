package com.yacht.bootcamp.walkietalkie.SenderSockets;

import android.os.AsyncTask;
import android.util.Log;

import com.yacht.bootcamp.walkietalkie.HelperClasses.TextMessage;
import com.yacht.bootcamp.walkietalkie.MainActivity;
import com.yacht.bootcamp.walkietalkie.ReceivingSockets.SocketReceivingTask;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Created by gebruiker on 27/05/15.
 */
public class SocketServerTask extends AsyncTask<Void, Integer, Void>{

    private boolean running;
    private List<TextMessage> msgs;

    public SocketServerTask(List<TextMessage> msgs) {
        this.msgs = msgs;
        this.running = true;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            while (running) {
                Socket client = serverSocket.accept();
                SenderSocketTask srTask = new SenderSocketTask(client);
                srTask.doInBackground(msgs);
            }
        } catch (IOException exc){
            Log.d("CymWT", exc.getMessage());
        }
        return null;
    }

    public void stopRunning(){
        this.running = false;
    }

}
