package com.shomazzapp.vavilonWalls.View.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.shomazzapp.vavilonWalls.Requests.CommentRequset;
import com.shomazzapp.vavilonWalls.Requests.DocumentRequest;
import com.shomazzapp.vavilonWalls.Utils.Constants;
import com.shomazzapp.vavilonWalls.Utils.NetworkHelper;
import com.shomazzapp.vavilonWalls.Utils.Tasks.DeleteFileAsyncTask;
import com.shomazzapp.vavilonWalls.Utils.Tasks.DownloadAsyncTask;
import com.shomazzapp.vavilonWalls.Utils.Tasks.SetWallpaperAsyncTask;
import com.shomazzapp.vavilonWalls.Utils.WallsLoader;
import com.shomazzapp.vavilonWalls.View.MainActivity;
import com.shomazzapp.walls.R;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiPhoto;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ooo.oxo.library.widget.PullBackLayout;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class WallpaperFragment extends DialogFragment implements PullBackLayout.Callback {

    public static final String log = "WallpaerFragment";
    private final Handler mHideHandler = new Handler();
    @BindView(R.id.wallpaper_fragment_main_frame)
    FrameLayout mainFrame;
    @BindView(R.id.tag_tv)
    TextView tagsView;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.back_button)
    ImageButton backBtn;
    @BindView(R.id.bottom_control_panel)
    RelativeLayout bottomControlPanel;
    @BindView(R.id.wallpaper_buttons_lay)
    LinearLayout buttonsLay;
    @BindView(R.id.puller)
    PullBackLayout puller;
    @BindView(R.id.wallpaper_activity_back_view)
    View backView;
    @BindView(R.id.set_btn)
    Button setButton;
    @BindView(R.id.download_btn)
    Button downloadButton;
    private Activity activity;
    private boolean isNewCategory;
    private boolean isForSavedWalls;
    private Animation fadeout;
    private Animation fadein;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            bottomControlPanel.startAnimation(fadein);
            bottomControlPanel.setVisibility(View.VISIBLE);
            backBtn.startAnimation(fadein);
            backBtn.setVisibility(View.VISIBLE);
        }
    };
    private WallsLoader wallsLoader;
    private ArrayList<VKApiPhoto> wallpapers;
    private VKApiPhoto currentWallpaper;
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (NetworkHelper.isOnLine(activity)) {
                currentWallpaper = wallpapers.get(position);
                displayWallpaperInfo(position);
            } else {
                Toast.makeText(activity, Constants.ERROR_NETWORK_MSG, Toast.LENGTH_SHORT).show();
                onBack();
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private ArrayList<File> savedWallpapers;
    private int currentSavedWallPosition;
    ViewPager.OnPageChangeListener viewPagerPageChangeListenerSaved = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            currentSavedWallPosition = position;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private WallpaperFragment.MyViewPagerAdapter myViewPagerAdapter;
    private boolean mVisible;

    public static String getAviableLink(VKApiPhoto currentWallpaper, boolean isNewCategory) {
        String url;
        Log.d(log, "Comments amount == " + currentWallpaper.comments
                + "  album id = " + currentWallpaper.album_id + "  isNewCategory = " + isNewCategory);
        if (currentWallpaper.comments > 0 || isNewCategory) {
            VKApiComment comment = new CommentRequset(currentWallpaper.id).getComment();
            if (comment != null && comment.attachments.get(0) != null) {
                url = new DocumentRequest(comment.attachments.get(0).toAttachmentString()
                        .toString()).getAddress();
                Log.d(log, "download original photo");
            } else url = MainActivity.getPhotoMaxQualityLink(currentWallpaper);
        } else url = MainActivity.getPhotoMaxQualityLink(currentWallpaper);
        return url;
    }

    public static String getFileNameFromURL(String urlString) {
        Log.d(log, "Get file name from " + urlString);
        if (urlString != null) {
            if (urlString.startsWith("https://vk"))
                return urlString.substring(urlString.indexOf("?hash=") + "?hash=".length(),
                        urlString.indexOf("?hash=") + "?hash=".length() + Constants.FILE_NAME_LENGHT)
                        + Constants.FILE_ADDICTION;
            else return urlString.substring(urlString.length() - 4 - Constants.FILE_NAME_LENGHT,
                    urlString.length() - 4) + Constants.FILE_ADDICTION;
        } else return null;
    }

    public static String addSpaces(String oldString) {
        String newString = oldString;
        StringBuilder builder = new StringBuilder(newString);
        int i = 1;
        while (i < newString.length()) {
            if (builder.charAt(i) == ' '
                    && builder.charAt(i - 1) != ' ') {
                i++;
                builder.insert(i, "   ");
            }
            i++;
        }
        return builder.toString();
    }

    public void setParametrsForVKWalls(ArrayList<VKApiPhoto> walls,
                                       int currentPosition, WallsLoader wallsLoader) {
        isForSavedWalls = false;
        setWallsLoader(wallsLoader);
        this.wallpapers = walls;
        this.isNewCategory = wallsLoader.isNewCategory();
        this.currentWallpaper = wallpapers.get(currentPosition);
        currentSavedWallPosition = currentPosition;
    }

    public void setParametrsForSavedWalls(ArrayList<File> walls,
                                          int currentPosition, WallsLoader wallsLoader) {
        this.savedWallpapers = walls;
        setWallsLoader(wallsLoader);
        isForSavedWalls = true;
        currentSavedWallPosition = currentPosition;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = getActivity();
    }

    private void init(View v) {
        Log.d(log, "init!");
        ButterKnife.bind(this, v);
        mVisible = true;
        myViewPagerAdapter = new WallpaperFragment.MyViewPagerAdapter();
        if (!isForSavedWalls) {
            //wallpapers = (ArrayList<VKApiPhoto>) getIntent().getSerializableExtra(Constants.EXTRA_WALLS);
            //isNewCategory = getIntent().getBooleanExtra(Constants.EXTRA_IS_NEW_CATEGORY, false);
            //currentWallpaper = wallpapers.get(getIntent()
            //        .getIntExtra(Constants.EXTRA_WALL_POSITION, 0));
            Log.d(log, "wallpapers size = " + wallpapers.size()
                    + " current wallpaper " + currentSavedWallPosition
                    + "\n text : " + currentWallpaper.text
                    + "\n text1 : " + wallpapers.get(currentSavedWallPosition).text);
            tagsView.setText(addSpaces(currentWallpaper.text));
            downloadButton.setText("DOWNLOAD");
            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        } else {
            downloadButton.setText("DELETE");
            viewPager.addOnPageChangeListener(viewPagerPageChangeListenerSaved);
        }
        //currentSavedWallPosition = getIntent()
        //        .getIntExtra(Constants.EXTRA_WALL_POSITION, 0);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.setCurrentItem(currentSavedWallPosition);

        fadeout = AnimationUtils.loadAnimation(activity, R.anim.fadeout);
        fadein = AnimationUtils.loadAnimation(activity, R.anim.fadein);

        setOnTouchListenners();
        puller.setCallback(this);
    }

    private void setOnTouchListenners() {
        final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && view.getId() != R.id.back_button)
                    ((Button) view).setTextSize(12);
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    switch (view.getId()) {
                        case R.id.download_btn:
                            ((Button) view).setTextSize(16);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isForSavedWalls) onDownload();
                                    else
                                        deleteFile(savedWallpapers.get(currentSavedWallPosition));
                                }
                            }, 300);
                            break;
                        case R.id.set_btn:
                            ((Button) view).setTextSize(16);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isForSavedWalls) onSet();
                                    else onSet(savedWallpapers.get(currentSavedWallPosition));
                                }
                            }, 300);
                            break;
                        case R.id.back_button:
                            onBack();
                            break;
                    }
                }
                return false;
            }
        };
        downloadButton.setOnTouchListener(onTouchListener);
        setButton.setOnTouchListener(onTouchListener);
        backBtn.setOnTouchListener(onTouchListener);
    }

    private void toggle() {
        if (mVisible) hide(false);
        else show();
    }

    private void hide(boolean onlyControlPanels) {
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        bottomControlPanel.startAnimation(fadeout);
        bottomControlPanel.setVisibility(View.GONE);
        backBtn.startAnimation(fadeout);
        backBtn.setVisibility(View.GONE);
        mVisible = false;
        if (!onlyControlPanels) {
            viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        mHideHandler.removeCallbacks(mShowPart2Runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) activity).getSupportActionBar().hide();
        show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(log, "destroy");
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.postDelayed(mShowPart2Runnable, 0);
    }

    public void onBack() {
        wallsLoader.closeWallpaperFragment();
        //TODO:update savedWalls
        //if(isForSavedWalls) wallsLoader.
        /*activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);*/
        //activity.getWindow().
    }

    public void setWallsLoader(WallsLoader wallsLoader) {
        this.wallsLoader = wallsLoader;
    }

    public void onSet() {
        if (NetworkHelper.isOnLine(activity)) {
            String url = getAviableLink(currentWallpaper, isNewCategory);
            if (getDestinationFileFromUrl(url).exists())
                setWallpaper(getDestinationFileFromUrl(url), null);
            else {
                downloadFile(url, new DownloadAsyncTask.AsyncResponse() {
                    @Override
                    public void processFinish(File file) {
                        setWallpaper(file, null);
                    }
                });
            }
        } else {
            Toast.makeText(activity, Constants.ERROR_NETWORK_MSG, Toast.LENGTH_SHORT).show();
            onBack();
        }
    }

    public void onSet(File file) {
        setWallpaper(file, null);
    }

    public void onDownload() {
        if (NetworkHelper.isOnLine(activity)) {
            String url = getAviableLink(currentWallpaper, isNewCategory);
            if (!getDestinationFileFromUrl(url).exists()) downloadFile(url, null);
            else Toast.makeText(activity, Constants.FILE_EXISTS_MSG, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, Constants.ERROR_NETWORK_MSG, Toast.LENGTH_SHORT).show();
            onBack();
        }
    }

    public void setWallpaper(File f, SetWallpaperAsyncTask.AsyncResponse delegate) {
        new SetWallpaperAsyncTask(activity, delegate).execute(f);
    }

    public void deleteFile(File f) {
        new DeleteFileAsyncTask(activity, null).execute(f);
    }

    private void downloadFile(String url, DownloadAsyncTask.AsyncResponse delegate) {
        new DownloadAsyncTask(activity, delegate).execute(url);
    }

    private void displayWallpaperInfo(int position) {
        tagsView.setText(addSpaces(wallpapers.get(position).text));
    }

    public File getDestinationFileFromUrl(String url) {
        File folder = new File(Environment.getExternalStorageDirectory(),
                Constants.FOLDER_NAME);
        File file = new File(folder, getFileNameFromURL(url));
        Log.d(log, "getDestinationFileFromUrl :  url = " + url
                + "\n getFileNameFromUrl = " + getFileNameFromURL(url));
        return file;
    }

    @Override
    public void onPullStart() {
        hide(true);
        Log.d(log, "onPullStart called");
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar != null) actionBar.show();
    }

    @Override
    public void onPull(float v) {
        /*Log.d(log, "onPull called, v = " + v);
        if (v <= 0.7)
            backView.setAlpha(0.7f - v);

        mainFrame.setScaleX(1 - v * 0.3f);
        mainFrame.setScaleY(1 - v * 0.3f);*/
    }

    @Override
    public void onPullCancel() {
        Log.d(log, "onPullCancel called");
        show();
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar != null) actionBar.hide();
    }

    @Override
    public void onPullComplete() {
        onBack();
        Log.d(log, "onPullComplete called");
        //mainFrame.setScaleX(1f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallpaper, container, false);
        //isForSavedWalls = getIntent().getBooleanExtra(Constants.EXTRA_IS_FOR_SAVED_WALLS, true);
        Log.d(log, "OncreateView!!!!!!!!");
        if (isForSavedWalls || NetworkHelper.isOnLine(activity)) init(view);
        else {
            Toast.makeText(activity, Constants.ERROR_NETWORK_MSG, Toast.LENGTH_SHORT).show();
            onBack();
        }
        show();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void notifyWallsUodated() {
        if (myViewPagerAdapter != null)
            myViewPagerAdapter.notifyDataSetChanged();
    }

    public void setWalls(ArrayList<VKApiPhoto> wallpapers) {
        this.wallpapers = wallpapers;
    }

    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;
        private RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.vk_clear_shape);

        public MyViewPagerAdapter() {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);
            final ImageView imView = view.findViewById(R.id.wallpaper_preview);
            final ProgressBar progressBar = view.findViewById(R.id.progress_bar_fullscreen);
            progressBar.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(activity)
                            .load(isForSavedWalls ? savedWallpapers.get(position) : wallpapers.get(position).photo_2560)
                            .transition(withCrossFade())
                            .listener(new WallpaperFragment.MyRequestListenner(view.getContext(), progressBar))
                            //.thumbnail(0.25f)
                            //.error(R.drawable.ic_ab_app)
                            .apply(options)
                            .into(imView);
                }
            }).run();
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
            if (isForSavedWalls) return savedWallpapers.size();
            else return wallpapers.size();
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

    private class MyRequestListenner implements RequestListener<Drawable> {

        ProgressBar progressBar;
        Context context;

        public MyRequestListenner(Context context, ProgressBar progressBar) {
            this.progressBar = progressBar;
            this.context = context;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
            System.out.println("Exception ! Model : " + model);
            e.printStackTrace();
            /*String s;
            if (NetworkHelper.isOnLine(context)) s = Constants.ERROR_LOAD_MSG;
            else s = Constants.ERROR_NETWORK_MSG;
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();*/
            progressBar.setVisibility(View.GONE);
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            progressBar.setVisibility(View.GONE);
            return false;
        }
    }
}