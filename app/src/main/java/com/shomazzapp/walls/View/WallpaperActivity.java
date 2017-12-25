package com.shomazzapp.walls.View;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shomazzapp.walls.R;
import com.shomazzapp.walls.Requests.CommentRequset;
import com.shomazzapp.walls.Requests.DocumentRequest;
import com.shomazzapp.walls.Utils.Constants;
import com.shomazzapp.walls.Utils.DownloadAsyncTask;
import com.vk.sdk.api.model.VKApiPhoto;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WallpaperActivity extends AppCompatActivity {

    @BindView(R.id.tag_tv)
    TextView tagsView;

    @BindView(R.id.wallpaper_iview)
    ImageView imView;

    private VKApiPhoto wallpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        ButterKnife.bind(this);
        wallpaper = getIntent().getParcelableExtra(Constants.EXTRA_WALL);
        Glide.with(this)
                .load(wallpaper.photo_2560).asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                //.error(R.drawable.ic_ab_app)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imView);
        System.out.println(wallpaper.photo_2560);
        tagsView.setText(wallpaper.text);
    }

    public void onDownload(View v) {
        String url = new DocumentRequest(new CommentRequset(wallpaper.id).getComment()
                .attachments.get(0).toAttachmentString()
                .toString()).getAddress();
        if (!getDestinationFileFromUrl(url).exists()) downloadFile(url, null);
        else Toast.makeText(this, Constants.FILE_EXISTS_MSG, Toast.LENGTH_SHORT).show();
    }

    public void onSet(View v) {
        String url = new DocumentRequest(new CommentRequset(wallpaper.id).getComment()
                .attachments.get(0).toAttachmentString()
                .toString()).getAddress();
        if (getDestinationFileFromUrl(url).exists()) setWallpaper(getDestinationFileFromUrl(url));
        else {
            downloadFile(url, new DownloadAsyncTask.AsyncResponse() {
                @Override
                public void processFinish(File file) {
                    setWallpaper(file);
                }
            });
        }
    }

    public void setWallpaper(File f) {
        String path = f.getAbsolutePath();
        Bitmap bmp = BitmapFactory.decodeFile(path);
        WallpaperManager m = WallpaperManager.getInstance(this);
        try {
            m.setBitmap(bmp);
            Toast.makeText(this, Constants.SUCCES_MSG, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, Constants.ERROR_SETTING_WALL_MSG, Toast.LENGTH_SHORT).show();
        }
    }

    public File getDestinationFileFromUrl(String url) {
        File folder = new File(Environment.getExternalStorageDirectory(),
                Constants.FOLDER_NAME);
        File file = new File(folder, getFileNameFromURL(url));
        return file;
    }

    public void onBackClick(View v) {
        onBackPressed();
    }

    private void downloadFile(String url, DownloadAsyncTask.AsyncResponse delegate) {
        new DownloadAsyncTask(this, delegate).execute(url);
    }

    public static String getFileNameFromURL(String urlString) {
        if (urlString != null) {
            try {
                URL url = new URL(urlString);
                return URLDecoder.decode(url.getFile(), "UTF-8").
                        replaceAll("[^A-Za-z0-9_/\\.]", "")
                        .substring(17, 21) + Constants.FILE_ADDICTION;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else return null;
    }
}