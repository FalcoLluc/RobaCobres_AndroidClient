package com.example.robacobres_androidclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.robacobres_androidclient.adapters.MyChatIndividualAdapter;
import com.example.robacobres_androidclient.callbacks.PrivateCallback;
import com.example.robacobres_androidclient.models.ChatIndividual;
import com.example.robacobres_androidclient.models.User;
import com.example.robacobres_androidclient.services.ServiceBBDD;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PrivateMessagesActivity extends AppCompatActivity implements PrivateCallback {
    List<String> names;
    ServiceBBDD serviceREST;
    private RecyclerView recyclerView;
    private MyChatIndividualAdapter adapter1;
    List<ChatIndividual> chats;
    String userName;
    String selectedOption;
    private Button btnSend;
    private TextView textEnviar;
    private Button btnIniciar;
    private TextView personaPerNouChat;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_private_messages);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textEnviar=findViewById(R.id.textAEnviar);
        btnSend=findViewById(R.id.btnSend);
        btnIniciar=findViewById(R.id.btnIniciar);
        personaPerNouChat=findViewById(R.id.nouChat);
        spinner = findViewById(R.id.spinner);
        this.names = (List<String>) getIntent().getSerializableExtra("names");
        names.add(0,"Select here who you want to chat with");
        this.userName = getIntent().getStringExtra("userName");
        serviceREST = ServiceBBDD.getInstance(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recyclerView = findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chats = new ArrayList<>();
        adapter1 = new MyChatIndividualAdapter(this,  null,chats);
        recyclerView.setAdapter(adapter1);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOption = parent.getItemAtPosition(position).toString();
                if(!selectedOption.equals("Select here who you want to chat with")){
                    Toast.makeText(PrivateMessagesActivity.this, "Selected: " + selectedOption, Toast.LENGTH_SHORT).show();
                    getMessages(selectedOption);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Quan no hi ha cap selecció
            }
        });
    }
    public void getMessages(String name){
        serviceREST.getPrivateMessagesWith(this, name);
    }
    @Override
    public void onPrivateCallbackNames(List<User> lista){
        names.clear();
        for(User u:lista) names.add(u.getName());
        names.add(0,"Select here who you want to chat with");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOption = parent.getItemAtPosition(position).toString();
                if(!selectedOption.equals("Select here who you want to chat with")){
                    Toast.makeText(PrivateMessagesActivity.this, "Selected: " + selectedOption, Toast.LENGTH_SHORT).show();
                    getMessages(selectedOption);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Quan no hi ha cap selecció
            }
        });
    }
    @Override
    public void onMessage(String errorMessage){
        Toast.makeText(PrivateMessagesActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(){
        Toast.makeText(PrivateMessagesActivity.this,"ERROR", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrivateCallbackMessages(List<ChatIndividual> lista){
        adapter1.setPrivateMessages(lista);
        adapter1.notifyDataSetChanged();
        textEnviar.setText("");
    }

    public void onClickSend1(View view) {
        if(!selectedOption.equals("Select here who you want to chat with")) serviceREST.postPrivateMessage(this, new ChatIndividual(userName,selectedOption,textEnviar.getText().toString()));
        else Toast.makeText(PrivateMessagesActivity.this, "Select a user to chat with", Toast.LENGTH_SHORT).show();
    }

    public void onClickBotonRetroceder(View view){
        this.finish();
    }

    public void onClickIniciar(View view) {
        serviceREST.postPrivateMessage(this, new ChatIndividual(userName,personaPerNouChat.getText().toString().trim(),"Bon dia, vull començar a chatejar"));
        personaPerNouChat.setText("");
        serviceREST.getPrivateNames(this);
    }
}
