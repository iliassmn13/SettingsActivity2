package com.example.assignment1;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SharedPreferenceManager {
    private SharedPreferences sharedPrefs;
    private Context context;
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
        Toast.makeText(context, "Saved name: " + getCount1Name(), Toast.LENGTH_LONG).show();
        return name;
    }

    public String putCount2Name(String name)
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("count2Name", name);
        editor.apply();
        Toast.makeText(context, "Saved name: " + getCount2Name(), Toast.LENGTH_LONG).show();
        return name;
    }

    public String putCount3Name(String name)
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("count3Name", name);
        editor.apply();
        Toast.makeText(context, "Saved name: " + getCount3Name(), Toast.LENGTH_LONG).show();
        return name;
    }

    public String putMaxCount(String name)
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("maxCount", name);
        editor.apply();
        Toast.makeText(context, "Saved count: " + getMaxCount(), Toast.LENGTH_LONG).show();
        return name;
    }

    public void clearSharedPreferences()
    {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(context, "Cleared shared preferences", Toast.LENGTH_LONG).show();
    }
}
