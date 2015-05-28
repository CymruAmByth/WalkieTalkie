package com.yacht.bootcamp.walkietalkie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.yacht.bootcamp.walkietalkie.ReceivingSockets.SocketReceivingTask;
import com.yacht.bootcamp.walkietalkie.SenderSockets.SocketServerTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Created by gebruiker on 18/05/15.
 */
public class WifiDirectBroadCastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;

    public WifiDirectBroadCastReceiver(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, MainActivity mActivity) {
        super();
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.mActivity = mActivity;
        this.discoverPeers();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if(state != WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Log.d("CymWT", "wifi p2p disabled");
                Toast.makeText(mActivity, "Wifi p2p is disabled", Toast.LENGTH_LONG);
                //actions to enable p2p
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if(mManager != null){
                mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        mActivity.updatePeers(peers);
                    }

                });
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            Log.d("CymWT", "My connection changed");
            if(mManager == null)
                return;
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo.isConnected()){
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        if(info.isGroupOwner) {
                            Log.d("CymWT", "I'm contacting");
                            //set up serversocket to exchange data
                            SocketServerTask task = new SocketServerTask(mActivity.msgBuffer);
                            task.execute();
                            //breaking the connection
                            Handler timerHandler = new Handler();
                            Runnable timerRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    //task.stopRunning();
                                    mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("CymWT", "I broke the connection");
                                        }

                                        @Override
                                        public void onFailure(int reason) {
                                            Log.d("CymWT", "I can't break the connection");
                                        }
                                    });
                                }
                            };
                            timerHandler.postDelayed(timerRunnable, 10000);

                        }
                        else {
                            //contact groupowner for message
                            Log.d("CymWT", "I'm contacted");
                            SocketReceivingTask rTask = new SocketReceivingTask(mActivity);
                            rTask.execute(new InetSocketAddress(info.groupOwnerAddress, 8888));
                        }
                    }
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }

    }

    public void discoverPeers(){
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener(){

            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
                Log.d("CymWT", "peer discovery failed");

            }
        });
    }
}
