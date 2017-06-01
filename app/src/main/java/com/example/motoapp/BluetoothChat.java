/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.motoapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluetoothChat extends Activity
{
    // Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;
    private Button mScanButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private static BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private static BluetoothChatService mChatService = null;

    public static byte[] Utf8ToBig5(String pStrUtf8, int pIntLen)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            baos.write(new String(pStrUtf8).getBytes("Big5"));
            for (int idx = baos.size(); idx < pIntLen; idx++)
                baos.write(0x20);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public static byte[] StringToBytes(String pStr)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] byteData = pStr.getBytes();
        for (byte b : byteData)
            baos.write(b);
        baos.write((byte) 0x00);
        return baos.toByteArray();
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (D)
            Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.main);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });
        this.mScanButton = (Button) findViewById(R.id.button_scan);
        this.mScanButton.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                Intent serverIntent = new Intent(BluetoothChat.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        });
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the BluetoothChatService to perform bluetooth connections
        if (mChatService == null)
        {
            mChatService = new BluetoothChatService(this, mHandler);
        }

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED)
        {
            Intent serverIntent = new Intent(BluetoothChat.this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }
        else
        {
            BluetoothChat.this.sendMessage("");
        }

    }

    @Override public void onStart()
    {
        super.onStart();
        if (D)
            Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        }
        else
        {
            if (mChatService == null)
                setupChat();
        }
    }

    @Override public synchronized void onResume()
    {
        super.onResume();
        if (D)
            Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (mChatService != null)
        {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE)
            {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    private void setupChat()
    {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
    }

    @Override public synchronized void onPause()
    {
        super.onPause();
        if (D)
            Log.e(TAG, "- ON PAUSE -");
    }

    @Override public void onStop()
    {
        super.onStop();
        if (D)
            Log.e(TAG, "-- ON STOP --");
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        if (D)
            Log.e(TAG, "--- ON DESTROY ---");
    }

    private void ensureDiscoverable()
    {
        if (D)
            Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
        {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     * 
     * @param message
     *            A string of text to send.
     */
    private void sendMessage(String message)
    {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED)
        {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() >= 0)
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            {
                EditText editTextOut = (EditText) findViewById(R.id.edit_text_out);

                if (editTextOut != null && editTextOut.getText().length() > 0)
                {
                    String strOut = "";

                    strOut += "^XA";
                    strOut += "^FWN";
                    strOut += "^CW0";
                    strOut += "^CI28";
                    strOut += "^FO10,70";
                    strOut += "^A0N50,50";
                    strOut += "^FD<<<測試>>>";
                    strOut += "^FS";
                    strOut += "^XZ";
                    // byte[] send = strOut.getBytes();
                    byte[] send = Utf8ToBig5(strOut, 0);
                    for (byte b : send)
                        baos.write(b);
                }
                else
                {
                    String keys[] = getIntent().getStringArrayExtra("keys");
                    String values[] = getIntent().getStringArrayExtra("values");
                    ArrayList<MyEntry<String, String>> keyValues = new ArrayList<MyEntry<String, String>>();
                    for (int i = 0; i < keys.length && i < values.length; i++)
                    {
                        String key = keys[i];
                        String value = values[i];
                        keyValues.add(new MyEntry<String, String>(key, value));
                    }

                    this.print(keyValues);
                }

            }

            mChatService.write(baos.toByteArray());
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);

            finish();
        }
    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener()
    {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event)
        {
            // If the action is a key-up event on the return key, send the
            // message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP)
            {
                String message = view.getText().toString();
                sendMessage(message);
            }
            if (D)
                Log.i(TAG, "END onEditorAction");
            return true;
        }
    };

    private final void setStatus(int resId)
    {
        /*final ActionBar actionBar = getActionBar();
        if (actionBar != null)
        {
            actionBar.setSubtitle(resId);
        }*/
    }

    private final void setStatus(CharSequence subTitle)
    {
       /* final ActionBar actionBar = getActionBar();
        if (actionBar != null)
        {
            actionBar.setSubtitle(subTitle);
        }*/
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler()
    {
        @Override public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case MESSAGE_STATE_CHANGE:
                if (D)
                    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1)
                {
                case BluetoothChatService.STATE_CONNECTED:
                    //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                    mConversationArrayAdapter.clear();
                {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    String strOut = "";
                    String strBmp = "";
                    {
                        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo2);
                        // strOut += "~DGE:LOGO.GRF," + ((logo.getWidth() + 7) /
                        // 8 * logo.getHeight()) + "," + ((logo.getWidth() + 7)
                        // / 8-1) + ",";

                        for (int y = 0; y < logo.getHeight(); y++)
                        {
                            for (int x = 0; x < logo.getWidth() + 7; x += 8)
                            {
                                int bwValues[] = new int[8];
                                int bwValue = 0;
                                for (int k = 0; k < 8; k++)
                                {
                                    if (x + k < logo.getWidth())
                                    {
                                        int pixel = logo.getPixel(x + k, y);
                                        int a = pixel >> 24;
                                        int r = 0xff & (pixel >> 16);
                                        int g = 0xff & (pixel >> 8);
                                        int b = 0xff & (pixel);
                                        int grey = (r + g + b) / 3;

                                        if (grey < 128)
                                        {
                                            bwValues[k] = 1;
                                            bwValue = (bwValue << 1) + 1;
                                        }
                                        else
                                        {
                                            bwValues[k] = 0;
                                            bwValue = (bwValue << 1) + 0;
                                        }
                                    }
                                    else
                                    {
                                        bwValues[k] = 0;
                                    }
                                }

                                if (bwValue < 16)
                                {
                                    strBmp += "0";
                                }
                                strBmp += Integer.toString(bwValue, 16);
                            }
                            // break;
                        }
                        strOut += "~DGE:LOGO.GRF," + (strBmp.length() / 2) + "," + (strBmp.length() / 2 / logo.getHeight()) + ",";
                    }
                    strOut += strBmp.toUpperCase();

                    byte[] send = Utf8ToBig5(strOut, 0);
                    for (byte b : send)
                        baos.write(b);
                    mChatService.write(baos.toByteArray());
                    // Reset out string buffer to zero and clear the edit text
                    // field
                    mOutStringBuffer.setLength(0);
                    mOutEditText.setText(mOutStringBuffer);

                    BluetoothChat.this.sendMessage("");
                }
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    setStatus(R.string.title_connecting);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    setStatus(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (D)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode)
        {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK)
            {
                connectDevice(data);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK)
            {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            }
            else
            {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void connectDevice(Intent data)
    {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case R.id.connect_scan:
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
        case R.id.discoverable:
            // Ensure this device is discoverable by others
            ensureDiscoverable();
            return true;
        }
        return false;
    }
    public void print(ArrayList<MyEntry<String, String>> keyValues)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String strOut = "";

        strOut += "^XA^PON^MNY";
        //strOut += "^LH0,0^A@N,,,B:900.ARF";
        strOut += "^LH5,10";
        String fontString = "^A@,24,24,B:900.ARF";
                          
        int y = 10;
        final int line_height = 30;
        final int leading_space = 5;

        for (MyEntry<String, String> keyValue : keyValues)
        {
            String key = keyValue.key;
            String value = keyValue.value;

            if (key.length() > 0 && key.startsWith("@"))
            {
                strOut += "^FO" + (leading_space+20) + "," + y + "^BCN,100,N,N,N^FD" + value + "^FS";
                y += 100;
            }
            else if (key.length() > 0 && key.startsWith("_"))
            {
                y += 10;
                strOut += "^FO" + leading_space + "," + y + "^FD" + key.substring(1) + "�G" + value + "^FS";
                strOut += "^FO" + (leading_space + 1) + "," + (y + 1) + "^FD" + key.substring(1) + "^FS";
                y += line_height;
            }
            else if (key.length() > 0)
            {
                y += 10;

                if (value.length() > 16 - key.length())
                {
                    strOut += "^FO" + leading_space + "," + y + fontString + "^FD" + key + "�G" + value.substring(0, 16 - key.length()) + "^FS";
                    strOut += "^FO" + (leading_space + 1) + "," + (y + 1) + fontString + "^FD" + key + "^FS";
                    y += line_height + 10;
                    strOut += "^FO" + leading_space + "," + y + fontString + "^FD";
                    /* for (int i = key.length() - Math.max(0, value.length() -
                     * 32 - key.length() * 2); i >= 0; i--) { strOut += "__"; } */
                    strOut += "  ";
                    strOut += value.substring(16 - key.length()) + "^FS";
                }
                else
                {
                    strOut += "^FO" + leading_space + "," + y + fontString + "^FD" + key + "�G" + value + "^FS";
                    strOut += "^FO" + (leading_space + 1) + "," + (y + 1) + fontString + "^FD" + key + "^FS";
                }
                y += line_height;
            }
            else
            {
                y += 10;
                strOut += "^FO" + leading_space + "," + y + fontString + "^FD" + value + "^FS";
                y += line_height;
            }
        }

        strOut += "^FO390,20^XGE:LOGO.GRF,1,1^FS";
        strOut += "^XZ";

        byte[] send = Utf8ToBig5(strOut, 0);
        for (byte b : send)
            baos.write(b);

        mChatService.write(baos.toByteArray());
        // Reset out string buffer to zero and clear the edit text field
        mOutStringBuffer.setLength(0);
        mOutEditText.setText(mOutStringBuffer);
    }

    public void print1(ArrayList<MyEntry<String, String>> keyValues)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String strOut = "";

        strOut += "^XA^PON^MNY";
        strOut += "^LH0,0^A@N,,,B:900.ARF";

        int y = 10;
        final int line_height = 30;
        final int leading_space = 5;

        for (MyEntry<String, String> keyValue : keyValues)
        {
            String key = keyValue.key;
            String value = keyValue.value;

            if (key.length() > 0 && key.startsWith("@"))
            {
                strOut += "^FO" + leading_space + "," + y + "^BCN,100,N,N,N^FD" + value + "^FS";
                y += 100;
            }
            else if (key.length() > 0 && key.startsWith("_"))
            {
                y += 10;
                strOut += "^FO" + leading_space + "," + y + "^FD" + key.substring(1) + "�G" + value + "^FS";
                strOut += "^FO" + (leading_space + 1) + "," + (y + 1) + "^FD" + key.substring(1) + "^FS";
                y += line_height;
            }
            else if (key.length() > 0)
            {
                y += 10;

                if (value.length() > 16 - key.length())
                {
                    strOut += "^FO" + leading_space + "," + y + "^FD" + key + "�G" + value.substring(0, 16 - key.length()) + "^FS";
                    strOut += "^FO" + (leading_space + 1) + "," + (y + 1) + "^FD" + key + "^FS";
                    y += line_height + 10;
                    strOut += "^FO" + leading_space + "," + y + "^FD";
                    /* for (int i = key.length() - Math.max(0, value.length() -
                     * 32 - key.length() * 2); i >= 0; i--) { strOut += "__"; } */
                    strOut += "  ";
                    strOut += value.substring(16 - key.length()) + "^FS";
                }
                else
                {
                    strOut += "^FO" + leading_space + "," + y + "^FD" + key + "�G" + value + "^FS";
                    strOut += "^FO" + (leading_space + 1) + "," + (y + 1) + "^FD" + key + "^FS";
                }
                y += line_height;
            }
            else
            {
                y += 10;
                strOut += "^FO" + leading_space + "," + y + "^FD" + value + "^FS";
                y += line_height;
            }
        }

        strOut += "^FO390,20^XGE:LOGO.GRF,1,1^FS";
        strOut += "^XZ";

        byte[] send = Utf8ToBig5(strOut, 0);
        for (byte b : send)
            baos.write(b);

        mChatService.write(baos.toByteArray());
        // Reset out string buffer to zero and clear the edit text field
        mOutStringBuffer.setLength(0);
        mOutEditText.setText(mOutStringBuffer);
    }

    public class MyEntry<K, V> implements Map.Entry<K, V>
    {
        final K key;
        V value;

        public MyEntry(K key, V value)
        {
            this.key = key;
            this.value = value;
        }

        @Override public K getKey()
        {
            return key;
        }

        @Override public V getValue()
        {
            return value;
        }

        @Override public V setValue(V value)
        {
            V old = this.value;
            this.value = value;
            return old;
        }
    }
}
