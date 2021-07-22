package com.tejpratapsingh.pdfcreator.views.basic;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.Space;

/**
 * This view takes remaining height of the page, and next content will start from new page.
 */
public class PDFPageBreakView extends PDFView {
    public PDFPageBreakView(Context context) {
        super(context);

        this.setView(new Space(context))
                .setLayout(new LinearLayout.LayoutParams(1, 1, 0));
    }
}
