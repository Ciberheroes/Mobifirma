package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RequestTask extends AsyncTask<Void, Void, String> {

    private final Context context;
    private final String jsonInputString;
    private final String stringURL;
    private final int port;
    private final OnRequestListener listener;

    public RequestTask(Context context, String jsonInputString, String stringURL, int port, OnRequestListener listener) {
        this.context = context;
        this.jsonInputString = jsonInputString;
        this.stringURL = stringURL;
        this.port = port;
        this.listener = listener;
    }

    /*Override
    protected String doInBackground(Void... voids) {
        try {
            System.setProperty("javax.net.ssl.trustStore", context.getFilesDir().getAbsolutePath());
            System.setProperty("javax.net.ssl.trustStorePassword", "ciberheroes");
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            String[] cs = factory.getDefaultCipherSuites();

            BufferedReader input;
            DataOutputStream output;
            SSLSocket socket = (SSLSocket) factory.createSocket(stringURL, port);

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new DataOutputStream(socket.getOutputStream());

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
    }*/

    @Override
    protected String doInBackground(Void... voids) {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[0];
                        }
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory factory = sslContext.getSocketFactory();

            BufferedReader input;
            PrintWriter output;
            SSLSocket socket = (SSLSocket) factory.createSocket(stringURL, port);
            //socket.setSoTimeout(5000);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream());

            output.println(jsonInputString);
            output.flush();

            String response = input.readLine();
            output.close();
            input.close();
            socket.close();

            return response;
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
