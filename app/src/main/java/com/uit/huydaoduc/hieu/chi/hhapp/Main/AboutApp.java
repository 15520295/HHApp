package com.uit.huydaoduc.hieu.chi.hhapp.Main;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.WindowManager;

import com.uit.huydaoduc.hieu.chi.hhapp.R;
import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

public class AboutApp extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        //Bản v1
        AboutView view = AboutBuilder.with(this)
                .setPhoto(R.drawable.logosbike6)
                .setCover(R.mipmap.profile_cover)
                .setName(getString(R.string.tks))
                .setSubTitle(getString(R.string.tks_sub))
                .setAppIcon(R.drawable.logosbike6)
                .setAppName(R.string.app_name)
                .addGooglePlayStoreLink("https://play.google.com/store/apps/details?id=com.uit.huydaoduc.hieu.chi.hhapp")
                .addGitHubLink("15520295")
                .addFacebookLink("huydd.1997")
                .addFiveStarsAction()
                .setVersionNameAsAppSubTitle()
                .addShareAction(R.string.app_name)
                .addUpdateAction()
                .addFeedbackAction("huydd.1997@gmail.com")
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(true)
                .build();

        addContentView(view, params);

    }
}
