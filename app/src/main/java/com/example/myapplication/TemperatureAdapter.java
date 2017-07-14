package com.example.myapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TemperatureAdapter extends RecyclerView.Adapter<TemperatureAdapter.ViewHolder> {
    public final Context context;
    private final List<RestClient.Temperature> titles;

    public TemperatureAdapter(Context context) {
        this.context = context;
        titles = new ArrayList<>();
    }

    public void setTemperatures(List<RestClient.Temperature> temperatures) {
        titles.clear();
        titles.addAll(temperatures);
        notifyDataSetChanged();
    }

    @Override
    public TemperatureAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TemperatureAdapter.ViewHolder holder, int position) {
        RestClient.Temperature temperature = titles.get(position);
        holder.maxTempTw.setText("Max: " + temperature.getTemp_max() + "º");
        holder.minTempTw.setText("Min: " + temperature.getTemp_min() + "º");
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView maxTempTw;
        public final TextView minTempTw;
        public ViewHolder(View itemView) {
            super(itemView);
            maxTempTw = (TextView) itemView.findViewById(R.id.max_temp_tw);
            minTempTw = (TextView) itemView.findViewById(R.id.min_temp_tw);
        }
    }
}
