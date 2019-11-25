package com.example.checkbud.utils;

import android.content.Context;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.checkbud.R;
import com.example.checkbud.data.CheckEntry;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link CheckAdapter} uses a {@link Cursor} to populate a {@link RecyclerView}.
 */
public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.CheckViewHolder> {

    //context and cursor to be used throughout this adapter.
    private final Context ctxt;
    private List<CheckEntry> checkEntries;

    /**
     * Constructor
     *
     * @param ctxt is the context wherein the adapter operates
     */
    public CheckAdapter(@NonNull Context ctxt) {
        this.ctxt = ctxt;
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

        View view = LayoutInflater.from(ctxt).inflate(R.layout.info_list, parent, false);
        view.setFocusable(true);

        return new CheckViewHolder(view);
    }

    /**
     * get data for specified position to display
     *
     * @param holder   the viewHolder to be populated
     * @param position of the database provided by the cursor.
     */
    @Override
    public void onBindViewHolder(@NonNull CheckViewHolder holder, int position) {
        final CheckEntry currentCheckEntry = checkEntries.get(position);

        holder.dateTv.setText(currentCheckEntry.getDate());
        holder.invalidTv.setText(String.valueOf(currentCheckEntry.getInvalid()));
        holder.validTv.setText(String.valueOf(currentCheckEntry.getValid()));
        holder.noteTv.setText(String.valueOf(currentCheckEntry.getNote()));
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
        notifyDataSetChanged();
    }

    /**
     * holding the child views for the viewHolder to be constructed
     */
    class CheckViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.info_date_tv)
        TextView dateTv;
        @BindView(R.id.info_invalid_tv)
        TextView invalidTv;
        @BindView(R.id.info_note_tv)
        TextView noteTv;
        @BindView(R.id.info_valid_tv)
        TextView validTv;

        /**
         * Constructor
         *
         * @param itemView is the view in question
         */
        CheckViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
