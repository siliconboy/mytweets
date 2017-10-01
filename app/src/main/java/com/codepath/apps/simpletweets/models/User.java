package com.codepath.apps.simpletweets.models;

import com.codepath.apps.simpletweets.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by yingbwan on 9/26/2017.
 */

@Table(database = MyDatabase.class)
@Parcel(analyze = {User.class})
public class User extends BaseModel {
    @PrimaryKey
    @Column
    long id;
    @Column
    String name;
    @Column
    String screenName;
    @Column
    String profileImageUrl;
    @Column
    String draft;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }

    public User() {
    }

    public User(JSONObject object) {
        super();

        try {
            this.id = object.getLong("id");
            this.name = object.getString("name");
            this.screenName = object.getString("screen_name");
            this.profileImageUrl = object.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User(jsonObject);
        return user;
    }

    // Record Finders
    public static User byId(long id) {
        return new Select().from(User.class).where(User_Table.id.eq(id)).querySingle();
    }

}
