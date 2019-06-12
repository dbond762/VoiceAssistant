package com.example.voiceassistant;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.util.Consumer;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Phaser;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AI {
    private static Map<String, String> simpleQueries = new HashMap<String, String>() {{
        put("привет", "привет");
        put("как дела", "дела норм");
        put("чем занимаешься", "отвечаю на дурацкие вопросы");
        put("как тебя зовут", "меня зовут Алексей");
        put("кто тебя создал", "меня создал какой-то программист");
        put("есть ли жизнь на марсе", "на Марсе жизни нет");
        put("какого цвета небо", "небо голубое");
        put("кто мудрейший из людей", "мудрее всех, без сомнения, Сократ");
    }};

    // key - regex, value - our`s methods
    private static Map<String, Query> trickyQueries = new HashMap<String, Query>() {{
        put("какая погода в городе (\\p{L}+)", new Query() {
            @Override
            public void exec(Matcher matcher, Consumer<String> callback) {
                getWeather(matcher, callback);
            }
        });
        put("какой сегодня день", new Query() {
            @Override
            public void exec(Matcher matcher, Consumer<String> callback) {
                getDate("Сегодня MM.dd.yyyy", callback);
            }
        });
        put("сколько сейчас времени", new Query() {
            @Override
            public void exec(Matcher matcher, Consumer<String> callback) {
                getDate("Сейчас HH:mm:ss", callback);
            }
        });
        put("расскажи афоризм", new Query() {
            @Override
            public void exec(Matcher matcher, Consumer<String> callback) {
                getQuote(callback);
            }
        });
    }};

    private interface Query {
        void exec(Matcher matcher, final Consumer<String> callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static void getAnswer(String question, final Consumer<String> callback) {
        String userQuestion = question.toLowerCase();

        final List<String> answers = new ArrayList<>();

        for (String databaseQuestion : simpleQueries.keySet()) {
            if (userQuestion.contains(databaseQuestion)) {
                answers.add(simpleQueries.get(databaseQuestion));
            }
        }

        //final Phaser wg = new Phaser(1);
        //wg.register();

        for (String regex : trickyQueries.keySet()) {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(userQuestion);
            if (matcher.find()) {
                //wg.register();
                Objects.requireNonNull(trickyQueries.get(regex)).exec(matcher, new Consumer<String>() {
                    @Override
                    public void accept(String res) {
                        answers.add(res);
                        //wg.arriveAndDeregister();
                        callback.accept(TextUtils.join(", ", answers));
                    }
                });

            }
        }

        //wg.arriveAndAwaitAdvance();

        if (answers.isEmpty()) {
            answers.add("OK");
        }

        String res = TextUtils.join(", ", answers);
        callback.accept(Character.toUpperCase(res.charAt(0)) + res.substring(1));
    }

    private static void getWeather(Matcher matcher, final Consumer<String> callback) {
        String cityName = matcher.group(1);
        Weather.getWeather(cityName, new Consumer<String>() {
            @Override
            public void accept(String weatherForecast) {
                callback.accept(weatherForecast);
            }
        });
    }

    private static void getDate(String format, final Consumer<String> callback) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        callback.accept(dateFormat.format(new Date()));
    }

    private static void getQuote(final Consumer<String> callback) {
        getQuote(new Consumer<String>() {
            @Override
            public void accept(String quote) {
                callback.accept(quote);
            }
        });
    }
}
