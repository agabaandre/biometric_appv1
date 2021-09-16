package ug.app.ihrisbiometric.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import ug.app.ihrisbiometric.R;
import ug.app.ihrisbiometric.extra.DatabaseHandler;
import ug.app.ihrisbiometric.extra.SessionHandler;
import ug.app.ihrisbiometric.utility.FingerLib;

public class MainActivity extends AppCompatActivity {

    private static FingerLib m_szHost;
    String RFID_POWER_PATH = "/proc/gpiocontrol/set_id";

    DatabaseHandler database;
    SessionHandler session;

    public ProgressDialog prgDialog;
    TextView m_txtStatus, m_textHeader;
    ImageView m_fingerprint, m_fingerprint2;
    FloatingActionButton fab1, fab2;
    Handler openDeviceHandler;

    public String facilityId;
    public String facilityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("IHRIS BIOMETRIC");

        TextView facilityName = findViewById(R.id.facilityName);


        database = new DatabaseHandler(getApplicationContext());
        session = new SessionHandler(getApplicationContext());

        session.checkLogin();

        HashMap<String, String> user = session.getUserDetails();

        facilityId = user.get(SessionHandler.KEY_FACILITYID);
//        facilityName = user.get(SessionHandler.KEY_FACILITY);

        facilityName.setText(user.get(SessionHandler.KEY_FACILITY));

        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Syncing local and remote databases. Please wait...");
        prgDialog.setCancelable(true);

        // Set Keep Screen On
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG);

        InitWidget();
        SetInitialState();

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                m_szHost.CloseDevice();
                SetInitialState();

                m_textHeader.setText("Enroll Staff");

                openDeviceHandler.postDelayed(openDeviceRunnable, 1000);
                fab1.setVisibility(View.GONE);
                fab2.setVisibility(View.VISIBLE);
                m_fingerprint.setVisibility(View.GONE);
                m_fingerprint2.setVisibility(View.VISIBLE);
            }
        });

        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_szHost.CloseDevice();

                SetInitialState();

                m_textHeader.setText("Clock In/Out");
                openDeviceHandler.postDelayed(openDeviceRunnable, 1000);
                fab1.setVisibility(View.VISIBLE);
                fab2.setVisibility(View.GONE);
                m_fingerprint.setVisibility(View.VISIBLE);
                m_fingerprint2.setVisibility(View.GONE);
            }
        });

        m_fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockUser();
            }
        });

        m_fingerprint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enrollUser();
            }
        });

        openDeviceHandler = new Handler();

        m_txtStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = m_txtStatus.getText().toString();
                if(text.contains("Open Device Success")) {
                    Toast.makeText(MainActivity.this, "Device successfully openned", Toast.LENGTH_SHORT).show();
                } else if(text.contains("All Templates are Empty")) {
                    Toast.makeText(MainActivity.this, "No fingerprints currently registered", Toast.LENGTH_SHORT).show();
                } else if(text.contains("Result : Success") && text.contains("Template No :")) {
                    Toast.makeText(MainActivity.this, "Fingerprint registered", Toast.LENGTH_SHORT).show();
                } else if(text.contains("Fail to receive response")) {
//                    Toast.makeText(MainActivity.this, "App is unable to connect to Fingerprint scanner", Toast.LENGTH_SHORT).show();
                    reload();
                } else if(text.contains("Can not connect to device")) {
//                    Toast.makeText(MainActivity.this, "Can not connect", Toast.LENGTH_SHORT).show();
                    reload();
                }
            }
        });
    }

    public void reload() {
        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        } else {
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_view_enrolled) {
            Intent i = new Intent(MainActivity.this, EnrolledActivity.class);
            startActivity(i);
        } else if(id == R.id.action_signout) {
            session.logoutUser();
        } else if(id == R.id.action_sync_clock) {
//            syncClockSQLiteMySQLDB();
            //Check if there is enroll data that requires sync
            String msg = database.getEnrollSyncStatus();
            if(msg.contains("Enroll records in sync")) {
                syncClockSQLiteMySQLDB();
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("SYNC ENROLL RECORDS");
                alertDialog.setMessage("Please sync all enroll records before syncing Time log records to server");
                alertDialog.setIcon(R.drawable.sync_alert);
                alertDialog.show();
            }
        } else if(id == R.id.action_sync_enroll) {
            syncEnrollSQLiteMySQLDB();
        } else if(id == R.id.action_wipe_device) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_wipe, null);
            final EditText mDevicepass = (EditText) mView.findViewById(R.id.et_devicepass);
            Button wipe = (Button) mView.findViewById(R.id.btnWipe);

            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();

            wipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mDevicepass.getText().toString().isEmpty()) {
                        String passcode = mDevicepass.getText().toString();
                        if(Integer.parseInt(passcode) == 15102018) {
                            m_szHost.Run_CmdDeleteAll();
                            database.dropDatabase();
                            m_txtStatus.setText("All records cleared");
//                            Toast.makeText(MainActivity.this, "Everything has been cleared", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            m_txtStatus.setText("Invalid passcode");
//                            Toast.makeText(MainActivity.this, "Invalid passcode", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void enrollUser() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_enroll, null);
        final EditText mIHRISPID = (EditText) mView.findViewById(R.id.et_ihrispid);
        Button enroll = (Button) mView.findViewById(R.id.btnEnroll);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIHRISPID.getText().toString().isEmpty()) {

                    final int userId = Integer.parseInt(mIHRISPID.getText().toString());

                    m_szHost.Run_CmdGetUserCount();

                    m_txtStatus.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String text = m_txtStatus.getText().toString();
                            if(text.contains("Success") && text.contains("Enroll Count")) {

                                String[] parts = text.split("");
                                String userCount = parts[parts.length - 1].toString();

                                int fingerprint = Integer.parseInt(userCount) + 1;

                                m_szHost.Run_CmdEnroll(fingerprint);

                                HashMap<String, String> queryValues = new HashMap<String, String>();
                                queryValues.put("fingerprint", String.valueOf(fingerprint));
                                queryValues.put("ihrispid", String.valueOf(userId));
                                queryValues.put("facilityId", facilityId);
                                database.insertEnrollFingerprint(queryValues);
                            }
                        }
                    });

                    dialog.dismiss();
                }
            }
        });

    }

    private void clockUser() {
        m_szHost.Run_CmdIdentify();
        m_txtStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = m_txtStatus.getText().toString();
                if (text.contains("Result") && text.contains("Success") && text.contains("Template No")) {
                    String[] parts = text.split("");
                    String fingerprint = parts[parts.length - 1].toString();
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                    HashMap<String, String> queryValues = new HashMap<String, String>();
                    queryValues.put("fingerprint", fingerprint);
                    queryValues.put("timestamp", String.valueOf(timestamp));
                    queryValues.put("facilityId", facilityId);
                    database.insertClockFingerprint(queryValues);
                }
            }
        });
    }

    private void InitWidget() {
        m_fingerprint = (ImageView) findViewById(R.id.fingerprint);
        m_fingerprint2 = (ImageView) findViewById(R.id.fingerprint2);

        m_txtStatus = (TextView) findViewById(R.id.txtStatus);

        m_textHeader = (TextView) findViewById(R.id.textHeader);

        if (m_szHost == null) {
            m_szHost = new FingerLib(this, m_txtStatus);
        } else {
            m_szHost.SZOEMHost_Lib_Init(this, m_txtStatus);
        }
    }

    private void SetInitialState() {
        m_txtStatus.setText("Openning device...");
    }

    public void PowerControl(int state) {
        if (state == 0 || state == 1) {
            try {
                FileWriter localFileWriterOn = new FileWriter(new File(RFID_POWER_PATH));
                if (state == 1) {
                    localFileWriterOn.write("1");
                } else if (state == 0) {
                    localFileWriterOn.write("0");
                }
                localFileWriterOn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Runnable openDeviceRunnable = new Runnable() {
        @Override
        public void run() { m_szHost.OpenDevice("/dev/ttyMT3", 115200);
        }
    };

    public void syncClockSQLiteMySQLDB(){
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        ArrayList<HashMap<String, String>> userList =  database.getAllClocks();
        if(userList.size()!=0){
            if(database.clockSyncCount() != 0){
                prgDialog.show();
                params.put("clockJSON", database.composeClockJSONfromSQLite());
                client.post("https://hris2.health.go.ug/attendance/biometric/index.php/api/clock",params ,new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        prgDialog.dismiss();
                        try {
                            JSONArray arr = new JSONArray(new String(responseBody));

                            for(int i=0; i<arr.length();i++){
                                JSONObject obj = (JSONObject)arr.get(i);

                                String status = (String) obj.get("status").toString();
                                if(status.equals("yes")) {
                                    database.updateClockSyncStatus(obj.get("fingerprint").toString(), obj.get("status").toString());
                                }

                            }
                            m_txtStatus.setText("Time log synced to server");
                        } catch (JSONException e) {

                            Toast.makeText(getApplicationContext(), "Invalid response received from server", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        prgDialog.dismiss();
                        if(statusCode == 404){
                            Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                        }else if(statusCode == 500){
                            Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(), "Time log in sync", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "No time log that requires syncing", Toast.LENGTH_LONG).show();
        }
    }

    public void syncEnrollSQLiteMySQLDB(){
        //Create AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        ArrayList<HashMap<String, String>> userList =  database.getAllEnroll();
        if(userList.size()!=0){
            if(database.enrollSyncCount() != 0){
                prgDialog.show();
                params.put("enrollJSON", database.composeEnrollSONfromSQLite());

                client.post("http://hris.health.go.ug/hrattendance/biometric/index.php/api/enroll",params ,new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        prgDialog.dismiss();

                        try {
                            JSONArray arr = new JSONArray(new String(responseBody));

                            for(int i=0; i<arr.length();i++){
                                JSONObject obj = (JSONObject)arr.get(i);

                                String status = (String) obj.get("status");

                                if(status.equals("yes")) {
                                    database.updateEnrollSyncStatus(obj.get("fingerprint").toString(), obj.get("status").toString());
                                }

                            }

                            m_txtStatus.setText("Enroll data synced to server");
                        } catch (JSONException e) {

                            Toast.makeText(getApplicationContext(), "Invalid response received from server", Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        prgDialog.dismiss();
                        //Toast.makeText(MainActivity.this, "Failed to Clock User", Toast.LENGTH_SHORT).show();
                        prgDialog.hide();
                        if(statusCode == 404){
                            Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                        }else if(statusCode == 500){

                            Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Unable to connect to server. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(), "Enroll data in sync", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "No data that requires Sync", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PowerControl(1);
    }

    @Override
    public void onPause() {
        super.onPause();
        PowerControl(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        openDeviceHandler.postDelayed(openDeviceRunnable, 3000);
    }

    @Override
    public void onStop() {
        super.onStop();
        PowerControl(0);
        openDeviceHandler.removeCallbacks(openDeviceRunnable);

    }
}
