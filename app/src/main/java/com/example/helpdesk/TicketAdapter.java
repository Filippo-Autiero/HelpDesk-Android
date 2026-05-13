package com.example.helpdesk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private ArrayList<Ticket> ticketList;
    private OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onTicketClick(Ticket ticket);
    }

    public TicketAdapter(ArrayList<Ticket> ticketList, OnTicketClickListener listener) {
        this.ticketList = ticketList;
        this.listener = listener;
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvTicketId;
        TextView tvTitolo;
        TextView tvDove;
        TextView tvData;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTicketId = itemView.findViewById(R.id.tvTicketId);
            tvTitolo = itemView.findViewById(R.id.tvTitolo);
            tvDove = itemView.findViewById(R.id.tvDove);
            tvData = itemView.findViewById(R.id.tvData);
        }
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);

        holder.tvTicketId.setText("Ticket #" + ticket.ID);
        holder.tvTitolo.setText(ticket.Titolo != null ? ticket.Titolo : "");
        holder.tvDove.setText(ticket.Dove != null ? ticket.Dove : "");
        holder.tvData.setText(ticket.Dataapertura != null ? ticket.Dataapertura : "");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTicketClick(ticket);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public void setTicketList(ArrayList<Ticket> ticketList) {
        this.ticketList = ticketList;
        notifyDataSetChanged();
    }
}