package com.c.vpn.model;

import com.alibaba.fastjson.JSONObject;

public class RegLoginEvent {
    public JSONObject data;
    public RegLoginEvent(JSONObject data){
        this.data = data;
    }
}
