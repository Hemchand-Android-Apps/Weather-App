package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText enteredText;
    TextView resultTextView;
    TextView descTextView;
    String enteredCity;

    public void getWeather(View view) {
        enteredCity = enteredText.getText().toString();
        String url = "http://openweathermap.org/data/2.5/weather?q=" + enteredCity + "&appid=b6907d289e10d714a6e88b30761fae22";
        try {
            GetWeather getWeather= new GetWeather();
            getWeather.execute(url);
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(enteredText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather : )", Toast.LENGTH_SHORT).show();
        }
    }

    public class GetWeather extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String result = "";
            try {
                URL myUrl = new URL(url[0]);
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader ir = new InputStreamReader(in);
                int data = ir.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = ir.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather : )", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject tempValues = jsonObject.getJSONObject("main");

                String minTemp = tempValues.getString("temp_min");
                String maxTemp = tempValues.getString("temp_max");
                String currentTemp = tempValues.getString("temp");
                resultTextView.setText("Current temp in " + enteredCity + " is : " + currentTemp + "\r\n" + "Minimum " + minTemp + " C" + "\r\n" + "Maximum: " + maxTemp + " C");

                JSONArray descValues = jsonObject.getJSONArray("weather");
                String message = "";

                for (int i = 0; i < descValues.length(); i++) {
                    JSONObject object = descValues.getJSONObject(i);
                    String main = object.getString("main");
                    String description = object.getString("description");

                    message += main + " : " + description + "\r\n";
                }
                descTextView.setText(message);

                enteredText.getText().clear();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather : )", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enteredText = findViewById(R.id.cityInput);
        resultTextView = findViewById(R.id.resultTextView);
        resultTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        descTextView = findViewById(R.id.descTextView);
        descTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }
}
