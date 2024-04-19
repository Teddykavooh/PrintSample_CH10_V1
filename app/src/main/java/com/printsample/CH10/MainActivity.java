package com.printsample.CH10;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sr.SrPrinter;

import recieptservice.com.recieptservice.PrinterInterface;

public class MainActivity extends AppCompatActivity {
//    SharedPreferences sp;
//    public String tag = "MainActivity";
    private int printerStatus;
    private Button offButton;
    private Button onButton;
    String deviceId;
    private boolean isPrinterBound = false;
    private PrinterInterface printerService;
    public ServiceConnection serviceConnection;

    @Override
    public void onStart() {
        super.onStart();
    }

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        onButton = findViewById(R.id.printOn);
        offButton = findViewById(R.id.printOff);
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("onCreate_device_id", deviceId);

        // Call the function from the library
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                // Handle service connection
//                SrPrinter.LocalBinder binder = (SrPrinter.LocalBinder) iBinder;
//                printerService = PrinterInterface.newInstance();
//                PrinterInterface = printerService
                isPrinterBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                // Handle service disconnection
                isPrinterBound = false;
            }
        };

        // Bind printer
        SrPrinter.bindPrinter(MainActivity.this, serviceConnection);
    }

    // Method to print text
    private void printText(String text) throws RemoteException {
        if (isPrinterBound && printerService != null) {
            printerService.printText(text);
        } else {
            // Handle case where printer service is not connected
            Toast.makeText(getApplicationContext(), "Printer out of reach",
                    Toast.LENGTH_LONG).show();
        }
    }

    // Method to print text
    private void printBlanks(int blank_line) throws RemoteException {
        if (isPrinterBound && printerService != null) {
            printerService.nextLine(blank_line);
        } else {
            // Handle case where printer service is not connected
            Toast.makeText(getApplicationContext(), "Printer out of reach",
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub

        disableFunctionLaunch(true);
        /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);*/

        super.onResume();
//        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        receiver = new BatteryReceiver();
//        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO Auto-generated method stub
        disableFunctionLaunch(false);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        PosApiHelper.getInstance().SysLogSwitch(0);
        if (isPrinterBound) {
            unbindService(serviceConnection);
            isPrinterBound = false;
        }
    }

    /*Turning printer on*/
    public void turnOn(View v){
        printerStatus = 1;
//        posApiHelper.SysLogSwitch(printerStatus);
        onButton.setVisibility(View.INVISIBLE);
        offButton.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), "Printer turned ON", Toast.LENGTH_SHORT).show();
    }



    /*Turning printer off*/
    public void turnOff(View v){
        printerStatus = 0;
//        posApiHelper.SysLogSwitch(0);
        offButton.setVisibility(View.INVISIBLE);
        onButton.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), "Printer turned OFF", Toast.LENGTH_SHORT).show();
    }

    /*disable the power key to avoid print process interruption. Recommended to block the return,
    power, Home buttons as well*/
    private static final String DISABLE_FUNCTION_LAUNCH_ACTION = "android.intent.action.DISABLE_FUNCTION_LAUNCH";
    private void disableFunctionLaunch(boolean state) {
        Intent disablePowerKeyIntent = new Intent(DISABLE_FUNCTION_LAUNCH_ACTION);
        if (state) {
            disablePowerKeyIntent.putExtra("state", true);
        } else {
            disablePowerKeyIntent.putExtra("state", false);
        }
        sendBroadcast(disablePowerKeyIntent);
    }


    /*Printing Activity begins*/
    public void onClickPrint(View v) throws RemoteException {
        EditText myText = findViewById(R.id.editText);
        String text = myText.getText().toString();
        if (printerStatus == 0) {
            Toast.makeText(getApplicationContext(), "Turn printer ON to continue!!!",
                    Toast.LENGTH_SHORT).show();
        } else {
            printText(text);
        }
    }
    public void onClickPrintOpen (MenuItem item) throws RemoteException {
        if (printerStatus == 0) {
            Toast.makeText(getApplicationContext(), "Turn printer ON to continue!!!",
                    Toast.LENGTH_SHORT).show();
        } else {
            int blank_line = 2;
            printBlanks(blank_line);
        }
    }

    /** Tester Sample */
    public void testApiSample(MenuItem item) throws RemoteException {
        printerService.printText("I am working optimally !!!");
        printerService.printText("Next are some 5 blank lines ...");
        printerService.nextLine(5);
        printerService.printText("End of test.");
    }
}
