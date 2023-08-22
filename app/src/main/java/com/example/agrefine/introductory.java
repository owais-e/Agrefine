package com.example.agrefine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.FirebaseDatabase;

public class introductory extends AppCompatActivity {

    ImageView text, bg, text2, slogan;
    LottieAnimationView lottie1,lottie2;

    private static final int num_pages = 3;
    private ViewPager viewPager;
    private ScreenSlidePagerAdapter pagerAdapter;

    Animation anim;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory);


        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        //slogan=findViewById(R.id.sloganBlack);
        text2=findViewById(R.id.topTextBlack);
        bg=findViewById(R.id.bgImage);
        lottie1=findViewById(R.id.lottie);

        viewPager=findViewById(R.id.pager);
        pagerAdapter=new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        anim = AnimationUtils.loadAnimation(this,R.anim.fading_effect);
        viewPager.startAnimation(anim);


        bg.animate().translationY(-2900).setDuration(1000).setStartDelay(4000);

        //slogan.animate().translationX(1400).setDuration(1000).setStartDelay(4000)
        text2.animate().scaleX(0).scaleY(0).setDuration(500).setStartDelay(3800);
        lottie1.animate().scaleX(0).scaleY(0).setDuration(500).setStartDelay(3800);
        //lottie2.animate().translationY(1400).setDuration(1000).setStartDelay(4000);

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                onboardingFragment1 tab1= new onboardingFragment1();
                return tab1;
                case 1:
                    onboardingFragment2 tab2= new onboardingFragment2();
                    return tab2;
                case 2:
                    onboardingFragment3 tab3= new onboardingFragment3();
                    return tab3;
            }
            return null;
        }

        @Override
        public int getCount() {
            return num_pages;
        }
    }

}