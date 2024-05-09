package com.example.myapplication;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;


public class KeyStoreManager {


    /*public static KeyPair generateKeyPair(Context context) throws Exception {
        KeyPair keyPair = null;
        File file = new File(context.getFilesDir(), "key.txt");
        if (file.exists()) {
            try {
                Path path = Paths.get(file.getAbsolutePath());
                byte[] keyBytes = Files.readAllBytes(path);
                String keyString = new String(keyBytes);
                String[] keys = keyString.split("\n");
                String publicKeyString = keys[0];
                String privateKeyString = keys[1];
                keyPair = new KeyPair(
                        KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString))),
                        KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString)))
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            KeyPairGenerator kgen = KeyPairGenerator.getInstance("RSA");
            kgen.initialize(2048);
            keyPair = kgen.generateKeyPair();
            String publicKeyString = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKeyString = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            try (FileWriter writer = new FileWriter(file.getAbsolutePath())) {
                writer.write(publicKeyString);
                writer.write("\n");
                writer.write(privateKeyString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return keyPair;
    }*/

    public static KeyPair getKeyPair(Context context) throws Exception {
        KeyPair keyPair = null;
        File file = new File(context.getFilesDir(), "keys.txt");
        file.delete();
        String keyString;
        if (file.exists()) {
            Path path = Paths.get(file.getAbsolutePath());
            byte[] keyBytes = Files.readAllBytes(path);
            keyString = new String(keyBytes);
        }else {
            ByteArrayOutputStream outputStream;
            try (InputStream is = context.getAssets().open("keys.txt")) {
                outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096]; // Tamaño del buffer de lectura
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
            byte[] fileBytes = outputStream.toByteArray();
            keyString = new String(fileBytes, StandardCharsets.UTF_8);
        }

        try{
            String[] keys = keyString.split("\n");
            byte[] publicKeyString = convertStringToBytes(keys[0]);
            byte[] privateKeyString = convertStringToBytes(keys[1]);
            keyPair = new KeyPair(
                    KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyString)),
                    KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyString))
            );
        } catch (Exception e) {
                e.printStackTrace();
        }

        return keyPair;
    }

    public static byte[] convertStringToBytes(String cadenaStr) {
        String cadena = cadenaStr.replace("[", "").replace("]", "").replace("\r", "");

        // Dividir la cadena por las comas y los espacios
        String[] partes = cadena.split(", ");

        // Crear un array de bytes
        byte[] arrayBytes = new byte[partes.length];

        // Convertir cada parte de la cadena a un byte y almacenarlo en el array de bytes
        for (int i = 0; i < partes.length; i++) {
            arrayBytes[i] = Byte.parseByte(partes[i]);
        }
        return arrayBytes;
    }

    public static String generateKey(Context context) throws Exception {
        try {
            // Generar un par de claves RSA
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // Tamaño de la clave
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // Obtener la clave pública y privada del par de claves
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // Codificar la clave pública y privada en Base64
            String publicKeyBase64 = Arrays.toString(publicKey.getEncoded());
            String privateKeyBase64 = Arrays.toString(privateKey.getEncoded());

            return "publicKeyBase64 + \n\n + privateKeyBase64";
            // Imprimir las claves codificadas en Base64
        } catch (NoSuchAlgorithmException e) {
            // Manejar excepciones
            e.printStackTrace();
            return null;
        }
    }

}

