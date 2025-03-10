package com.example.assignment1;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferenceManager {
    private SharedPreferences sharedPrefs;
    private Context context;
    private ArrayList<String> eventList;
    public SharedPreferenceManager(Context context) {
        this.sharedPrefs = context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE);
        this.context = context;
    }
    public void saveProfileName(String name)
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("profileName",name );
        editor.commit();
    }

    public String getCount1Name()
    {
        return sharedPrefs.getString("count1Name", null);
    }
    public String getCount2Name()
    {
        return sharedPrefs.getString("count2Name", null);
    }
    public String getCount3Name()
    {
        return sharedPrefs.getString("count3Name", null);
    }
    public String getMaxCount()
    {
        return sharedPrefs.getString("maxCount", null);
    }

    public String putCount1Name(String name)
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("count1Name", name);
        editor.apply();
        Toast.makeText(context, "Saved name: " + getCount1Name(), Toast.LENGTH_SHORT).show();
        return name;
    }

    public String putCount2Name(String name)
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("count2Name", name);
        editor.apply();
        Toast.makeText(context, "Saved name: " + getCount2Name(), Toast.LENGTH_SHORT).show();
        return name;
    }

    public String putCount3Name(String name)
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("count3Name", name);
        editor.apply();
        Toast.makeText(context, "Saved name: " + getCount3Name(), Toast.LENGTH_SHORT).show();
        return name;
    }

    public String putMaxCount(String name)
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("maxCount", name);
        editor.apply();
        Toast.makeText(context, "Saved count: " + getMaxCount(), Toast.LENGTH_SHORT).show();
        return name;
    }

    public void clearSharedPreferences()
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(context, "Cleared shared preferences", Toast.LENGTH_SHORT).show();
    }

    public int getEventCounting() {
        return sharedPrefs.getInt("EventCounting", 0);
    }

    public int putEventCounting(int Count) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt("EventCounting", Count);
        editor.apply();
        Toast.makeText(context, "Saved count: " + getEventCounting(), Toast.LENGTH_SHORT).show();
        return Count;
    }

    public int getEvent1Click() {
        return sharedPrefs.getInt("Event1Click", 0);
    }

    public int putEvent1Click(int Count) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt("Event1Click", Count);
        editor.apply();
        Toast.makeText(context, "Saved count: " + getEvent1Click(), Toast.LENGTH_SHORT).show();
        return Count;
    }

    public int getEvent2Click() {
        return sharedPrefs.getInt("Event2Click", 0);
    }

    public int putEvent2Click(int Count) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt("Event2Click", Count);
        editor.apply();
        Toast.makeText(context, "Saved count: " + getEvent2Click(), Toast.LENGTH_SHORT).show();
        return Count;
    }

    public int getEvent3Click() {
        return sharedPrefs.getInt("Event3Click", 0);
    }

    public int putEvent3Click(int Count) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt("Event3Click", Count);
        editor.apply();
        Toast.makeText(context, "Saved count: " + getEvent3Click(), Toast.LENGTH_SHORT).show();
        return Count;
    }

    public void putEventList(ArrayList<String> eventList) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        JSONArray jsonArray = new JSONArray(eventList);
        editor.putString("eventList", jsonArray.toString());
        editor.apply();
    }

    public ArrayList<String> getEventList() {
        String eventListJson = sharedPrefs.getString("eventList", null);
        ArrayList<String> eventList = new ArrayList<>();
        if (eventListJson != null) {
            try {
                JSONArray jsonArray = new JSONArray(eventListJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    eventList.add(jsonArray.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return eventList;
    }
}
