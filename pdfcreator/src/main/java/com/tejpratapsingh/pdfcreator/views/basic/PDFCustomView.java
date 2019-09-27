package com.tejpratapsingh.pdfcreator.views.basic;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.io.Serializable;

public class PDFCustomView extends PDFView implements Serializable {

    private PDFCustomView(Context context) {
        super(context);
    }

    private PDFCustomView(Context context, View view, int width, int height) {
        super(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(width, height, 0));

        super.setView(view);
    }

    @Override
    public View getView() {
        return super.getView();
    }
}
