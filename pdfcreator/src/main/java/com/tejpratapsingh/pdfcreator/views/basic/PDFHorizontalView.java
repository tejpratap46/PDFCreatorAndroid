package com.tejpratapsingh.pdfcreator.views.basic;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.Serializable;

public class PDFHorizontalView extends PDFView implements Serializable {

    public PDFHorizontalView(Context context) {
        super(context);

        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        linearLayout.setLayoutParams(childLayoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        super.setView(linearLayout);
    }

    @Override
    public PDFHorizontalView addView(PDFView viewToAdd) {
        getView().addView(viewToAdd.getView());
        super.addView(viewToAdd);
        return this;
    }

    @Override
    public PDFHorizontalView setLayout(ViewGroup.LayoutParams layoutParams) {
        super.setLayout(layoutParams);
        return this;
    }

    @Override
    public LinearLayout getView() {
        return (LinearLayout) super.getView();
    }
}
