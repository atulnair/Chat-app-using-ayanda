package sample;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sintulabs.ayanda.R;
import sintulabs.p2p.Ayanda;
import sintulabs.p2p.Bluetooth;
import sintulabs.p2p.IBluetooth;

/**
 * Created by sabzo on 1/14/18.
 */

public class BluetoothActivity extends AppCompatActivity {
    private Button btnAnnounce;
    private Button btnDiscover;
    private Bluetooth bt;
    private ListView lvBtDeviceNames;
    private ArrayAdapter<String> peersAdapter = null;
    private List peerNames = new ArrayList();
    private HashMap<String, BluetoothDevice> devices = new HashMap<>();


    BluetoothDevice device;
    private EditText edit;
    Button send;
    private Ayanda a;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        a = new Ayanda(this, new IBluetooth() {
            @Override
            public void actionDiscoveryStarted(Intent intent) {

            }

            @Override
            public void actionDiscoveryFinished(Intent intent) {

            }

            @Override
            public void stateChanged(Intent intent) {

            }

            @Override
            public void scanModeChange(Intent intent) {

            }

            @Override
            public void actionFound(Intent intent) {
                peersAdapter.clear();
                peersAdapter.addAll(a.btGetDeviceNamesDiscovered());
                devices = a.btGetDevices();
            }

            @Override
            public void dataRead(byte[] bytes, int length) {
                String readMessage = new String(bytes, 0, length);
//                Toast.makeText(BluetoothActivity.this, readMessage, Toast.LENGTH_LONG)
//                        .show();
            }

            @Override
            public void connected(BluetoothDevice device) {
                String message = "Hello World";
                try {
                    Log.e("s",device.toString());
                    a.btSendData(device, message.getBytes()); // maybe a class for a device that's connected
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        }, null, null);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                Bluetooth.BT_PERMISSION_REQUEST_LOCATION);
        setContentView(R.layout.bluetooth_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createView();
        setListeners();
    }

    private void createView() {
        lvBtDeviceNames = (ListView) findViewById(R.id.lvBtDeviceNames);
        peersAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, peerNames);
        lvBtDeviceNames.setAdapter(peersAdapter);
        btnAnnounce = (Button) findViewById(R.id.btnBtAnnounce);
        btnDiscover = (Button) findViewById(R.id.btnBtDiscover);


        send = findViewById(R.id.send);
        edit = findViewById(R.id.edit);
    }

    private void setListeners() {
        View.OnClickListener btnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.btnBtAnnounce:
                        a.btAnnounce();
                        break;
                    case R.id.btnBtDiscover:
                        a.btDiscover();
                        break;
                    case R.id.send:
                        sendMessage();
//                        Intent intent = new Intent(BluetoothActivity.this,MessageActivity.class);
//                        Log.e("kkkk0",device.getAddress());
//                        intent.putExtra("btdevice", device);
//                        startActivity(intent);
                        break;

                }
            }
        };
        AdapterView.OnItemClickListener clickPhone = new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                device = devices.get(peerNames.get(pos));
                a.btConnect(device);
            }
        };

        btnAnnounce.setOnClickListener(btnClick);
        btnDiscover.setOnClickListener(btnClick);
        lvBtDeviceNames.setOnItemClickListener(clickPhone);

        send.setOnClickListener(btnClick);
    }


    public void sendMessage(){
        String message = edit.getText().toString();
        try {
            Log.e("s",device.toString());
            device = a.getActiveDevice();
            a.btSendData(device, message.getBytes()); // maybe a class for a device that's connected
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        a.btRegisterReceivers();
    }



    @Override
    protected void onPause() {
        super.onPause();
        a.btUnregisterReceivers();
    }



}
