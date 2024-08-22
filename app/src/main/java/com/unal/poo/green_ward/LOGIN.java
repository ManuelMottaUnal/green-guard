package com.unal.poo.green_ward;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LOGIN extends AppCompatActivity {

    Button iniciar;
    Button inicial;
    EditText usuarioEditText;
    EditText contraseñaEditText;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Asocia los EditText y los Botones con sus IDs en el layout
        usuarioEditText = findViewById(R.id.editText);
        contraseñaEditText = findViewById(R.id.editTextP);
        iniciar = findViewById(R.id.button);
        inicial = findViewById(R.id.button2);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = usuarioEditText.getText().toString();
                String contraseña = contraseñaEditText.getText().toString();

                if (!usuario.isEmpty() && !contraseña.isEmpty()) {

                    verificarCredenciales(usuario, contraseña);
                } else {
                    Toast.makeText(LOGIN.this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        inicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LOGIN.this, Registrar.class);
                startActivity(i);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void verificarCredenciales(final String usuario, final String contraseña) {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean credencialesValidas = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String usuarioFirebase = snapshot.child("usuario").getValue(String.class);
                    String contraseñaFirebase = snapshot.child("contraseña").getValue(String.class);
                    if (usuario.equals(usuarioFirebase) && contraseña.equals(contraseñaFirebase)) {
                        credencialesValidas = true;
                        break;
                    }
                }

                if (credencialesValidas) {
                    Intent i = new Intent(LOGIN.this, Plantas.class);
                    startActivity(i);
                } else {
                    Toast.makeText(LOGIN.this, "Usuario o contraseña incorrectos.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores de la base de datos
                Toast.makeText(LOGIN.this, "Error al acceder a la base de datos.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}