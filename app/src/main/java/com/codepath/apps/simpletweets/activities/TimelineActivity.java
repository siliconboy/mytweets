package com.codepath.apps.simpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.simpletweets.R;
import com.codepath.apps.simpletweets.TwitterApp;
import com.codepath.apps.simpletweets.TwitterClient;
import com.codepath.apps.simpletweets.adapters.EndlessRecyclerViewScrollListener;
import com.codepath.apps.simpletweets.adapters.TweetAdapter;
import com.codepath.apps.simpletweets.fragments.TweetFragment;
import com.codepath.apps.simpletweets.interfaces.TweetDialogListener;
import com.codepath.apps.simpletweets.models.Tweet;
import com.codepath.apps.simpletweets.models.User;
import com.codepath.apps.simpletweets.networks.NetworkUtils;
import com.codepath.apps.simpletweets.utils.ItemClickSupport;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static com.codepath.apps.simpletweets.models.Tweet.fromJson;


public class TimelineActivity extends AppCompatActivity implements TweetDialogListener {
    private TwitterClient client;
    TweetAdapter adapter;
    ArrayList<Tweet> tweets;
    @BindView(R.id.rvTweet)
    RecyclerView rvTweets;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    User mUser;
    Long mMaxId;
    Long mSinceId;

    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient();
        ButterKnife.bind(this);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        tweets = new ArrayList<>();
        adapter = new TweetAdapter(tweets);
        rvTweets.setAdapter(adapter);

        if (NetworkUtils.isNetworkAvailable(this) || NetworkUtils.isOnline()) {
            Toast.makeText(this, "offline mode. Loading local data.", Toast.LENGTH_LONG).show();
            //     Snackbar.make(searchLayout, R.string.net_error, Snackbar.LENGTH_LONG).show();

            tweets.addAll(Tweet.recentItems());
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);

        getCredential();

        populateTimeline(1L, Long.MAX_VALUE - 1);
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page, view);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

        rvTweets.setItemAnimator(new SlideInUpAnimator());

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(rvTweets.getContext(), DividerItemDecoration.VERTICAL);
        rvTweets.addItemDecoration(itemDecoration);

        ItemClickSupport.addTo(rvTweets).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    //create intent
                    Intent intent = new Intent(getApplicationContext(), TweetActivity.class);
                    //get article
                    Tweet tweet = tweets.get(position);
                    intent.putExtra("tweet", Parcels.wrap(tweet));
                    //launch activity
                    startActivity(intent);

                }
        );

    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset, View view) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        final int curSize = adapter.getItemCount();

        // Create the Handler object (on the main thread by default)
        Handler handler = new Handler();
        // Define the code block to be executed
        Runnable runnableCode = () -> {
            // load data using network api
            //TODO:
            //// 3. Reset endless scroll listener when performing a new search
            //scrollListener.resetState();
            populateTimeline(1L, tweets.get(curSize - 1).getId());

        };
        // Run the above code block on the main thread after 500 miliseconds
        handler.postDelayed(runnableCode, 500);
    }

    public void getCredential() {
        client.getCredential(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    mUser = User.fromJSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Twitter.client", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("Twitter.client", response.toString());

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Twitter.client", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("Twitter.client", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("Twitter.client", responseString);
                throwable.printStackTrace();
            }
        });
    }


    public void populateTimeline(Long sinceId, Long maxId) {

        client.getHomeTimeline(sinceId, maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Twitter.client", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                tweets.addAll(fromJson(response));
                adapter.notifyItemInserted(tweets.size() - 1);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Twitter.client", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("Twitter.client", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("Twitter.client", responseString);
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            TweetFragment tweetFragment;
            FragmentManager fm = getSupportFragmentManager();
            tweetFragment = TweetFragment.newInstance(mUser);
            tweetFragment.show(fm, "fragment_tweet");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishDialog(String msg) {

        client.postTweet(msg, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet = fromJson(response);
                tweets.add(tweet);
                adapter.notifyItemInserted(tweets.size() - 1);
                rvTweets.scrollToPosition(adapter.getItemCount() - 1);
                Log.d("Twitter.return", tweet.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                Log.d("Twitter.client", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Twitter.client", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("Twitter.client", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("Twitter.client", responseString);
                throwable.printStackTrace();
            }
        });

    }
}
