package com.codepath.apps.simpletweets.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.GlideApp;
import com.codepath.apps.simpletweets.utils.MyUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yingbwan on 9/26/2017.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    List<Tweet> mTweets;
    Context context;

    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);
        Log.d("DEBUG", "tweet uer id :" + tweet.getUserId());
        Log.d("DEBUG", "id:" + tweet.getId());
        holder.tvBody.setText(tweet.getBody());
        User usr = User.byId(tweet.getUserId());
        holder.tvUserName.setText(usr.getName() + "@" + usr.getScreenName());
        holder.tvTimeGap.setText(MyUtils.getRelativeTimeAgo(tweet.getTimestamp()));
        GlideApp.with(context).load(usr.getProfileImageUrl()).override(75, 75).fitCenter().into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvBody)
        TextView tvBody;
        @BindView(R.id.tvTimeGap)
        TextView tvTimeGap;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
