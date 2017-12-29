package com.shomazzapp.walls.View.Fragments;

import android.app.DialogFragment;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
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
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class WallpaperFragment extends DialogFragment {

    @BindView(R.id.tag_tv)
    TextView tagsView;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.set_btn)
    Button setBtn;
    @BindView(R.id.download_btn)
    Button downloadBtn;
    @BindView(R.id.back_lin_lay)
    LinearLayout back_linLayout;
    @BindView(R.id.layout_with_buttons)
    RelativeLayout layoutWithButtons;

    private ArrayList<VKApiPhoto> wallpapers;
    private VKApiPhoto currentWallpaper;
    private MyViewPagerAdapter myViewPagerAdapter;
    private Context context;

    private int currentPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    public void setListeners() {
        /*imView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutWithButtons.setVisibility(View.VISIBLE);
                System.out.println("onClickLayout onclick!!!");
            }
        });
        layoutWithButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutWithButtons.setVisibility(View.GONE);
                System.out.println("Layout with button onclick!!!");
            }
        });*/
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = new DocumentRequest(new CommentRequset(currentWallpaper.id).getComment()
                        .attachments.get(0).toAttachmentString()
                        .toString()).getAddress();
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
        });
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = new DocumentRequest(new CommentRequset(currentWallpaper.id).getComment()
                        .attachments.get(0).toAttachmentString()
                        .toString()).getAddress();
                if (!getDestinationFileFromUrl(url).exists()) downloadFile(url, null);
                else Toast.makeText(context, Constants.FILE_EXISTS_MSG, Toast.LENGTH_SHORT).show();
            }
        });
        back_linLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
                onDestroy();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallpaper, container, false);
        ButterKnife.bind(this, view);
        setListeners();
        tagsView.setText(currentWallpaper.text);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(currentPosition);
            }
        });
        layoutWithButtons.setVisibility(View.VISIBLE);
        return view;
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

    private void displayWallpaperInfo(int position) {
        tagsView.setText(wallpapers.get(position).text);
    }

    public void setCurrentPosition(int position) {
        this.currentWallpaper = wallpapers.get(position);
        this.currentPosition = position;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setWallpaper(File f) {
        String path = f.getAbsolutePath();
        Bitmap bmp = BitmapFactory.decodeFile(path);
        WallpaperManager m = WallpaperManager.getInstance(context);
        try {
            m.setBitmap(bmp);
            Toast.makeText(context, Constants.SUCCES_MSG, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, Constants.ERROR_SETTING_WALL_MSG, Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile(String url, DownloadAsyncTask.AsyncResponse delegate) {
        new DownloadAsyncTask(context, delegate).execute(url);
    }

    public File getDestinationFileFromUrl(String url) {
        File folder = new File(Environment.getExternalStorageDirectory(),
                Constants.FOLDER_NAME);
        File file = new File(folder, getFileNameFromURL(url));
        return file;
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

    public void setWalls(ArrayList<VKApiPhoto> wallpapers) {
        this.wallpapers = wallpapers;
    }

    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;
        private RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(R.drawable.vk_clear_shape);

        public MyViewPagerAdapter() {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);
            ImageView imView = view.findViewById(R.id.wallpaper_preview);
            Glide.with(context)
                    .load(wallpapers.get(position).photo_2560)
                    .transition(withCrossFade())
                    .thumbnail(0.25f)
                    //.error(R.drawable.ic_ab_app)
                    .apply(options)
                    .into(imView);
            container.addView(view);
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
