package com.fiec.provafinal;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;

public class FirebaseSingleton {

    private static FirebaseSingleton instance = null;

    // Construtor privado
    private FirebaseSingleton(){
        try {
            // Caminho para o arquivo JSON da chave de serviço do Firebase
            String filePath = System.getenv("HOMEPATH") + "/Downloads/fiec2024-projeto.json";

            // Configuração do Firebase
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new FileInputStream(filePath)))
                    .build();

            // Inicializa o Firebase com as opções fornecidas
            FirebaseApp.initializeApp(options);
        } catch (Exception e){
            e.printStackTrace();  // Melhor prática: imprimir a stacktrace para debugging
        }
    }

    // Método estático para obter a instância do singleton
    public static FirebaseSingleton getInstance(){
        if (instance == null) {
            // Se a instância não foi criada ainda, cria uma nova
            instance = new FirebaseSingleton();
        }
        // Retorna a instância do FirebaseSingleton
        return instance;
    }
}
