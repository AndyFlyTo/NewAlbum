package com.example.chenchen.newapplication.album;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.example.chenchen.newapplication.R;
import com.example.chenchen.newapplication.album.Database.MyDatabaseHelper;
import com.example.chenchen.newapplication.album.Database.MyDatabaseOperator;
import com.example.chenchen.newapplication.album.imageloader.ImageScan;
import com.example.chenchen.newapplication.tensorflow.Config;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.utils.L;

import java.io.Serializable;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by chenchen on 18-5-2.
 */

public class ImageScanActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = ImageScanActivity.class.getSimpleName();
    public final static String EXTRA_IMAGE_INFO_LIST = "ImageInfoList";
    public final static String EXTRA_IMAGE_INFO = "ImageInfo";

    public final static String EXTRA_NEW_IMAGE_LIST = "NewImageList";
    private ActionBar actionBar;


    private ViewPager mPreviewViewPager;
    private PagerAdapter mPreviewPagerAdapter;
    private ViewPager.OnPageChangeListener mPreviewChangeListener;
    private CurrentImageListener imageListener;
    private TextView mTitleView;

    private View mHeaderView;
    private View mFooterView;
    private int imageIndex;
    private ImageView image_delete;
//    private CheckBox mImageSelectedBox;
    MyDatabaseHelper helper;

    /**
     * 所有图片的列表
     */
    private List<String> mPreviewImageInfoList;
    /**
     * 刚进入页面显示的图片
     */
    private String mPreviewImageInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_preview);
        actionBar = getSupportActionBar();
        //没有左部返回键
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");


        if (Build.VERSION.SDK_INT >= 11) {
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if (View.SYSTEM_UI_FLAG_VISIBLE == visibility) {//此处需要添加顶部和底部消失和出现的动画效果
                        Log.d(TAG, "SYSTEM_UI_FLAG_VISIBLE");
//                        mHeaderView.startAnimation(AnimationUtils.loadAnimation(ImagePreviewActivity.this, R.anim.top_enter_anim));
                        mFooterView.startAnimation(AnimationUtils.loadAnimation(ImageScanActivity.this, R.anim.bottom_enter_anim));
//
                    } else {
                        Log.i(TAG, "SYSTEM_UI_FLAG_INVISIBLE");
//                        mHeaderView.startAnimation(AnimationUtils.loadAnimation(ImageScanActivity.this, R.anim.top_exit_anim));
                        mFooterView.startAnimation(AnimationUtils.loadAnimation(ImageScanActivity.this, R.anim.bottom_exit_anim));
//
                    }
                }
            });
        }

//        mImageLoaderWrapper = ImageLoaderFactory.getLoader();

        mPreviewImageInfo = (String) getIntent().getSerializableExtra(EXTRA_IMAGE_INFO);
        mPreviewImageInfoList = (List<String>) getIntent().getSerializableExtra(EXTRA_IMAGE_INFO_LIST);
        Log.d("chen", "文件夹数目=" + mPreviewImageInfoList.size());
        initView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_class, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //actionBar点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("chen", "optionsItemSelected exec!!");
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_class:
                Log.d("chen", "跳转到分类页面");
                Intent intent = new Intent(ImageScanActivity.this, ClassiedOnePictureActivity.class);
                if (imageListener == null) {
                    Log.d("chen", "imageListener=null");
                    intent.putExtra("image_url", mPreviewImageInfo);
                } else
                    intent.putExtra("image_url", imageListener.getCurrentImage());
                startActivity(intent);
                break;
            
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {

        // TODO: 18-5-7 index暂时不需要
//        mTitleView = (TextView) findViewById(R.id.tv_title);
        if (mPreviewImageInfo != null && mPreviewImageInfoList != null) {
            if (mPreviewImageInfoList.contains(mPreviewImageInfo)) {
                imageIndex = mPreviewImageInfoList.indexOf(mPreviewImageInfo);
                Log.d("chen", "imageIndex=" + imageIndex);
                setPositionToTitle(imageIndex);
            }
        }

        image_delete= (ImageView) findViewById(R.id.image_delete);
        if (mPreviewImageInfo != null) {
//            mImageSelectedBox.setChecked(mPreviewImageInfo.isSelected());
        }
//        mImageSelectedBox.setOnCheckedChangeListener(this);
        image_delete.setOnClickListener(this);

        mPreviewViewPager = (ViewPager) findViewById(R.id.gallery_viewpager);
        mPreviewPagerAdapter = new PreviewPagerAdapter();
        mPreviewViewPager.setAdapter(mPreviewPagerAdapter);
        if (mPreviewImageInfo != null && mPreviewImageInfoList != null && mPreviewImageInfoList.contains(mPreviewImageInfo)) {
            int initShowPosition = mPreviewImageInfoList.indexOf(mPreviewImageInfo);
            Log.d("chen", "此时position是：" + initShowPosition);
            mPreviewViewPager.setCurrentItem(initShowPosition);

        }
        mPreviewChangeListener = new PreviewChangeListener();
        mPreviewViewPager.addOnPageChangeListener(mPreviewChangeListener);

//        findViewById(R.id.iv_back).setOnClickListener(this);

//        mHeaderView = findViewById(R.id.header_view);
        mFooterView = findViewById(R.id.footer_view);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.image_delete) {
            new MaterialDialog.Builder(this)
                    .title("确定要删除吗？")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (helper == null)
                                helper = new MyDatabaseHelper(ImageScanActivity.this, Config.DB_NAME, null, Config.dbversion);
                            SQLiteDatabase db = helper.getWritableDatabase();
                            String url;
                            final int id;
                            if (imageListener == null) {
                                Log.d("chen", "imageListener=null");
                                url = mPreviewImageInfo;
                                id = mPreviewImageInfoList.indexOf(mPreviewImageInfo);
                            } else {
                                url = imageListener.getCurrentImage();
                                id = imageListener.getCurrentId();
                            }
                            Log.d("chen", "delete url=" + url);
                            db.delete("AlbumPhotos", "url=?", new String[]{url});
                            db.delete("AlbumFolder","image=?",new String[]{url});
                            db.close();
                            //同时也要触发下一页显示
                            int imageId = id + 1;
                            if (imageId == mPreviewImageInfoList.size()) {
                                finish();
                            } else
                                mPreviewViewPager.setCurrentItem(imageId);

                            // TODO: 18-5-20 如果删除的是最后一个 弹出
                            Log.d("chen", "current index =" + imageId);
                            //// TODO: 18-5-20如果目录下仅有一张照片且被删除，此时还要更新文件夹相册 

                            updateMediaImage(url);
                        }
                    })
                    .positiveText("Yes")
                    .negativeText("No")
                    .positiveColor(getResources().getColor(R.color.colorPrimary))
                    .negativeColor(getResources().getColor(R.color.colorPrimary))
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();


        }
    }

    //通知系统媒体库删除
    public void updateMediaImage(String filePath) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = ImageScanActivity.this.getContentResolver();
        String where = MediaStore.Images.Media.DATA + "='" + filePath + "'";
        mContentResolver.delete(uri, where, null);
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(EXTRA_NEW_IMAGE_LIST, (Serializable) mPreviewImageInfoList);
        setResult(Activity.RESULT_OK, data);
        super.onBackPressed();
    }

//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if (buttonView == mImageSelectedBox) {
//            int currentPosition = mPreviewViewPager.getCurrentItem();
//            ImageInfo imageInfo = mPreviewImageInfoList.get(currentPosition);
//            imageInfo.setIsSelected(isChecked);
//        }
//    }

    /**
     * 监听PhotoView的点击事件
     */
    private PhotoViewAttacher.OnViewTapListener mOnPreviewTapListener = new PhotoViewAttacher.OnViewTapListener() {
        @Override
        public void onViewTap(View view, float v, float v1) {
            toggleImmersiveMode();
        }
    };


    /**
     * 相册适配器
     */
    private class PreviewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mPreviewImageInfoList == null) {
                return 0;
            }
            return mPreviewImageInfoList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            PhotoView galleryPhotoView = (PhotoView) view.findViewById(R.id.iv_show_image);
            galleryPhotoView.setScale(1.0f);//让图片在滑动过程中恢复回缩放操作前原图大小
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View galleryItemView = View.inflate(ImageScanActivity.this, R.layout.preview_image_item, null);

            String image_url = mPreviewImageInfoList.get(position);
            PhotoView galleryPhotoView = (PhotoView) galleryItemView.findViewById(R.id.iv_show_image);
            galleryPhotoView.setOnViewTapListener(mOnPreviewTapListener);

            Glide
                    .with(ImageScanActivity.this)
                    .load(image_url)
//                    .centerCrop()
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .crossFade()
                    .thumbnail(0.1f).into(galleryPhotoView);


            container.addView(galleryItemView);
            return galleryItemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }

    /**
     * 相册详情页面滑动监听
     */
    private class PreviewChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Log.d("chen", "onPageSelected");
            Log.d("chen", "position is =" + position);

//            mImageSelectedBox.setOnCheckedChangeListener(null);//先反注册监听，避免重复更新选中的状态

//            setPositionToTitle(position);
            String imageInfo = mPreviewImageInfoList.get(position);

            imageListener = new CurrentImageListener() {
                @Override
                public String getCurrentImage() {
                    return imageInfo;
                }

                @Override
                public int getCurrentId() {
                    return position;
                }
            };

//            mImageSelectedBox.setChecked(imageInfo.isSelected());

//            mImageSelectedBox.setOnCheckedChangeListener(ImagePreviewActivity.this);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 设置标题现实当前所处的位置
     *
     * @param position
     */
    private void setPositionToTitle(int position) {
        if (mPreviewImageInfoList != null) {
//            String title = String.format(getString(R.string.image_index), position + 1, mPreviewImageInfoList.size());
//            mTitleView.setText(title);
        }
    }

    /**
     * 切换沉浸栏模式（Immersive - Mode）
     */
    private void toggleImmersiveMode() {
        if (Build.VERSION.SDK_INT >= 11) {
            int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
            // Navigation bar hiding:  Backwards compatible to ICS.
            if (Build.VERSION.SDK_INT >= 14) {
                uiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            // Status bar hiding: Backwards compatible to Jellybean
            if (Build.VERSION.SDK_INT >= 16) {
                uiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
            // Immersive mode: Backward compatible to KitKat.
            if (Build.VERSION.SDK_INT >= 18) {
                uiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
    }


}
