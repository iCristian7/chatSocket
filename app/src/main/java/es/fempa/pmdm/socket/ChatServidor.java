package es.fempa.pmdm.socket;

import android.media.Image;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Set;

import android.content.Intent;

public class ChatServidor extends AppCompatActivity
{
    TextView myTV;
    //Button btncliente, btnservidor;
    EditText ipServer;
    private ImageView send;
    EditText messageArea;
    LinearLayout layout;
    ScrollView scrollView;
    RelativeLayout layout_2;

    Socket socket;
    ServerSocket serverSocket;
    boolean ConectionEstablished;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    int mPuerto=1048;
    //Hilo para escuchar los mensajes que le lleguen por el socket
    GetMessagesThread HiloEscucha;

    /*Variable para el servidor*/
    WaitingClientThread HiloEspera;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ipServer=(EditText) findViewById(R.id.ipServer);

        myTV=(TextView) findViewById(R.id.datos);
        Intent data = getIntent();
        mPuerto = data.getIntExtra("puerto",-1);
        String tipo = data.getStringExtra("tipo");
        if(tipo.equals("cliente")){
            startClient();
        }else {
            startServer();
        }

        send = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) findViewById(R.id.layout2);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // en este metodo es donde se escribe el mensaje al alinearlo
                //no tocar xd
                String messageText = messageArea.getText().toString();
                TextView textView = new TextView(ChatServidor.this);
                textView.setTextColor(getResources().getColor(R.color.negro));

                textView.setText(messageText);

                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp2.weight = 1.0f;
                lp2.setMargins(0,25,5,0);

                //alineacion de texto
                lp2.gravity = Gravity.LEFT;
                textView.setBackgroundResource(R.drawable.bubble2_whatsapp);


                lp2.gravity = Gravity.RIGHT;
                textView.setBackgroundResource(R.drawable.bubble_whatsapp);

                textView.setLayoutParams(lp2);
                layout.addView(textView);
                //scrollView.fullScroll(View.FOCUS_DOWN);
                messageArea.setText("");
                sendMessage(messageText);
            }
        });
    }

    public void startServer()
    {
        //ipServer.setEnabled(false);

        SetText("\nComenzamos Servidor!");
        (HiloEspera=new WaitingClientThread()).start();
    }

    public void startClient()
    {
        String TheIP="192.168.100.4";
        if(TheIP.length()>5)
        {
            //ipServer.setEnabled(false);

            (new ClientConnectToServer(TheIP)).start();

            SetText("\nComenzamos Cliente!");
            AppenText("\nNos intentamos conectar al servidor: "+TheIP);
        }
    }

    public void AppenText(String text)
    {
        runOnUiThread(new appendUITextView(text+"\n"));
    }

    public void SetText(String text)
    {
        runOnUiThread(new setUITextView(text));
    }

    private class WaitingClientThread extends Thread
    {
        public void run()
        {
            SetText("Esperando Usuario...");
            try
            {
                //Abrimos el socket
                serverSocket = new ServerSocket(mPuerto);

                //Mostramos un mensaje para indicar que estamos esperando en la direccion ip y el puerto...
                AppenText("Creado el servidor\n Dirección: "+getIpAddress()+" Puerto: "+serverSocket.getLocalPort());

                //Creamos un socket que esta a la espera de una conexion de cliente
                socket = serverSocket.accept();

                //Una vez hay conexion con un cliente, creamos los streams de salida/entrada
                try {
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                }catch(Exception e){ e.printStackTrace();}

                ConectionEstablished=true;

                //Iniciamos el hilo para la escucha y procesado de mensajes
                (HiloEscucha=new GetMessagesThread()).start();

                //Enviamos mensajes desde el servidor.
                //(new EnvioMensajesServidor()).start();
                HiloEspera=null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
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
                SetText("Conectando con el servidor: " + mIp + ":" + mPuerto + "...\n\n");//Mostramos por la interfaz que nos hemos conectado al servidor} catch (IOException e) {

                socket = new Socket(mIp, mPuerto);//Creamos el socket

                try {
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                }catch(Exception e){ e.printStackTrace();}

                ConectionEstablished=true;
                //Iniciamos el hilo para la escucha y procesado de mensajes
                (HiloEscucha=new GetMessagesThread()).start();

                //new EnvioMensajesCliente().start();

            } catch (Exception e) {
                e.printStackTrace();
                AppenText("Error: " + e.getMessage());
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
                    ipServer.setEnabled(true);
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
            try {
                dataOutputStream.writeUTF(msg);//Enviamos el mensaje
            } catch (IOException e) {
                e.printStackTrace();
            }
            //dataOutputStream.close();
            //AppenText("Enviado: "+msg);

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
                if(line!="" && line.length()!=0) {//Comprobamos que esa cadena tenga contenido
                    //aqui es donde hay q alinea el texto q recibe(a la derecha, como en el metodo de arriba)
                    //habra que distinguir donde se escribe dependiendo de la pantalla si es server o cliente no?
                    //no
                    //por q aqui solo salen los mensajes recibidos, es decir estos van siempre a la izquierda
                    //ok, sabes lo q tienes q hacer no?
                    //TODO alinear mensajes a la derecha
                    //TODO configurar en las Clases ConfigCliente y Config Servidor que la ip y el puerto se recogen del layout
                    //TODO y se envian a la clase ChatServidor
                    //esto es lo basico si sacas esto aprobamos seguro leete el pdf a ver como lo ves
                    //solo faltaria eso de q al conectarse el cliente sepa si existe el servidor ok?
                    //pero centrate en estos TODO ok-
                    AppenText("Recibido: " + line);//Procesamos la cadena recibida
                }
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

    protected class setUITextView implements Runnable
    {
        private String text;
        public setUITextView(String text){this.text=text;}
        public void run(){myTV.setText(text);}
    }

    protected class appendUITextView implements Runnable
    {
        private String text;
        public appendUITextView(String text){this.text=text;}
        public void run(){myTV.append(text);}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisconnectSockets();
    }

}

