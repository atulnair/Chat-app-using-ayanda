package sample;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import sintulabs.ayanda.R;
import sintulabs.p2p.Ayanda;
import sintulabs.p2p.IBluetooth;
import sintulabs.p2p.Utils;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView mRecyclerView;
    ArrayList<ChatMessage> chatMessages;
    Adapter adapter;
    private Ayanda a;
    EditText editView;
    Button sendButton;
    BluetoothDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mRecyclerView = (RecyclerView) findViewById(R.id.ChatRecycle);
        chatMessages = new ArrayList<>();
        adapter = new Adapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        editView = findViewById(R.id.edit);
        sendButton = findViewById(R.id.send);
        sendButton.setOnClickListener(this);

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

            }

            @Override
            public void dataRead(byte[] bytes, int length) {
                String readMessage = new String(bytes, 0, length);
                chatMessages.add(new ChatMessage(readMessage,0));
                Log.e("chat",readMessage);

            }

            @Override
            public void connected(BluetoothDevice devic) {
                device = devic;
            }



        }, null, null);

        if(getIntent().getExtras()!=null){
            device =  getIntent().getExtras().getParcelable("btdevice");
            Log.e("de",device.toString());
            a.btConnect(device);
        }

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.send){
            String message = editView.getText().toString();
            editView.getText().clear();
            try {

//                device = a.getActiveDevice();
                a.btSendData(device, message.getBytes());
                chatMessages.add(new ChatMessage(message,0));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public class ViewH extends RecyclerView.ViewHolder {
        TextView messageView;


        public ViewH(View itemView) {
            super(itemView);
           messageView = findViewById(R.id.message);

        }
    }

    public class Adapter extends RecyclerView.Adapter<ViewH> {

        @Override
        public ViewH onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflate = getLayoutInflater();
            View v = inflate.inflate(R.layout.chat_view, parent, false);

            return new ViewH(v);
        }

        @Override
        public void onBindViewHolder(final ViewH holder, int position) {
            final ChatMessage chat = chatMessages.get(position);

            holder.messageView.setText(chat.getMessage());
            if(chat.getSent()==1){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.START;
                holder.messageView.setLayoutParams(params);
            }
            else {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.END;
                holder.messageView.setLayoutParams(params);
            }


        }

        @Override
        public int getItemCount() {
            return chatMessages.size();
        }
    }

}
