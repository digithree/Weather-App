package ie.simonkenny.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    private final static String API_KEY = "fa2c429b43f443af6dc8bf796ae2e8db";

    public interface ApiService {
        @GET("data/2.5/weather?APPID="+API_KEY)
        Call<CurrentWeatherModel> getCurrentWeather(@Query("q") String query);
    }

    ApiService apiService;

    TextView temperatureTextView;
    TextView humidityTextView;
    TextView pressureTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupApiService();

        temperatureTextView = (TextView) findViewById(R.id.temperature_text_view);
        humidityTextView = (TextView) findViewById(R.id.humidity_text_view);
        pressureTextView = (TextView) findViewById(R.id.pressure_text_view);

        Button updateButton = (Button) findViewById(R.id.update_button);
        if (updateButton != null) {
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeCurrentWeatherRequest();
                }
            });
        }
    }


    private void setupApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    private void makeCurrentWeatherRequest() {
        // Set up the call
        Call<CurrentWeatherModel> call = apiService.getCurrentWeather("Limerick,ie");
        // Make the call asynchronously
        call.enqueue(new Callback<CurrentWeatherModel>() {
            @Override
            public void onResponse(Call<CurrentWeatherModel> call, Response<CurrentWeatherModel> response) {
                CurrentWeatherModel currentWeatherModel = response.body();
                if (response.isSuccessful() && currentWeatherModel != null) {
                    // update UI with weather data
                    temperatureTextView.setText(getString(R.string.field_temperature) + currentWeatherModel.getMain().getTemp());
                    humidityTextView.setText(getString(R.string.field_humidity) + currentWeatherModel.getMain().getHumidity());
                    pressureTextView.setText(getString(R.string.field_pressure) + currentWeatherModel.getMain().getPressure());
                    Toast.makeText(getApplicationContext(), "Current Weather updated", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Get Current Weather request failed", Toast.LENGTH_LONG).show();
                    // To get the error description, we run the risk of an IOException error if it doesn't exist
                    // so we must surround in a try / catch blocks.
                    try {
                        // Write the error to the Log
                        Log.d("getCurrentWeather", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherModel> call, Throwable t) {
                // Write the error to the Log
                Log.d("getCurrentWeather", "request failed", t);
                Toast.makeText(getApplicationContext(), "Get Current Weather request failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}
