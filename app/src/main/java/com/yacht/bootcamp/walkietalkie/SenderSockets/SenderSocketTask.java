package com.yacht.bootcamp.walkietalkie.SenderSockets;

import android.os.AsyncTask;
import android.util.Log;

import com.yacht.bootcamp.walkietalkie.HelperClasses.TextMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by gebruiker on 28/05/15.
 */
public class SenderSocketTask extends AsyncTask <List<TextMessage>, Integer, Void> {

    Socket socket;

    public SenderSocketTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    protected Void doInBackground(List<TextMessage>... params) {
        ObjectInputStream inStream;
        ObjectOutputStream outStream;
        try {
            outStream = new ObjectOutputStream(socket.getOutputStream());
            inStream = new ObjectInputStream(socket.getInputStream());
            long lastTimeStamp = inStream.readLong();
            for (TextMessage msg : params[0]) {
                if (msg.messageAlreadySent(lastTimeStamp)) {
                    outStream.writeBoolean(true);
                    outStream.writeObject(msg);
                }
            }
            outStream.writeBoolean(false);
            inStream.close();
            outStream.close();
            socket.close();
        } catch(IOException exc){
            Log.d("CymWT", exc.getMessage());
        }
        return null;
    }
}
