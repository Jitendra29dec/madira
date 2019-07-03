package com.madira.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.madira.Model.State_model;
import com.madira.R;
import com.madira.Rate_StateWise_Activity;

import java.util.List;

public class StateAdapter extends RecyclerView.Adapter<StateAdapter.MyViewHolder> {
    List<State_model> State_List;
    State_model state_model;
    Context context;


    public StateAdapter(Context context,List<State_model> State_List ){
        this.context = context;
        this.State_List = State_List;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_state, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        state_model = State_List.get(position);

        holder.State_Name.setText(state_model.getState_Name());

        holder.Main_lay.setTag(position);

        holder.Main_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, Rate_StateWise_Activity.class);
                i.putExtra("state_id",State_List.get(Integer.parseInt(v.getTag().toString())).getState_Id());
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return State_List == null ? 0: State_List.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView State_Name;
        RelativeLayout Main_lay;
        public MyViewHolder(View itemView) {
            super(itemView);
            State_Name = itemView.findViewById(R.id.state_name);
            Main_lay = itemView.findViewById(R.id.main_lay);
        }
    }
}
