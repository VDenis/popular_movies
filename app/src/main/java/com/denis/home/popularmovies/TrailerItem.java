package com.denis.home.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Denis on 01.01.2016.
 */
public class TrailerItem implements Parcelable {
    public String link;
    public String name;
    public String thumbnail;

    public TrailerItem(String link, String name, String thumbnail) {
        this.link = link;
        this.name = name;
        this.thumbnail = thumbnail;
    }

    protected TrailerItem(Parcel in) {
        link = in.readString();
        name = in.readString();
        thumbnail = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(link);
        dest.writeString(name);
        dest.writeString(thumbnail);
    }

    public static final Creator<TrailerItem> CREATOR = new Creator<TrailerItem>() {
        @Override
        public TrailerItem createFromParcel(Parcel in) {
            return new TrailerItem(in);
        }

        @Override
        public TrailerItem[] newArray(int size) {
            return new TrailerItem[size];
        }
    };
}
