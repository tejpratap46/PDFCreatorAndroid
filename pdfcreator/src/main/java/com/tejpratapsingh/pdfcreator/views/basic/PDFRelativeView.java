package com.tejpratapsingh.pdfcreator.views.basic;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.Serializable;

public class PDFRelativeView extends PDFView implements Serializable {
    private static final String TAG = "PDFRelativeView";

    public PDFRelativeView(Context context) {
        super(context);

        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams childLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeLayout.setLayoutParams(childLayoutParams);

        super.setView(relativeLayout);
    }

    @Override
    public PDFRelativeView addView(PDFView viewToAdd) {
        getView().addView(viewToAdd.getView());
        super.addView(viewToAdd);
        return this;
    }

    @Override
    public PDFRelativeView setLayout(LinearLayout.LayoutParams layoutParams) {
        super.setLayout(layoutParams);
        return this;
    }

    @Override
    public PDFRelativeView setLayout(RelativeLayout.LayoutParams layoutParams) {
        super.setLayout(layoutParams);
        return this;
    }

    @Override
    public RelativeLayout getView() {
        return (RelativeLayout) super.getView();
    }
}

