package com.tejpratapsingh.pdfcreatorandroid;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.tejpratapsingh.pdfcreator.activity.PDFViewerActivity;

public class PdfViewerActivity extends PDFViewerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pdf Viewer");
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.colorTransparentBlack)));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
