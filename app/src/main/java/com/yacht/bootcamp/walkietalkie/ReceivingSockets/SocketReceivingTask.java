package com.yacht.bootcamp.walkietalkie.ReceivingSockets;

import android.os.AsyncTask;
import android.util.Log;

import com.yacht.bootcamp.walkietalkie.HelperClasses.TextMessage;
import com.yacht.bootcamp.walkietalkie.MainActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by gebruiker on 27/05/15.
 */
public class SocketReceivingTask extends AsyncTask<InetSocketAddress, Void, Void> {

    private MainActivity mActivity;

    public SocketReceivingTask(MainActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    protected Void doInBackground(InetSocketAddress... params) {
        try{
            Socket socket = new Socket();
            socket.bind(null);
            socket.connect(params[0], 1000);
            ObjectInputStream inStream;
            ObjectOutputStream outStream;
            inStream = new ObjectInputStream(socket.getInputStream());
            outStream = new ObjectOutputStream(socket.getOutputStream());
            outStream.writeLong(mActivity.getLastTimestamp());
            while(inStream.readBoolean()){
                TextMessage msg = (TextMessage) inStream.readObject();
                mActivity.addMessage(msg, true);
                Log.d("CymWT", msg.toString());
            }
            inStream.close();
            outStream.close();
            socket.close();
        } catch (IOException e){
            Log.d("CymWT", e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.d("CymWT", e.getMessage());
        }
        return null;
    }

}
