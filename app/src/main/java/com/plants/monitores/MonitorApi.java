package com.plants.monitores;

import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface MonitorApi {
    @GET("/monitor/findAll")
    Call<List<Monitor>> getMonitors();
}
