package com.zx.security.core.validate.code.image;

import com.zx.security.core.properties.SecurityProperties;
import com.zx.security.core.validate.code.abstracts.CaptchaGenerator;
import lombok.Setter;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * author:ZhengXing
 * datetime:2017-11-26 13:01
 * 基础图形验证码生成器接口实现类
 */
@Setter
public class DefaultImageCaptchaGenerator implements CaptchaGenerator<ImageCaptcha> {

    @Autowired
    private SecurityProperties securityProperties;


    @Override
    public ImageCaptcha createCaptcha(ServletWebRequest request) {
        int width = ServletRequestUtils.getIntParameter(
                request.getRequest(),
                "width",
                securityProperties.getCaptcha().getImage().getWidth());
        int height = ServletRequestUtils.getIntParameter(
                request.getRequest(),
                "height",
                securityProperties.getCaptcha().getImage().getHeight());
        int length = ServletRequestUtils.getIntParameter(
                request.getRequest(),
                "length",
                securityProperties.getCaptcha().getImage().getLength());

        int expireSecond = ServletRequestUtils.getIntParameter(
                request.getRequest(),
                "expireSecond",
                securityProperties.getCaptcha().getImage().getExpireSecond());

        //创建一张内存中的缓存图片
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        ///背景色
        //通过graphics绘制图像
        Graphics graphics = bufferedImage.getGraphics();
        //设置颜色
        graphics.setColor(Color.yellow);
        //填充
        graphics.fillRect(0, 0, width, height);

        ///画边框
        graphics.setColor(Color.blue);
        graphics.drawRect(0, 0, width-1, height-1);

        //写字母
        String content = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcedfghijklmnopqrstuvwxyz1234567890";
        //设置字体颜色
        graphics.setColor(Color.red);
        //设置字体及大小
        graphics.setFont(new Font("宋体", Font.BOLD, 20));
        int x=10;
        int y=20;
        StringBuilder code = new StringBuilder();
        for(int i = 0; i < length; i++)
        {
            int index = RandomUtils.nextInt(content.length());
            char letter = content.charAt(index);
            code.append(letter);
            graphics.drawString(letter+" ", x, y);
            x = x+10;
        }

        //绘制干扰线
        int x1;
        int x2;
        int y1;
        int y2;
        graphics.setColor(Color.LIGHT_GRAY);
        for(int i = 0;i <10;i++)
        {
            x1=RandomUtils.nextInt(width);
            x2=RandomUtils.nextInt(width);
            y1=RandomUtils.nextInt(height);
            y2=RandomUtils.nextInt(height);
            graphics.drawLine(x1, y1, x2, y2);
        }

        return new ImageCaptcha(bufferedImage, code.toString(), expireSecond);
    }
}
