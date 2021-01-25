package com.example.bluetooth;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Message;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView tvBluethoothstate;
    public BtInterface bt = null;
    private static long lastTime = 0;
    BluetoothAdapter bluetoothAdapter;
    Button btnBlue,btnRed,btnYellow,btnSend;
    public EditText etSend;
    BluetoothManager bluetoothManager;
    ImageView ivOff,ivOn;

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String data = msg.getData().getString("receivedData");
            long t = System.currentTimeMillis();
            if (t - lastTime > 100) {// Pour éviter que les messages soit coupés
                //tvBluethoothstate.append("\n");
                lastTime = System.currentTimeMillis();
                //time();
            }
            //tvLampState.append(data);
        }
    };

    final Handler handlerStatus = new Handler() {
        public void handleMessage(Message msg) {
            int co = msg.arg1;
            if (co == 1) {
                tvBluethoothstate.append("Connected");
            } else if (co == 2) {
                tvBluethoothstate.append("Disconnected");
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBlue= findViewById(R.id.btnBlue);
        btnRed = findViewById(R.id.btnRed);
        btnYellow = findViewById(R.id.btnYellow);
        etSend = findViewById(R.id.etSend);
        btnSend = findViewById(R.id.btnSend);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        tvBluethoothstate = findViewById(R.id.tvBluethoothstate);
        ivOff = findViewById(R.id.ivOff);
        ivOn = findViewById(R.id.ivOn);
        ivOn.setVisibility(View.INVISIBLE);

        bt = new BtInterface(handlerStatus, handler);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

    }

    /*********** Activate and deactivate Bluetooth **********************/
    /***************************** MENU *********************************/
    public void activate(){
        if (!bluetoothAdapter.isEnabled()) {
            final int CODE_DEMANDE_ACTIVATION = 1234;
            Intent demandeActivationBLE =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(demandeActivationBLE, CODE_DEMANDE_ACTIVATION);
            Toast.makeText(this,"Bluetooth Activé",Toast.LENGTH_LONG).show();
            tvBluethoothstate.setText("connected");
            //tvBluethoothstate.setTextColor();
        }else {
            bluetoothAdapter.disable();
            Toast.makeText(this,"Bluetooth Désactivé",Toast.LENGTH_LONG).show();
            tvBluethoothstate.setText("disconnected");
        }
    }


    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true ;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        int id=item.getItemId();

        if(id==R.id.select){
            //reglagehorloge.setVisibility(View.VISIBLE);
        }

        if(id==R.id.bluetooth_act ) {
            activate();
        }

        if(id==R.id.connect_wav){
            if(bluetoothAdapter.isEnabled()){
                bt.connect();
            }
            else {
                Toast.makeText(this,"SVP acitivez votre Bluetooth",Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void blueLed(View view) {
        bt.sendData("b");
    }

    public void redLed(View view) {
        bt.sendData("g");
    }

    public void YellowLed(View view) {
        bt.sendData("d");
    }

    public void sendvalue(View view) {
        bt.sendData(etSend.getText().toString());
    }

    public void btnOff(View view) {
        if(ivOff.getVisibility()== View.VISIBLE)
        {
            bt.sendData("On");
            ivOn.setVisibility(View.VISIBLE);
            ivOff.setVisibility(View.INVISIBLE);
        }
    }

    public void btnOn(View view) {
        if(ivOn.getVisibility()== View.VISIBLE)
        {
            bt.sendData("Off");
            ivOn.setVisibility(View.INVISIBLE);
            ivOff.setVisibility(View.VISIBLE);
        }
    }
    /*********************************************************************/
}
