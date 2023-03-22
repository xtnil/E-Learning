package com.codestudioapps.learing.app.models.content;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Contents implements Parcelable {
    String title;
    ArrayList<Item> items = new ArrayList<>();

    public Contents(String title, ArrayList<Item> items) {
        this.title = title;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeList(items);
    }

    protected Contents(Parcel in) {
        title = in.readString();
        in.readList(items, Contents.class.getClassLoader());
    }

    public static Creator<Contents> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<Contents> CREATOR = new Creator<Contents>() {
        @Override
        public Contents createFromParcel(Parcel source) {
            return new Contents(source);
        }

        @Override
        public Contents[] newArray(int size) {
            return new Contents[size];
        }
    };
}