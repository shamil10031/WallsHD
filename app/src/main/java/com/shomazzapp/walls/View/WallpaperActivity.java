package com.shomazzapp.walls.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shomazzapp.walls.R;
import com.shomazzapp.walls.Requests.CommentRequset;
import com.shomazzapp.walls.Requests.DocumentRequest;
import com.shomazzapp.walls.Utils.Constants;
import com.vk.sdk.api.model.VKApiPhoto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

public class WallpaperActivity extends AppCompatActivity {

    private ImageView imView;
    private VKApiPhoto wallpaper;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        imView = (ImageView) findViewById(R.id.wallpaper_iview);
        wallpaper = getIntent().getParcelableExtra(Constants.EXTRA_WALL);

        Glide.with(this)
                .load(wallpaper.photo_2560)
                //.error(R.drawable.ic_ab_app)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imView);
        System.out.println(wallpaper.photo_2560);
        context = this;
    }

    public void onDownload(View v) {
        String url = new DocumentRequest(new CommentRequset(wallpaper.id).getComment()
                .attachments.get(0).toAttachmentString()
                .toString()).getAddress();
        downloadFile(url);
        System.out.println(url);
    }

    public void onBackClick(View v) {
        onBackPressed();
    }

    private void downloadFile(String url) {
        final ProgressDialog progressDialog = new ProgressDialog(this);

        new AsyncTask<String, Integer, File>() {
            private Exception m_error = null;

            @Override
            protected void onPreExecute() {
                progressDialog.setMessage("Downloading ...");
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
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
                    url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();
                    File folder = new File(Environment.getExternalStorageDirectory(),
                            Constants.FOLDER_NAME);
                    if (!folder.exists())
                        folder.mkdirs();
                    file = new File(folder,
                            URLDecoder.decode(url.getFile(),
                                    "UTF-8").replaceAll("[^A-Za-z0-9_/\\.]", "").substring(17, 21) + "_hdWalls.jpg");
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

            @Override
            protected void onPostExecute(File file) {
                if (m_error != null) {
                    m_error.printStackTrace();
                    return;
                }
                progressDialog.hide();
            }
        }.execute(url);
    }

}