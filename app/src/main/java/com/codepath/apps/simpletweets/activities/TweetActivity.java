package com.codepath.apps.simpletweets.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.helper.GlideApp;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TweetActivity extends AppCompatActivity {

    // show detail of a tweet
    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvScreen)
    TextView tvScreen;
    @BindView(R.id.tvBody)
    TextView tvBody;
    @BindView(R.id.ibRepeat)
    ImageButton ibRepeat;
    @BindView(R.id.ibReply)
    ImageView ivReply;
    @BindView(R.id.ibStar)
    ImageView ivStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        ButterKnife.bind(this);

        Tweet tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));

        tvBody.setText(tweet.getBody());
        User usr = User.byId(tweet.getUserId());
        tvName.setText(usr.getName());
        tvScreen.setText(usr.getScreenName());
        GlideApp.with(this).load(usr.getProfileImageUrl()).override(150,150).fitCenter().apply(RequestOptions.circleCropTransform()).into(ivImage);
        //Log.d("DEBUG", "data: " + tweet.getBody());
    }


}
