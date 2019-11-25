package com.example.checkbud.utils;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.checkbud.R;
import com.example.checkbud.data.CheckEntry;
import com.example.checkbud.databinding.InfoListBinding;

import java.util.Collections;
import java.util.List;

/**
 * {@link CheckAdapter} uses a {@link Cursor} to populate a {@link RecyclerView}.
 */
public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.CheckViewHolder> {

    //context and cursor to be used throughout this adapter.
    private List<CheckEntry> checkEntries;


    /**
     * Constructor
     */
    public CheckAdapter() {
    }

    /**
     * Setup a new view and increase the count.
     *
     * @param parent   that contains the viewHolders.
     * @param viewType not needed for this build
     * @return the new holder.
     */
    @NonNull
    @Override
    public CheckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        InfoListBinding infoListBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.info_list, parent, false);
        return new CheckViewHolder(infoListBinding);
    }

    /**
     * get data for specified position to display
     *
     * @param holder   the viewHolder to be populated
     * @param position of the database provided by the cursor.
     */
    @Override
    public void onBindViewHolder(@NonNull CheckViewHolder holder, int position) {
        CheckEntry currentCheckEntry = checkEntries.get(position);

        holder.bind(currentCheckEntry);
    }

    /**
     * retrieve amount of items to be displayed from cursor
     *
     * @return the the amount.
     */
    @Override
    public int getItemCount() {
        if (checkEntries == null) return 0;
        return checkEntries.size();
    }

    public void setCheckEntries(List<CheckEntry> checkEntries) {
        this.checkEntries = checkEntries;
        Collections.reverse(this.checkEntries);
        notifyDataSetChanged();
    }

    /**
     * holding the child views for the viewHolder to be constructed
     */
    class CheckViewHolder extends RecyclerView.ViewHolder {

        private final InfoListBinding infoListBinding;

        /**
         * Constructor
         *
         * @param infoListBinding is the databinding of the item
         */
        CheckViewHolder(InfoListBinding infoListBinding) {
            super(infoListBinding.getRoot());
            this.infoListBinding = infoListBinding;
        }

        public void bind(CheckEntry checkEntry) {
            infoListBinding.setCheckEntry(checkEntry);
            infoListBinding.executePendingBindings();
        }
    }
}
