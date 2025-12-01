package com.example.trails_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trails_app.databinding.ActivityConsultTrailsBinding;
import com.example.trails_app.databinding.ItemTrailBinding;
import com.example.trails_app.domain.entities.TrailEntity;
import com.example.trails_app.repository.PositionRepository;
import com.example.trails_app.repository.SQLitePositionRepository;
import com.example.trails_app.repository.SQLiteTrailRepository;
import com.example.trails_app.repository.TrailRepository;
import com.example.trails_app.usecases.TrailOperationsUseCase;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ConsultTrailsActivity extends AppCompatActivity {
    private ActivityConsultTrailsBinding binding;
    private TrailOperationsUseCase trailOperationsUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConsultTrailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppDatabaseHelper dbHelper = new AppDatabaseHelper(this);
        TrailRepository trailRepository = new SQLiteTrailRepository(dbHelper);
        trailOperationsUseCase = new TrailOperationsUseCase(trailRepository);

        List<TrailEntity> trails = trailOperationsUseCase.getTrails();

        binding.trailsContainer.removeAllViews();

        for (TrailEntity trail : trails) {
            ItemTrailBinding itemBinding = ItemTrailBinding.inflate(getLayoutInflater(), binding.trailsContainer, false);

            itemBinding.itemTrailName.setText(trail.name());

            if (trail.beginning() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                itemBinding.itemTrailDate.setText(trail.beginning().format(formatter));
            }

            itemBinding.itemAverageSpeed.setText(String.format(Locale.getDefault(), "Vel. Média: %.1f km/h", trail.averageSpeed()));
            itemBinding.itemMaxSpeed.setText(String.format(Locale.getDefault(), "Vel. Máx: %.1f km/h", trail.maximumSpeed()));
            itemBinding.itemTrailCalories.setText(String.format(Locale.getDefault(), "Calorias: %.0f kcal", trail.caloricExpenditure()));
            itemBinding.itemTrailDistance.setText(String.format(Locale.getDefault(), "Distância: %.2f km/h", trail.distance()));
            itemBinding.itemTrailDuration.setText("Tempo: " + trail.duration());

            itemBinding.btnItemView.setOnClickListener(v -> {


            });


            itemBinding.btnItemEdit.setOnClickListener(v -> {
                if (itemBinding.editTrailName.getVisibility() == View.GONE) {
                    itemBinding.editTrailName.setText(trail.name());
                    itemBinding.itemTrailName.setVisibility(View.GONE);
                    itemBinding.editTrailName.setVisibility(View.VISIBLE);
                    itemBinding.btnItemEdit.setText("Salvar");
                } else {
                    String newTrailName = itemBinding.editTrailName.getText().toString();

                    if (!newTrailName.isEmpty()) {
                        TrailEntity trilhaAtualizada = new TrailEntity(
                                trail.trailId(),
                                newTrailName,
                                trail.beginning(),
                                trail.ending(),
                                trail.caloricExpenditure(),
                                trail.averageSpeed(),
                                trail.maximumSpeed(),
                                trail.distance(),
                                trail.duration()
                        );
                        trailOperationsUseCase.updateTrail(trilhaAtualizada);
                        itemBinding.itemTrailName.setText(newTrailName);
                    }
                    itemBinding.editTrailName.setVisibility(View.GONE);
                    itemBinding.itemTrailName.setVisibility(View.VISIBLE);
                    itemBinding.btnItemEdit.setText("Editar");
                }
            });

            itemBinding.btnItemDelete.setOnClickListener(v -> {
                trailOperationsUseCase.deleteTrail(trail.trailId());
                binding.trailsContainer.removeView(itemBinding.getRoot());
                Toast.makeText(ConsultTrailsActivity.this, "Trilha excluída!", Toast.LENGTH_SHORT).show();
            });

            itemBinding.btnItemView.setOnClickListener(v -> {
                if (itemBinding.expandableContent.getVisibility() == View.VISIBLE) {
                    itemBinding.expandableContent.setVisibility(View.GONE);
                    itemBinding.btnItemView.setText("Ver");
                } else {
                    itemBinding.expandableContent.setVisibility(View.VISIBLE);
                    itemBinding.btnItemView.setText("Ocultar");
                }
            });

            binding.trailsContainer.addView(itemBinding.getRoot());
        }
    }
}