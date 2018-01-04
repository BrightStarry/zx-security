package com.zx.security.core.social.qq.api;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * author:ZhengXing
 * datetime:2018-01-02 21:20
 * qq用户信息
 * 从文档中获取的字段
 *
 * 使用IDEA的多行编辑,..几秒搞定.舒服
 */
@Data
@Accessors(chain = true)
public class QQUserInfo {

    private String openId;//用户id 不是返回来的,需要自行添加

    private Integer is_lost;//..文档中没有
    private Integer ret;//返回码
    private String msg;//如果ret<0，会有相应的错误信息提示，返回数据全部用UTF-8编码。
    private String nickname;//用户在QQ空间的昵称。
    private String figureurl;//大小为30×30像素的QQ空间头像URL。
    private String figureurl_1;//大小为50×50像素的QQ空间头像URL。
    private String figureurl_2;//大小为100×100像素的QQ空间头像URL。
    private String figureurl_qq_1;//大小为40×40像素的QQ头像URL。
    private String figureurl_qq_2;//大小为100×100像素的QQ头像URL。需要注意，不是所有的用户都拥有QQ的100x100的头像，但40x40像素则是一定会有。
    private String gender;//性别。 如果获取不到则默认返回"男"
    private String is_yellow_vip;//标识用户是否为黄钻用户（0：不是；1：是）。
    private String vip;//标识用户是否为黄钻用户（0：不是；1：是）
    private String yellow_vip_level;//黄钻等级
    private String level;//黄钻等级
    private String is_yellow_year_vip;//标识是否为年费黄钻用户（0：不是； 1：是）
    private String province;//省份
    private String city;//城市
    private String year;//出生年分
}
