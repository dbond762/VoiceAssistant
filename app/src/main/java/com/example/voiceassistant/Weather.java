package com.example.voiceassistant;

import android.support.v4.util.Consumer;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

class Weather {
    public static class Condition {
        @SerializedName("text")
        String text;
    }

    public static class Forecast {
        @SerializedName("temp_c")
        Float temperature;

        @SerializedName("condition")
        Condition condition;
    }

    public static class ApiResult {
        @SerializedName("current")
        Forecast current;
    }

    public interface WeatherService {
        @GET("/v1/current.json?key=0d5f43eaf2144b36bcf165626190706")
        Call<ApiResult> getCurrentWeather(@Query("q") String city, @Query("lang") String lang);
    }

    static void getWeather(String city, final Consumer<String> callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.apixu.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Call<ApiResult> call = retrofit
                .create(WeatherService.class)
                .getCurrentWeather(city, "ru");

        call.enqueue(new Callback<ApiResult>() {
            @Override
            public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                ApiResult result = response.body();
                String answer = "Не знаю такого города";
                if (result != null) {
                    answer = String.format("Там сейчас %s, где-то %d градусов",
                            result.current.condition.text,
                            result.current.temperature.intValue());
                }
                callback.accept(answer);
            }

            @Override
            public void onFailure(Call<ApiResult> call, Throwable t) {
                Log.w("WEATHER", t.getMessage());
            }
        });
    }
}
