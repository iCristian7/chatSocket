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

/**
 * Created by Christian on 20/11/2017.
 */

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

    GetMessagesThread HiloEscucha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_cl);

        dirIP = (EditText) findViewById(R.id.TextDIP);
        Puerto = (EditText) findViewById(R.id.TextPuerto);
        Nombre = (EditText) findViewById(R.id.TextNombre);
        button = (Button)findViewById(R.id.button);

    }

    public void volverInicio(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

    public void startClient(View v)
    {
        String TheIP = dirIP.getText().toString();
        if(TheIP.length()>5)
        {
            button.setEnabled(false);
            dirIP.setEnabled(false);

            (new ClientConnectToServer(TheIP)).start();


        }
    }

    private class ClientConnectToServer extends Thread
    {
        String mIp;
        public ClientConnectToServer(String ip){mIp=ip;}
        public void run()
        {
            //TODO Connect to server
            try {

                socket = new Socket(mIp, puerto);

                try {
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                }catch(Exception e){ e.printStackTrace();}

                ConectionEstablished=true;
                //Iniciamos el hilo para la escucha y procesado de mensajes
                (HiloEscucha=new GetMessagesThread()).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private class GetMessagesThread extends Thread {
        public boolean executing;
        private String line;


        public void run() {
            executing = true;

            while (executing) {
                line = "";
                line = ObtenerCadena();//Obtenemos la cadena del buffer
                if (line != "" && line.length() != 0) {
                }//Comprobamos que esa cadena tenga contenido
            }
        }

        public void setExecuting(boolean execute) {
            executing = execute;
        }


        private String ObtenerCadena() {
            String cadena = "";

            try {
                cadena = dataInputStream.readUTF();//Leemos del datainputStream una cadena UTF
                Log.d("ObtenerCadena", "Cadena reibida: " + cadena);

            } catch (Exception e) {
                e.printStackTrace();
                executing = false;
            }
            return cadena;
        }
    }

}
