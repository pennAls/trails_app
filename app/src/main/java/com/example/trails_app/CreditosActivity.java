package com.example.trails_app; // ALTERE PARA O SEU PACOTE

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


public class CreditosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Garanta que o nome do seu layout XML é 'activity_creditos'
        setContentView(R.layout.activity_creditos);
    }
}