package com.tejpratapsingh.pdfcreator.views.basic;

import android.content.Context;

import java.io.Serializable;

public class PDFVerticalView extends PDFView implements Serializable {
    private static final String TAG = "PDFVerticalView";

    public PDFVerticalView(Context context) {
        super(context);
    }

    @Override
    public PDFVerticalView addView(PDFView viewToAdd) {
        super.addView(viewToAdd);
        return this;
    }

    @Override
    public PDFVerticalView setWeight(float weight) {
        super.setWeight(weight);
        return this;
    }
}
