package com.madira.Adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madira.Model.StateRate_Model;
import com.madira.R;

import java.util.List;

public class StateRate_Adapter extends RecyclerView.Adapter<StateRate_Adapter.MyViewHolder> {

    List<StateRate_Model> Rate_List;
    Context context;
    StateRate_Model stateRate_model;


    public StateRate_Adapter ( Context context,  List<StateRate_Model> Rate_List){
        this.context = context;
        this.Rate_List = Rate_List;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_state_rate, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        stateRate_model = Rate_List.get(position);

        holder.Brand.setText(stateRate_model.getBrand());
        holder.Type.setText(stateRate_model.getLiq_type());
        holder.Price.setText(stateRate_model.getPrice());
        holder.Unit.setText(stateRate_model.getSize()+" "+stateRate_model.getUnit());

    }

    @Override
    public int getItemCount() {
        return Rate_List == null ? 0 : Rate_List.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Brand, Type, Price, Unit;
        public MyViewHolder(View itemView) {
            super(itemView);

            Brand = itemView.findViewById(R.id.brnd);
            Type = itemView.findViewById(R.id.liq);
            Price = itemView.findViewById(R.id.prc);
            Unit = itemView.findViewById(R.id.unt);
        }
    }
}
