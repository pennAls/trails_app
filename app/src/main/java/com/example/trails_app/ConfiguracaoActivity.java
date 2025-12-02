package com.example.trails_app;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class ConfiguracaoActivity extends AppCompatActivity {

    // Componentes da UI
    private EditText edtPeso, edtAltura, edtDataNascimento;
    private RadioGroup rgSexo, rgTipoMapa, rgNavegacao;
    private RadioButton rbMasculino, rbFeminino, rbVetorial, rbSatelite, rbNorthUp, rbCourseUp;
    private Button btnSalvar;

    // Nome do arquivo de preferências
    private static final String PREFS_NAME = "TrilhaPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);


        inicializarComponentes();


        carregarPreferencias();


        edtDataNascimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });


        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarPreferencias();
            }
        });
    }

    private void inicializarComponentes() {
        edtPeso = findViewById(R.id.edtPeso);
        edtAltura = findViewById(R.id.edtAltura);
        edtDataNascimento = findViewById(R.id.edtDataNascimento);
        rgSexo = findViewById(R.id.rgSexo);
        rgTipoMapa = findViewById(R.id.rgTipoMapa);
        rgNavegacao = findViewById(R.id.rgNavegacao);

        rbMasculino = findViewById(R.id.rbMasculino);
        rbFeminino = findViewById(R.id.rbFeminino);
        rbVetorial = findViewById(R.id.rbVetorial);
        rbSatelite = findViewById(R.id.rbSatelite);
        rbNorthUp = findViewById(R.id.rbNorthUp);
        rbCourseUp = findViewById(R.id.rbCourseUp);
        btnSalvar = findViewById(R.id.btnSalvar);
    }

    private void mostrarDatePicker() {
        final Calendar c = Calendar.getInstance();
        int ano = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Formatar a data para exibir no EditText
                        String data = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        edtDataNascimento.setText(data);
                    }
                }, ano, mes, dia);
        datePickerDialog.show();
    }

    private void salvarPreferencias() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();


        editor.putString("peso", edtPeso.getText().toString());
        editor.putString("altura", edtAltura.getText().toString());
        editor.putString("data_nascimento", edtDataNascimento.getText().toString());


        int idSexoSelecionado = rgSexo.getCheckedRadioButtonId();
        if (idSexoSelecionado == R.id.rbMasculino) {
            editor.putString("sexo", "Masculino");
        } else if (idSexoSelecionado == R.id.rbFeminino) {
            editor.putString("sexo", "Feminino");
        }



        if (rgTipoMapa.getCheckedRadioButtonId() == R.id.rbSatelite) {
            editor.putInt("tipo_mapa", 2); // GoogleMap.MAP_TYPE_SATELLITE
        } else {
            editor.putInt("tipo_mapa", 1); // GoogleMap.MAP_TYPE_NORMAL (Default)
        }


        if (rgNavegacao.getCheckedRadioButtonId() == R.id.rbCourseUp) {
            editor.putString("navegacao", "course_up");
        } else {
            editor.putString("navegacao", "north_up"); // Default
        }

        editor.apply();
        Toast.makeText(this, "Configurações Salvas!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void carregarPreferencias() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);


        edtPeso.setText(prefs.getString("peso", ""));
        edtAltura.setText(prefs.getString("altura", ""));
        edtDataNascimento.setText(prefs.getString("data_nascimento", ""));


        String sexo = prefs.getString("sexo", "");
        if (sexo.equals("Masculino")) {
            rbMasculino.setChecked(true);
        } else if (sexo.equals("Feminino")) {
            rbFeminino.setChecked(true);
        }


        int tipoMapa = prefs.getInt("tipo_mapa", 1);
        if (tipoMapa == 2) {
            rbSatelite.setChecked(true);
        } else {
            rbVetorial.setChecked(true);
        }


        String nav = prefs.getString("navegacao", "north_up");
        if (nav.equals("course_up")) {
            rbCourseUp.setChecked(true);
        } else {
            rbNorthUp.setChecked(true);
        }
    }
}