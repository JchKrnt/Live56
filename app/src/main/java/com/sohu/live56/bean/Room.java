package com.sohu.live56.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.sohu.kurento.bean.RoomBean;

/**
 * Created by jingbiaowang on 2015/8/13.
 */
public class Room extends RoomBean implements Parcelable {
    /**
     * 视频房间播放状态。
     */
    public enum State {
        LIVING, OVER;
    }

    private State state;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.state.name());
    }

    public Room() {
    }

    protected Room(Parcel in) {
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


    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
