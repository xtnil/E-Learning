package com.codestudioapps.learing.app.models.content;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Item implements Parcelable {
    String tagLine;
    ArrayList<String> details = new ArrayList<>();

    public Item(String tagLine, ArrayList<String> details) {
        this.tagLine = tagLine;
        this.details = details;
    }

    public String getTagLine() {
        return tagLine;
    }

    public ArrayList<String> getDetails() {
        return details;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tagLine);
        dest.writeList(details);
    }

    protected Item(Parcel in) {
        tagLine = in.readString();
        in.readList(details, Item.class.getClassLoader());
    }

    public static Creator<Item> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}