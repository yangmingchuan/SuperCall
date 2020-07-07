package com.maiya.leetcode.util.file.download;

import android.os.AsyncTask;

import com.maiya.leetcode.util.file.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class FileDownloadTask extends AsyncTask<Void, Long, Boolean> {

    public static final int DOWN_LOAD_FILTER_TYPE = 0X00001;
    public static final int DOWN_LOAD_NO_FILTER_TYPE = 0X00002;

    private FileDownloadCallback callback;
    private String downloadUrl;
    private File target;
    //开始下载时间，用户计算加载速度
    private long previousTime;
    private int downLoadType;

    public FileDownloadTask(String url, File target, FileDownloadCallback callback) {
        this(url, target, DOWN_LOAD_FILTER_TYPE, callback);
    }

    public FileDownloadTask(String url, File target, int type, FileDownloadCallback callback) {
        this.downloadUrl = url;
        this.callback = callback;
        this.target = target;
        this.downLoadType = type;

        FileUtils.mkdirs(target.getParentFile());
        if (target.exists()) {
            target.delete();
        }
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        previousTime = System.currentTimeMillis();
        if (callback != null) {
            callback.onStart();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean suc = false;
        try {
            //apk 铃声下载原有逻辑，部分视频url存在被替换字符 需做判断
            if (downLoadType == DOWN_LOAD_FILTER_TYPE) {
                downloadUrl = URLEncoder.encode(downloadUrl, "utf-8").replaceAll("\\+", "%20");
                downloadUrl = downloadUrl.replaceAll("%3A", ":").replaceAll("%2F", "/");
            }
            URL url = new URL(downloadUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setReadTimeout(300000);
            urlConnection.setConnectTimeout(30000);
            long totalLength = (long) urlConnection.getContentLength();
            saveFile(urlConnection.getInputStream(), totalLength);
            if (totalLength == target.length()) {
                suc = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suc;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        if (callback != null && values != null && values.length >= 2) {
            long sum = values[0];
            long total = values[1];
            int progress = (int) ((sum * 1f / total) * 100);
            //计算下载速度
            long totalTime = (System.currentTimeMillis() - previousTime) / 1000;
            if (totalTime == 0) {
                totalTime += 1;
            }
            long networkSpeed = sum / totalTime;
            callback.onProgress(progress, networkSpeed);
        }
    }

    @Override
    protected void onPostExecute(Boolean suc) {
        super.onPostExecute(suc);
        if (suc) {
            if (callback != null) {
                callback.onDone();
            }
        } else {
            if (callback != null) {
                callback.onFailure();
            }
        }
    }

    private void saveFile(InputStream is, long totalLength) {
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            long sum = 0;
            FileUtils.mkdirs(target.getParentFile());
            fos = new FileOutputStream(target);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);

                if (callback != null) {
                    publishProgress(sum, totalLength);
                }
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

