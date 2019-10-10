package com.c.vpn.model;

import com.alibaba.fastjson.JSONObject;

public class RegSaltEvent {
    public JSONObject data;
    public RegSaltEvent(JSONObject data){
        this.data = data;
    }
}
