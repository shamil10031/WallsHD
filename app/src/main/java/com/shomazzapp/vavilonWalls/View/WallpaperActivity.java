package com.shomazzapp.vavilonWalls.View;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.shomazzapp.vavilonWalls.Requests.CommentRequset;
import com.shomazzapp.vavilonWalls.Requests.DocumentRequest;
import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.shomazzapp.vavilonWalls.Utils.DownloadAsyncTask;
import com.shomazzapp.walls.R;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiPhoto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class WallpaperActivity extends AppCompatActivity {

    @BindView(R.id.tag_tv)
    TextView tagsView;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.back_button)
    ImageButton backBtn;
    @BindView(R.id.bottom_control_panel)
    RelativeLayout bottomControlPanel;

    private Animation fadeout;
    private Animation fadein;
    private ArrayList<VKApiPhoto> wallpapers;
    private VKApiPhoto currentWallpaper;
    private MyViewPagerAdapter myViewPagerAdapter;

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            bottomControlPanel.startAnimation(fadein);
            bottomControlPanel.setVisibility(View.VISIBLE);
            backBtn.startAnimation(fadein);
            backBtn.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
        mVisible = true;
        ButterKnife.bind(this);
        wallpapers = (ArrayList<VKApiPhoto>) getIntent().getSerializableExtra(Constants.EXTRA_WALLS);
        currentWallpaper = wallpapers.get(getIntent()
                .getIntExtra(Constants.EXTRA_WALL_POSITION, 0));
        tagsView.setText(currentWallpaper.text);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setCurrentItem(getIntent()
                .getIntExtra(Constants.EXTRA_WALL_POSITION, 0));
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN);
        }
    }

    private void toggle() {
        if (mVisible) hide();
        else show();
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        bottomControlPanel.startAnimation(fadeout);
        bottomControlPanel.setVisibility(View.GONE);
        backBtn.startAnimation(fadeout);
        backBtn.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    public void onBack(View v) {
        onBackPressed();
    }

    public void onSet(View v) {
        String url;
        VKApiComment comment = new CommentRequset(currentWallpaper.id).getComment();
        if (comment != null && comment.attachments.get(0) != null)
            url = new DocumentRequest(comment.attachments.get(0).toAttachmentString()
                    .toString()).getAddress();
        else url = currentWallpaper.photo_2560;
        if (getDestinationFileFromUrl(url).exists())
            setWallpaper(getDestinationFileFromUrl(url));
        else {
            downloadFile(url, new DownloadAsyncTask.AsyncResponse() {
                @Override
                public void processFinish(File file) {
                    setWallpaper(file);
                }
            });
        }
    }

    public void onDownload(View v) {
        String url;
        VKApiComment comment = new CommentRequset(currentWallpaper.id).getComment();
        if (comment != null && comment.attachments.get(0) != null)
            url = new DocumentRequest(comment.attachments.get(0).toAttachmentString()
                    .toString()).getAddress();
        else url = currentWallpaper.photo_2560;
        if (!getDestinationFileFromUrl(url).exists()) downloadFile(url, null);
        else Toast.makeText(this, Constants.FILE_EXISTS_MSG, Toast.LENGTH_SHORT).show();
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

    private void displayWallpaperInfo(int position) {
        tagsView.setText(wallpapers.get(position).text);
    }

    private void downloadFile(String url, DownloadAsyncTask.AsyncResponse delegate) {
        new DownloadAsyncTask(this, delegate).execute(url);
    }

    public File getDestinationFileFromUrl(String url) {
        File folder = new File(Environment.getExternalStorageDirectory(),
                Constants.FOLDER_NAME);
        File file = new File(folder, getFileNameFromURL(url));
        return file;
    }

    public static String getFileNameFromURL(String urlString) {
        System.out.println("!!!!!!!!!!!!!!!!!!! TUTTTTUUT: " + urlString);
        if (urlString != null) {
            /*try {
                URL url = new URL(urlString);
                return URLDecoder.decode(url.getFile(), "UTF-8").
                        replaceAll("[^A-Za-z0-9_/\\.]", "")
                        .substring(17, 21) + Constants.FILE_ADDICTION;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }*/
            if (urlString.startsWith("https://vk"))
                return urlString.substring(urlString.indexOf("?hash=") + "?hash=".length(),
                        urlString.indexOf("?hash=") + "?hash=".length() + Constants.FILE_NAME_LENGHT)
                        + Constants.FILE_ADDICTION;
            else return urlString.substring(urlString.length() - 4 - Constants.FILE_NAME_LENGHT,
                    urlString.length() - 4) + Constants.FILE_ADDICTION;
        } else return null;
    }


    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            currentWallpaper = wallpapers.get(position);
            displayWallpaperInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;
        private RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.vk_clear_shape);

        public MyViewPagerAdapter() {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);
            ImageView imView = view.findViewById(R.id.wallpaper_preview);
            Glide.with(WallpaperActivity.this)
                    .load(wallpapers.get(position).photo_2560)
                    .transition(withCrossFade())
                    .thumbnail(0.25f)
                    //.error(R.drawable.ic_ab_app)
                    .apply(options)
                    .into(imView);
            container.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggle();
                }
            });
            return view;
        }

        @Override
        public int getCount() {
            return wallpapers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
