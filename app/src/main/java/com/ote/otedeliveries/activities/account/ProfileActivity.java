package com.ote.otedeliveries.activities.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.ote.otedeliveries.R;
import com.ote.otedeliveries.activities.home.HomeActivity;
import com.ote.otedeliveries.databinding.ActivityProfileBinding;
import com.ote.otedeliveries.fragments.HistoryFragment;
import com.ote.otedeliveries.fragments.WalletFragment;
import com.ote.otedeliveries.models.User;
import com.ote.otedeliveries.utils.PhotoPickerActivity;
import com.ote.otedeliveries.utils.PrefManager;
import com.ote.otedeliveries.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class ProfileActivity extends AppCompatActivity {
    @BindView(R.id.tab_layout) TabLayout tab_layout;
    @BindView(R.id.view_pager) ViewPager view_pager;
    @BindView(R.id.user_profile_image) CircularImageView user_profile_image;

    Realm realm;
    User user;

    public static final int REQUEST_PROFILE_PHOTO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityProfileBinding activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        ButterKnife.bind(this);

        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(HistoryFragment.newInstance(), getResources().getString(R.string.history));
        adapter.addFragment(WalletFragment.newInstance(), getResources().getString(R.string.wallet));
        view_pager.setAdapter(adapter);

        tab_layout.setupWithViewPager(view_pager);

        realm = Utils.getRealmInstance();
        user = realm.where(User.class).findFirst();

        activityProfileBinding.content.setUser(user);

        String profilePicture = PrefManager.getProfilePicture();

        if(!TextUtils.isEmpty(profilePicture)) {
            byte[] decodedFrontPhotoByteArray = Base64.decode(profilePicture, Base64.DEFAULT);
            Bitmap profilePhotoBitmap = BitmapFactory.decodeByteArray(decodedFrontPhotoByteArray, 0, decodedFrontPhotoByteArray.length);

            Glide.with(this)
                    .load(profilePhotoBitmap)
                    .into(user_profile_image);

            user_profile_image.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
        }

        user_profile_image.setOnClickListener(v -> {
            Dexter.withActivity(ProfileActivity.this)
                    .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if(report.areAllPermissionsGranted()){
                                showPickerOptions(REQUEST_PROFILE_PHOTO, getString(R.string.profile_photo));
                            }

                            if(report.isAnyPermissionPermanentlyDenied()){
                                showSettingsDialog();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        });
    }

    private void loadPhoto(String url, AppCompatImageView target){
        Glide.with(this)
                .load(url)
                .into(target);

        target.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void showPickerOptions(final int requestID, String title) {
        PhotoPickerActivity.showImagePickerOptions(this, new PhotoPickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent(requestID);
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent(requestID);
            }
        }, title);
    }

    private void launchCameraIntent(int requestID) {
        Intent intent = new Intent(ProfileActivity.this, PhotoPickerActivity.class);
        intent.putExtra(PhotoPickerActivity.INTENT_IMAGE_PICKER_OPTION, PhotoPickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(PhotoPickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(PhotoPickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(PhotoPickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(PhotoPickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(PhotoPickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(PhotoPickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, requestID);
    }

    private void launchGalleryIntent(int requestID) {
        Intent intent = new Intent(ProfileActivity.this, PhotoPickerActivity.class);
        intent.putExtra(PhotoPickerActivity.INTENT_IMAGE_PICKER_OPTION, PhotoPickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(PhotoPickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(PhotoPickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(PhotoPickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, requestID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_PROFILE_PHOTO){
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;

                Uri uri = data.getParcelableExtra("path");

                Bitmap profilePhotoBitmap = null;

                try {
                    profilePhotoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    ByteArrayOutputStream profilePhotoByteArrayOutputStream = new ByteArrayOutputStream();
                    profilePhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, profilePhotoByteArrayOutputStream);
                    byte[] profilePhotoByteArray = profilePhotoByteArrayOutputStream .toByteArray();

                    String profilePhotoString = Base64.encodeToString(profilePhotoByteArray, Base64.DEFAULT);

                    PrefManager.setProfilePicture(profilePhotoString);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                loadPhoto(uri.toString(), user_profile_image);
            }
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @OnClick(R.id.back_button)
    public void backButton(){
        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}