package ru.kpfu.itis.paramonov.oriscontrolworkjavafx.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.json.JSONException;
import org.json.JSONObject;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.BotApplication;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.chat.ChatApplication;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.chat.server.ChatServer;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.http_client.HttpClient;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.http_client.HttpClientImpl;
import ru.kpfu.itis.paramonov.oriscontrolworkjavafx.model.Command;

import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class BotPageController {
    @FXML
    private TextField command;

    @FXML
    private TextArea conversation;

    private boolean isBotStarted = false;

    private final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                String message = command.getText();
                if (message.charAt(0) != '/') {
                    onUnknownCommand();
                }
                else {
                    try {
                        String[] commandAndParams = message.substring(1).split(" ");
                        String inputCommand = commandAndParams[0];

                        ArrayList<String> params = new ArrayList<>();
                        if (commandAndParams.length > 1) {
                            params.addAll(Arrays.asList(commandAndParams).subList(1, commandAndParams.length));
                        }
                        if (isBotStarted) appendMessage(message, ENTITY_USER);
                        handleCommand(Command.valueOf(inputCommand), params);

                        command.clear();
                        keyEvent.consume();
                    } catch (IllegalArgumentException e) {
                        onUnknownCommand();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    };

    private void onUnknownCommand() {
        command.clear();
        if (isBotStarted) {
            appendMessage("Unable to identify command, type /help for command info", ENTITY_BOT);
        }
    }

    private final String API_KEY_WEATHER = "8b4a0acd96ca08b566885cabd39599e7";

    private final String API_KEY_CURRENCY = "cur_live_qcBxihqT5y7UTdaKoffcw08BMoFGEZDQCQhGRvvS";

    private void handleCommand(Command command, List<String> params) throws Exception {
        switch (command) {
            case start -> {
                if (isBotStarted) {
                    appendMessage("The bot is already working", ENTITY_SYSTEM);
                }
                else {
                    isBotStarted = true;
                    appendMessage("The bot is working", ENTITY_SYSTEM);
                }
            }
            default -> {
                if (!isBotStarted) appendMessage("Please start bot by typing /start command", ENTITY_SYSTEM);
                else {
                    HttpClient httpClient = new HttpClientImpl(API_KEY_WEATHER);
                    switch (command) {
                        case end -> {
                            isBotStarted = false;
                            appendMessage("The bot was stopped", ENTITY_SYSTEM);
                        }
                        case weather -> {
                            HashMap<String, String> getParams = new HashMap<>();
                            getParams.put("q", params.get(0));
                            getParams.put("appid", API_KEY_WEATHER);
                            String resp = httpClient.get("https://api.openweathermap.org/data/2.5/weather", getParams);

                            try {
                                JSONObject json = new JSONObject(resp);
                                String weatherType = json.getJSONArray("weather").getJSONObject(0).getString("description");
                                Double temperature = json.getJSONObject("main").getDouble("temp") - 273d;
                                Long humidity = json.getJSONObject("main").getLong("humidity");

                                StringBuilder botResp = new StringBuilder("\nWeather type: ");
                                botResp.append(weatherType).append("\n").append("Temperature: ").append(String.format("%.4f", temperature))
                                                .append("\n").append("Humidity: ").append(humidity).append("%");

                                appendMessage(botResp.toString(), ENTITY_BOT);

                            } catch (JSONException | NullPointerException e) {
                                appendMessage("No such city exists", ENTITY_BOT);
                            }
                        }

                        case currency -> {
                            HashMap<String, String> getParams = new HashMap<>();
                            getParams.put("apikey", API_KEY_CURRENCY);
                            getParams.put("currencies", "RUB");
                            getParams.put("base_currency", params.get(0).toUpperCase());

                            String resp = httpClient.get("https://api.currencyapi.com/v3/latest", getParams);

                            try {
                                JSONObject json = new JSONObject(resp);
                                Double exchangeRate = json.getJSONObject("data").getJSONObject("RUB").getDouble("value");
                                appendMessage(String.format("1 %s = %.3f RUB", params.get(0).toUpperCase(), exchangeRate), ENTITY_BOT);

                                String[] dates = getPreviousWeek();
                                List<Double> values = new ArrayList<>();
                                for (String day : dates) {
                                    HashMap<String, String> historicalRatesParams = new HashMap<>();
                                    historicalRatesParams.put("apikey", API_KEY_CURRENCY);
                                    historicalRatesParams.put("currencies", "RUB");
                                    historicalRatesParams.put("base_currency", params.get(0).toUpperCase());
                                    historicalRatesParams.put("date", day);

                                    String resp2 = httpClient.get("https://api.currencyapi.com/v3/historical", historicalRatesParams);

                                    JSONObject json2 = new JSONObject(resp2);
                                    Double historicalExchangeRate = json2.getJSONObject("data").getJSONObject("RUB").getDouble("value");
                                    values.add(historicalExchangeRate);
                                }

                                drawExchangeRatesPreviousWeek(values);

                            } catch (JSONException | NullPointerException e) {
                                appendMessage("No such currency exists", ENTITY_BOT);
                            }
                        }

                        case chat -> {
                            ChatApplication chat = new ChatApplication();
                            chat.start(BotApplication.getPrimaryStage());
                        }
                    }
                }
            }
        }
    }

    private void drawExchangeRatesPreviousWeek(List<Double> values) {
        Double maxValue = -1d;
        Double minValue = 99999d;

        for (Double value : values) {
            if (value.compareTo(maxValue) > 0) maxValue = value;
            if (value.compareTo(minValue) < 0) minValue = value;
        }

        Double difference = maxValue - minValue;

        String message = "∎";
        for (Double value : values) {
            Double diff = value - minValue;
            for (int i = 0; i < diff * 10; i++) {
                message += "∎";
            }
            appendMessage(message + " " + value, ENTITY_SYSTEM);
            message = "∎";
        }
    }

    private String[] getPreviousWeek() throws ParseException {
        LocalDateTime currentTime = LocalDateTime.now();
        String today = "" + currentTime.getYear() + "-" + currentTime.getMonthValue() + "-" + currentTime.getDayOfMonth();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myDate = dateFormat.parse(today);

        String[] dates = new String[7];

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(myDate);

        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);

            Date previousDate = calendar.getTime();
            String result = dateFormat.format(previousDate);
            dates[i] = result;
        }

        return dates;
    }

    @FXML
    private void initialize() {
        command.setOnKeyPressed(keyEventHandler);
    }

    private void appendMessage(String message, String entity) {
        switch (entity) {
            case ENTITY_USER -> conversation.appendText("you: " + message + "\n");
            case ENTITY_BOT -> conversation.appendText("bot: " + message + "\n");
            case ENTITY_SYSTEM -> conversation.appendText(message + "\n");
        }
    }

    private final String ENTITY_USER = "user";
    private final String ENTITY_BOT = "bot";
    private final String ENTITY_SYSTEM = "system";
}
