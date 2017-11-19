package com.zx.wiremock;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * author:ZhengXing
 * datetime:2017-11-19 18:15
 * 向wireMock传递
 *
 */
public class MockService {

    public static void main(String[] args) throws IOException {
        //设置端口,本机无需设置ip
        WireMock.configureFor(8090);
        //清除之前所有映射
        WireMock.removeAllMappings();

        String  url = "/order/1";
        String fileName = "01";
        mock(url, fileName);


    }

    private static void mock(String url, String fileName) throws IOException {
        //读取文件
        ClassPathResource resource = new ClassPathResource("mock/response/"+ fileName+".json");
        String content = FileUtils.readFileToString(resource.getFile(), "utf-8");
        //创建一个接口
        WireMock
                .stubFor(WireMock.get(WireMock.urlPathEqualTo(url))
                        .willReturn(WireMock.aResponse().withBody(content)
                                .withStatus(200)));
    }
}
