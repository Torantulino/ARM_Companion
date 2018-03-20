package significantgravitas.toran.armcompanion;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BlueConnectThread extends Thread {
    BluetoothSocket finalSocket;
    BluetoothDevice finalDevice;
    String MY_UUID = "452ee9f7-0eec-4102-8d2c-62ef6f1026b9";
    BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();

    public BlueConnectThread(BluetoothDevice device) {
        BluetoothSocket tmpSocket = null;
        finalDevice = device;

        //Try to create a socket with the same UUID as ARM's RFCOMM Server
        try {
            tmpSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
        }
        catch (IOException exc){
            Log.e("ERROR-TAG","Socket's create() method failed", exc);
        }
        finalSocket = tmpSocket;
    }

    public void run() {
        //Cancel discovery to avoid slowing connection
        defaultAdapter.cancelDiscovery();

        // Try to connect to remote device through socket
        try {
            finalSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect, try to close socket, then return.
            try {
                finalSocket.close();
            } catch (IOException closeException) {
                Log.e("ERROR-TAG", "Could not close the client socket", closeException);
            }
            return;
        }
        // Connection attempt succeeded. Perform associated work in separate thread:

        BlueService.ConnectedThread blueService = new BlueService().new ConnectedThread(finalSocket);
        blueService.start();

    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            finalSocket.close();
        } catch (IOException exc) {
            Log.e("ERROR-TAG", "Could not close client socket", exc);
        }
    }
}