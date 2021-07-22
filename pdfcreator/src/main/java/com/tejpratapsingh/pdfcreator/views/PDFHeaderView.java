package com.tejpratapsingh.pdfcreator.views;

import android.content.Context;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.tejpratapsingh.pdfcreator.views.basic.PDFVerticalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFView;

import java.io.Serializable;

public class PDFHeaderView extends PDFVerticalView implements Serializable {

    public PDFHeaderView(@NonNull Context context) {
        super(context);
    }

    @Override
    public PDFHeaderView addView(@NonNull PDFView viewToAdd) {
        super.addView(viewToAdd);
        return this;
    }

    @Override
    public LinearLayout getView() {
        return (LinearLayout) super.getView();
    }
}
