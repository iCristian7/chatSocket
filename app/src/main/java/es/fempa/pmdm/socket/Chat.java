package es.fempa.pmdm.socket;

import android.app.Activity;
import android.os.Handler;
import android.os.StrictMode;
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
import android.content.Intent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class Chat extends Activity {
    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    RelativeLayout layout_2;
    Thread clientThread = null;

    private String response="";

    private TextView text;
    private int SERVERPORT;
    private String SERVER_IP;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    Handler updateConversationHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) findViewById(R.id.layout2);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        Intent data = getIntent();
        String tipo = data.getStringExtra("tipo");
        SERVERPORT = data.getIntExtra("puerto", -1);
        SERVER_IP = data.getStringExtra("ip");
        text = (TextView) findViewById(R.id.datos);



        this.clientThread = new Thread(new Chat.ClientThread());
        this.clientThread.start();
    }
/*
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                TextView textView = new TextView(Chat.this);
                textView.setTextColor(getResources().getColor(R.color.negro));

                textView.setText(messageText);

                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp2.weight = 1.0f;
                lp2.setMargins(0,25,5,0);


                lp2.gravity = Gravity.LEFT;
                textView.setBackgroundResource(R.drawable.bubble2_whatsapp);


                lp2.gravity = Gravity.RIGHT;
                textView.setBackgroundResource(R.drawable.bubble_whatsapp);

                textView.setLayoutParams(lp2);
                layout.addView(textView);
                scrollView.fullScroll(View.FOCUS_DOWN);
                messageArea.setText("");

            }
        });*/


                public void onClick(View view) {
                    String messageText = messageArea.getText().toString();

                    TextView textView = new TextView(Chat.this);
                    textView.setTextColor(getResources().getColor(R.color.negro));
                    textView.setPadding(25,18,20,0);
                    textView.setText(messageText);

                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp2.weight = 1.0f;
                    lp2.setMargins(0,25,5,0);




                    lp2.gravity = Gravity.RIGHT;
                    textView.setBackgroundResource(R.drawable.burbuja_verde);

                    textView.setLayoutParams(lp2);
                    layout.addView(textView);
                    //scrollView.fullScroll(View.FOCUS_DOWN);
                    messageArea.setText("");
                    try {
                        PrintWriter out = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())),
                                true);
                        out.println(messageText);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

        class ClientThread implements Runnable {

            @Override
            public void run() {
                try{
                    socket = new Socket(SERVER_IP , SERVERPORT);


                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }










}
