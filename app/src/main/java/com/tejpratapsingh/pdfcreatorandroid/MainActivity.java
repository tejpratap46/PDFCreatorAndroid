package com.tejpratapsingh.pdfcreatorandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.tejpratapsingh.pdfcreator.activity.PDFCreatorActivity;
import com.tejpratapsingh.pdfcreator.views.basic.PDFTextView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFVerticalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFView;

import java.io.File;

public class MainActivity extends PDFCreatorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        setContentView(R.layout.activity_main);
//
//        PDFView pdfView = new PDFView(getApplicationContext());
//        PDFVerticalView pdfVerticalView = new PDFVerticalView(getApplicationContext());
//        PDFTextView pdfTextView1 = new PDFTextView(getApplicationContext());
//        pdfTextView1.setText("pdfTextView1");
//        pdfVerticalView.addView(pdfTextView1);
//        PDFTextView pdfTextView2 = new PDFTextView(getApplicationContext());
//        pdfTextView2.setText("pdfTextView2");
//        pdfVerticalView.addView(pdfTextView2);
//        pdfView.addView(pdfVerticalView);
//
//        View view = pdfView.getView();
//
//        FrameLayout container = findViewById(R.id.container);
//        container.addView(view);
//
//        Intent intent = new Intent(MainActivity.this, PDFCreatorActivity.class);
//        intent.putExtra(PDFCreatorActivity.EXTRA_HEADER_VIEW, pdfView);
//
//        startActivity(intent);
    }
}
