package com.example.vizora;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List; // Fontos import

public class WaterClockLogAdapter extends RecyclerView.Adapter<WaterClockLogAdapter.LogItemViewHolder> {

    // A belső adatlista, amit az Adapter megjelenít
    private List<WaterClockLogItem> mDataList;

    // Listener interface a listaelem/gombkattintások kezelésére az Activity-ben
    public interface OnItemClickListener {
        void onDeleteClick(WaterClockLogItem item); // Törlés gomb kattintás
        void onItemClick(WaterClockLogItem item);
        void onUpdateClick(WaterClockLogItem item);
    }

    private OnItemClickListener listener;

    // Konstruktor
    public WaterClockLogAdapter() {
        this.mDataList = new ArrayList<>();
    }


    public void setData(List<WaterClockLogItem> newData) {
        mDataList.clear();
        mDataList.addAll(newData);
        notifyDataSetChanged();

    }

    // Setter a listener beállításához
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public LogItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Itt "fújjuk fel" az item layout fájlt!
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_water_clock_log, parent, false);
        return new LogItemViewHolder(itemView);
    }

    // Ez a metódus tölti fel az adatokat a ViewHolder-ben lévő nézetekbe
    @Override
    public void onBindViewHolder(@NonNull LogItemViewHolder holder, int position) {


        int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition == RecyclerView.NO_POSITION) {
            // Elvileg nem szabadna ide jutnunk normálisan, de jó védekezni
            return;
        }

        WaterClockLogItem currentItem = mDataList.get(adapterPosition);

        // Feltöltjük a TextView-kat az adatokkal az item layoutból
        holder.textViewEmail.setText(currentItem.getEmail());
        holder.textViewContractNumber.setText("Szerződésszám: " + currentItem.getContractNumber()); // Formázás
        holder.textViewWaterClockId.setText("Gyári azonosító: " + currentItem.getWaterclockID());   // Formázás
        holder.textViewWaterCubic.setText("Mért érték: " + currentItem.getWaterCubic() + " m³"); // Formázás

        // Kattintáskezelő a Törlés gombhoz
        holder.buttonDelete.setOnClickListener(v -> {
            // Megszerezzük a pontos elemet a kattintás pillanatában a friss pozíció alapján
            int clickedPosition = holder.getAdapterPosition();
            if (listener != null && clickedPosition != RecyclerView.NO_POSITION) {
                WaterClockLogItem clickedItem = mDataList.get(clickedPosition);
                listener.onDeleteClick(clickedItem); // Átadjuk a listenernek az elemet (benne az ID-val)
            }
        });
        holder.buttonUpdate.setOnClickListener(v -> {
            // Megszerezzük a pontos elemet a kattintás pillanatában a friss pozíció alapján
            int clickedPosition = holder.getAdapterPosition();
            if (listener != null && clickedPosition != RecyclerView.NO_POSITION) {
                WaterClockLogItem clickedItem = mDataList.get(clickedPosition);
                listener.onUpdateClick(clickedItem); // Átadjuk a listenernek az elemet (benne az ID-val)
            }
        });

        // Kattintáskezelő az egész listaelemhez (pl. módosításhoz vagy részletekhez)
        holder.itemView.setOnClickListener(v -> {
            int clickedPosition = holder.getAdapterPosition();
            if (listener != null && clickedPosition != RecyclerView.NO_POSITION) {
                WaterClockLogItem clickedItem = mDataList.get(clickedPosition);
                listener.onItemClick(clickedItem); // Átadjuk a listenernek az elemet
            }
        });

    }

    // Visszaadja az elemek számát a listában
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    // Ez a belső ViewHolder osztály
    public static class LogItemViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEmail;
        TextView textViewContractNumber;
        TextView textViewWaterClockId;
        TextView textViewWaterCubic;
        Button buttonDelete; // A törlés gomb referenciája
        Button buttonUpdate;

        public LogItemViewHolder(@NonNull View itemView) {
            super(itemView);
            // Megkeressük a nézeteket az item layoutban az ID-k alapján
            textViewEmail = itemView.findViewById(R.id.textViewItemEmail);
            textViewContractNumber = itemView.findViewById(R.id.textViewItemContractNumber);
            textViewWaterClockId = itemView.findViewById(R.id.textViewItemWaterClockId);
            textViewWaterCubic = itemView.findViewById(R.id.textViewItemWaterCubic);
            buttonDelete = itemView.findViewById(R.id.buttonItemDelete);
            buttonUpdate = itemView.findViewById(R.id.buttonItemModify);
        }
    }
}
