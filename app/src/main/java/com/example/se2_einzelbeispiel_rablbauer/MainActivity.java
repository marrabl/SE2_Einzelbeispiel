package com.example.se2_einzelbeispiel_rablbauer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    EditText matNrInput;
    TextView serverResponse;
    TextView ausgabeBerechnung;
    TextView serverResponseHint;
    TextView sortedHint;
    //Socket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        matNrInput = findViewById(R.id.inputMatrikelnummer);
        serverResponse = findViewById(R.id.textViewSever);
        ausgabeBerechnung = findViewById(R.id.textViewBerechnung);
        sortedHint = findViewById(R.id.textViewSorted);
        serverResponseHint = findViewById(R.id.textViewServerResponse);

        Button btnAbschicken = findViewById(R.id.buttonAbschicken);
        btnAbschicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matrikelnummerServer();
            }
        });

        Button btnBerechne = findViewById(R.id.buttonBerechne);
        btnBerechne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortMatrikelnummer();
            }
        });
    }

    public void matrikelnummerServer() {
        String matNr = matNrInput.getText().toString();
        String host = "se2-submission.aau.at";
        int port = 20080;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(host, port);            // Verbindung zu Netzwerk herstellen
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.write(matNr);
                    out.newLine();
                    out.flush();

                    String messageFromServer = in.readLine();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            serverResponseHint.setText("Server Antwort:");
                            serverResponse.setText(messageFromServer);
                        }
                    });
                    out.close();
                    in.close();
                    socket.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("MainActivity", "Fehler beim Verbinden mit dem Server: " + e.getMessage());
                }
            }
        }).start();
    }

    public void sortMatrikelnummer() {

        String matNr = matNrInput.getText().toString();

        char[] ziffern = matNr.toCharArray();

        StringBuilder geradeZahlen = new StringBuilder();
        StringBuilder ungeradeZahlen = new StringBuilder();

        for (char ziffer : ziffern) {
            int num = Character.getNumericValue(ziffer);
            if (num % 2 == 0) {
                geradeZahlen.append(ziffer);
            } else {
                ungeradeZahlen.append(ziffer);
            }
        }

        char[] sortedGerade = geradeZahlen.toString().toCharArray();
        Arrays.sort(sortedGerade);
        char[] sortedUngerade = ungeradeZahlen.toString().toCharArray();
        Arrays.sort(sortedUngerade);

        StringBuilder sortedMatrikelnummer = new StringBuilder();
        sortedMatrikelnummer.append(sortedGerade);
        sortedMatrikelnummer.append(sortedUngerade);

        ausgabeBerechnung.setText(sortedMatrikelnummer);
        sortedHint.setText("Sortierte Matrikelnummer:");

    }
}