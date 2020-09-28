package dk.osandweb.flic2unity;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import io.flic.flic2libandroid.Flic2Button;
import io.flic.flic2libandroid.Flic2ButtonListener;
import io.flic.flic2libandroid.Flic2Manager;
import io.flic.flic2libandroid.Flic2ScanCallback;

interface UnityCallback {
    public void onFlicClick();
    public void onFlicFailure();
}


public class Flic {

    public void LogNativeAndroidMessage() {
        Log.d("Unity", "native logcat message");
    }

    public void initAndGetInstance(final Context context, final UnityCallback callback) {

        Log.d("flic2unity", "Initializing flic2manager");

        Flic2Manager manager = Flic2Manager.initAndGetInstance(context, new Handler());


        manager.getInstance();
                manager.startScan(new Flic2ScanCallback() {
            @Override
            public void onDiscoveredAlreadyPairedButton(Flic2Button button) {
                Log.d("flic2unity", "Found an already paired button. Try another button.");
                button.addListener(new Flic2ButtonListener() {
                    @Override
                    public void onButtonUpOrDown(Flic2Button button, boolean wasQueued, boolean lastQueued, long timestamp, boolean isUp, boolean isDown) {
                        Log.d("flic2unity", "Button is doing stuff");

                        if (isDown) {
                            Log.d("flic2unity", "Button was pressed");
                        }
                    }
                });
            }

            @Override
            public void onDiscovered(String bdAddr) {
                Log.d("flic2unity", "Found Flic2, now connecting...");
            }

            @Override
            public void onConnected() {
                Log.d("flic2unity", "Connected. Now pairing...");
            }

            @Override
            public void onComplete(int result, int subCode, Flic2Button button) {
                if (result == Flic2ScanCallback.RESULT_SUCCESS) {
                    // Success!
                    button.addListener(new Flic2ButtonListener() {
                        @Override
                        public void onButtonUpOrDown(Flic2Button button, boolean wasQueued, boolean lastQueued, long timestamp, boolean isUp, boolean isDown) {

                            if (isDown) {
                                // Do something
                                callback.onFlicClick();
                            }
                        }
                    });

                } else {
                    callback.onFlicFailure();
                }
            }
        });
    }
}
