package com.tejpratapsingh.pdfcreator.views.basic;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;

public class PDFTextView extends PDFView implements Serializable {

    private static final float TEXT_SIZE_HEADER = 32;
    private static final float TEXT_SIZE_H1 = 24;
    private static final float TEXT_SIZE_H2 = 20;
    private static final float TEXT_SIZE_H3 = 16;
    private static final float TEXT_SIZE_P = 12;
    private static final float TEXT_SIZE_SMALL = 10;

    public enum PDF_TEXT_SIZE {
        HEADER(TEXT_SIZE_HEADER),
        H1(TEXT_SIZE_H1),
        H2(TEXT_SIZE_H2),
        H3(TEXT_SIZE_H3),
        P(TEXT_SIZE_P),
        SMALL(TEXT_SIZE_SMALL);

        // declaring private variable for getting values
        private final float fontSize;

        // getter method
        public float getFontSize() {
            return this.fontSize;
        }

        // enum constructor - cannot be public or protected
        PDF_TEXT_SIZE(float action) {
            this.fontSize = action;
        }
    }

    private SpannableString text = new SpannableString("");

    public PDFTextView(Context context, PDF_TEXT_SIZE size) {
        super(context);

        TextView textView = new TextView(context);
        LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        textView.setLayoutParams(childLayoutParams);

        textView.setTextColor(Color.BLACK);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size.getFontSize());

        super.setView(textView);
    }

    @Override
    protected PDFView addView(PDFView viewToAdd) throws IllegalStateException {
        throw new IllegalStateException("Cannot add subview to TextView");
    }

    public PDFTextView setText(SpannableString text) {
        this.text = text;
        getView().setText(text);
        return this;
    }

    public PDFTextView setText(String text) {
        this.text = new SpannableString(text);
        getView().setText(text);
        return this;
    }

    public SpannableString getText() {
        return text;
    }

    @Override
    public PDFTextView setLayout(ViewGroup.LayoutParams layoutParams) {
        super.setLayout(layoutParams);
        return this;
    }

    public PDFTextView setTextColor(int color) {
        ((TextView) this.getView()).setTextColor(color);
        return this;
    }

    public PDFTextView setTextTypeface(Typeface typeface) {
        getView().setTypeface(typeface);
        return this;
    }

    @Override
    public TextView getView() {
        return (TextView) super.getView();
    }
}
