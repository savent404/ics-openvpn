package com.c.vpn.model;

import java.util.List;

public class ServerListModel {

    /**
     * code : 0
     * desc : OK
     * sites : [{"name":"LV1 美国1","username":"demo@outlook.com","passwd":"Abc123","config":["client","nobind","dev tun","comp-lzo","tun-mtu 1500","resolv-retry infinite","remote-cert-tls server",";remote 156.236.75.96 16391 udp",";remote 156.233.65.151 16393 udp","remote 173.242.115.109 16393 udp","","cipher AES-256-CBC","key-direction 1","","<key>"]}]
     */

    private int code;
    private String desc;
    private List<SitesBean> sites;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<SitesBean> getSites() {
        return sites;
    }

    public void setSites(List<SitesBean> sites) {
        this.sites = sites;
    }

    public static class SitesBean {
        /**
         * name : LV1 美国1
         * username : demo@outlook.com
         * passwd : Abc123
         * config : ["client","nobind","dev tun","comp-lzo","tun-mtu 1500","resolv-retry infinite","remote-cert-tls server",";remote 156.236.75.96 16391 udp",";remote 156.233.65.151 16393 udp","remote 173.242.115.109 16393 udp","","cipher AES-256-CBC","key-direction 1","","<key>"]
         */

        private String name;
        private String username;
        private String passwd;
        private List<String> config;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPasswd() {
            return passwd;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        public List<String> getConfig() {
            return config;
        }

        public void setConfig(List<String> config) {
            this.config = config;
        }
    }
}
