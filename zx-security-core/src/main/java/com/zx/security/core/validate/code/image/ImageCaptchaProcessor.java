package com.zx.security.core.validate.code.image;

import com.zx.security.core.validate.code.abstracts.AbstractCaptchaProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * author:ZhengXing
 * datetime:2017-11-26 15:45
 * 图形验证码处理器实现类
 */
@Component
public class ImageCaptchaProcessor extends AbstractCaptchaProcessor<ImageCaptcha> {
    @Override
    protected void process1(ServletWebRequest request, ImageCaptcha captcha) throws ServletRequestBindingException, IOException {
        //将流输出回去,ServletWebRequest也是可以保存response的
        ImageIO.write(captcha.getImage(),"JPEG",request.getResponse().getOutputStream());
    }
}
