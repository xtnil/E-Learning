package com.codestudioapps.learing.app.models.quiz;

import android.os.Parcel;
import android.os.Parcelable;

public class ResultModel implements Parcelable {
    private String question;
    private String givenAns;
    private String correctAns;
    private boolean isCorrect;
    private boolean isSkip;

    public ResultModel(String question, String givenAns, String correctAns, boolean isCorrect, boolean isSkip) {
        this.question = question;
        this.givenAns = givenAns;
        this.correctAns = correctAns;
        this.isCorrect = isCorrect;
        this.isSkip = isSkip;
    }

    public String getQuestion() {
        return question;
    }

    public String getGivenAns() {
        return givenAns;
    }

    public String getCorrectAns() {
        return correctAns;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public boolean isSkip() {
        return isSkip;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(question);
        dest.writeString(givenAns);
        dest.writeString(correctAns);
        dest.writeByte((byte) (isCorrect ? 1 : 0));
        dest.writeByte((byte) (isSkip ? 1 : 0));
    }

    protected ResultModel(Parcel in) {
        question = in.readString();
        givenAns = in.readString();
        correctAns = in.readString();
        isCorrect = in.readByte() != 0;
        isSkip = in.readByte() != 0;
    }

    public static Creator<ResultModel> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<ResultModel> CREATOR = new Creator<ResultModel>() {
        @Override
        public ResultModel createFromParcel(Parcel source) {
            return new ResultModel(source);
        }

        @Override
        public ResultModel[] newArray(int size) {
            return new ResultModel[size];
        }
    };
}
