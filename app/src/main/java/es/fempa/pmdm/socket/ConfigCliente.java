package es.fempa.pmdm.socket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import static es.fempa.pmdm.socket.R.id.ipServer;


public class ConfigCliente extends AppCompatActivity {

    EditText dirIP;
    EditText Puerto;
    EditText Nombre;
    Button button;

    Socket socket;
    ServerSocket serverSocket;
    boolean ConectionEstablished;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    int puerto = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_cl);

        dirIP = (EditText) findViewById(R.id.TextDIP);
        Puerto = (EditText) findViewById(R.id.TextPuerto);
        Nombre = (EditText) findViewById(R.id.TextNombre);
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfigCliente.this, ChatServidor.class);
                intent.putExtra("tipo", "cliente");
                intent.putExtra("puerto",Integer.parseInt(Puerto.getText().toString()));
                intent.putExtra("ip", dirIP.getText().toString());
                startActivity(intent);
            }
        });

    }



}
