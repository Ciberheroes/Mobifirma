package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputFilter;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    // Setup Server information
    protected static String server = "192.168.1.133";
    protected static int port = 7070;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        //camas.setText("0");
        //sillas.setText("0");
        //mesas.setText("0");
        //sillones.setText("0");

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

        if ((camas.equals("0") && sillas.equals("0") && mesas.equals("0") && sillones.equals("0"))) {
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
                                    // 1. Extraer los datos de la vista (Hecho arriba)

                                    // 2. Firmar los datos

                                    // 3. Enviar los datos
                                    try{
                                        JSONObject jsonObject = new JSONObject();
                                        try {
                                            jsonObject.put("camas", camas);
                                            jsonObject.put("sillas", sillas);
                                            jsonObject.put("mesas", mesas);
                                            jsonObject.put("sillones", sillones);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        RequestTask task = new RequestTask(jsonObject.toString(), "http://"+server+":"+port+"/request", new RequestTask.OnRequestListener() {
                                            @Override
                                            public void onRequestResult(String result) {
                                               try {
                                                    Toast.makeText(MainActivity.this, "Petición enviada", Toast.LENGTH_SHORT).show();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(MainActivity.this, "Error al enviar petición", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        task.execute();
                                        } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                    )
                            .

                    setNegativeButton(android.R.string.no, null)

                            .

                    show();
        }
    }

    public String parseInput(String s) {
        try {
            return String.valueOf(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return "0";
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
                int input = Integer.parseInt(dest.subSequence(0, dstart).toString() + source + dest.subSequence(dend, dest.length()).toString());
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