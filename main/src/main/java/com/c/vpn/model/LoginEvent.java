package com.c.vpn.model;

import com.alibaba.fastjson.JSONObject;

public class LoginEvent {
    public JSONObject data;
    public LoginEvent(JSONObject data){
        this.data = data;
    }
}
