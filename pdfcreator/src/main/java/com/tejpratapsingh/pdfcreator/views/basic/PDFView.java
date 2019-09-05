package com.tejpratapsingh.pdfcreator.views.basic;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tejpratapsingh.pdfcreator.views.PDFFooterView;
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView;

import java.io.Serializable;
import java.util.ArrayList;

public class PDFView implements Serializable {
    private static final String TAG = "PDFView";

    private int paddingTop = 0, paddingRight = 0, paddingBottom = 0, paddingLeft = 0;

    private ArrayList<PDFView> childViewList = new ArrayList<>();

    private float layoutWeight = 1;

    private boolean hasParent = false;

    private View currentView = null;

    private Context context;

    public PDFView(Context context) {
        this.context = context;
        Log.d(TAG, "PDFView: initiated: " + this.getClass().getSimpleName());

        this.currentView = PDFView.convertToView(context, this);
    }

    public PDFView setPadding(int left, int top, int right, int bottom) {
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
        this.paddingLeft = left;

        this.currentView.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);

        return this;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public PDFView addView(PDFView viewToAdd) throws IllegalStateException {
        if (viewToAdd.hasParent) {
            throw new IllegalStateException("View already has parent");
        }
        if (this instanceof PDFVerticalView) {
            ((LinearLayout) this.currentView).addView(viewToAdd.currentView);
        } else if (this instanceof PDFHorizontalView) {
            ((LinearLayout) this.currentView).addView(viewToAdd.currentView);
        } else if (this instanceof PDFHeaderView) {
            ((LinearLayout) this.currentView).addView(viewToAdd.currentView);
        } else if (this instanceof PDFFooterView) {
            ((LinearLayout) this.currentView).addView(viewToAdd.currentView);
        } else if (this instanceof PDFImageView) {
            throw new IllegalStateException("Adding subview to Image is not allowed");
        } else if (this instanceof PDFTextView) {
            throw new IllegalStateException("Adding subview to TextView is not allowed");
        } else {
            ((FrameLayout) this.currentView).addView(viewToAdd.currentView);
        }
        viewToAdd.hasParent = true;
        childViewList.add(viewToAdd);
        return this;
    }

    public ArrayList<PDFView> getChildViewList() {
        return childViewList;
    }

    public PDFView setWeight(float weight) {
        this.layoutWeight = weight;
        return this;
    }

    public float getLayoutWeight() {
        return layoutWeight;
    }

    public View getView() {
        return this.currentView;
    }

    public static View convertToView(Context context, PDFView pdfViewToConvert) {
        if (pdfViewToConvert instanceof PDFVerticalView) {
            LinearLayout childView = new LinearLayout(context);
            LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, pdfViewToConvert.layoutWeight);
            childView.setLayoutParams(childLayoutParams);
            childView.setOrientation(LinearLayout.VERTICAL);
            childView.setPadding(pdfViewToConvert.paddingLeft, pdfViewToConvert.paddingTop, pdfViewToConvert.paddingRight, pdfViewToConvert.paddingBottom);

            return childView;
        } else if (pdfViewToConvert instanceof PDFHorizontalView) {
            LinearLayout childView = new LinearLayout(context);
            LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, pdfViewToConvert.layoutWeight);
            childView.setLayoutParams(childLayoutParams);
            childView.setOrientation(LinearLayout.HORIZONTAL);
            childView.setPadding(pdfViewToConvert.paddingLeft, pdfViewToConvert.paddingTop, pdfViewToConvert.paddingRight, pdfViewToConvert.paddingBottom);

            return childView;
        } else if (pdfViewToConvert instanceof PDFHeaderView) {
            LinearLayout childView = new LinearLayout(context);
            LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, pdfViewToConvert.layoutWeight);
            childView.setLayoutParams(childLayoutParams);
            childView.setOrientation(LinearLayout.VERTICAL);
            childView.setPadding(pdfViewToConvert.paddingLeft, pdfViewToConvert.paddingTop, pdfViewToConvert.paddingRight, pdfViewToConvert.paddingBottom);

            return childView;
        } else if (pdfViewToConvert instanceof PDFFooterView) {
            LinearLayout childView = new LinearLayout(context);
            LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, pdfViewToConvert.layoutWeight);
            childView.setLayoutParams(childLayoutParams);
            childView.setOrientation(LinearLayout.VERTICAL);
            childView.setPadding(pdfViewToConvert.paddingLeft, pdfViewToConvert.paddingTop, pdfViewToConvert.paddingRight, pdfViewToConvert.paddingBottom);

            return childView;
        } else if (pdfViewToConvert instanceof PDFImageView) {
            ImageView childView = new ImageView(context);
            LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, pdfViewToConvert.layoutWeight);
            childView.setLayoutParams(childLayoutParams);
            childView.setPadding(pdfViewToConvert.paddingLeft, pdfViewToConvert.paddingTop, pdfViewToConvert.paddingRight, pdfViewToConvert.paddingBottom);

            return childView;
        } else if (pdfViewToConvert instanceof PDFTextView) {
            TextView childView = new TextView(context);
            LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, pdfViewToConvert.layoutWeight);
            childView.setLayoutParams(childLayoutParams);
            childView.setPadding(pdfViewToConvert.paddingLeft, pdfViewToConvert.paddingTop, pdfViewToConvert.paddingRight, pdfViewToConvert.paddingBottom);

            return childView;
        } else {
            FrameLayout childView = new FrameLayout(context);
            LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, pdfViewToConvert.layoutWeight);
            childView.setLayoutParams(childLayoutParams);
            childView.setPadding(pdfViewToConvert.paddingLeft, pdfViewToConvert.paddingTop, pdfViewToConvert.paddingRight, pdfViewToConvert.paddingBottom);

            return childView;
        }
    }
}
