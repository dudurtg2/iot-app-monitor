package com.plants.monitores;

import android.os.Bundle;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.activity.EdgeToEdge;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.appcompat.app.AppCompatActivity;

import com.plants.monitores.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView editText;
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

                        String statusBomba = monitorMaisRecente.getPampStatus() ? "LIGADA" : "DESLIGADA";
                        String statusLuz = monitorMaisRecente.getLightStatus() ? "LIGADA" : "DESLIGADA";

                        String data = "ID: " + monitorMaisRecente.getId() + "\n" +
                                "TEMPERATURA: " + monitorMaisRecente.getTemperature() + "C゜\n" +
                                "UMIDADE: " + monitorMaisRecente.getHumidity() + "\n" +
                                "UMID DO SOLO: " + monitorMaisRecente.getSolidhumidity() + "\n\n" +
                                "STATUS DA BOMBA: " + "\n" + statusBomba + "\n\n" +
                                "STATUS DA LUZ: " + "\n" + statusLuz + "\n\n" +
                                "DATA: " + monitorMaisRecente.getDate();
                        binding.Texto.setText(data);
                    }
                } else {
                    binding.Texto.setText("Falha ao obter dados");
                }
            }

            @Override
            public void onFailure(Call<List<Monitor>> call, Throwable t) {
                binding.Texto.setText("Erro: " + t.getMessage());
            }
        });
    }

}