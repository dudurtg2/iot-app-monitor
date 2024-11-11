package com.plants.monitores;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.activity.EdgeToEdge;

import android.os.Handler;

import com.plants.monitores.databinding.ActivityMainBinding;
import com.plants.monitores.entite.Monitor;
import com.plants.monitores.repository.MonitorApi;
import com.plants.monitores.servise.RetrofitClient;

public class MainActivity extends AppCompatActivity {

    private Handler handler;
    private Runnable runnable;
    private ActivityMainBinding binding;
    private static final int INTERVALO_ATUALIZACAO = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handler = new Handler();

        // Criar a tarefa repetida
        runnable = new Runnable() {
            @Override
            public void run() {
                // Chamar a API
                atualizarMonitores();

                // Reagendar o Runnable para rodar novamente após o intervalo definido
                handler.postDelayed(this, INTERVALO_ATUALIZACAO);
            }
        };

        // Iniciar a primeira execução
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Parar o handler quando a activity for destruída
        handler.removeCallbacks(runnable);
    }

    // Método para atualizar os monitores
    private void atualizarMonitores() {
        MonitorApi monitorApi = RetrofitClient.getRetrofitInstance().create(MonitorApi.class);
        Call<List<Monitor>> call = monitorApi.getMonitors();

        call.enqueue(new Callback<List<Monitor>>() {
            @Override
            public void onResponse(Call<List<Monitor>> call, Response<List<Monitor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Monitor> monitorList = response.body();


                    Monitor monitorMaisRecente = null;
                    for (Monitor monitor : monitorList) {
                        if (monitorMaisRecente == null || monitor.getId() > monitorMaisRecente.getId()) {
                            monitorMaisRecente = monitor;
                        }
                    }

                    if (monitorMaisRecente != null) {
                        binding.TextoTemp.setText( "TEMPERATURA: " + monitorMaisRecente.getTemperature() + "C゜");
                        binding.TextoUmidade.setText( "UMIDADE: " + monitorMaisRecente.getHumidity() + "%");
                        binding.TextoData.setText( "DATA: " + monitorMaisRecente.getDate());
                    }
                } else {
                    binding.TextoTemp.setText("Falha ao obter dados");
                    binding.TextoUmidade.setText("Falha ao obter dados");
                    binding.TextoData.setText("Falha ao obter dados");
                }
            }

            @Override
            public void onFailure(Call<List<Monitor>> call, Throwable t) {
                binding.TextoTemp.setText("Erro: " + t.getMessage());
                binding.TextoUmidade.setText("Erro: " + t.getMessage());
                binding.TextoData.setText("Erro: " + t.getMessage());
            }
        });
    }

}