package com.c.vpn.model;

public class UserInfoModel {
    /**
     * code : 0
     * desc : ok
     * user : {"id":"demo@outlook.com","expireDate":"2019-11-09T08:00:00.000Z","registerDate":"2019-10-08T02:04:24.000Z","lastUpdateDate":"2019-10-08T02:04:24.000Z","level":0}
     */

    private int code;
    private String desc;
    private UserBean user;

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

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class UserBean {
        /**
         * id : demo@outlook.com
         * expireDate : 2019-11-09T08:00:00.000Z
         * registerDate : 2019-10-08T02:04:24.000Z
         * lastUpdateDate : 2019-10-08T02:04:24.000Z
         * level : 0
         */

        private String id;
        private String expireDate;
        private String registerDate;
        private String lastUpdateDate;
        private int level;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getExpireDate() {
            return expireDate;
        }

        public void setExpireDate(String expireDate) {
            this.expireDate = expireDate;
        }

        public String getRegisterDate() {
            return registerDate;
        }

        public void setRegisterDate(String registerDate) {
            this.registerDate = registerDate;
        }

        public String getLastUpdateDate() {
            return lastUpdateDate;
        }

        public void setLastUpdateDate(String lastUpdateDate) {
            this.lastUpdateDate = lastUpdateDate;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }
    }
}
