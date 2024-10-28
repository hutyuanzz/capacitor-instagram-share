package com.p2ssoftware.app.instagram_share;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@CapacitorPlugin(name = "ShareInstagram")
public class ShareInstagramPlugin extends Plugin {

    @PluginMethod
    public void shareToStory(PluginCall call) {
        String imageUriString = call.getString("imageUrl");
        String appID = call.getString("appID");

        if (imageUriString == null) {
            call.reject("Image URL required.");
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Uri> futureUri = loadImageAsync(imageUriString, executor);

        new Thread(() -> {
            try {
                Uri uri = futureUri.get();

                if (uri != null) {
                    handleAddToStory(uri, appID, call);
                } else {
                    call.reject("Unable to load image.");
                }
            } catch (Exception e) {
                call.reject("Error loading image: " + e.getMessage());
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }
        }).start();
    }

    private Future<Uri> loadImageAsync(String imageUrl, ExecutorService executor) {
        return executor.submit(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);  // 10s
                connection.setReadTimeout(15000);     // 15s
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new Exception("Unable to load image. HTTP code: " + connection.getResponseCode());
                }

                InputStream input = connection.getInputStream();
                File cacheDir = getContext().getCacheDir();
                File file = new File(cacheDir, "shared_image.jpg");

                try (FileOutputStream output = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = input.read(buffer)) != -1) {
                        output.write(buffer, 0, len);
                    }
                }
                input.close();

                return FileProvider.getUriForFile(getContext(),
                        getContext().getPackageName() + ".fileprovider", file);
            } catch (Exception e) {
                Log.e("ImageLoader", "Error loading image", e);
                return null;
            }
        });
    }

    private void handleAddToStory(Uri imageUri, String appID, PluginCall call) {
        Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
        intent.setPackage("com.instagram.android");
        intent.putExtra("source_application", appID);
        intent.setDataAndType(imageUri, "image/jpeg");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Activity activity = getActivity();

        if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
            activity.startActivityForResult(intent, 0);

            JSObject ret = new JSObject();
            ret.put("message", "Photo shared successfully.");
            call.resolve(ret);
        } else {
            call.reject("Instagram is not installed.");
        }
    }
}
