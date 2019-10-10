package com.c.vpn.model;

import com.alibaba.fastjson.JSONObject;

public class ServerListEvent {
    public JSONObject data;
    public ServerListEvent(JSONObject data){
        this.data = data;
    }
}
