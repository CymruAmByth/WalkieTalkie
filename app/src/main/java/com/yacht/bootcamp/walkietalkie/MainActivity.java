package com.yacht.bootcamp.walkietalkie;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yacht.bootcamp.walkietalkie.HelperClasses.TextMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiDirectBroadCastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private TextView tvPeers;
    private List<TextMessage> msgs;
    public List<TextMessage> msgBuffer;
    private long lastTimestamp;

    private WifiP2pDeviceList peers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msgs = new ArrayList<>();
        msgBuffer = new ArrayList<>();
        peers = new WifiP2pDeviceList();
        lastTimestamp = 0;

        tvPeers = (TextView)findViewById(R.id.tvNoOfPeers);

        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiDirectBroadCastReceiver(mManager, mChannel, this);
        mReceiver.discoverPeers();

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch(item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_refreshPeers:
                tvPeers.setText("Searching peers");
                mReceiver.discoverPeers();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
        mReceiver.discoverPeers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    protected void updatePeers(WifiP2pDeviceList peers) {
        this.peers = peers;
        tvPeers.setText("No. of connected peers: " + peers.getDeviceList().size());
    }

    public void addMessage(TextMessage msg, boolean incoming){
        msgs.add(0, msg);
        if(incoming){
            lastTimestamp = msg.getTimeStamp();
        }
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void connectClicked(View view){
        TextMessage msg = new TextMessage("Joey", "Hello world!");
        msgBuffer.add(msg);
        for(WifiP2pDevice peer : peers.getDeviceList()){
            WifiP2pConfig config = new WifiP2pConfig();
            config.groupOwnerIntent = 15;
            config.deviceAddress = peer.deviceAddress;
            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d("CymWT", "Ping succes");
                }

                @Override
                public void onFailure(int reason) {
                    Log.d("CymWT", "Ping failed");
                }
            });
        }
    }
}
