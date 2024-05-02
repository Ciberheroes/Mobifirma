package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    // Setup Server information
    protected static String server = "192.168.1.133";
    protected static int port = 7070;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Capturamos el boton de Enviar
        View button = findViewById(R.id.button_send);

        // Llama al listener del boton Enviar
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        // Capturamos los campos de texto
        final EditText camas = (EditText) findViewById(R.id.bedNumber);
        final EditText sillas = (EditText) findViewById(R.id.chairNumber);
        final EditText mesas = (EditText) findViewById(R.id.tableNumber);
        final EditText sillones = (EditText) findViewById(R.id.couchNumber);

        // Default 0
        camas.setText("0");
        sillas.setText("0");
        mesas.setText("0");
        sillones.setText("0");

        // Listener para los campos de texto
        camas.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() > 1 && s.charAt(0) == '0') {
                    camas.setText(s.subSequence(1, s.length()));
                    camas.setSelection(camas.getText().length());
                }
                if (s.toString().isEmpty()) {
                    camas.setText("0");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        sillas.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() > 1 && s.charAt(0) == '0') {
                    sillas.setText(s.subSequence(1, s.length()));
                    sillas.setSelection(sillas.getText().length());
                }
                if (s.toString().isEmpty()) {
                    sillas.setText("0");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        mesas.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() > 1 && s.charAt(0) == '0') {
                    mesas.setText(s.subSequence(1, s.length()));
                    mesas.setSelection(mesas.getText().length());
                }
                if (s.toString().isEmpty()) {
                    mesas.setText("0");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        sillones.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() > 1 && s.charAt(0) == '0') {
                    sillones.setText(s.subSequence(1, s.length()));
                    sillones.setSelection(sillones.getText().length());
                }
                if (s.toString().isEmpty()) {
                    sillones.setText("0");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
    }

    // Creación de un cuadro de dialogo para confirmar pedido
    private void showDialog() throws Resources.NotFoundException {
        EditText camas = (EditText) findViewById(R.id.bedNumber);
        EditText sillas = (EditText) findViewById(R.id.chairNumber);
        EditText mesas = (EditText) findViewById(R.id.tableNumber);
        EditText sillones = (EditText) findViewById(R.id.couchNumber);

        if ((camas.getText().toString().equals("0") && sillas.getText().toString().equals("0") && mesas.getText().toString().equals("0") && sillones.getText().toString().equals("0"))) {
            // Mostramos un mensaje emergente;
            Toast.makeText(getApplicationContext(), "Selecciona al menos un elemento", Toast.LENGTH_SHORT).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Enviar")
                    .setMessage("Se va a proceder al envio")
                    .setIcon(android.R.drawable.ic_dialog_email)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                // Catch ok button and send information
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    // 1. Extraer los datos de la vista
                                    // 2. Firmar los datos

                                    // 3. Enviar los datos

                                    Toast.makeText(MainActivity.this, "Petición enviada correctamente", Toast.LENGTH_SHORT).show();
                                }
                            }

                    )
                            .

                    setNegativeButton(android.R.string.no, null)

                            .

                    show();
        }
    }


}