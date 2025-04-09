package com.militaryservices.app.security;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAKeyGenerator {

    public static void produceKeys() {
        try {
            // Generate RSA key pair
            KeyPair keyPair = generateRSAKeyPair();

            // Save the private key to a file
            savePrivateKey((RSAPrivateKey) keyPair.getPrivate(), "private_key.pem");

            // Save the public key to a file
            savePublicKey((RSAPublicKey) keyPair.getPublic(), "public_key.pem");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to generate the RSA key pair
    public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Key size 2048-bit
        return keyPairGenerator.generateKeyPair();
    }

    // Method to save the private key to a PEM file
    public static void savePrivateKey(RSAPrivateKey privateKey, String filename) throws IOException {
        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getEncoder().encodeToString(privateKey.getEncoded()) + "\n"
                + "-----END PRIVATE KEY-----";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(privateKeyPem);
        }
    }

    // Method to save the public key to a PEM file
    public static void savePublicKey(RSAPublicKey publicKey, String filename) throws IOException {
        String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getEncoder().encodeToString(publicKey.getEncoded()) + "\n"
                + "-----END PUBLIC KEY-----";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(publicKeyPem);
        }
    }

    public static RSAPrivateKey loadPrivateKey() {
        byte[] keyBytes = new byte[0];
        try {
            keyBytes = new FileInputStream("private_key.pem").readAllBytes();
            String privateKeyPEM = new String(keyBytes);
            privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
            KeyFactory keyFactory = null;
            keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decoded));
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static RSAPublicKey loadPublicKey() {
        try {
            byte[] keyBytes = new FileInputStream("public_key.pem").readAllBytes();
            String publicKeyPEM = new String(keyBytes);
            publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(decoded));
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

}

