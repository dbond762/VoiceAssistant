package com.example.voiceassistant;

import android.support.v4.util.Consumer;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

class Quote {
    public static class ApiResult {
        @SerializedName("quoteText")
        String text;

        @SerializedName("quoteAuthor")
        String author;
    }

    public interface QuoteService {
        @GET("/api/1.0/?method=getQuote&format=json")
        Call<ApiResult> getQuote(@Query("lang") String lang);
    }

    static void getQuote(final Consumer<String> callback) {
        OkHttpClient okHttp = new OkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.forismatic.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Call<ApiResult> call = retrofit
                .create(QuoteService.class)
                .getQuote("ru");

        call.enqueue(new Callback<ApiResult>() {
            @Override
            public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                ApiResult result = response.body();
                String answer = "Кончились афоризмы";
                if (result != null) {
                    answer = String.format("%s\n%s",
                            result.text,
                            result.author);
                }
                callback.accept(answer);
            }

            @Override
            public void onFailure(Call<ApiResult> call, Throwable t) {
                Log.w("QUOTE", t.getMessage());
            }
        });
    }
}
