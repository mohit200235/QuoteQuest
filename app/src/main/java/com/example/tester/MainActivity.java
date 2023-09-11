package com.example.tester;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tester.adapter.RecyclerViewAdapter;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;

    private RecyclerViewAdapter recyclerViewAdapter;

    SwipeRefreshLayout srl;
    public static final String KEY = "UoNn9w7L0pTdRtrYMkcQrFJ4S08Sv0HrQ0BPsOEH";

    List<Quote> quotes;
    private boolean isRefreshing = false;
    private boolean Notification_showed = false;
    private boolean isLoading = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        srl = findViewById(R.id.swipe);

        srl.setOnRefreshListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!srl.isRefreshing() && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    // Load more data here
                    loadMoreData();
                }
            }
        });

        getApiData();

    }

    public void getApiData() {
        isRefreshing = true;
        Retrofit retrofit = RetroFitClass.getRetrofitInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Quote>> quoteCall = apiService.getQuote(KEY);

        quoteCall.enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) {
                isRefreshing = false;
                quotes = response.body();
                if (quotes != null) {

                    if (recyclerViewAdapter == null) {

                        //notification will shown after that
                        Notification_Quote();
//                        getImage();
                        recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, quotes);
                        recyclerView.setAdapter(recyclerViewAdapter);
                    } else {
                        recyclerViewAdapter.addQuotes(quotes);
                    }
                    srl.setRefreshing(false);
                } else {
                    Log.d("response", "onResponse: " + "error");
                    srl.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<Quote>> call, Throwable t) {
                isRefreshing = false;

                Log.d("response", "onFailure: " + t);

                Toast.makeText(MainActivity.this, "There is an internal problem:", Toast.LENGTH_SHORT).show();
                onRefresh();

            }
        });
    }

    private void Notification_Quote() {
        if (!Notification_showed) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                Notification_showed = false;
            }

            long alarmTime = calendar.getTimeInMillis();

            Intent intent = new Intent(MainActivity.this, MyReciever.class);
            intent.putExtra("q", quotes.get(0).getQuote());
            PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setRepeating(AlarmManager.RTC, alarmTime, AlarmManager.INTERVAL_DAY, pi);
            Toast.makeText(MainActivity.this, "Notification created!", Toast.LENGTH_SHORT).show();
            Notification_showed = true;
        }
    }

    private void loadMoreData() {
        isLoading = true;
        getApiData();
    }

    @Override
    public void onRefresh() {
        recyclerViewAdapter = null;
        getApiData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}