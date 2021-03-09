package com.tejpratapsingh.pdfcreator.views.basic;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.io.Serializable;

public class PDFFrameLayout extends PDFView implements Serializable {
    private static final String TAG = "PDFFrameLayout";

    public PDFFrameLayout(Context context) {
        super(context);

        FrameLayout linearLayout = new FrameLayout(context);
        FrameLayout.LayoutParams childLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        linearLayout.setLayoutParams(childLayoutParams);

        super.setView(linearLayout);
    }

    @Override
    public PDFFrameLayout addView(PDFView viewToAdd) {
        getView().addView(viewToAdd.getView());
        super.addView(viewToAdd);
        return this;
    }

    @Override
    public PDFView setLayout(ViewGroup.LayoutParams layoutParams) {
        return super.setLayout(layoutParams);
    }

    @Override
    public FrameLayout getView() {
        return (FrameLayout) super.getView();
    }
}
