package com.example.se2_einzelbeispiel_rablbauer;

import android.os.Bundle;
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

    // Initialisierung
    EditText matNrInput;
    TextView serverResponse;
    TextView ausgabeBerechnung;
    TextView serverResponseHint;
    TextView sortedHint;

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

        // Binding der Views
        matNrInput = findViewById(R.id.inputMatrikelnummer);
        serverResponse = findViewById(R.id.textViewSever);
        ausgabeBerechnung = findViewById(R.id.textViewBerechnung);
        sortedHint = findViewById(R.id.textViewSorted);
        serverResponseHint = findViewById(R.id.textViewServerResponse);

        // Binding der Buttons
        Button btnAbschicken = findViewById(R.id.buttonAbschicken);
        btnAbschicken.setOnClickListener(v -> {
            matrikelnummerServer();         // wenn Button geklickt wird, wird die Methode matrikelnummerServer() aufgerufen
        });

        Button btnBerechne = findViewById(R.id.buttonBerechne);
        btnBerechne.setOnClickListener(v -> {
            sortMatrikelnummer();           // wenn Button geklickt wird, wird die Methode sortMatrikelnummer aufgerufen
        });
    }

    // Methode zur Kommunikation mit dem Server
    public void matrikelnummerServer() {
        String outputHeadline = "Server Antwort:";
        String matNr = matNrInput.getText().toString();     // Input vom TextInputEditText View einlesen und in String konvertieren für spätere Übergabe
        String host = "se2-submission.aau.at";              // Server host
        int port = 20080;                                   // Port des Servers
        new Thread(() -> {
            try {
                Socket socket = new Socket(host, port);                                                         // Verbindung zu Netzwerk herstellen
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));      // Output, Nachricht an Server im Bytestream
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));         // Input, Nachricht von Server im Bytestream

                out.write(matNr);       // Übergeben der Matrikelnummer
                out.newLine();          // Leerzeichen, um ende der Übergabe zu markieren
                out.flush();            // Leeren

                String messageFromServer = in.readLine();       // Nachricht von Server empfangen

                runOnUiThread(() -> {
                    serverResponseHint.setText(outputHeadline);      // Textviews anpassen
                    serverResponse.setText(messageFromServer);          // Textview ändert sich zu Nachricht des Servers
                });

                out.close();        // OutputStream schließen
                in.close();         // InputStream schließen
                socket.close();     // Socket schließen

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Methode zur individuellen Aufgabe; Matrikelnummer sortieren, zuerst gerade Zahlen reihen, danach ungerade Zahlen
    public void sortMatrikelnummer() {
        String outputHeadline = "Sortierte Matrikelnummer:";    // Output Überschrift
        String matNr = matNrInput.getText().toString();         // Input von View
        char[] ziffern = matNr.toCharArray();                   // Input in Chararray speichern

        StringBuilder geradeZahlen = new StringBuilder();       // Stringbuilder um Teilstrings zu erstellen
        StringBuilder ungeradeZahlen = new StringBuilder();

        for (char ziffer : ziffern) {
            int num = Character.getNumericValue(ziffer);        // Char Ziffer in Integer Ziffer umwandeln
            if (num % 2 == 0) {                                 // überprüfen ob gerade
                geradeZahlen.append(ziffer);                    // hinzufügen zu jeweiligen Teilstring
            } else {
                ungeradeZahlen.append(ziffer);
            }
        }

        char[] sortedGerade = geradeZahlen.toString().toCharArray();
        Arrays.sort(sortedGerade);
        char[] sortedUngerade = ungeradeZahlen.toString().toCharArray();
        Arrays.sort(sortedUngerade);

        StringBuilder sortedMatrikelnummer = new StringBuilder();       // Endstring festlegen
        sortedMatrikelnummer.append(sortedGerade);                      // Teilstrings anfügen
        sortedMatrikelnummer.append(sortedUngerade);

        ausgabeBerechnung.setText(sortedMatrikelnummer);                // Textview anpassen
        sortedHint.setText(outputHeadline);
    }
}