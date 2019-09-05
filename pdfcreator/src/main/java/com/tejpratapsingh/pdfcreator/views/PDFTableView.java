package com.tejpratapsingh.pdfcreator.views;

import android.content.Context;

import com.tejpratapsingh.pdfcreator.views.basic.PDFHorizontalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFVerticalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFView;

import java.io.Serializable;

public class PDFTableView extends PDFVerticalView implements Serializable {

    public PDFTableView(Context context) {
        super(context);
    }

    /**
     * Does some thing in old style.
     *
     * @deprecated use {PDFTableRowView.addView()} instead.
     */
    @Deprecated
    @Override
    public PDFTableView addView(PDFView viewToAdd) throws IllegalStateException {
        throw new IllegalStateException("Add a row or column to add view");
    }

    @Override
    public PDFTableView setWeight(float weight) {
        super.setWeight(weight);
        return this;
    }

    public class PDFTableRowView extends PDFHorizontalView {

        public PDFTableRowView(Context context) {
            super(context);
        }

        /**
         * Add row to table, call addRow with equal number of views each time
         *
         * @param views
         * @return
         */
        private PDFTableRowView addRow(PDFView... views) {
            for (PDFView pdfView : views) {
                pdfView.setWeight(1);
                super.addView(pdfView);
            }

            return this;
        }
    }
}
