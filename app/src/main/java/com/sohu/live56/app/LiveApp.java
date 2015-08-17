package com.sohu.live56.app;

import android.app.Application;
import android.content.SharedPreferences;

import com.sohu.live56.bean.UserInfo;
import com.sohu.live56.util.Constants;

/**
 * Created by jingbiaowang on 2015/8/17.
 */
public class LiveApp extends Application {

    private SharedPreferences spf;

    private UserInfo userInfo = new UserInfo();

    @Override
    public void onCreate() {
        super.onCreate();
        spf = getSharedPreferences(Constants.SPF_NAME, MODE_PRIVATE);
    }

    public UserInfo getUserInfo() {

        this.userInfo.setName(spf.getString("name", ""));
        this.userInfo.setPwd(spf.getString("pwd", ""));
        this.userInfo.setCareer(spf.getString("career", ""));
        this.userInfo.setImgUrl(spf.getString("imgUrl", ""));
        String loginStr = spf.getString("loginType", "");
        if (!loginStr.equals(""))
            userInfo.setLoginType(UserInfo.LoginType.valueOf(loginStr));
        return userInfo;
    }

    public void saveUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        SharedPreferences.Editor edit = spf.edit();
        edit.putString("name", userInfo.getName());
        edit.putString("pwd", userInfo.getPwd());
        edit.putString("imgUrl", userInfo.getImgUrl());
        edit.putString("loginType", userInfo.getLoginType().name());
        edit.putString("career", userInfo.getCareer());

        edit.commit();
    }

    public void saveUserName(String name) {
        SharedPreferences.Editor edit = spf.edit();
        edit.putString("name", name);

        edit.commit();
    }

    public void saveUserPwd(String pwd) {
        SharedPreferences.Editor edit = spf.edit();
        edit.putString("pwd", pwd);

        edit.commit();
    }

    public boolean checkLogin() {

        if (userInfo.getName() != null && !userInfo.equals(""))
            return true;
        else return false;
    }
}
