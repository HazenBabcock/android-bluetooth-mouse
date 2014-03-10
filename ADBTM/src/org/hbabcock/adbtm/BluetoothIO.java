/*
 * Bluetooth IO class.
 * 
 * Much of the code comes from here:
 *   https://github.com/luugiathuy/Remote-Bluetooth-Android
 * 
 * And here:
 *   http://developer.android.com/guide/topics/connectivity/bluetooth.html
 *   
 * Hazen 03/14
 */

package org.hbabcock.adbtm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
 * Variables and methods listed alphabetically.
 */
public class BluetoothIO {

    // Unique UUID for this application
    private static final boolean DEBUG = true;
    private static final UUID MY_UUID = UUID.fromString("1a68fa50-a83b-11e3-9be7-425861b86ab6");
    private static final String TAG = "BluetoothIO";
    
    private final BluetoothAdapter mBluetoothAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private final Handler mHandler;
    
    public BluetoothIO(Context context, Handler handler) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
    }

    public void connect(BluetoothDevice device) {
    	if (DEBUG) Log.d(TAG, "connect to: " + device);

    	/*
    	 * Close current connections.
    	 */
    	if (mConnectThread != null) {
    		mConnectThread.cancel(); 
    		mConnectThread = null;
    	}
    	if (mConnectedThread != null) {
    		mConnectedThread.cancel(); 
    		mConnectedThread = null;
    	}

    	// Start the thread to connect with the given device
    	mConnectThread = new ConnectThread(device);
    	mConnectThread.start();
    }
    
    public void connected(BluetoothSocket socket){
    	 if (DEBUG) Log.d(TAG, "connected");
    	 
    	 mConnectedThread = new ConnectedThread(socket);
    	 mConnectedThread.start();
    	 
     	Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_CONNECT);
     	mHandler.sendMessage(msg);
    }
    
    /*
     * Handles IO on the connection.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
     
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
     
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
     
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
     
        public void run() {
            int bytes; // bytes returned from read()
            byte[] buffer = new byte[1024];  // buffer store for the stream
     
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream.
                    bytes = mmInStream.read(buffer);
                } 
                catch (IOException e) {
                    if (DEBUG) Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }

    	/* 
    	 * Send a message. The characters <> are used by the receiver to tell
    	 * when one message ends and another starts.
    	 */
    	public void sendMessage(String msg){
    		String to_send = msg + "<>";
    		byte[] bytes = to_send.getBytes();
    		write(bytes);		
    	}

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }
     
        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private void connectionLost() {
    	Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_TOAST);
    	Bundle bundle = new Bundle();
    	bundle.putString(MainActivity.TOAST, "Device connection was lost");
    	msg.setData(bundle);
    	mHandler.sendMessage(msg);    	
    	
    	stop();
    }

    /*
     * Creates the connection.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
     
        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }
     
        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();
     
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
     
            // Do work to manage the connection (in a separate thread)
            connected(mmSocket);
        }
     
        /* Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public void sendMessage(String msg){
    	if (mConnectedThread != null){
    		mConnectedThread.sendMessage(msg);
    	}
    }
    
    public void stop() {
        if (DEBUG) Log.d(TAG, "stop");
        if (mConnectThread != null) {
        	mConnectThread.cancel(); 
        	mConnectThread = null;
        }
        if (mConnectedThread != null) {
        	mConnectedThread.cancel(); 
        	mConnectedThread = null;
        }
    	Message msg = mHandler.obtainMessage(MainActivity.MESSAGE_DISCONNECT);
    	mHandler.sendMessage(msg);
    }
}
