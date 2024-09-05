package com.unal.poo.green_ward.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FetchWeatherTask {

    private ExecutorService executorService;
    private DatabaseReference databaseReference;

    public FetchWeatherTask() {
        // Inicializa el executorService con un solo hilo
        this.executorService = Executors.newSingleThreadExecutor();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void fetchWeather(final String apiUrl, final Context context) {
        // Ejecuta la tarea en un hilo en segundo plano
        executorService.execute(() -> {
            String result = null;
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                result = response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Cambia al hilo principal para actualizar la interfaz de usuario
            final String finalResult = result;
            new Handler(Looper.getMainLooper()).post(() -> {
                if (finalResult != null) {
                    Log.i("Clima", apiUrl);
                    Log.i("Clima", finalResult);
                    saveWeatherData(finalResult);
                } else {
                    Toast.makeText(context, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void saveWeatherData(String jsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> jsonMap = gson.fromJson(jsonString, type);

        // Guardar el Map en Firebase
        databaseReference.child("Invernadero").child("Clima").setValue(jsonMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Operación exitosa
                        Log.d("Clima", "Datos de clima guardados exitosamente.");
                    } else {
                        // Error al guardar los datos
                        Log.e("Clima", "Error al guardar los datos de clima.", task.getException());
                    }
                });
    }
}
