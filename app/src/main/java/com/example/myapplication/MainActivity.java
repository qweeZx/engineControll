package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    //Объявление элементов GUI
    ArrayList<Entry> voltageEntries = new ArrayList<>();
    LineDataSet voltageDataSet;
    LineData voltageData;
    LineChart voltageChart;

    ArrayList<Entry> currentEntries = new ArrayList<>();
    LineDataSet currentDataSet;
    LineData currentData;
    LineChart currentChart;

    ProgressBar motorProgressBar;
    TextView currentInd;
    TextView voltageInd;
    TextView lvlInd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //настройка Actvity
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //Инициализация графических элеметов
        motorProgressBar = findViewById(R.id.motorProgressBar);
        voltageChart = findViewById(R.id.voltageChart);
        currentChart = findViewById(R.id.currentChart);
        currentInd = findViewById(R.id.currentIndicator);
        voltageInd = findViewById(R.id.voltageIndicator);
        lvlInd = findViewById(R.id.lvlInd);

        //Настройка графиков
        currentChart.setAutoScaleMinMaxEnabled(false);
        currentChart.getAxisLeft().setAxisMaximum(30);
        currentChart.getAxisLeft().setAxisMinimum(0);
        currentChart.getAxisRight().setEnabled(false);
        currentChart.setDescription(null);
        currentChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        currentChart.getLegend().setEnabled(false);
        currentChart.setScaleEnabled(false);

        voltageChart.setAutoScaleMinMaxEnabled(false);
        voltageChart.getAxisLeft().setAxisMaximum(12);
        voltageChart.getAxisLeft().setAxisMinimum(0);
        voltageChart.getAxisRight().setEnabled(false);
        voltageChart.setDescription(null);
        voltageChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        voltageChart.getLegend().setEnabled(false);
        voltageChart.setScaleEnabled(false);

        //Инициализация графиков
        voltageEntries = new ArrayList<>();
        for(int i = 0; i <= 20; i++){
            voltageEntries.add(new Entry(i,0f));
        }
        voltageDataSet = new LineDataSet(voltageEntries,null);
        voltageData = new LineData(voltageDataSet);
        voltageChart.getXAxis().setValueFormatter(new HourAxisValueFormatter());
        voltageData.setDrawValues(false);
        voltageChart.setData(voltageData);
        voltageDataSet.setHighlightEnabled(false);
        voltageDataSet.setColor(Color.parseColor("#0C3CF4"));
        voltageDataSet.setCircleColor(Color.parseColor("#0C3CF4"));
        voltageChart.invalidate();

        currentEntries = new ArrayList<>();
        for(int i = 0; i <= 20; i++){
            currentEntries.add(new Entry(i,0f));
        }
        currentDataSet = new LineDataSet(currentEntries,null);
        currentData = new LineData(currentDataSet);
        currentChart.getXAxis().setValueFormatter(new HourAxisValueFormatter());
        currentData.setDrawValues(false);
        currentChart.setData(currentData);
        currentDataSet.setHighlightEnabled(false);
        currentDataSet.setColor(Color.parseColor("#E91E63"));
        currentDataSet.setCircleColor(Color.parseColor("#E91E63"));
        currentChart.invalidate();

        //Обработка нажатий кнопок
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

    /**
     * Метод добавляет в график новое значение слева и убирает значение справа, работает по принципу FIFO
     * @param voltage значение Y
     */
    public void changeVoltageChart(float voltage){
        runOnUiThread(() -> {
            voltageInd.setText(String.format("Напряжение: %.2f В", voltage));
            voltageDataSet.removeFirst();
            voltageDataSet.addEntry(new Entry(voltageEntries.get(voltageEntries.size() - 1).getX() + 1, voltage));
            voltageData.notifyDataChanged();
            voltageChart.notifyDataSetChanged();
            voltageChart.invalidate();
        });
    }

    /**
     * Метод добавляет в график новое значение слева и убирает значение справа, работает по принципу FIFO
     * @param current значение Y
     */
    public void changeCurrentChart(float current){
        runOnUiThread(() -> {
            currentInd.setText(String.format("Ток: %.2f мА", current));
            currentDataSet.removeFirst();
            currentDataSet.addEntry(new Entry(currentEntries.get(currentEntries.size() - 1).getX() + 1, current));
            currentData.notifyDataChanged();
            currentChart.notifyDataSetChanged();
            currentChart.invalidate();
        });
    }

    /**
     * Изменяет значение мощности
     * @param motor
     */
    public  void changeMotorProgressBar(int motor){
        runOnUiThread(() -> {
            lvlInd.setText(String.format("%d%%", (motor-200)/8));
            motorProgressBar.setProgress(motor - 200);
        });
    }

}

/**
 * Поток отвечающий за получение данных с сервера и отправку их в GUI
 */
class MyThread extends Thread{
    MainActivity activity;
    public MyThread(MainActivity activity){
        this.activity = activity;
    }

    @Override
    public void run() {
        //Установление соединения
        OkHttpClient client = new OkHttpClient();
        String ans = null;
        Request request = new Request.Builder()
                .url("http://cifra.h1n.ru/json_read.php")
                .build();
        while(true){
            //Парсинг json-ответа
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

