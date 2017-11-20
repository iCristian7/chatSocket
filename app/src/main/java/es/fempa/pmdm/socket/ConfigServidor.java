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

/**
 * Created by Christian on 20/11/2017.
 */

public class ConfigServidor extends AppCompatActivity {

    EditText Puerto;
    EditText Nombre;
    Button button;

    Socket socket;
    ServerSocket serverSocket;
    boolean ConectionEstablished;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    int puerto = 0;
    //Hilo para escuchar los mensajes que le lleguen por el socket
    GetMessagesThread HiloEscucha;


    /*Variable para el servidor*/
    WaitingClientThread HiloEspera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_ser);

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

    public void startServer(View v)
    {
        Puerto.setEnabled(false);
        Nombre.setEnabled(false);

        (HiloEspera=new WaitingClientThread()).start();
    }

    private class WaitingClientThread extends Thread
    {
        public void run()
        {

            try
            {
                int puerto = Integer.parseInt(String.valueOf(Puerto));

                serverSocket = new ServerSocket(puerto);

                socket = serverSocket.accept();

                try {

                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                }catch(Exception e){ e.printStackTrace();}

                    ConectionEstablished=true;

                (HiloEscucha=new GetMessagesThread()).start();

                (new EnvioMensajesServidor()).start();
                HiloEspera=null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private class EnvioMensajesServidor extends Thread
    {
        public void run()
        {
            String messages[]={"Bienvenido usuario a mi chat", "¿Estás bien?", "Bueno, pues molt bé, pues adiós"};
            int sleeptime[]={1000, 2000, 2000};
            sendVariousMessages(messages, sleeptime);
            DisconnectSockets();
        }
    }

    private class EnvioMensajesCliente extends Thread
    {
        public void run()
        {
            String messages[]={"Hola servidor", "No mucho, pero no te voy a contar mi vida", "Pues adiós :("};
            int sleeptime[]={1000, 2000, 1000};
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendVariousMessages(messages, sleeptime);
            DisconnectSockets();
        }
    }

    private void DisconnectSockets()
    {
        if(ConectionEstablished)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    button.setEnabled(true);
                    Puerto.setEnabled(true);
                }
            });
            ConectionEstablished = false;

            if (HiloEscucha != null)
            {
                HiloEscucha.setExecuting(false);
                HiloEscucha.interrupt();
                HiloEspera = null;
            }

            try {
                if (dataInputStream != null)
                    dataInputStream.close();
            } catch (Exception e) {
            } finally {
                dataInputStream = null;
                try {
                    if (dataOutputStream != null)
                        dataOutputStream.close();
                } catch (Exception e) {
                } finally {
                    dataOutputStream = null;
                    try {
                        if (socket != null)
                            socket.close();
                    } catch (Exception e) {
                    } finally {
                        socket = null;
                    }
                }
            }
        }
    }

    private void sendVariousMessages(String[] msgs, int[] time)
    {
        if(msgs!=null && time!=null && msgs.length==time.length)
            for(int i=0; i<msgs.length; i++)
            {
                sendMessage(msgs[i]);
                try {
                    Thread.sleep(time[i]);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
    }

    private void sendMessage(String txt)
    {
        new SendMessageSocketThread(txt).start();
    }

    private class SendMessageSocketThread extends Thread
    {
        private String msg;

        SendMessageSocketThread(String message)
        {
            msg=message;
        }

        @Override
        public void run()
        {
            try
            {
                dataOutputStream.writeUTF(msg);//Enviamos el mensaje
                //dataOutputStream.close();
            }catch (IOException e)
            {
                e.printStackTrace();
                //message += "¡Algo fue mal! " + e.toString() + "\n";
            }
        }
    }

    //Aqui obtenemos la IP de nuestro terminal
    private String getIpAddress()
    {
        String ip = "";
        try
        {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements())
                {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress())
                    {
                        ip += "IP de Servidor: " + inetAddress.getHostAddress() + "\n";
                    }

                }
            }
        } catch (SocketException e)
        {
            e.printStackTrace();
            ip += "¡Algo fue mal! " + e.toString() + "\n";
        }

        return ip;
    }

    private class GetMessagesThread extends Thread
    {
        public boolean executing;
        private String line;


        public void run()
        {
            executing=true;

            while(executing)
            {
                line="";
                line=ObtenerCadena();//Obtenemos la cadena del buffer
                if(line!="" && line.length()!=0){}//Comprobamos que esa cadena tenga contenido
            }
        }

        public void setExecuting(boolean execute){executing=execute;}


        private String ObtenerCadena()
        {
            String cadena="";

            try {
                cadena=dataInputStream.readUTF();//Leemos del datainputStream una cadena UTF
                Log.d("ObtenerCadena", "Cadena reibida: "+cadena);

            }catch(Exception e)
            {
                e.printStackTrace();
                executing=false;
            }
            return cadena;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisconnectSockets();
    }
}
