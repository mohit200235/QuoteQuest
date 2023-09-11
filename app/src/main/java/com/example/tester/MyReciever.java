package com.example.tester;

import static android.content.Context.NOTIFICATION_SERVICE;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyReciever extends BroadcastReceiver {

    public static final String CHANNEL_ID = "channel id";

    public static final int NOTIFICATION_ID = 100;
    @Override
    public void onReceive(Context context, Intent intent) {

//        String quote = intent.getStringExtra("q");
        fetchNewQuote(context);

    }

    private void fetchNewQuote(Context context) {
        // Make a network request to get a new quote
        Retrofit retrofit = RetroFitClass.getRetrofitInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Quote>> quoteCall = apiService.getQuote(MainActivity.KEY);

        quoteCall.enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(Call<List<Quote>> call, Response<List<Quote>> response) {
                List<Quote> quotes = response.body();

                if (quotes != null && !quotes.isEmpty()) {
                    // Display the newly fetched quote in the notification
                    String newQuote = quotes.get(0).getQuote();
                    showNotification(context, newQuote);
                }else{
                    showNotification(context,"Hey not feeling great, " +
                            "Just read some Quotes here 'Discover infinite inspirations'");
                }
            }

            @Override
            public void onFailure(Call<List<Quote>> call, Throwable t) {
                showNotification(context,"Hey not feeling great, " +
                        "Just read some Quotes here 'Discover infinite inspirations'");
            }
        });
    }

    private void showNotification(Context context, String newQuote) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder;
//        Notification notification;

        //Intent in app using click

        Intent notify = new Intent(context,MainActivity.class);
        notify.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context,101,notify,PendingIntent.FLAG_UPDATE_CURRENT);

        //Inbox style

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                .addLine(newQuote)
                .setBigContentTitle("Quote")
                .setSummaryText("Today's quote");

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle()
                .bigText(newQuote)
//                .setBigContentTitle("Today's Quote")
                .setSummaryText("Today's quote");

        Drawable drawable = ResourcesCompat.getDrawable(context.getResources() ,R.drawable.quotequest,null);

        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;

        Bitmap bitmap = bitmapDrawable.getBitmap();

        notificationBuilder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.quotequest)
                .setContentText(newQuote)
//                .setSubText("Today's Quote:")
                .setStyle(bigTextStyle)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "New Channel", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);


//        notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID,"New Channel"
//                , NotificationManager.IMPORTANCE_HIGH));

        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());


    }

}
