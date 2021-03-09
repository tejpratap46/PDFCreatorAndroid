package com.tejpratapsingh.pdfcreator.views;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tejpratapsingh.pdfcreator.views.basic.PDFHorizontalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFLineSeparatorView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFTextView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFVerticalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFView;

import java.io.Serializable;

public class PDFTableView extends PDFView implements Serializable {

    public PDFTableView(Context context, PDFTableRowView headerRow, PDFTableRowView firstRow) {
        super(context);
        PDFVerticalView verticalView = new PDFVerticalView(context);
        verticalView.addView(headerRow);
        verticalView.addView(new PDFLineSeparatorView(context).setBackgroundColor(Color.BLACK));
        verticalView.addView(firstRow);
        super.addView(verticalView);
    }

    /**
     * Does some thing in old style.
     *
     * @deprecated use {addView()} instead.
     */
    @Deprecated
    @Override
    public PDFTableView addView(PDFView viewToAdd) throws IllegalStateException {
        throw new IllegalStateException("Add a row or column to add view");
    }

    public PDFTableView addRow(PDFTableRowView rowView) {
        super.addView(rowView);
        return this;
    }

    public PDFTableView addSeparatorRow(PDFLineSeparatorView separatorView) {
        super.addView(separatorView);
        return this;
    }

    @Override
    public PDFView setLayout(ViewGroup.LayoutParams layoutParams) {
        return super.setLayout(layoutParams);
    }

    public static class PDFTableRowView extends PDFHorizontalView implements Serializable {

        public PDFTableRowView(Context context) {
            super(context);
        }

        /**
         * Does some thing in old style.
         *
         * @deprecated use {PDFTableRowView.addToRow()} instead.
         */
        @Deprecated
        @Override
        public PDFHorizontalView addView(PDFView viewToAdd) {
            throw new IllegalStateException("Cannot add subview to Horizontal View, Use createRow instead");
        }

        /**
         * Add row to table, call addRow with equal number of views each time
         *
         * @param TextViewToAdd add text
         * @return
         */
        public PDFTableRowView addToRow(PDFTextView TextViewToAdd) {
            TextViewToAdd.setLayout(new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            super.addView(TextViewToAdd);

            return this;
        }
    }
}
