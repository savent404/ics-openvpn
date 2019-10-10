package com.c.vpn.model;

import com.alibaba.fastjson.JSONObject;

public class ExitLoginActivityEvent {
    public JSONObject data;
    public ExitLoginActivityEvent(JSONObject data){
        this.data = data;
    }
}
