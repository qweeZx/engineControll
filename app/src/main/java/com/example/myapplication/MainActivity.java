package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    ArrayList<Entry> voltageEntries = new ArrayList<>();
    LineDataSet voltageDataSet;
    LineData voltageData;
    LineChart voltageChart;

    ArrayList<Entry> currentEntries = new ArrayList<>();
    LineDataSet currentDataSet;
    LineData currentData;
    LineChart currentChart;

    ProgressBar motorProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setLogo(getDrawable(R.drawable.irgups_logo));


        motorProgressBar = findViewById(R.id.motorProgressBar);
        voltageChart = findViewById(R.id.voltageChart);
        currentChart = findViewById(R.id.currentChart);

        currentChart.setAutoScaleMinMaxEnabled(false);
        currentChart.getAxisLeft().setAxisMaximum(12);
        currentChart.getAxisLeft().setAxisMinimum(0);
        currentChart.getAxisRight().setEnabled(false);
        currentChart.setDescription(null);

        voltageChart.setAutoScaleMinMaxEnabled(false);
        voltageChart.getAxisLeft().setAxisMaximum(12);
        voltageChart.getAxisLeft().setAxisMinimum(0);
        voltageChart.getAxisRight().setEnabled(false);
        voltageChart.setDescription(null);



        voltageEntries = new ArrayList<>();
        for(int i = 0; i <= 20; i++){
            voltageEntries.add(new Entry(i,0f));
        }
        voltageDataSet = new LineDataSet(voltageEntries,"voltage");
        voltageData = new LineData(voltageDataSet);
        voltageChart.getXAxis().setValueFormatter(new HourAxisValueFormatter());
        voltageData.setDrawValues(false);
        voltageChart.setData(voltageData);
        voltageChart.invalidate();

        currentEntries = new ArrayList<>();
        for(int i = 0; i <= 20; i++){
            currentEntries.add(new Entry(i,0f));
        }
        currentDataSet = new LineDataSet(currentEntries,"current");
        currentData = new LineData(currentDataSet);
        currentChart.getXAxis().setValueFormatter(new HourAxisValueFormatter());
        currentData.setDrawValues(false);
        currentChart.setData(currentData);
        currentChart.invalidate();

        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                            .url("http://cifra.h1n.ru/3.php?button1=1")
                            .build();
                            try (Response response = client.newCall(request).execute()){}
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                });
                thread.setDaemon(true);
                thread.start();
            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("http://cifra.h1n.ru/3.php?button2=1")
                                .build();
                        try (Response response = client.newCall(request).execute()){}
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.setDaemon(true);
                thread.start();
            }
        });

        Thread thread = new MyThread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void changeVoltageChart(float voltage){
        runOnUiThread(() -> {
            voltageDataSet.removeFirst();
            voltageDataSet.addEntry(new Entry(voltageEntries.get(voltageEntries.size() - 1).getX() + 1, voltage));
            voltageData.notifyDataChanged();
            voltageChart.notifyDataSetChanged();
            voltageChart.invalidate();
        });
    }

    public void changeCurrentChart(float current){
        runOnUiThread(() -> {
            currentDataSet.removeFirst();
            currentDataSet.addEntry(new Entry(currentEntries.get(currentEntries.size() - 1).getX() + 1, current));
            currentData.notifyDataChanged();
            currentChart.notifyDataSetChanged();
            currentChart.invalidate();
        });
    }

    public  void changeMotorProgressBar(int motor){
        runOnUiThread(() -> {
            motorProgressBar.setProgress(motor - 200);
        });
    }

}
class MyThread extends Thread{
    MainActivity activity;
    public MyThread(MainActivity activity){
        this.activity = activity;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient();
        String ans = null;
        Request request = new Request.Builder()
                .url("http://cifra.h1n.ru/json_read.php")
                .build();
        while(true){
            try (Response response = client.newCall(request).execute()){
                ans = response.body().string();
                JSONObject obj = new JSONObject(ans);

                String voltageStr = obj.getString("voltage");
                float voltage = Float.parseFloat(voltageStr);
                activity.changeVoltageChart(voltage);

                String currentStr = obj.getString("current");
                float current = Float.parseFloat(currentStr);
                activity.changeCurrentChart(current);

                int motor;
                String motorStr = obj.getString("motor");
                motor = Integer.parseInt(motorStr);
                activity.changeMotorProgressBar(motor);

                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

