package com.tejpratapsingh.pdfcreator.views.basic;

import android.content.Context;

import java.io.Serializable;

public class PDFImageView extends PDFView implements Serializable {

    public PDFImageView(Context context) {
        super(context);
    }

    @Override
    public PDFImageView setWeight(float weight) {
        super.setWeight(weight);
        return this;
    }
}
