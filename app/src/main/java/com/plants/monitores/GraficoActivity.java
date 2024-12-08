package com.plants.monitores;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.plants.monitores.databinding.ActivityGraficoBinding;
import com.plants.monitores.databinding.ActivityMainBinding;
import com.plants.monitores.entite.Monitor;
import com.plants.monitores.repository.MonitorApi;
import com.plants.monitores.servise.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraficoActivity extends AppCompatActivity {
    private LineChart chartTemperature, chartAirHumidity, chartSoilHumidity;
    private Spinner spinnerDates;
    private TextView voltar;
    private List<Monitor> monitorList = new ArrayList<>(); // Lista de dados recebidos da API
    private List<String> uniqueDates = new ArrayList<>(); // Datas únicas para o Spinner
    private ActivityGraficoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico);
        EdgeToEdge.enable(this);
        binding = ActivityGraficoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Inicializa os componentes
        chartTemperature = findViewById(R.id.chartTemperature);
        chartAirHumidity = findViewById(R.id.chartAirHumidity);
        chartSoilHumidity = findViewById(R.id.chartSoilHumidity);
        spinnerDates = findViewById(R.id.spinnerDates);

        binding.voltar.setOnClickListener(v -> {
            startActivity(new Intent(GraficoActivity.this, MainActivity.class));
            finish();
        });


        fetchMonitorData();
    }

    private void fetchMonitorData() {
        MonitorApi monitorApi = RetrofitClient.getRetrofitInstance().create(MonitorApi.class);
        Call<List<Monitor>> call = monitorApi.getMonitors();

        call.enqueue(new Callback<List<Monitor>>() {
            @Override
            public void onResponse(Call<List<Monitor>> call, Response<List<Monitor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    monitorList = response.body(); // Armazena os dados
                    extractUniqueDates();
                }
            }

            @Override
            public void onFailure(Call<List<Monitor>> call, Throwable t) {
                // Log ou tratamento de erro
            }
        });
    }

    private void extractUniqueDates() {
        // Extrai as datas únicas dos dados recebidos
        Set<String> dateSet = new HashSet<>();
        for (Monitor monitor : monitorList) {
            dateSet.add(monitor.getDate());
        }
        uniqueDates = new ArrayList<>(dateSet);

        // Configura o Spinner com as datas únicas
        setupSpinner();
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, uniqueDates);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDates.setAdapter(adapter);

        spinnerDates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDate = uniqueDates.get(position);
                filterAndRenderData(selectedDate); // Filtra e atualiza os gráficos
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nenhuma ação necessária
            }
        });
    }

    private void filterAndRenderData(String selectedDate) {
        // Filtra os dados pela data selecionada
        List<Monitor> filteredData = new ArrayList<>();
        for (Monitor monitor : monitorList) {
            if (monitor.getDate().equals(selectedDate)) {
                filteredData.add(monitor);
            }
        }

        // Renderiza os gráficos com os dados filtrados
        renderTemperatureChart(filteredData);
        renderAirHumidityChart(filteredData);
        renderSoilHumidityChart(filteredData);
    }

    private void renderTemperatureChart(List<Monitor> filteredData) {
        ArrayList<Entry> temperatureEntries = new ArrayList<>();

        for (int i = 0; i < filteredData.size(); i++) {
            Monitor monitor = filteredData.get(i);
            temperatureEntries.add(new Entry(i, (float) monitor.getTemperature())); // Temperatura
        }

        LineDataSet temperatureDataSet = new LineDataSet(temperatureEntries, "Temperatura");
        temperatureDataSet.setColor(getResources().getColor(R.color.red));
        temperatureDataSet.setValueTextColor(getResources().getColor(R.color.black));
        temperatureDataSet.setLineWidth(2f);

        LineData lineData = new LineData(temperatureDataSet);
        chartTemperature.setData(lineData);
        chartTemperature.invalidate(); // Atualiza o gráfico
    }

    private void renderAirHumidityChart(List<Monitor> filteredData) {
        ArrayList<Entry> airHumidityEntries = new ArrayList<>();

        for (int i = 0; i < filteredData.size(); i++) {
            Monitor monitor = filteredData.get(i);
            airHumidityEntries.add(new Entry(i, (float) monitor.getHumidity())); // Umidade do ar
        }

        LineDataSet airHumidityDataSet = new LineDataSet(airHumidityEntries, "Umidade do Ar");
        airHumidityDataSet.setColor(getResources().getColor(R.color.blue));
        airHumidityDataSet.setValueTextColor(getResources().getColor(R.color.black));
        airHumidityDataSet.setLineWidth(2f);

        LineData lineData = new LineData(airHumidityDataSet);
        chartAirHumidity.setData(lineData);
        chartAirHumidity.invalidate(); // Atualiza o gráfico
    }

    private void renderSoilHumidityChart(List<Monitor> filteredData) {
        ArrayList<Entry> soilHumidityEntries = new ArrayList<>();

        for (int i = 0; i < filteredData.size(); i++) {
            Monitor monitor = filteredData.get(i);
            soilHumidityEntries.add(new Entry(i, (float) monitor.getSolidhumidity())); // Umidade do solo
        }

        LineDataSet soilHumidityDataSet = new LineDataSet(soilHumidityEntries, "Umidade do Solo");
        soilHumidityDataSet.setColor(getResources().getColor(R.color.green));
        soilHumidityDataSet.setValueTextColor(getResources().getColor(R.color.black));
        soilHumidityDataSet.setLineWidth(2f);

        LineData lineData = new LineData(soilHumidityDataSet);
        chartSoilHumidity.setData(lineData);
        chartSoilHumidity.invalidate(); // Atualiza o gráfico
    }
}
