package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyPair;
import java.security.Signature;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    // Setup Server information
    //protected static String server = "192.168.1.139";
    protected static String server = "10.0.2.2";
    protected static int port = 3343;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        copySSLFile();
        System.setProperty("javax.net.ssl.trustStore", this.getFilesDir().getAbsolutePath());
        System.setProperty("javax.net.ssl.trustStorePassword", "ciberheroes");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.textView);
        String text = "Petición de material";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);

        // Capturamos el boton de Enviar
        View button = findViewById(R.id.button_send);

        // Llama al listener del boton Enviar
        button.setOnClickListener(view -> showDialog());

        // Capturamos los campos de texto
        final EditText camas =  findViewById(R.id.bedNumber);
        final EditText sillas = findViewById(R.id.chairNumber);
        final EditText mesas = findViewById(R.id.tableNumber);
        final EditText sillones = findViewById(R.id.couchNumber);

        camas.setFilters(new InputFilter[]{new InputFilterMinMax("0", "300")});
        sillas.setFilters(new InputFilter[]{new InputFilterMinMax("0", "300")});
        mesas.setFilters(new InputFilter[]{new InputFilterMinMax("0", "300")});
        sillones.setFilters(new InputFilter[]{new InputFilterMinMax("0", "300")});
     
    }

    // Creación de un cuadro de dialogo para confirmar pedido
    private void showDialog() throws Resources.NotFoundException {
        String camas = parseInput(((EditText) findViewById(R.id.bedNumber)).getText().toString());
        String sillas = parseInput(((EditText) findViewById(R.id.chairNumber)).getText().toString());
        String mesas = parseInput(((EditText) findViewById(R.id.tableNumber)).getText().toString());
        String sillones = parseInput(((EditText) findViewById(R.id.couchNumber)).getText().toString());
        String clientId = ((EditText) findViewById(R.id.clientId)).getText().toString();
        if (camas.equals("0") && sillas.equals("0") && mesas.equals("0") && sillones.equals("0")) {
            Toast.makeText(getApplicationContext(), "Selecciona al menos un elemento", Toast.LENGTH_SHORT).show();
        } else {
            if (clientId.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Introduce un ID de cliente", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this)
                .setTitle("Enviar")
                .setMessage("Se va a proceder al envio")
                .setIcon(android.R.drawable.ic_dialog_email)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    // Catch ok button and send information
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 1. Extraer los datos de la vista (Hecho arriba)

                        // 2. Firmar los datos

                        JSONObject jsonData = new JSONObject();
                        JSONObject message = new JSONObject();
                        try {
                            message.put("camas", camas);
                            message.put("sillas", sillas);
                            message.put("mesas", mesas);
                            message.put("sillones", sillones);
                            jsonData.put("message", message.toString());
                            jsonData.put("clientId", clientId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        KeyPair keys = null;
                        try {
                            KeyStoreManager.generateKey(getApplicationContext());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        try{
                            keys = KeyStoreManager.getKeyPair(getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try{
                            Signature signature = Signature.getInstance("SHA256withRSA");
                            //String publicKeyStr = Base64.getEncoder().encodeToString(keys.getPublic().getEncoded());
                            signature.initSign(keys.getPrivate());
                            signature.update(message.toString().getBytes());
                            byte[] signedData = signature.sign();
                            jsonData.put("signature", Arrays.toString(signedData));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        // 3. Enviar los datos
                        try{
                            RequestTask task = new RequestTask(MainActivity.this, jsonData.toString(), server, port, new RequestTask.OnRequestListener() {
                                @Override
                                public void onRequestResult(String result) {
                                   try {
                                        Toast.makeText(MainActivity.this, "Petición enviada", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(MainActivity.this, "Error al enviar petición", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onRequestFailure(String errorMessage) {
                                    // Manejar el error de solicitud aquí
                                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                            task.execute();
                            } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton(android.R.string.no, null)
                .show();
        }
    }

    public String parseInput(String s) {
        try {
            return String.valueOf(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return "0";
        }
    }
    private void copySSLFile() {
        try {
            File destinationFile = new File(this.getFilesDir(), "keystore.jks");
            try (InputStream inputStream = getAssets().open("keystore.jks")) {
                try (FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class InputFilterMinMax implements InputFilter {
        private final int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                String prefix = dest.subSequence(0, dstart).toString();
                String suffix = dest.subSequence(dend, dest.length()).toString();
                String inputText = prefix + source + suffix;

                if (inputText.startsWith("0") && !inputText.equals("0")) {
                    return "";
                }
                int input = Integer.parseInt(dest.subSequence(0, dstart).toString() + source + dest.subSequence(dend, dest.length()));
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException ignored) {
            }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }


    }
}