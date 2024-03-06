package com.example.se2_einzelbeispiel_rablbauer;

import android.os.Bundle;
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
    TextView sortedHint;
    Socket socket;


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
    public void matrikelnummerServer(){
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                String matNr = matNrInput.getText().toString();
                try {
                    socket = new Socket("se2-submission.aau.at", 20080);// Verbindung zu Netzwerk herstellen

                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out.write(matNr);
                    out.close();

                    String messageFromServer = in.readLine();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            serverResponse.setText(messageFromServer);
                        }
                    });

                    in.close();
                    out.close();
                    socket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
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