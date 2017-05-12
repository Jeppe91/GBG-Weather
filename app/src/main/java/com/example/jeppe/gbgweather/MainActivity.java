package com.example.jeppe.gbgweather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Jesper Kjellqvist on 2017-05-10.
 */

public class MainActivity extends AppCompatActivity
{
    public Context context;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                updateWeatherInfo(view);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void updateWeatherInfo(View view)
    {
        new GetWeatherData().execute();
    }

    private class GetWeatherData extends AsyncTask<Void, Void, Void>
    {
        private String valueTemperature;
        private String unitTemperature;
        private String valueAirPressure;
        private String unitAirPressure;
        private String valueWindSpeed;
        private String unitWindSpeed;
        private String valueRainFall;
        private String unitRainFall;
        private String data;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void...arg0)
        {
            try
            {
                DataHandler dataHandler = new DataHandler(readFromAssets(context,"key.txt"));
                data = dataHandler.getData();
                Log.d(TAG, "Response from url: " + data);
            }

            catch (IOException e)
            {
                Log.d(TAG, "No response: " + data);
            }

            if(data != null)
            {
                try
                {
                    JSONObject jsonObj = new JSONObject(data);
                    JSONObject weather = jsonObj.getJSONObject("Weather");

                    JSONObject temperature = weather.getJSONObject("Temperature");
                    valueTemperature = temperature.getString("Value");
                    unitTemperature = temperature.getString("Unit");

                    JSONObject airPressure = weather.getJSONObject("AirPressure");
                    valueAirPressure = airPressure.getString("Value");
                    unitAirPressure = airPressure.getString("Unit");

                    JSONObject windSpeed = weather.getJSONObject("WindSpeed");
                    valueWindSpeed = windSpeed.getString("Value");
                    unitWindSpeed = windSpeed.getString("Unit");

                    JSONObject rainFall = weather.getJSONObject("RainFall");
                    unitRainFall = rainFall.getString("Unit");
                    valueRainFall = rainFall.getString("Value");
                }

                catch(final JSONException e)
                {
                    //If there is no rain, the json value of "RainFall" will not exist, if that
                    //happens the value will be set to 0.
                    valueRainFall = "0";
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            }

            else
            {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(),
                                       "Couldn't get json from server!",
                                       Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

            TextView temp = (TextView) findViewById(R.id.temperature_data);
            temp.setText(valueTemperature + "  " + unitTemperature);

            TextView air = (TextView) findViewById(R.id.air_pressure_data);
            air.setText(valueAirPressure + "  " + unitAirPressure);

            TextView wind = (TextView) findViewById(R.id.wind_speed_data);
            wind.setText(valueWindSpeed + "  " + unitWindSpeed);

            TextView rain = (TextView) findViewById(R.id.rainfall_data);
            rain.setText(valueRainFall + "  " + unitRainFall);
        }

    }


    public String readFromAssets(Context context, String filename) throws IOException,
            RuntimeException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets()
                                                                                 .open(filename)));

        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();

        while (mLine != null)
        {
            sb.append(mLine);
            mLine = reader.readLine();
        }

        reader.close();
        return sb.toString();
    }
}
