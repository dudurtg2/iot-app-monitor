package com.plants.monitores;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.widget.Toast;

import com.plants.monitores.databinding.ActivityMainBinding;
import com.plants.monitores.entite.Monitor;
import com.plants.monitores.repository.MonitorApi;
import com.plants.monitores.servise.RetrofitClient;

public class MainActivity extends AppCompatActivity {

    private Handler handler;
    private Runnable runnable;
    private ActivityMainBinding binding;
    private static final int INTERVALO_ATUALIZACAO = 3000;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handler = new Handler();
        context = this;

        // Criar a tarefa repetida
        runnable = new Runnable() {
            @Override
            public void run() {
                // Chamar a API
                atualizarMonitores(context);

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
    private void atualizarMonitores(Context context) {
        MonitorApi monitorApi = RetrofitClient.getRetrofitInstance().create(MonitorApi.class);
        Call<List<Monitor>> call = monitorApi.getMonitors();

        call.enqueue(new Callback<List<Monitor>>() {
            @Override
            public void onResponse(Call<List<Monitor>> call, Response<List<Monitor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Monitor> monitorList = response.body();


                    Monitor monitorMaisRecente = null;
                    int Count = 0;
                    String dia = "";
                    for (Monitor monitor : monitorList) {
                        if (monitorMaisRecente == null || monitor.getId() > monitorMaisRecente.getId()) {
                            monitorMaisRecente = monitor;
                        }
                        if (dia.equals("")) {
                            Count++;
                            dia = monitor.getDate();
                        } else if (!dia.equals(monitor.getDate())) {
                            Count++;
                            dia = monitor.getDate();
                        }

                    }

                    binding.diaImput.setText(String.valueOf(Count));

                    if (monitorMaisRecente != null) {
                        binding.TemperaturaImput.setText(monitorMaisRecente.getTemperature() + "°");
                        binding.HumidityImput.setText(monitorMaisRecente.getHumidity() + "%");

                        int whiteColor = ContextCompat.getColor(context, R.color.white);
                        int blackColor = ContextCompat.getColor(context, R.color.black);

                        if (monitorMaisRecente.isLightStatus()) {
                            binding.LuzIndicador.setTextColor(whiteColor);
                        } else {
                            binding.LuzIndicador.setTextColor(blackColor);
                        }

                        if (monitorMaisRecente.isHumidityStatus()) {
                            binding.HumificadorIndicador.setTextColor(whiteColor);
                        } else {
                            binding.HumificadorIndicador.setTextColor(blackColor);
                        }

                        if (monitorMaisRecente.isVentStatus()) {
                            binding.VentiladorIndicador.setTextColor(whiteColor);
                        } else {
                            binding.VentiladorIndicador.setTextColor(blackColor);
                        }

                        if (monitorMaisRecente.isPampStatus()) {
                            binding.AguaIndicador.setTextColor(whiteColor);
                            binding.info.setText("A umidade do solo atual registrada pelo sensor é essencial para monitorar as condições ideais.\nAtualmente estar em "+monitorMaisRecente.getSolidhumidity()+"%, sua estufa precisa de um pouco de água.");
                        } else {
                            binding.AguaIndicador.setTextColor(blackColor);
                            binding.info.setText("A umidade do solo atual registrada pelo sensor é essencial para monitorar as condições ideais.\nAtualmente estar em "+monitorMaisRecente.getSolidhumidity()+"%, sua estufa está com a umidade ideal.");

                        }
                    } else {
                        Toast.makeText(context, "Nenhum monitor encontrado", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<List<Monitor>> call, Throwable t) {

            }
        });
    }

}