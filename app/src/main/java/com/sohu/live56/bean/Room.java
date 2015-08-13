package com.sohu.live56.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jingbiaowang on 2015/8/13.
 */
public class Room implements Parcelable {
    /**
     * 视频房间播放状态。
     */
    enum State {
        LIVING, OVER;
    }

    private int id;
    private String title;
    private State state;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.state.name());
    }

    public Room() {
    }

    protected Room(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.state = State.valueOf(in.readString());
    }

    public static final Parcelable.Creator<Room> CREATOR = new Parcelable.Creator<Room>() {
        public Room createFromParcel(Parcel source) {
            return new Room(source);
        }

        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
