package com.google.android.gms.samples.vision.face.facetracker;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ControlPanelActivity extends AppCompatActivity {

    Button btnGo,btnStop,btnRight,btnLeft,btnBackup;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.activity_control_panel);

        //call the widgtes
        btnGo       = (Button)findViewById(R.id.btn_forward);
        btnStop     = (Button)findViewById(R.id.btn_stop);
        btnRight    = (Button)findViewById(R.id.btn_right);
        btnLeft     = (Button)findViewById(R.id.btn_left);
        btnBackup   = (Button)findViewById(R.id.btn_back);

        if (address!=null) {
            new ConnectBT().execute(); //Call the class to connect
        }

        //commands to be sent to bluetooth
        btnGo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                move(1);      //method to turn on
            }
        });

        //commands to be sent to bluetooth
        btnStop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                move(2);      //method to turn on
            }
        });

        //commands to be sent to bluetooth
        btnLeft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                move(3);      //method to turn on
            }
        });

        //commands to be sent to bluetooth
        btnRight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                move(4);      //method to turn on
            }
        });

        //commands to be sent to bluetooth
        btnBackup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                move(5);      //method to turn on
            }
        });

    }

    private void move(int cmd)
    {
        byte b[] = new byte[1];
        if (btSocket!=null)
        {
            try
            {
                b[0] = (byte)cmd;
                btSocket.getOutputStream().write(b);
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //move(stop);
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
    }


    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ControlPanelActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

}
