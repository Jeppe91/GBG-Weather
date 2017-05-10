package com.example.jeppe.gbgweather;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jeppe on 2017-05-10.
 */

public class DataHandeler
{
    private URL url;

    public DataHandeler(String secret_key) throws MalformedURLException
    {
        url = new URL("http://data.goteborg.se/AirQualityService/v1.0/LatestMeasurement/" + secret_key + "?format=Json");
    }

    public String getData() throws IOException
    {
        String response = null;
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        try
        {
            InputStream in = new BufferedInputStream(connection.getInputStream());
            response = convertStreamToString(in);
        }

        finally
        {
            connection.disconnect();
        }

        return response;

    }

    private String convertStreamToString(InputStream stream)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();

        String line;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append('\n');
            }
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        finally
        {
            try
            {
                stream.close();
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

}
