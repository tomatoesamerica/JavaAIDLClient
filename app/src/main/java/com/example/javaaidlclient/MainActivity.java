package com.example.javaaidlclient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.javaaidlserver.Data;
import com.example.javaaidlserver.Person;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String SERVER_URI = "com.example.javaaidlserver";
    public static final String SERVER_ACTION = "aidl.service";
    public Data dataServer;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            Log.d(TAG, "Service Connected");
            dataServer = Data.Stub.asInterface((IBinder) iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "Service Disconnected");
            dataServer = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
//        initConnection();

        btnSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtA.length() > 0 && edtB.length() > 0 && dataServer != null && appInstalledOrNot(SERVER_URI)) {
                    try {
                        edtResult.setText(String.valueOf(dataServer.Sum(Integer.valueOf(edtA.getText().toString()), Integer.valueOf(edtB.getText().toString()))));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataServer != null && appInstalledOrNot(SERVER_URI)) {
                    try {
                        List<String> strings = dataServer.getData();
                        StringBuffer data = new StringBuffer();
                        for (String s : strings) {
                            data.append(s + "   ");
                        }
                        tvData.setText(data);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        btnPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataServer != null && appInstalledOrNot(SERVER_URI)) {
                    try {
                        List<Person> list = dataServer.getPerson();
                        StringBuffer data = new StringBuffer();
                        for (Person s : list) {
                            data.append(s.age + "   " + s.name + "\n");
                        }
                        tvPerson.setText(data);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    EditText edtA, edtB, edtResult;
    Button btnSum, btnData, btnPerson;
    TextView tvData, tvPerson;

    void initUI() {
        tvData = findViewById(R.id.tv_data);
        edtA = findViewById(R.id.edt_a);
        edtB = findViewById(R.id.edt_b);
        edtResult = findViewById(R.id.edt_result);
        btnSum = findViewById(R.id.btn_sum);
        btnData = findViewById(R.id.btn_getdata);
        btnPerson = findViewById(R.id.btn_person);
        tvPerson = findViewById(R.id.tv_person);
    }

    private void initConnection() {
        if (dataServer == null) {
            Intent intent = new Intent(Data.class.getName());

            /*this is service name which has been declared in the server's manifest file in service's intent-filter*/
            intent.setAction(SERVER_ACTION);

            /*From 5.0 annonymous intent calls are suspended so replacing with server app's package name*/
            intent.setPackage(SERVER_URI);

            // binding to remote service
            bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        initConnection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean appInstalled;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            appInstalled = false;
        }
        return appInstalled;
    }

}