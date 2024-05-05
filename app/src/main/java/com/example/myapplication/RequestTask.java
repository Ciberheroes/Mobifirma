package com.example.myapplication;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.SocketTimeoutException;

public class RequestTask extends AsyncTask<Void, Void, String> {

    private String jsonInputString;
    private String stringURL;
    private OnRequestListener listener;

    public RequestTask(String jsonInputString, String stringURL, OnRequestListener listener) {
        this.jsonInputString = jsonInputString;
        this.stringURL = stringURL;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        StringBuilder response = new StringBuilder();
        try {
            // URL de destino para la solicitud POST (debe ser HTTPS)
            URL url = new URL(stringURL);

            // Abrir conexión
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Configurar la solicitud
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            // Escribir el JSON en el cuerpo de la solicitud
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            byte[] input = jsonInputString.getBytes("utf-8");
            wr.write(input, 0, input.length);

            // Leer la respuesta
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // Cerrar la conexión
            connection.disconnect();
        } catch (SocketTimeoutException e) {
            // Manejar la excepción de tiempo de espera
            e.printStackTrace();
            response.append("Error: Tiempo de espera de conexión excedido.");
        } catch (Exception e) {
            // Manejar otras excepciones
            e.printStackTrace();
            response.append("Error: ").append(e.getMessage());
        }
        return response.toString();
    }


    @Override
    protected void onPostExecute(String result) {
        if (listener != null && !result.contains("Error")) {
            listener.onRequestResult(result);
        } else if (listener != null) {
            listener.onRequestFailure(result);
        }

    }

    public interface OnRequestListener {
        void onRequestResult(String result);
        void onRequestFailure(String errorMessage);
    }
}
