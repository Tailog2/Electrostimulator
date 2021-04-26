package com.example.electrostimulator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

public class Save extends AppCompatActivity {
    Context packageContext;
    List list;


    public Save(Context packageContext) {
        this.packageContext = packageContext;
    }

    public void saveHistory(int mode, int timePassed, Date startTime, Date finishTime){
        if (list != null) {
            try {
                list = loudHistory();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            list = new List();
            System.out.println(list.history_data);
        }
        Data data = new Data(mode, timePassed, startTime, finishTime);
        Gson gson = new Gson();
        list.history_data.add(data);
        String json = gson.toJson(list);
        SharedPreferences history = packageContext.getSharedPreferences("History2", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = history.edit();
        editor.putString("history_data", String.valueOf(json));
        editor.apply();
    }

    public List loudHistory() throws IOException {

        SharedPreferences history = packageContext.getSharedPreferences("History2", Context.MODE_PRIVATE);
        String textList = history.getString("history_data",null);
        if (textList == null){
            return new List();
        }
        Gson gson = new Gson();
        List list = gson.fromJson(textList, List.class);
        return list;
    }

        private static class List {
            ArrayList<Data> history_data;
            public List (){
                history_data = new ArrayList<>();
            }
        }
        private static class Data {
        private int mode;
        private int duration;
        private Date startTime;
        private Date finishTime;
        public Data(int mode, int duration, Date startTime, Date finishTime) {
            this.mode = mode;
            this.duration = duration;
            this.startTime = startTime;
            this.finishTime = finishTime;
        }
    }
}
