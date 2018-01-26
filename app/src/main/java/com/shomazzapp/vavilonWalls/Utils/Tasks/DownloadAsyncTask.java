package com.shomazzapp.vavilonWalls.Utils.Tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.shomazzapp.vavilonWalls.View.WallpaperActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadAsyncTask extends AsyncTask<String, Integer, File> {

    public interface AsyncResponse {
        void processFinish(File file);
    }

    private AsyncResponse delegate;
    private Exception m_error = null;
    private final ProgressDialog progressDialog;
    private Context context;

    public DownloadAsyncTask(Context context, AsyncResponse delegate) {
        this.delegate = delegate;
        this.context = context;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Downloading ...");
        progressDialog.setCancelable(true);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(false);
            }
        });
    }

    @Override
    protected File doInBackground(String... params) {

        int totalSize;
        int downloadedSize;
        byte[] buffer;
        int bufferLength;

        URL url;
        HttpURLConnection urlConnection;
        InputStream inputStream;
        File file;
        FileOutputStream fos;
        try {
            File folder = new File(Environment.getExternalStorageDirectory(),
                    Constants.FOLDER_NAME);
            if (!folder.exists())
                folder.mkdirs();

            url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.connect();

            file = new File(folder, WallpaperActivity.getFileNameFromURL(params[0]));
            System.out.println(file.getAbsolutePath());
            file.createNewFile();

            fos = new FileOutputStream(file);
            inputStream = urlConnection.getInputStream();
            totalSize = urlConnection.getContentLength();
            downloadedSize = 0;
            buffer = new byte[1024];
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                publishProgress(downloadedSize, totalSize);
            }
            fos.close();
            inputStream.close();
            return file;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            m_error = e;
        } catch (IOException e) {
            e.printStackTrace();
            m_error = e;
        }
        return null;
    }

    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress((int) ((values[0] / (float) values[1]) * 100));
    }

    //TODO: fix cancel
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(File file) {
        if (m_error != null) {
            m_error.printStackTrace();
            Toast.makeText(context, Constants.ERROR_DOWNLOAD_MSG, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, Constants.SUCCES_MSG, Toast.LENGTH_SHORT).show();
            if (delegate != null)
                delegate.processFinish(file);
        }
        progressDialog.hide();
        return;
    }
}