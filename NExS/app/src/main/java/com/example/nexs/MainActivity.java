package com.example.nexs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.nexs.adapters.SportsNewsAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Toolbar mainToolBar;
    DrawerLayout drawerLayout;

    //Remove this
    Button feedButton;

    //RecyclerViews
    RecyclerView sportsRv;
    RecyclerView eduRv;
    RecyclerView internationalRv;

    RecyclerView.LayoutManager horizontalLayoutManager;
    RecyclerView.LayoutManager horizontalLayoutManager2;
    RecyclerView.LayoutManager horizontalLayoutManager3;
    SportsNewsAdapter sportsNewsAdapter;
    SportsNewsAdapter educationNewsAdapter;
    SportsNewsAdapter internationalNewsAdapter;

    //temporary data
    ArrayList<NewCard> tempData;
    ArrayList<NewCard> tempData2;
    ArrayList<NewCard> tempData3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tempData = new ArrayList<>();
        tempData.add(new NewCard("Glenn Maxwell bought by RCB",R.drawable.maxwell));
        tempData.add(new NewCard("Glenn Maxwell bought by RCB",R.drawable.maxwell));
        tempData.add(new NewCard("Glenn Maxwell bought by RCB",R.drawable.maxwell));

        tempData2 = new ArrayList<>();
        tempData2.add(new NewCard("New NEP policy",R.drawable.edu_min));
        tempData2.add(new NewCard("New NEP policy",R.drawable.edu_min));
        tempData2.add(new NewCard("New NEP policy",R.drawable.edu_min));

        tempData3 = new ArrayList<>();
        tempData3.add(new NewCard("Biden wins",R.drawable.biden));
        tempData3.add(new NewCard("Biden wins",R.drawable.biden));
        tempData3.add(new NewCard("Biden wins",R.drawable.biden));

        mainToolBar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        drawerLayout = findViewById(R.id.drawer_layout);

        feedButton = findViewById(R.id.feed_button);
        feedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FeedActivity.class));
            }
        });

        sportsRv = findViewById(R.id.sports_news_rv);
        sportsRv.setHasFixedSize(true);

        eduRv = findViewById(R.id.education_news_rv);
        eduRv.setHasFixedSize(true);

        internationalRv = findViewById(R.id.international_news_rv);
        internationalRv.setHasFixedSize(true);

        horizontalLayoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,true);
        horizontalLayoutManager2= new LinearLayoutManager(this,RecyclerView.HORIZONTAL,true);
        horizontalLayoutManager3 = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,true);
        sportsNewsAdapter = new SportsNewsAdapter(tempData);
        educationNewsAdapter = new SportsNewsAdapter(tempData2);
        internationalNewsAdapter = new SportsNewsAdapter(tempData3);


        sportsRv.setLayoutManager(horizontalLayoutManager);
        sportsRv.setAdapter(sportsNewsAdapter);

        eduRv.setLayoutManager(horizontalLayoutManager2);
        eduRv.setAdapter(educationNewsAdapter);

        internationalRv.setLayoutManager(horizontalLayoutManager3);
        internationalRv.setAdapter(internationalNewsAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_menu_search : onSearchRequested();
                return true;
            case android.R.id.home : drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }
}
