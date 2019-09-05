package com.tejpratapsingh.pdfcreator.views.basic;

import android.content.Context;
import android.text.SpannableString;
import android.widget.TextView;

import java.io.Serializable;

public class PDFTextView extends PDFView implements Serializable {

    private SpannableString text = new SpannableString("");

    public PDFTextView(Context context) {
        super(context);
    }

    public void setText(SpannableString text) {
        this.text = text;
        ((TextView) this.getView()).setText(text);
    }

    public void setText(String text) {
        this.text = new SpannableString(text);
        ((TextView) this.getView()).setText(text);
    }

    public SpannableString getText() {
        return text;
    }

    @Override
    public PDFTextView setWeight(float weight) {
        super.setWeight(weight);
        return this;
    }
}
