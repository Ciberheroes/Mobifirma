package com.example.myapplication;

import android.content.Context;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class KeyStoreManager {


    public static KeyPair generateKeyPair(Context context) throws Exception {
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
    }

}

