package com.tejpratapsingh.pdfcreator.views;

import android.content.Context;

import com.tejpratapsingh.pdfcreator.views.basic.PDFView;

import java.io.Serializable;

public class PDFFooterView extends PDFView implements Serializable {

    public PDFFooterView(Context context) {
        super(context);
    }

    @Override
    public PDFFooterView addView(PDFView viewToAdd) {
        super.addView(viewToAdd);
        return this;
    }

    @Override
    public PDFFooterView setWeight(float weight) {
        super.setWeight(weight);
        return this;
    }
}
