package com.unal.poo.green_ward;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unal.poo.green_ward.util.FetchWeatherTask;

import java.util.Objects;


public class Clima extends AppCompatActivity {

    TextView mTempView;
    TextView mTempExterior;
    TextView mHumView;
    TextView mHumExterior;
    TextView puerta;
    TextView textViewClimaDescripcion;
    Button AC;
    ImageView imageViewClima;
    Integer data = 0;
    double latitud = 0;
    double longitud = 0;
    String url;

    private DatabaseReference mDatabaseReference;
    private FetchWeatherTask fetchWeatherTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clima);

        // Asocia las TextView en tu layout
        mTempView = findViewById(R.id.textViewTemperaturaInteriorValue);
        mTempExterior = findViewById(R.id.textViewTemperaturaExteriorValue);
        mHumView = findViewById(R.id.textViewHumedadInteriorValue);
        puerta = findViewById(R.id.textViewAC);
        mHumExterior = findViewById(R.id.textViewHumedadExteriorValue);
        imageViewClima = findViewById(R.id.imageViewClima);
        textViewClimaDescripcion = findViewById(R.id.TextViewClimaDescripcion);

        // Inicializa la referencia a la base de datos
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        fetchWeatherTask = new FetchWeatherTask();

        AC = findViewById(R.id.buttonAC);

        // Botón para enviar datos
        AC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Alternar el valor de 'data'
                data = (data == 0) ? 1 : 0;

                // Establecer el valor en Firebase
                mDatabaseReference.child("Puerta").setValue(data, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null) {
                            // Manejar errores
                            Log.e("clima", "Error al escribir en Firebase: " + error.getMessage());
                        } else {
                            // Éxito
                            Log.d("clima", "Datos actualizados correctamente.");
                        }
                    }
                });
            }
        });

        // Agrega un listener para escuchar cambios en la base de datos
        mDatabaseReference.child("Invernadero").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("Temperatura")) {
                    try {
                        double temperature = Double.parseDouble(dataSnapshot.child("Temperatura").getValue().toString());
                        mTempView.setText(String.valueOf(temperature) + "°C");
                    } catch (NumberFormatException e) {
                        Log.e("clima", "Error al convertir la temperatura: " + e.getMessage());
                        mTempView.setText("Error de formato");
                    }
                } else {
                    Log.d("clima", "No existe el nodo 'Temperatura'");
                }

                if (dataSnapshot.exists() && dataSnapshot.hasChild("Humedad")) {
                    try {
                        double humidity = Double.parseDouble(dataSnapshot.child("Humedad").getValue().toString());
                        mHumView.setText(String.valueOf(humidity) + "%");
                    } catch (NumberFormatException e) {
                        Log.e("clima", "Error al convertir la humedad: " + e.getMessage());
                        mHumView.setText("Error de formato");
                    }
                } else {
                    Log.d("clima", "No existe el nodo 'Humedad'");
                }

                if (dataSnapshot.exists() && dataSnapshot.hasChild("Ubicacion")) {
                    try {
                        longitud = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("Ubicacion").child("longitud").getValue()).toString());
                        latitud = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("Ubicacion").child("latitud").getValue()).toString());
                        Log.i("Clima", "Latitud: " + latitud);
                        Log.i("Clima", "Longitud: " + longitud);
                        readWeather();
                    } catch (Exception e) {
                        Log.e("clima", e.getMessage());
                    }

                }
                if (dataSnapshot.exists() && dataSnapshot.hasChild("Clima")) {
                    DataSnapshot mainDataSnapshot = dataSnapshot.child("Clima").child("main");
                    if (mainDataSnapshot.exists() && mainDataSnapshot.hasChild("humidity")) {
                        mHumExterior.setText(mainDataSnapshot.child("humidity").getValue(Long.class).toString() + "%");
                    }
                    if (mainDataSnapshot.exists() && mainDataSnapshot.hasChild("temp")) {
                        mTempExterior.setText(mainDataSnapshot.child("temp").getValue(Long.class).toString() + "°C");
                    }
                    DataSnapshot weatherDataSnapshot = dataSnapshot.child("Clima").child("weather").child("0").child("main");
                    if (weatherDataSnapshot.exists()) {
                        String mainWeatherValue = weatherDataSnapshot.getValue(String.class);
                        Log.i("Clima", "Valor clima => " + mainWeatherValue);
                        url = "";
                        // Evaluar el valor de 'main' y asignar la URL correspondiente
                        String descripcionClima = "";
                        switch (mainWeatherValue) {
                            case "Clear":
                                url = "https://publicdomainvectors.org/photos/sivvus_weather_symbols_1.png";
                                descripcionClima = "Despejado";
                                break;
                            case "Clouds":
                                url = "https://publicdomainvectors.org/photos/stylized_basic_cloud.png";
                                descripcionClima = "Nublado";
                                break;
                            case "Rain":
                                url = "https://publicdomainvectors.org/photos/weather-showers-scattered.png";
                                descripcionClima = "Lluvia";
                                break;
                            case "Thunderstorm":
                                url = "https://publicdomainvectors.org/photos/thunderstorm.png";
                                descripcionClima = "Tormenta";
                                break;
                            case "Snow":
                                url = "https://publicdomainvectors.org/photos/sivvus_weather_symbols_5.png";
                                descripcionClima = "Nieve";
                                break;
                            case "Drizzle":
                                url = "https://publicdomainvectors.org/photos/spite_rain.png";
                                descripcionClima = "Llovizna";
                                break;
                            case "Mist":
                            case "Fog":
                            case "Haze":
                                url = "https://publicdomainvectors.org/photos/Surreal-Misty-Valley.png";
                                descripcionClima = "Niebla";
                                break;
                            default:
                                url = "https://publicdomainvectors.org/photos/mono-question-mark.png";
                                descripcionClima = "Desconocido";
                                break;
                        }
                        textViewClimaDescripcion.setText(descripcionClima);
                        updateImageWeater();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores de la base de datos
                Log.e("clima", "Error en la base de datos: " + error.getMessage());
            }
        });

        mDatabaseReference.child("Puerta").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Obtener el valor de 'Puerta'
                if (dataSnapshot.exists()) {
                    Integer puertaValue = dataSnapshot.getValue(Integer.class);
                    if (puertaValue != null) {
                        // Mostrar "Cerrada" si el valor es 1, "Abierta" si el valor es 0
                        if (puertaValue == 1) {
                            puerta.setText("Cerrada");
                        } else if (puertaValue == 0) {
                            puerta.setText("Abierta");
                        } else {
                            puerta.setText("Valor no reconocido");
                        }
                    } else {
                        puerta.setText("Valor de Puerta no disponible");
                    }
                } else {
                    puerta.setText("Nodo 'Puerta' no existe");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores
                puerta.setText("Error al leer los datos: " + databaseError.getMessage());
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void readWeather() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitud + "&lon=" + longitud + "&appid=38ecca0a2e0879ba5a84ed007561a819&units=metric";
        fetchWeatherTask.fetchWeather(apiUrl, this);
    }

    private void updateImageWeater() {
        Glide.with(this).load(url).into(imageViewClima);
    }


}