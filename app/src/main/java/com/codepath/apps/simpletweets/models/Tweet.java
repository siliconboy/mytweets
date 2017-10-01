package com.codepath.apps.simpletweets.models;

import com.codepath.apps.simpletweets.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yingbwan on 9/26/2017.
 */

@Table(database = MyDatabase.class)
@Parcel(analyze = {Tweet.class})
public class Tweet extends BaseModel {

    // Define database columns and associated fields
    @PrimaryKey
    @Column
    Long id;
    @Column
    Long userId;

    @Column
    String timestamp;
    @Column
    String body;



    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getBody() {
        return body;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Tweet() {
    }

    // Add a constructor that creates an object from the JSON response
    public Tweet(JSONObject object) {
        super();

        try {
            this.id = object.getLong("id");
            this.userId = object.getJSONObject("user").getLong("id");
            if (User.byId(this.userId) == null) {
                User user = User.fromJSON(object.getJSONObject("user"));
                user.save();
            }

            this.timestamp = object.getString("created_at");
            this.body = object.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = new Tweet(tweetJson);
            tweet.save();
            tweets.add(tweet);
        }

        return tweets;
    }

    public static Tweet fromJson(JSONObject jsonObject) {
        Tweet tweet = new Tweet(jsonObject);
        tweet.save();
        return tweet;
    }

    // Record Finders
    public static Tweet byId(long id) {
        return new Select().from(Tweet.class).where(Tweet_Table.id.eq(id)).querySingle();
    }

    public static List<Tweet> recentItems() {
        return new Select().from(Tweet.class).orderBy(Tweet_Table.id, false).limit(300).queryList();
    }

}
