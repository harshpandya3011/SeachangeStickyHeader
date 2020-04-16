package com.seachange.healthandsafty.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.shimmer.ShimmerFrameLayout;

import com.seachange.healthandsafty.helper.PreferenceHelper;
import com.seachange.healthandsafty.R;
import com.seachange.healthandsafty.helper.View.Flip3dAnimation;
import com.seachange.healthandsafty.utils.UtilStrings;

public class MainActivity extends BaseActivity {

    private RelativeLayout mProgressView, mStartView;
    private Button mManagerTourButton, mCheckButton, mHomeButton, mManagerHomeButton, selectUser, login, reset, jsaButton, jsaQuestionButton, scanButton;
    private ImageView mLogo;
    ShimmerFrameLayout container;
    private TextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIndicator = findViewById(R.id.avi);
        mProgressView = findViewById(R.id.progress_rel);
        mManagerTourButton = findViewById(R.id.managerbutton);
        mHomeButton = findViewById(R.id.homebutton);
        mManagerHomeButton = findViewById(R.id.homeManagerbutton);
        selectUser = findViewById(R.id.selectUserbutton);
        login = findViewById(R.id.loginbutton);
        reset = findViewById(R.id.resetbutton);
        jsaButton = findViewById(R.id.jsabutton);
        jsaQuestionButton = findViewById(R.id.jsaQuestionbutton);
        mLogo = findViewById(R.id.logo);
        container = findViewById(R.id.shimmer_view_container);
        mStartView = findViewById(R.id.anim_layout);
        mTv = findViewById(R.id.cayo_title);
        scanButton = findViewById(R.id.scanbutton);

        mHomeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CaygoHomeActivity.class);
                intent.putExtra(UtilStrings.MANAGER_HOME, false);
                startActivity(intent);
            }
        });

        mManagerTourButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManagerTourActivity.class);
                startActivity(intent);
            }
        });

        mManagerHomeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CaygoHomeActivity.class);
                intent.putExtra(UtilStrings.MANAGER_HOME, true);
                startActivity(intent);
            }
        });

        selectUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectUserActivity.class);
                intent.putExtra(UtilStrings.MANAGER_HOME, true);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        jsaButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JSAActivity.class);
                startActivity(intent);
            }
        });

        jsaQuestionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JSAQuestionActivity.class);
                startActivity(intent);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
//                NotificationCenter notificationCenter = new NotificationCenter();
//                notificationCenter.Notify(mCtx, "test", 1001 );
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QRCodeCaptureActivity.class);
//                Intent intent = new Intent(MainActivity.this, QRCodeScannerActivity.class);

                startActivity(intent);
            }
        });

        final ViewGroup transitionsContainer = findViewById(R.id.container);
        mCheckButton =  transitionsContainer.findViewById(R.id.button);

        EditText c = new EditText(mCtx);

        View.OnFocusChangeListener v = c.getOnFocusChangeListener();


        mCheckButton.setOnClickListener(new View.OnClickListener() {
            boolean visible;

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
//                TransitionManager.beginDelayedTransition(transitionsContainer);
//                visible = !visible;
//                mTextMessage.setVisibility(visible ? View.VISIBLE : View.GONE);
                PreferenceHelper.getInstance(mCtx).resetCurrentHazards();
                Intent intent = new Intent(MainActivity.this, CaygoZoneCheckActivity.class);
                startActivity(intent);
            }

        });

        //get data from device
//        PreferenceHelper.getInstance(mCtx).getHazardSource();
//        PreferenceHelper.getInstance(mCtx).fetchDataFromServer();
        mStartView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
//        loadStartAnimation();

//        mIndicator.smoothToShow();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mIndicator.smoothToHide();
//                mProgressView.setVisibility(View.GONE);
//                loadStartAnimation();
//            }
//        }, 1000);
    }


    private void loadStartAnimation() {

        Flip3dAnimation animation = new Flip3dAnimation(mLogo);
        animation.applyPropertiesInRotation();
        mLogo.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //shine
                container.startShimmerAnimation();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        container.stopShimmerAnimation();
                        mTv.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.Tada)
                                .duration(1000)
                                .repeat(0)
                                .playOn(findViewById(R.id.cayo_title));
                        //start app
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                YoYo.with(Techniques.SlideOutRight)
                                        .duration(1000)
                                        .repeat(0)
                                        .playOn(findViewById(R.id.anim_layout));
                            }
                        }, 1000);

                    }
                }, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
