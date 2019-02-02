package com.bencorp.papp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

public class FindActivity extends AppCompatActivity {
    RecyclerView recycleView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private JSONArray resultsFound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        setTitle("");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recycleView = (RecyclerView) findViewById(R.id.resultCards);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recycleView.setLayoutManager(layoutManager);
        recycleView.setHasFixedSize(true);
        loadResults();
    }

    private void loadResults(){
        String intent = getIntent().getStringExtra("results");
        try {
            resultsFound = new JSONArray(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = (RecyclerAdapter) new RecyclerAdapter(resultsFound,this,FindActivity.this);
        recycleView.setAdapter(adapter);

    }
}
