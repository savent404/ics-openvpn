package com.c.vpn.model;

import com.alibaba.fastjson.JSONObject;

public class RegEvent {
    public JSONObject data;
    public RegEvent(JSONObject data){
        this.data = data;
    }
}
