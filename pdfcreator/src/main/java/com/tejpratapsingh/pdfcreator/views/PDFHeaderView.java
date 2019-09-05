package com.tejpratapsingh.pdfcreator.views;

import android.content.Context;

import com.tejpratapsingh.pdfcreator.views.basic.PDFView;

import java.io.Serializable;

public class PDFHeaderView extends PDFView implements Serializable {

    public PDFHeaderView(Context context) {
        super(context);
    }

    @Override
    public PDFHeaderView addView(PDFView viewToAdd) {
        super.addView(viewToAdd);
        return this;
    }

    @Override
    public PDFHeaderView setWeight(float weight) {
        super.setWeight(weight);
        return this;
    }
}
