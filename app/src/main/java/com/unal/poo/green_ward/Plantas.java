package com.unal.poo.green_ward;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    ImageView mImagen1View;
    ImageView mImagen2View;
    ImageView mImagen3View;

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
                Intent i = new Intent( Plantas.this, Sincronizar.class);
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

        // Asocia las TextView en el layout
        mImagen1View = findViewById(R.id.imagen1);
        mImagen2View = findViewById(R.id.imagen2);
        mImagen3View = findViewById(R.id.imagen3);

        // Inicializa la referencia a la base de datos
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Agrega un listener para escuchar cambios en la base de datos
        mDatabaseReference.child("Invernadero").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("Humedad de piso 1")) {
                    try {
                        int humedad = Integer.parseInt(dataSnapshot.child("Humedad de piso 1").getValue().toString());
                        asignarEmoji(humedad, mImagen1View);
                    } catch (NumberFormatException e) {
                        Log.e("plantas", "Error al convertir a int: " + e.getMessage());

                    }

                } else {
                    Log.d("plantas", "No existe el nodo 'Humedad de piso 1:'");
                    // Manejar el caso en el que los datos o el nodo no existen
                }

                if (dataSnapshot.exists() && dataSnapshot.hasChild("Humedad de piso 2")) {
                    try {
                        int humedad = Integer.parseInt(dataSnapshot.child("Humedad de piso 2").getValue().toString());
                        asignarEmoji(humedad, mImagen2View);
                    } catch (NumberFormatException e) {
                        Log.e("plantas", "Error al convertir a int: " + e.getMessage());

                    }

                } else {
                    Log.d("plantas", "No existe el nodo 'Humedad de piso 2:'");
                    // Manejar el caso en el que los datos o el nodo no existen
                }

                if (dataSnapshot.exists() && dataSnapshot.hasChild("Humedad de piso 3")) {
                    try {
                        int humedad = Integer.parseInt(dataSnapshot.child("Humedad de piso 3").getValue().toString());
                        asignarEmoji(humedad, mImagen3View);
                    } catch (NumberFormatException e) {
                        Log.e("plantas", "Error al convertir a int: " + e.getMessage());

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
    private void asignarEmoji(int humedad, ImageView imageView) {
        if (humedad < 30) {
            imageView.setImageResource(R.drawable.emoji_triste);
        } else if (humedad >= 30 && humedad <= 50) {
            imageView.setImageResource(R.drawable.emoji_serio);
        } else {
            imageView.setImageResource(R.drawable.emoji_feliz);
        }
    }
}