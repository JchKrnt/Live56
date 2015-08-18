package com.sohu.live56.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用户信息。
 */
public class UserInfo implements Parcelable {

    public enum LoginType {
        WEIXIN, MICROBLOG, QQ, TEL;

    }


    private String name;
    private String pwd;
    private String career;
    private String imgUrl;
    private LoginType loginType;
    private String telNum;


    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }


    public String getName() {
        return name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public LoginType getLoginType() {
        return loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.pwd);
        dest.writeString(this.career);
        dest.writeString(this.imgUrl);
        dest.writeString(this.loginType.name());
        dest.writeString(this.telNum);
    }

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        this.name = in.readString();
        this.pwd = in.readString();
        this.career = in.readString();
        this.imgUrl = in.readString();
        this.loginType = LoginType.valueOf(in.readString().toUpperCase());
        this.telNum = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}
