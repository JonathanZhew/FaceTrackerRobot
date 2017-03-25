package com.google.android.gms.samples.vision.face.facetracker;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.zip.CheckedInputStream;

public class MainActivity extends AppCompatActivity {

    //widgets
    Button btnPaired;
    Button btnTracker;
    Button btnPanel;
    CheckBox chkFxCam;
    String CameraName;
    ListView devicelist;
    String btAddress;
    //Bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";
    public static String CAMERA_ID = "cam_id";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Calling widgets
        btnPaired = (Button)findViewById(R.id.button);
        btnTracker = (Button)findViewById(R.id.btn_track);
        btnPanel = (Button)findViewById(R.id.btn_panel);
        devicelist = (ListView)findViewById(R.id.listView);
        chkFxCam = (CheckBox)findViewById(R.id.btn_select_cam);
        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        CameraName = "facing_front";

        if(myBluetooth == null)
        {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        else if(!myBluetooth.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                pairedDevicesList();
            }
        });

        btnTracker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                // Make an intent to start next activity.
                Intent i = new Intent(MainActivity.this, FaceTrackerActivity.class);

                //Change the activity.
                i.putExtra(EXTRA_ADDRESS, btAddress);
                i.putExtra(CAMERA_ID, CameraName);
                startActivity(i);
            }
        });

        btnPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Make an intent to start next activity.
                Intent i = new Intent(MainActivity.this, ControlPanelActivity.class);

                //Change the activity.
                i.putExtra(EXTRA_ADDRESS, btAddress);
                i.putExtra(CAMERA_ID, CameraName);
                startActivity(i);
            }
        });

        chkFxCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Is the view now checked?
                boolean checked = ((CheckBox) v).isChecked();
                if(checked) {
                    CameraName = "facing_front";
                }
                else{
                    CameraName = "facing_bcak";
                }
            }
        });
    }

    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            btAddress = info.substring(info.length() - 17);

            // Make an intent to start next activity.
            //Intent i = new Intent(MainActivity.this, FaceTrackerActivity.class);

            //Change the activity.
            //i.putExtra(EXTRA_ADDRESS, btAddress); //this will be received at ledControl (class) Activity
            //i.putExtra(CAMERA_ID, CameraName);
            //startActivity(i);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
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

            showPopup(MainActivity.this, new Point(0,500));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    // The method that displays the popup.
    private void showPopup(final Activity context, Point p) {
        int popupWidth = 1000;
        int popupHeight = 1000;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_about, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Clear the default translucent background
        //popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        // Getting a reference to Close button, and close the popup when clicked.
        Button close = (Button) layout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }
}
