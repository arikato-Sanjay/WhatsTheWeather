package skysanjay.t.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultTV;

    public class DownloadWeather extends AsyncTask<String, Void , String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL newURL;
            HttpURLConnection urlConnection = null;
            try {
                newURL = new URL(urls[0]);
                urlConnection = (HttpURLConnection) newURL.openConnection();
                InputStream input = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(input);
                int data = reader.read();

                while (data != -1){
                    char current = (char)  data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject newObject = new JSONObject(s);
                String weatherInfo = newObject.getString("weather");

                JSONArray weatherArray = new JSONArray(weatherInfo);
                String msg = "";
                for (int i=0; i<weatherArray.length();i++){
                    JSONObject JSONPart = weatherArray.getJSONObject(i);
                    String main = JSONPart.getString("main");
                    String description = JSONPart.getString("description");
                    if (!main.equals("") && !description.equals("")){
                        msg += main + ": " + description + "\r\n";
                    }
                }
                if (!msg.equals("")){
                    resultTV.setText(msg);
                } else {
                    Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityNameET);
        resultTV = findViewById(R.id.resultTV);

    }

    public void getWeather(View view){
        try {
            DownloadWeather newWeather = new DownloadWeather();
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(),"UTF-8");
            newWeather.execute("https://openweathermap.org/data/2.5/weather?q="+ encodedCityName +"&appid=439d4b804bc8187953eb36d2a8c26a02");

            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(cityName.getWindowToken(),0);
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could not find weather",Toast.LENGTH_SHORT).show();
        }

    }
}
