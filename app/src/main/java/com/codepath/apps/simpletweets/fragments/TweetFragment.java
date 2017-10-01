package com.codepath.apps.simpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.activities.TimelineActivity;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.utils.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *
 */
public class TweetFragment extends DialogFragment {
    @BindView(R.id.etTweet)
    EditText etTweet;
    @BindView(R.id.ivImage)
    ImageView ivImage;
    @BindView(R.id.tvCount)
    TextView tvCount;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.btnClose)
    ImageButton btnClose;
    @BindView(R.id.btnTweet)
    Button btnTweet;

    private final int MAX_LIMIT = 140;
    private Unbinder unbinder;
    private User mUser;
    private String msg;

    public TweetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TweetFragment.
     */
    public static TweetFragment newInstance(User user) {
        TweetFragment fragment = new TweetFragment();

        fragment.setUser(user);
        return fragment;
    }

    public void setUser(User user) {
        mUser = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container);
        unbinder = ButterKnife.bind(this, view);


        btnTweet.setOnClickListener(view1 -> {
            TimelineActivity act = (TimelineActivity) getActivity();
            msg = etTweet.getText().toString();
            act.onFinishDialog(msg);
            dismiss();
        });

        etTweet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    tvCount.setText(String.valueOf(MAX_LIMIT - s.length()));
                    if (s.length() > MAX_LIMIT) {
                        btnTweet.setEnabled(false);
                    } else {
                        btnTweet.setEnabled(true);
                    }
                } else {
                    tvCount.setText("0");
                }
            }
        });

        btnClose.setOnClickListener(view1 -> {
            dismiss();
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //User usr = User.byId(mTweet.getUserId());
        tvName.setText(mUser.getName());
        tvUserName.setText(mUser.getScreenName());
        etTweet.setText(""); //clear content
        GlideApp.with(getContext()).load(mUser.getProfileImageUrl()).fitCenter().override(75, 75).into(ivImage);
        tvCount.setText("140");   //max limit
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
