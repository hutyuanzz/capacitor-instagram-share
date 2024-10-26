package com.p2ssoftware.app.instagram_share;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

@CapacitorPlugin(name = "ShareInstagram")
public class ShareInstagramPlugin extends Plugin {

    private static final String TAG = "ShareInstagramPlugin";
    private static final String APP_ID = "9249109415116564";

    @PluginMethod
    public void echo(PluginCall call) {
        String imageUriString = call.getString("value");

        if (imageUriString == null) {
            call.reject("Cần cung cấp URI hình ảnh.");
            return;
        }

        Log.d(TAG, "Đang tải ảnh từ URL: " + imageUriString);

        // Tải ảnh từ URL
        new DownloadImageTask(call).execute(imageUriString);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Uri> {
        private final PluginCall call;

        public DownloadImageTask(PluginCall call) {
            this.call = call;
        }

        @Override
        protected Uri doInBackground(String... urls) {
            String imageUrl = urls[0];
            try {
                Log.d(TAG, "Đang kết nối tới URL: " + imageUrl);
        
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);  // 10 giây
                connection.setReadTimeout(15000);     // 15 giây
                connection.connect();
        
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Mã phản hồi HTTP: " + responseCode);
        
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Không thể tải ảnh - Mã phản hồi: " + responseCode);
                    return null;
                }
        
                InputStream input = connection.getInputStream();
                File cacheDir = getContext().getCacheDir();
                File file = new File(cacheDir, "shared_image.jpg");
        
                Log.d(TAG, "Đang lưu ảnh vào: " + file.getAbsolutePath());
        
                FileOutputStream output = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = input.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                }
                output.close();
                input.close();
        
                return FileProvider.getUriForFile(getContext(),
                        getContext().getPackageName() + ".fileprovider", file);
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi tải ảnh: ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Uri uri) {
            if (uri != null) {
                shareImageToInstagram(uri, call);
            } else {
                call.reject("Không thể tải ảnh.");
            }
        }
    }

    private void shareImageToInstagram(Uri imageUri, PluginCall call) {
        Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
        intent.setPackage("com.instagram.android");
        intent.putExtra("source_application", APP_ID);
        intent.setDataAndType(imageUri, "image/jpeg");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Activity activity = getActivity();

        if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
            activity.startActivityForResult(intent, 0);

            JSObject ret = new JSObject();
            ret.put("value", "Ảnh đã được chia sẻ thành công.");
            call.resolve(ret);
        } else {
            call.reject("Instagram chưa được cài đặt hoặc không hỗ trợ Intent này.");
        }
    }
}
