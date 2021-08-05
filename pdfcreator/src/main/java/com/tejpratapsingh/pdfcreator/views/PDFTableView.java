package com.tejpratapsingh.pdfcreator.views;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.tejpratapsingh.pdfcreator.views.basic.PDFHorizontalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFLineSeparatorView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFTextView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFVerticalView;
import com.tejpratapsingh.pdfcreator.views.basic.PDFView;

import java.io.Serializable;

public class PDFTableView extends PDFView implements Serializable {
    private static final String TAG = "PDFTableView";
    private final PDFTableRowView headerRow, firstRow;
    private int[] rowWidthPercent = {};

    public PDFTableView(@NonNull Context context, @NonNull PDFTableRowView headerRow, @NonNull PDFTableRowView firstRow) {
        super(context);
        this.headerRow = headerRow;
        this.firstRow = firstRow;

        PDFVerticalView verticalView = new PDFVerticalView(context);
        verticalView.addView(headerRow);
        verticalView.addView(new PDFLineSeparatorView(context).setBackgroundColor(Color.BLACK));
        verticalView.addView(firstRow);
        super.addView(verticalView);
    }

    /**
     * Does some thing in old style.
     *
     * @deprecated use {addRow()} instead.
     */
    @Deprecated
    @Override
    public PDFTableView addView(@NonNull PDFView viewToAdd) throws IllegalStateException {
        throw new IllegalStateException("Add a row or column to add view");
    }

    /**
     * Add new row to table
     * A new row will be added with columnWidth (if provided)
     *
     * @param rowView row to add
     * @return current instance
     */
    public PDFTableView addRow(@NonNull PDFTableRowView rowView) {
        if (this.rowWidthPercent.length > 0) {
            rowView.setColumnWidth(this.rowWidthPercent);
        }
        super.addView(rowView);
        return this;
    }

    /**
     * Set column width for every row
     * After calling this function, every row and header will follow this column width guideline
     *
     * @param columnWidthPercent width in percent {sum should be 100 percent}
     * @return current instance
     */
    public PDFTableView setColumnWidth(@NonNull int... columnWidthPercent) {
        this.headerRow.setColumnWidth(columnWidthPercent);
        this.firstRow.setColumnWidth(columnWidthPercent);
        for (PDFView pdfTableRow : this.getChildViewList()) {
            if (pdfTableRow instanceof PDFTableRowView) {
                ((PDFTableRowView) pdfTableRow).setColumnWidth(columnWidthPercent);
            }
        }
        this.rowWidthPercent = columnWidthPercent;
        return this;
    }

    public PDFTableView addSeparatorRow(PDFLineSeparatorView separatorView) {
        super.addView(separatorView);
        return this;
    }

    @Override
    public PDFView setLayout(@NonNull ViewGroup.LayoutParams layoutParams) {
        return super.setLayout(layoutParams);
    }

    public static class PDFTableRowView extends PDFHorizontalView implements Serializable {

        public PDFTableRowView(@NonNull Context context) {
            super(context);
        }

        /**
         * Does some thing in old style.
         *
         * @deprecated use {PDFTableRowView.addToRow()} instead.
         */
        @Deprecated
        @Override
        public PDFHorizontalView addView(@NonNull PDFView viewToAdd) {
            throw new IllegalStateException("Cannot add subview to Horizontal View, Use createRow instead");
        }

        /**
         * Set custom weight to each column in a row
         *
         * @param columnWidthPercent percent weight of column out of 100
         * @return current instance
         */
        public PDFTableRowView setColumnWidth(int... columnWidthPercent) {
            for (int i = 0; i < this.getChildViewList().size(); i++) {
                float columnWeight = 100F;
                if (i < columnWidthPercent.length) {
                    columnWeight = columnWidthPercent[i];
                } else {
                    columnWeight = columnWidthPercent[columnWidthPercent.length - 1];
                }
                PDFView pdfView = this.getChildViewList().get(i);
                pdfView.setLayout(new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT, (columnWeight / 100)));
            }
            return this;
        }

        /**
         * Add row to table, call addRow with equal number of views each time
         *
         * @param TextViewToAdd add text
         * @return current instance
         */
        public PDFTableRowView addToRow(@NonNull PDFTextView TextViewToAdd) {
            TextViewToAdd.setLayout(new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            super.addView(TextViewToAdd);

            return this;
        }
    }
}
