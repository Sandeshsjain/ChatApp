package com.example.socketexampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
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

public class MainActivity extends AppCompatActivity {
    EditText messgae_Et;
    TextView messagefromuser;
    Button sendData;
    public static String ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messagefromuser = findViewById(R.id.ipaddressET);
        messgae_Et = findViewById(R.id.messgae_Et);
        sendData = findViewById(R.id.send_data_button);
        Intent intent = getIntent();
        ip = intent.getStringExtra("IP");
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
                            messagefromuser.setText("User:- "+message);
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
}