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

import static android.R.attr.button;
import static es.fempa.pmdm.socket.R.id.ipServer;

public class ConfigServidor extends AppCompatActivity {
    private EditText puerto, nombre;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_ser);

        puerto = (EditText) findViewById(R.id.TextPuerto);
        nombre = (EditText) findViewById(R.id.TextNombre);
        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfigServidor.this, Chat.class);
                intent.putExtra("tipo", "servidor");
                intent.putExtra("puerto",Integer.parseInt(puerto.getText().toString()));
                startActivity(intent);
            }
        });
    }

   }
