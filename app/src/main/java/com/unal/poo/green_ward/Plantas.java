package com.unal.poo.green_ward;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Plantas extends AppCompatActivity {

    TextView mTierra1View;
    TextView mTierra2View;
    TextView mTierra3View;

    Button inicia;
    Button iniciar;
    Button inicial;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plantas);

        inicia= (Button)findViewById(R.id.buttonSinc);

        inicia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( Plantas.this, Synchronization.class);
                startActivity(i);
            }
        });

        iniciar= (Button)findViewById(R.id.buttonClima);

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( Plantas.this, Clima.class);
                startActivity(i);
            }
        });
        inicial= (Button)findViewById(R.id.buttonTanque);

        inicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( Plantas.this, Agua.class);
                startActivity(i);
            }
        });

        // Asocia las TextView en tu layout
        mTierra1View = findViewById(R.id.dato1);
        mTierra2View = findViewById(R.id.dato2);
        mTierra3View = findViewById(R.id.dato3);

        // Inicializa la referencia a la base de datos
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Agrega un listener para escuchar cambios en la base de datos
        mDatabaseReference.child("Invernadero").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("Humedad de piso 1")) {
                    try {
                        int temperature = Integer.parseInt(dataSnapshot.child("Humedad de piso 1").getValue().toString());
                        mTierra1View.setText(String.valueOf(temperature));
                    } catch (NumberFormatException e) {
                        Log.e("plantas", "Error al convertir a int: " + e.getMessage());
                        mTierra1View.setText("Error de formato");
                    }
                } else {
                    Log.d("plantas", "No existe el nodo 'Humedad de piso 1:'");
                    // Manejar el caso en el que los datos o el nodo no existen
                }

                if (dataSnapshot.exists() && dataSnapshot.hasChild("Humedad de piso 2")) {
                    try {
                        int temperature = Integer.parseInt(dataSnapshot.child("Humedad de piso 2").getValue().toString());
                        mTierra2View.setText(String.valueOf(temperature));
                    } catch (NumberFormatException e) {
                        Log.e("plantas", "Error al convertir a int: " + e.getMessage());
                        mTierra2View.setText("Error de formato");
                    }
                } else {
                    Log.d("plantas", "No existe el nodo 'Humedad de piso 2:'");
                    // Manejar el caso en el que los datos o el nodo no existen
                }

                if (dataSnapshot.exists() && dataSnapshot.hasChild("Humedad de piso 3")) {
                    try {
                        int temperature = Integer.parseInt(dataSnapshot.child("Humedad de piso 3").getValue().toString());
                        mTierra3View.setText(String.valueOf(temperature));
                    } catch (NumberFormatException e) {
                        Log.e("plantas", "Error al convertir a int: " + e.getMessage());
                        mTierra3View.setText("Error de formato");
                    }
                } else {
                    Log.d("plantas", "No existe el nodo 'Humedad de piso 3:'");
                    // Manejar el caso en el que los datos o el nodo no existen
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores de la base de datos
                Log.e("plantas", "Error en la base de datos: " + error.getMessage());
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}