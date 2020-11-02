package com.example.socketexampleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    EditText messgae_Et;
    TextView messagefromuser;
    Button sendData;
    public static String ip;
    public static String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messagefromuser = findViewById(R.id.ipaddressET);
        messgae_Et = findViewById(R.id.messgae_Et);
        sendData = findViewById(R.id.send_data_button);
        Intent intent = getIntent();
        ip = intent.getStringExtra("IP");
        name = intent.getStringExtra("name");
        Thread thread = new Thread(new Myserver());
        thread.start();
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackgroundTask backgroundTask = new BackgroundTask();
                backgroundTask.execute(ip,messgae_Et.getText().toString().trim());
                Toast.makeText(getApplicationContext(),"Message sent",Toast.LENGTH_SHORT).show();
                messgae_Et.setText("");
            }
        });
    }
    class Myserver implements Runnable{
        ServerSocket serverSocket;
        Socket mysocket;
        DataInputStream dataInputStream;
        Handler handler = new Handler();
        String message;
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(9700);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Waiting for client",Toast.LENGTH_SHORT).show();
                    }
                });
                while(true){
                    mysocket = serverSocket.accept();
                    dataInputStream = new DataInputStream(mysocket.getInputStream());
                    message = dataInputStream.readUTF();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showNotification(message);
                            messagefromuser.setText(name+" : "+message);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class BackgroundTask extends AsyncTask<String,Void,String>{
        Socket socket;
        DataOutputStream dataOutputStream;
        String ip,message;
        @Override
        protected String doInBackground(String... strings) {
            ip = strings[0];
            message = strings[1];
            try {
                socket = new Socket(ip,9700);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF(message);
                dataOutputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private void showNotification(String address) {
        int notificationId = new Random().nextInt(100);
        String channelId = "notification_channel_1";
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),channelId
        );
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentTitle("Message from user:");
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setContentText(address);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(notificationManager != null && notificationManager.getNotificationChannel(channelId)==null){
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Notification Channel 1",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationChannel.setDescription("this notification channel is used to notify user.");
                notificationChannel.enableVibration(true);
                notificationChannel.enableLights(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        Notification notification = builder.build();
        if(notificationManager != null){
            notificationManager.notify(notificationId,notification);
        }
    }
}