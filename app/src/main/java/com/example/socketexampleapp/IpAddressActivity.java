package com.example.socketexampleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IpAddressActivity extends AppCompatActivity {
    EditText edit_text_ip,edit_text_name;
    Button button_start_chat;
    UserAdapter userAdapter;
    public static MyAppDatabase myAppDatabase;
    List<User> userList;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_address);
        myAppDatabase = Room.databaseBuilder(getApplicationContext(),MyAppDatabase.class,"userDb").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        edit_text_ip = findViewById(R.id.edit_text_ip);
        recyclerView = findViewById(R.id.recycler_view);
        button_start_chat = findViewById(R.id.button_start_chat);
        edit_text_name = findViewById(R.id.edit_text_name);
        userList = new ArrayList<>();
        getData();
        button_start_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChat();
                userAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getData() {
        userList = myAppDatabase.myDeo().getUsers();
        userAdapter = new UserAdapter(this,userList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(userAdapter);
    }

    private void startChat() {
        if(edit_text_ip.getText().toString().trim() == null){
            edit_text_ip.setError("Please Enter Ip address to continue");
        }
        else if(edit_text_name.getText().toString().trim() == null){
            edit_text_name.setError("Please enter name first");
        }
        else{
            User user = new User();
            String ip = edit_text_ip.getText().toString().trim();
            String name = edit_text_name.getText().toString().trim();
            user.setIpAddress(ip);
            user.setName(name);
            edit_text_ip.setText("");
            edit_text_name.setText("");
            myAppDatabase.myDeo().addUser(user);
            Toast.makeText(getApplicationContext(),"Ip Added Successfully",Toast.LENGTH_SHORT).show();
            /*Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("IP",ip);
            startActivity(intent);*/
        }
    }

}