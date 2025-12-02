package com.example.trails_app;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        TextView tvAppTitle = findViewById(R.id.tvAppTitle);
        TextView tvVersion = findViewById(R.id.tvVersion);
        TextView tvDescription = findViewById(R.id.tvDescription);

        ImageView imgDev1 = findViewById(R.id.imgDev1);
        ImageView imgDev2 = findViewById(R.id.imgDev2);

        TextView tvDev1Name = findViewById(R.id.tvDev1Name);
        TextView tvDev2Name = findViewById(R.id.tvDev2Name);

        tvAppTitle.setText(getString(R.string.app_name));
        tvVersion.setText("Versão 1.0");

        String descricao = "A solução foi construída utilizando a linguagem Java e o Android SDK. " +
                "As funcionalidades essenciais incluem a persistência de dados do usuário e de todas as trilhas " +
                "(percurso, velocidades, gasto calórico) em um Banco de Dados SQLite. " +
                "O projeto aborda a criação de atividades para Configuração, Registro em Tempo Real e Consulta.";
        tvDescription.setText(descricao);

        imgDev1.setImageResource(R.drawable.andre_rocha);
        imgDev2.setImageResource(R.drawable.lucas_pena);

        tvDev1Name.setText("André Luís Souza Trindade Rocha - 200031957");
        tvDev2Name.setText("Lucas Pena de Araújo dos Santos - 200032368");
    }
}