package com.tejpratapsingh.pdfcreator.views.basic;

import android.content.Context;

import java.io.Serializable;

public class PDFHorizontalView extends PDFView implements Serializable {

    public PDFHorizontalView(Context context) {
        super(context);
    }

    @Override
    public PDFHorizontalView addView(PDFView viewToAdd) {
        super.addView(viewToAdd);
        return this;
    }

    @Override
    public PDFHorizontalView setWeight(float weight) {
        super.setWeight(weight);
        return this;
    }
}
