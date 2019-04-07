package com.example.gocon;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.pm.PackageInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.simple.parser.JSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


  //  DatabaseReference databaseReference;
    private Button btnSendSms, btnReadSms;
    private ListView lvSms;
    private EditText etPhoneNum, etMessage;
    private final static int REQUEST_CODE_PERMISSION_SEND_SMS = 123;
    private final static int REQUEST_CODE_PERMISSION_READ_SMS = 456;
    //new change
    private final int REQUEST_CODE_PERMISSION_RECEIVE_SMS = 789;
    //NEW CHANGE
    private final String SERVER = "http://localhost/get_product_details.php";




    ArrayList<String> smsMsgList = new ArrayList<String>();//productlist
    ArrayAdapter arrayAdapter;
    public static MainActivity instance;


    public static MainActivity Instance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  FirebaseApp.initializeApp(getApplicationContext());
        instance = this;
       // databaseReference = FirebaseDatabase.getInstance().getReference();
        etPhoneNum = findViewById(R.id.etPhoneNum);
        etMessage = findViewById(R.id.etMsg);
        btnSendSms = findViewById(R.id.btnSendSms);
        btnReadSms = findViewById(R.id.btnReadSms);
        btnSendSms.setEnabled(false);
        btnReadSms.setEnabled(false);
        lvSms = findViewById(R.id.lv_sms);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, smsMsgList);
        lvSms.setAdapter(arrayAdapter);

//#####################+++++++++++ Checking READ permission #####################+++++++++++
        if (checkPermission(Manifest.permission.READ_SMS)) {
            btnReadSms.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    (Manifest.permission.READ_SMS)}, REQUEST_CODE_PERMISSION_READ_SMS);
        }
//#####################+++++++++++ Checking SEND permission #####################+++++++++++


        if (checkPermission(Manifest.permission.SEND_SMS)) {
            btnSendSms.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    (Manifest.permission.SEND_SMS)}, REQUEST_CODE_PERMISSION_SEND_SMS);
        }
//#####################+++++++++++ Checking RECEIVE permission #####################+++++++++++
        if (!checkPermission(Manifest.permission.RECEIVE_SMS)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    (Manifest.permission.RECEIVE_SMS)}, REQUEST_CODE_PERMISSION_RECEIVE_SMS);
        }
//#####################+++++++++++ Checking permission - END#####################+++++++++++
        btnReadSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver cr = getContentResolver();
                Cursor c = cr.query(Uri.parse("content://sms/inbox"), null, null, null, null);

                /****** get all colomns name*******/
                StringBuffer info = new StringBuffer();
                for (int i = 0; i < c.getColumnCount(); i++) {
                    info.append("COLUMN_NAME:" + c.getColumnName(i) + "\n");
                }
                Toast.makeText(MainActivity.this, info.toString(), Toast.LENGTH_LONG).show();


                int indexBody = c.getColumnIndex("body");
                int indexPhone = c.getColumnIndex("address");
                int indexDate = c.getColumnIndex("date");
                int indexDateSent = c.getColumnIndex("date_sent");

                if (indexBody < 0 || !c.moveToFirst()) return;

                arrayAdapter.clear();
                do {
                  //  String id = databaseReference.push().getKey();
                    String phone = c.getString(indexPhone);

                    String msg = c.getString(indexBody);

                    String str = "SMS from : " + phone + "\n" + msg;
                    String date = c.getString(indexDate);

                    String dateSent = c.getString(indexDateSent);
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date thisDate = new Date(Long.parseLong(date));
                    date = df.format(thisDate);
                    thisDate = new Date(Long.parseLong(dateSent));
                    date = df.format(thisDate);

                    str += "\nDate:" + date;
                    str += "\nDateSent" + dateSent;

                    arrayAdapter.add(str);
                      //  writeNewMsg(msg,phone,date);


                    Toast.makeText(MainActivity.this,"....",Toast.LENGTH_SHORT).show();
                    //send sms to database
                    sendSms(phone,msg,date,dateSent);

                } while (c.moveToNext());
                c.close();

            }
        });



        btnSendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etMessage.getText().toString();
                String phoneNum = etPhoneNum.getText().toString();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNum, null, msg, null, null);
                Toast.makeText(getApplicationContext(), "SENT", Toast.LENGTH_SHORT).show();
                etMessage.setText("");
                etPhoneNum.setText("");
                //TODO recford all sendings sms , whichare send using your app
            }
        });




    }


   /* private void writeNewMsg(String msg,String phone,String date){

        databaseReference.setValue(msg);
        databaseReference.setValue(phone);
        databaseReference.setValue(date);
    }*/

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return checkPermission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btnSendSms.setEnabled(true);
                }
                break;
            case REQUEST_CODE_PERMISSION_READ_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btnReadSms.setEnabled(true);
                }
                break;
        }
    }

    public void sendSms(String Phone, String msg, String date, String dateSent) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = SERVER + "?PHONE=" + Phone;
        url += "?msg=" + Uri.encode(msg);
        url += "?date=" + Uri.encode(date);
        url += "?date_sent=" + Uri.encode(dateSent);

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
           Toast.makeText(MainActivity.this,"Error",Toast.LENGTH_SHORT).show();
             }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

 /*   public void refreshInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;

        arrayAdapter.clear();
        do {
            String str = "SMMS From:" + smsInboxCursor.getString(indexAddress) + "\n";
            str += smsInboxCursor.getString(indexBody);
            arrayAdapter.add(str);
        } while (smsInboxCursor.moveToNext());
    }
*/
    public void updateList(final String smsMsg) {
        arrayAdapter.insert(smsMsg, 0);
        arrayAdapter.notifyDataSetChanged();
    }

}