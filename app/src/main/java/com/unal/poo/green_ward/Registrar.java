package com.unal.poo.green_ward;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registrar extends AppCompatActivity {

    EditText usu;
    EditText con;
    Button reg;

    private DatabaseReference mDatabaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar);

        // Asocia los EditText y el Button con sus IDs en el layout
        usu = findViewById(R.id.eTT);
        con = findViewById(R.id.eTTP);
        reg = findViewById(R.id.button3);

        // Inicializa la referencia a la base de datos
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Configura el Listener para el botón
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = usu.getText().toString();
                String contraseña = con.getText().toString();

                if (!usuario.isEmpty() && !contraseña.isEmpty()) {
                    User user = new User(usuario, contraseña);

                    String key = mDatabaseReference.child("usuarios").push().getKey();

                    if (key != null) {
                        mDatabaseReference.child("usuarios").child(key).child("usuario").setValue(user.usuario);
                        mDatabaseReference.child("usuarios").child(key).child("contraseña").setValue(user.contraseña);

                        Log.d("Registrar", "Datos enviados correctamente.");

                        usu.setText("");
                        con.setText("");
                    }
                } else {
                    Log.d("Registrar", "Por favor, complete todos los campos.");
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registrar), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public static class User {
        public String usuario;
        public String contraseña;

        // Constructor vacío requerido por Firebase
        public User() {
        }

        public User(String usuario, String contraseña) {
            this.usuario = usuario;
            this.contraseña = contraseña;
        }
    }
}