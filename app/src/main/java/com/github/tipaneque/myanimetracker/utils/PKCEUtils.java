package com.github.tipaneque.myanimetracker.utils;

import android.util.Base64;
import android.util.Log;

import java.security.SecureRandom;

public class PKCEUtils {

    private static final String TAG = "PKCEUtils";

    public static String generateCodeVerifier() {
        try {
            SecureRandom secureRandom = new SecureRandom();

            // AUMENTAR o tamanho para garantir que tenha pelo menos 43 caracteres
            byte[] codeVerifier = new byte[64]; // Aumentei de 32 para 64 bytes

            secureRandom.nextBytes(codeVerifier);

            // Codificação Base64 URL Safe
            String verifier = Base64.encodeToString(
                    codeVerifier,
                    Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP
            );

            // Log para debug
            Log.d(TAG, "Code Verifier Generated: " + verifier);
            Log.d(TAG, "Code Verifier Length: " + verifier.length());

            // Verificação crítica
            if (verifier.length() < 43) {
                Log.e(TAG, "CODE VERIFIER TOO SHORT: " + verifier.length() + " characters");
                return generateGuaranteedCodeVerifier();
            }

            return verifier;

        } catch (Exception e) {
            Log.e(TAG, "Error in generateCodeVerifier: " + e.getMessage());
            return generateGuaranteedCodeVerifier();
        }
    }

    // Método que GARANTE um code_verifier válido
    private static String generateGuaranteedCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder verifier = new StringBuilder();

        // Caracteres permitidos pelo PKCE
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz" +
                "0123456789" +
                "-._~";

        // Gerar EXATAMENTE 64 caracteres (bem acima do mínimo 43)
        for (int i = 0; i < 64; i++) {
            int randomIndex = secureRandom.nextInt(allowedChars.length());
            verifier.append(allowedChars.charAt(randomIndex));
        }

        String result = verifier.toString();
        Log.d(TAG, "Guaranteed Code Verifier: " + result);
        Log.d(TAG, "Guaranteed Code Verifier Length: " + result.length());

        return result;
    }

    // EM PKCEUtils.java - SIMPLIFICAR
    public static String generateCodeChallenge(String codeVerifier) {

        Log.d("PKCE", "Using PLAIN method - challenge = verifier");
        return codeVerifier;
    }

    public static String generateRandomState() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] state = new byte[32];
        secureRandom.nextBytes(state);
        return Base64.encodeToString(
                state,
                Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP
        );
    }

    // Método de verificação
    public static void verifyPKCE(String codeVerifier, String codeChallenge) {
        Log.d(TAG, "=== PKCE VERIFICATION ===");
        Log.d(TAG, "Verifier: " + codeVerifier);
        Log.d(TAG, "Verifier Length: " + codeVerifier.length());
        Log.d(TAG, "Challenge: " + codeChallenge);
        Log.d(TAG, "Challenge Length: " + (codeChallenge != null ? codeChallenge.length() : "null"));

        if (codeVerifier.length() < 43) {
            Log.e(TAG, "❌ CODE VERIFIER TOO SHORT - MUST BE 43-128 CHARACTERS");
        } else if (codeVerifier.length() > 128) {
            Log.e(TAG, "❌ CODE VERIFIER TOO LONG - MUST BE 43-128 CHARACTERS");
        } else {
            Log.d(TAG, "✅ CODE VERIFIER LENGTH OK");
        }

        if (codeChallenge == null) {
            Log.e(TAG, "❌ CODE CHALLENGE IS NULL");
        } else {
            Log.d(TAG, "✅ CODE CHALLENGE GENERATED");
        }
    }
}