package com.example.tester.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tester.ApiService;
import com.example.tester.Quote;
import com.example.tester.R;
import com.example.tester.RetroFitClass;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    public static final String KEY = "YOUR_API_KEY";

    private List<Quote> quotes;


    public RecyclerViewAdapter(Context context, List<Quote> quotes){
        this.context = context;
        this.quotes= quotes;
    }
    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.quotes_card_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        holder.new_quote.setText("'" + quotes.get(position).getQuote() + "'");
        holder.author.setText("~ "+quotes.get(position).getAuthor());
        holder.category.setText(quotes.get(position).getSuccess() + " :");

        getImageForQuote(holder.cardView,position);

        textPainted(holder.new_quote);
        textPainted(holder.author);
        textPainted(holder.category);
        holder.new_quote.setTextColor(Color.WHITE);
        holder.author.setTextColor(Color.WHITE);
        holder.category.setTextColor(Color.GREEN);
    }

    private static void textPainted(TextView textView) {
        TextPaint textPaint = textView.getPaint();

// Set the outline color and thickness

        textPaint.setStrokeWidth(1); // Adjust thickness as needed
//        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setColor(Color.BLACK); // Outline color

// Optionally, set the outline to be drawn as a stroke
        textView.setLayerType(View.LAYER_TYPE_SOFTWARE, textPaint);

// Set the shadow to create the outline effect
        textView.setShadowLayer(10, 0, 0, Color.BLACK);
//        applyTextShadow(holder.new_quote);
    }

    private void applyTextShadow(TextView textView) {
        float shadowRadius = 2.0f;
        float shadowDx = 0.0f;
        float shadowDy = 0.0f;
        int shadowColor = Color.WHITE; // Adjust the shadow color as needed

        textView.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
    }

    private void getImageForQuote(CardView cardView, int position) {
        Retrofit retrofit = RetroFitClass.getRetrofitInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        // Make an API request to fetch a new image for the current quote
        Call<ResponseBody> imageCall = apiService.getImage("nature", KEY, "image/jpg");
        imageCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        byte[] imageBytes = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                        // Create a Drawable from the downloaded image
                        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);

                        // Set the background of the card view
                        cardView.setBackground(drawable);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.d("response2", "onResponse: error");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("response", "onFailure: " + t);
                Toast.makeText(context, "There is an internal problem:", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addQuotes(List<Quote> newQuotes) {
        int startPosition = quotes.size();
        quotes.addAll(newQuotes);
        notifyItemRangeInserted(startPosition, newQuotes.size());
    }



    @Override
    public int getItemCount() {
        return quotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView new_quote,author,category;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            new_quote = itemView.findViewById(R.id.new_quote);
            author = itemView.findViewById(R.id.author);
            category = itemView.findViewById(R.id.category);
            cardView = itemView.findViewById(R.id.card);
        }
    }
}
