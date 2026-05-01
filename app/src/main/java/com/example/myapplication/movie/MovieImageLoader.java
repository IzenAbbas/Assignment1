package com.example.myapplication.movie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MovieImageLoader {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(3);
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private MovieImageLoader() {
    }

    public static void load(String imageUrl, ImageView imageView) {
        imageView.setTag(imageUrl);
        EXECUTOR.execute(() -> {
            Bitmap bitmap = downloadBitmap(imageUrl);
            MAIN_HANDLER.post(() -> {
                Object currentTag = imageView.getTag();
                if (imageUrl.equals(currentTag) && bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            });
        });
    }

    private static Bitmap downloadBitmap(String imageUrl) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(imageUrl).openConnection();
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setDoInput(true);
            connection.connect();

            try (InputStream inputStream = connection.getInputStream()) {
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception ignored) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
