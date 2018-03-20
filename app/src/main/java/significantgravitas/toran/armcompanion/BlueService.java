package significantgravitas.toran.armcompanion;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;


public class BlueService {
    private Handler mHandler; // Handler that gets info from Bluetooth service

    // Constants used when transmitting messages between the service and the UI.
    private interface MessageConstants {
        int MESSAGE_READ = 0;
        int MESSAGE_WRITE = 1;
        int MESSAGE_TOAST = 2;
    }

    public class ConnectedThread extends Thread {
        BluetoothSocket finalSocket;
        InputStream inStream;
        OutputStream outStream;
        private byte[] streamBuffer; // Buffer store for the stream

        public ConnectedThread(BluetoothSocket socket){
            finalSocket = socket;

            // Try to get input and output streams
            try {
                inStream = socket.getInputStream();
            }
            catch (IOException exc) {
                Log.e("ERROR-TAG", "Error occurred when creating input stream", exc);
            }
            try {
                outStream = socket.getOutputStream();
            } catch (IOException exc) {
                Log.e("ERROR-TAG", "Error occurred when creating output stream", exc);
            }
        }

        public void run(){
            streamBuffer = new byte[1024];
            int numBytes;

            // Listen to Input Stream until Exception Occurs (Disconnect)
            while (true){
                try{
                    // Read from input stream
                    numBytes = inStream.read(streamBuffer);
                    // Send Bytes to Main Activity
                    Message readMsg = mHandler.obtainMessage(MessageConstants.MESSAGE_READ, numBytes, -1, streamBuffer);
                    readMsg.sendToTarget();
                }
                catch (IOException exc){
                    Log.d("DSCN-TAG", "Input stream disconnected.");
                    break;
                }
            }
        }

        //Call from Main Activity to send data to ARM
        public void write(byte[] bytes){
            try{
                // Write given bytes to outStream
                outStream.write(bytes);
                // Share sent message with Main Activity.
                Message writtenMsg = mHandler.obtainMessage(MessageConstants.MESSAGE_WRITE, -1, -1, streamBuffer);
                // Send to target
                writtenMsg.sendToTarget();
            }
            catch (IOException exc){
                Log.e("ERROR-TAG", "Error occurred when sending data to ARM", exc);

                // Send a failure message back to Main Activity.
                Message writeErrorMsg = mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call from Main Activity to close connection
        public void cancel() {
            //try to close socket
            try {
                finalSocket.close();
            } catch (IOException exc) {
                Log.e("ERROR-TAG", "Could not close the connect socket", exc);
            }
        }
    }
}


