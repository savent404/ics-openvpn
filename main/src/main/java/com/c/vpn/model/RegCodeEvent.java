package com.c.vpn.model;

import com.alibaba.fastjson.JSONObject;

public class RegCodeEvent {
    public JSONObject data;
    public RegCodeEvent(JSONObject data){
        this.data = data;
    }
}
