package com.example.activitieslayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder>{
    private Context context;
    private ArrayList<WeatherRVmodal> weatherRVmodalArrayList;

    public WeatherRVAdapter(ArrayList<WeatherRVmodal> weatherRVmodalArrayList) {
        this.context = context;
        this.weatherRVmodalArrayList = weatherRVmodalArrayList;
    }


    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WeatherRVmodal modal=weatherRVmodalArrayList.get(position);
        holder.TVtemperature.setText(modal.getTemperature()+"°c");
        Picasso.with(context).load("http".concat(modal.getIcon())).into(holder.IVcondition);
        holder.TVwind.setText(modal.getWindSpeed()+"km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(modal.getTime());
            holder.TVtime.setText(output.format(t));

        }catch (ParseException e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return weatherRVmodalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView TVtime,TVtemperature,TVwind;
        private ImageView IVcondition;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            TVtime=itemView.findViewById(R.id.TVtime);
            TVtemperature=itemView.findViewById(R.id.TVtemperature);
            TVwind=itemView.findViewById(R.id.TVwind);
            IVcondition=itemView.findViewById(R.id.IVcondition);
        }
    }
}
