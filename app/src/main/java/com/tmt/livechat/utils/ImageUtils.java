package com.tmt.livechat.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageUtils {

    // Prevent instantiation
    private ImageUtils() {

    }

    /**
     *
     * @param context
     * @return
     * @throws IOException
     */
    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }


    public static void displayImageFromUrl(final Context context, final String url,
                                           final ImageView imageView, Drawable placeholderDrawable) {
        displayImageFromUrl(context, url, imageView, placeholderDrawable, null);
    }

    /**
     * Displays an image from a URL in an ImageView.
     */
    public static void displayImageFromUrl(final Context context, final String url,
                                           final ImageView imageView, Drawable placeholderDrawable,final RequestListener listener) {
        final RequestOptions myOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(placeholderDrawable);

        if (listener != null && url != null) {
            Glide.with(context.getApplicationContext())
                    .load(url)
                    .thumbnail(0.1f)
                    .apply(myOptions)
                    .listener(listener)
                    .into(imageView);
        } else if (url != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context.getApplicationContext())
                            .load(url)
                            .thumbnail(0.1f)
                            .apply(myOptions)
                            .listener(listener)
                            .into(imageView);
                }
            });
        }
        else {
            Glide.with(context.getApplicationContext()).clear(imageView);
            imageView.setImageDrawable(null);
        }
    }


    /**
     * Displays an image from a URL in an ImageView.
     */
    public static void displayImageFromFile(final Context context, final File file, final ImageView imageView, final RequestListener listener) {
        final RequestOptions myOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        if (file != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context.getApplicationContext())
                            .load(file)
                            .thumbnail(0.1f)
                            .apply(myOptions)
                            .listener(listener)
                            .into(imageView);
                }
            });
        }
        else {
            Glide.with(context.getApplicationContext()).clear(imageView);
            imageView.setImageDrawable(null);
        }
    }



    /**
     * Displays an image from a URL in an ImageView.
     * If the image is loading or nonexistent, displays the specified placeholder image instead.
     */
    public static void displayImageFromUrlWithPlaceHolder(final Context context, final String url,
                                                          final ImageView imageView,
                                                          int placeholderResId) {
        RequestOptions myOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(placeholderResId);

        Glide.with(context)
                .load(url)
                .apply(myOptions)
                .into(imageView);
    }

    /**
     * Displays an image from a URL in an ImageView.
     */
    public static void displayGifImageFromUrl(Context context, String url, ImageView imageView, Drawable placeholderDrawable, RequestListener listener) {
        RequestOptions myOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(placeholderDrawable);

        if (listener != null) {
            Glide.with(context)
                    .asGif()
                    .load(url)
                    .apply(myOptions)
                    .listener(listener)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .asGif()
                    .load(url)
                    .apply(myOptions)
                    .into(imageView);
        }
    }

    /**
     * Displays an GIF image from a URL in an ImageView.
     */
    public static void displayGifImageFromUrl(Context context, String url, ImageView imageView, String thumbnailUrl, Drawable placeholderDrawable) {
        RequestOptions myOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(placeholderDrawable);

        if (thumbnailUrl != null) {
            Glide.with(context)
                    .asGif()
                    .load(url)
                    .apply(myOptions)
                    .thumbnail(Glide.with(context).asGif().load(thumbnailUrl))
                    .into(imageView);
        } else {
            Glide.with(context)
                    .asGif()
                    .load(url)
                    .apply(myOptions)
                    .into(imageView);
        }
    }
}