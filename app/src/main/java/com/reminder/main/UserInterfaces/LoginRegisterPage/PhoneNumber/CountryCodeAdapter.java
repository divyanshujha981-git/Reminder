package com.reminder.main.UserInterfaces.LoginRegisterPage.PhoneNumber;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CountryCodeAdapter {
    private final Context context;
    private final AutoCompleteTextView countryCodeAutocomplete;
    private final CountryCodeAdapterCallback callback;
    public interface CountryCodeAdapterCallback {
        void getCode(String code);
    }
    public CountryCodeAdapter (Context context, AutoCompleteTextView countryCodeAutocomplete, CountryCodeAdapterCallback callback) {
        this.context = context;
        this.countryCodeAutocomplete = countryCodeAutocomplete;
        this.callback = callback;
        setUp();
    }

    private void setUp() {
        try {
            // Load the JSON from the assets folder
            String jsonString = loadJSONFromAsset();
            if (jsonString == null) return;

            // Parse the JSON array
            JSONArray countriesJsonArray = new JSONArray(jsonString);
            List<String> countryCodes = new ArrayList<>();

            for (int i = 0; i < countriesJsonArray.length(); i++) {
                JSONObject countryObject = countriesJsonArray.getJSONObject(i);

                String displayName = String.format("%s (%s)", countryObject.getString("name"), countryObject.getString("code"));
                countryCodes.add(displayName);
            }

            // Create an ArrayAdapter and set it to the AutoCompleteTextView
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    context,
                    android.R.layout.simple_dropdown_item_1line,
                    countryCodes
            );
            countryCodeAutocomplete.setAdapter(adapter);

            // Set a default value (e.g., India)
            countryCodeAutocomplete.setText("+91", false);

            // When an item is selected, we only want the code (+91)
            countryCodeAutocomplete.setOnItemClickListener((parent, view, position, id) -> {
                String selectedString = ((String) parent.getItemAtPosition(position)).split(" ")[1];
                Log.d("TAG", "setUp: " + selectedString);
                // Extract the code from the parentheses
                String code = selectedString.substring(selectedString.lastIndexOf("(") + 1, selectedString.lastIndexOf(")"));
                callback.getCode(code);
                countryCodeAutocomplete.setText(code, false); // Update text to show only the code
            });


        }
        catch (JSONException e) {
            Log.e("TAG", "Error parsing JSON from asset", e);
        }
    }


    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = context.getAssets().open("countries.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.e("TAG", "Error reading JSON from asset", ex);
            return null;
        }
        return json;
    }


}
