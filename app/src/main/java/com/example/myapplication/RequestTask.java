package com.example.myapplication;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class RequestTask extends AsyncTask<Void, Void, String> {

    private final String jsonInputString;
    private final String stringURL;
    private final int port;
    private final OnRequestListener listener;

    public RequestTask(String jsonInputString, String stringURL, int port, OnRequestListener listener) {
        this.jsonInputString = jsonInputString;
        this.stringURL = stringURL;
        this.port = port;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {

            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            BufferedReader input;
            DataOutputStream output;
            try (SSLSocket socket = (SSLSocket) factory.createSocket(stringURL, port)) {

                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new DataOutputStream(socket.getOutputStream());
            }

            output.writeBytes(jsonInputString);
            output.flush();

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = input.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (SocketTimeoutException e) {
            return "Error: Tiempo de espera de conexión excedido.";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
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
