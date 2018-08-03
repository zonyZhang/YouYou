package com.youyou.constant;

/**
 * 项目相关常量
 *
 * @author zony
 */

public interface YouYouConstant {

    /**
     * 发送相关
     */
    interface SendConst {
        /**
         * ip地址
         */
        String IP = "192.168.190.69";

        /**
         * 端口
         */
        int PORT = 8080;
    }

    /**
     * 用户信息字段
     */
    interface UserField {

        /**
         * USERITEM
         */
        String USERITEM = "useritem";

        /**
         * 经度
         */
        String LAT = "lat";

        /**
         * 纬度
         */
        String LNG = "lng";

        /**
         * uuid
         */
        String UUID = "uuid";

        /**
         * 用户名
         */
        String NAME = "name";

        /**
         * 电话
         */
        String TEL = "tel";

        /**
         * 描述
         */
        String DES = "des";

        /**
         * 是否在线
         */
        String ISONLINE = "isonline";
    }
}
