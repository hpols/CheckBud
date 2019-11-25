package com.example.checkbud;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import com.example.checkbud.data.CheckEntry;
import com.example.checkbud.data.CheckViewModel;
import com.example.checkbud.databinding.ActivityOverviewBinding;
import com.example.checkbud.utils.CheckAdapter;

import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    private ActivityOverviewBinding overviewBinding;
    private CheckAdapter checkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overviewBinding = DataBindingUtil.setContentView(this, R.layout.activity_overview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        checkAdapter = new CheckAdapter(this);
        overviewBinding.recycler.setLayoutManager(layoutManager);
        overviewBinding.recycler.setAdapter(checkAdapter);

        CheckViewModel checkViewModel = ViewModelProviders.of(this).get(CheckViewModel.class);
        checkViewModel.getCheckEntries().observe(this, new Observer<List<CheckEntry>>() {
            @Override
            public void onChanged(@Nullable List<CheckEntry> checkEntries) {
                checkAdapter.setCheckEntries(checkEntries);

                if (checkAdapter.getItemCount() == 0) {
                    overviewBinding.infoEmptyTv.setVisibility(View.VISIBLE);
                } else {
                    overviewBinding.infoEmptyTv.setVisibility(View.GONE);
                }
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

}
