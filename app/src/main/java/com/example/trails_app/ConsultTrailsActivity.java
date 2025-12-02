package com.example.trails_app;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.trails_app.databinding.ActivityConsultTrailsBinding;
import com.example.trails_app.databinding.ItemTrailBinding;
import com.example.trails_app.domain.entities.PositionEntity;
import com.example.trails_app.domain.entities.TrailEntity;
import com.example.trails_app.repository.PositionRepository;
import com.example.trails_app.repository.SQLitePositionRepository;
import com.example.trails_app.repository.SQLiteTrailRepository;
import com.example.trails_app.repository.TrailRepository;
import com.example.trails_app.usecases.TrailOperationsUseCase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

            itemBinding.mapViewItem.onCreate(null);
            itemBinding.mapViewItem.getMapAsync(googleMap -> {
                googleMap.getUiSettings().setMapToolbarEnabled(false);

                List<PositionEntity> positions = trailOperationsUseCase.findPositionsByTrailId(trail.trailId());

                if (positions != null && !positions.isEmpty()) {
                    List<LatLng> points = new ArrayList<>();
                    for (PositionEntity p : positions) {
                        points.add(new LatLng(p.getLatitude(), p.getLongitude()));
                    }

                    PolylineOptions line = new PolylineOptions()
                            .addAll(points)
                            .width(8f)
                            .color(Color.RED)
                            .geodesic(true);

                    googleMap.addPolyline(line);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 20f));
                }
            });


            itemBinding.btnItemShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareTrailData(trail);
                }
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

    private void shareTrailData(TrailEntity trail) {
        List<PositionEntity> positions = trailOperationsUseCase.findPositionsByTrailId(trail.trailId());
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("TRILHA:,").append(trail.name()).append("\n");
        csvBuilder.append("Data:,").append(trail.beginning()).append("\n");
        csvBuilder.append("Distancia (km):,").append(trail.distance()).append("\n");
        csvBuilder.append("Duracao:,").append(trail.duration()).append("\n");
        csvBuilder.append("Calorias:,").append(trail.caloricExpenditure()).append("\n\n");
        csvBuilder.append("TIMESTAMP,LATITUDE,LONGITUDE,VELOCIDADE(m/s),PRECISAO\n");

        if (positions != null) {
            for (PositionEntity pos : positions) {
                csvBuilder.append(pos.getTimestamp()).append(",")
                        .append(pos.getLatitude()).append(",")
                        .append(pos.getLongitude()).append(",")
                        .append(pos.getInstantaneousSpeed()).append(",")
                        .append(pos.getAccuracy()).append("\n");
            }
        }

        try {
            File file = new File(getCacheDir(), "trilha_export.csv");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(csvBuilder.toString().getBytes());
            fos.close();

            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/csv");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Dados da Trilha: " + trail.name());
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Compartilhar via"));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gerar arquivo", Toast.LENGTH_SHORT).show();
        }
    }
}