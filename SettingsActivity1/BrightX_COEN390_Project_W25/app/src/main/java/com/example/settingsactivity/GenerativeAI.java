package com.example.settingsactivity;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GenerativeAI {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private String API_KEY = "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";

    public GenerativeAI(Context context) {
    }

    public List<String> getSuggestions(final String query, final String type, final String selectedBrand) { //Function to get AI-generated suggestions based on input type and query.
        List<String> suggestions = new ArrayList<>();

        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            String requestContent = "";

            if (type.equals("brand")) {
                if (selectedBrand.length() > 0) {
                    return suggestions;
                }

                if (query.length() == 0) {
                    requestContent = "Give me the 5 most popular professional camera brands. Only return a comma separated list of brand names with no extra text.";
                } else {
                    requestContent = "Give me ONLY camera brands that START WITH '" + query + "'. Return exactly the matching brands as a comma separated list with no extra text. If no brands start with this text, return empty.";
                }
            } else if (type.equals("model")) {
                if (selectedBrand.length() == 0) {
                    return suggestions;
                }

                if (query.length() == 0) {
                    requestContent = "Give me exactly 3 of the most popular professional camera models from the brand '" + selectedBrand + "'. Only return model names as a comma-separated list with no extra text.";
                } else {
                    requestContent = "Give me ONLY camera models from brand '" + selectedBrand + "' that START WITH '" + query + "'. Return exactly the matching models as a comma-separated list with no extra text. If no models start with this text, return empty.";
                }
            } else if (type.equals("lens")) {
                if (query.length() == 0) {
                    requestContent = "Give me exactly 3 of the most popular professional camera lenses used by photographers. Only return lens names as a comma-separated list with no extra text. Include specific lens models like '24-70mm f/2.8'.";
                } else {
                    requestContent = "Give me ONLY camera lenses that START WITH '" + query + "'. Return exactly the matching lenses as a comma-separated list with no extra text. If no lenses start with this text, return empty.";
                }
            }

            JSONObject json = new JSONObject();
            json.put("model", "gpt-4o");

            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", requestContent);
            messages.put(userMessage);
            json.put("messages", messages);

            json.put("temperature", 0.3);
            json.put("max_tokens", 100);

            String jsonString = json.toString();
            OutputStream os = connection.getOutputStream();
            os.write(jsonString.getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            System.out.println("API Response: " + response.toString());

            JSONObject responseJson = new JSONObject(response.toString());
            JSONArray choices = responseJson.getJSONArray("choices");

            if (choices.length() > 0) {
                JSONObject messageObject = choices.getJSONObject(0).getJSONObject("message");
                String result = messageObject.getString("content");

                if (result.trim().isEmpty()) {
                    return suggestions;
                }

                if (result.toLowerCase().contains("no brands")) {
                    return suggestions;
                }

                if (result.toLowerCase().contains("no models")) {
                    return suggestions;
                }

                if (result.toLowerCase().contains("no lenses")) {
                    return suggestions;
                }

                if (result.toLowerCase().contains("empty")) {
                    return suggestions;
                }

                if (result.toLowerCase().contains("cannot find")) {
                    return suggestions;
                }

                String[] parts = result.split(",");
                for (int i = 0; i < parts.length; i++) {
                    String cleaned = parts[i].trim();
                    if (cleaned.length() > 0) {
                        suggestions.add(cleaned);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return suggestions;
    }
}