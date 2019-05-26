package com.fxn.pixeditorsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.pixeditor.EditOptions;
import com.fxn.pixeditor.PixEditor;
import com.fxn.pixeditor.imageeditengine.interfaces.AddMoreImagesListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivityJava extends AppCompatActivity {
    EditOptions editoptions;
    private int RequestCode = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editoptions = EditOptions.init();
        editoptions.setRequestCode(124);
        editoptions.setAddMoreImagesListener(new AddMoreImagesListener() {
            @Override
            public void addMore(AppCompatActivity appCompatActivity,
                                ArrayList<String> arrayList, int i) {
                Pix.start(appCompatActivity, Options.init().setRequestCode(RequestCode)
                        .setCount(i)
                        .setPreSelectedUrls(arrayList));
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pix.start(MainActivityJava.this, Options.init()
                        .setRequestCode(RequestCode)
                        .setCount(5));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode1, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode1, resultCode, data);
        Log.e("data init", "requestCode1->  " + requestCode1 + "  resultCode->  " + resultCode);
        if (resultCode == Activity.RESULT_OK && requestCode1 == RequestCode) {
            final ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            for (String s : returnValue) {
                Log.e("data init", "->  " + s);
            }
            editoptions.setSelectedlist(returnValue);
            PixEditor.start(MainActivityJava.this, editoptions);
        }

        if (resultCode == Activity.RESULT_OK && requestCode1 == 124) {
            ArrayList<String> returnValue = data.getStringArrayListExtra(PixEditor.IMAGE_RESULTS);
            for (String s : returnValue) {
                Log.e("data", "->  " + s);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}