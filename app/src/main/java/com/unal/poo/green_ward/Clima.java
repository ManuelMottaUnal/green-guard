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



public class Clima extends AppCompatActivity {

    TextView mTempView;
    TextView mHumView;
    TextView Puerta;
    Button AC;
    Integer data = 0;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clima);

        // Asocia las TextView en tu layout
        mTempView = findViewById(R.id.textViewTemperaturaExteriorValue);
        mHumView = findViewById(R.id.textViewHumedadExteriorValue);
        Puerta = findViewById(R.id.textViewAC);

        // Inicializa la referencia a la base de datos
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

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
                            Puerta.setText("Cerrada");
                        } else if (puertaValue == 0) {
                            Puerta.setText("Abierta");
                        } else {
                            Puerta.setText("Valor no reconocido");
                        }
                    } else {
                        Puerta.setText("Valor de Puerta no disponible");
                    }
                } else {
                    Puerta.setText("Nodo 'Puerta' no existe");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores
                Puerta.setText("Error al leer los datos: " + databaseError.getMessage());
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imageViewClima = findViewById(R.id.imageViewClima);
        String url = "https://publicdomainvectors.org/photos/thunderstorm.png";

        Glide.with(this).load(url).into(imageViewClima);
    }
}