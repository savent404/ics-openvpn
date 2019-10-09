package com.c.vpn.model;

import com.alibaba.fastjson.JSONObject;

public class SaltEvent {
    public JSONObject data;
    public SaltEvent(JSONObject data){
        this.data = data;
    }
}
