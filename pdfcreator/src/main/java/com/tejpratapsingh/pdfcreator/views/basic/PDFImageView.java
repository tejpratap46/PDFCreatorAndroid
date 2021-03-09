package com.tejpratapsingh.pdfcreator.views.basic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;

public class PDFImageView extends PDFView implements Serializable {

    public PDFImageView(Context context) {
        super(context);

        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        imageView.setLayoutParams(childLayoutParams);

        super.setView(imageView);
    }

    @Override
    protected PDFView addView(PDFView viewToAdd) throws IllegalStateException {
        throw new IllegalStateException("Cannot add subview to Image");
    }

    public PDFImageView setImageResource(@DrawableRes int resId) {
        ((ImageView) super.getView()).setImageResource(resId);
        return this;
    }

    public PDFImageView setImageBitmap(Bitmap bitmap) {
        ((ImageView) super.getView()).setImageBitmap(bitmap);
        return this;
    }

    public PDFImageView setImageFile(File imageFile) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(imageFile);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap newAddedImageBitmap = BitmapFactory.decodeStream(fileInputStream, null, options);
        ((ImageView) super.getView()).clearColorFilter();
        ((ImageView) super.getView()).setImageBitmap(newAddedImageBitmap);
        return this;
    }

    public PDFImageView setImageScale(ImageView.ScaleType scaleType) {
        ((ImageView) super.getView()).setScaleType(scaleType);
        return this;
    }

    @Override
    public PDFImageView setLayout(ViewGroup.LayoutParams layoutParams) {
        super.setLayout(layoutParams);
        return this;
    }

    @Override
    public ImageView getView() {
        return (ImageView) super.getView();
    }
}
