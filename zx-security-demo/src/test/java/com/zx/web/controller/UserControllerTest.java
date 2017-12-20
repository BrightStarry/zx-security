package com.zx.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017-11-18 23:34
 * 用户控制层测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    //spring的web容器

//    private WebApplicationContext wac;

    //mvc模拟类
    @Autowired
    private MockMvc mockMvc;

//    //使用容器构建mvc模拟类
//    @Before
//    public void setup() {
//        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
//    }

    //文件上传
    @Test
    public void whenUploadSuccess() throws Exception {
        String result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/file")
                .file(
                        new MockMultipartFile("file",//方法中接收文件的参数名
                                "test.txt",//文件名
                                MediaType.MULTIPART_FORM_DATA_VALUE,//类型
                                "hello test".getBytes("UTF-8")//文件内容
                        )
                ))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }


    //查询用户列表
    @Test
    public void whenQuerySuccess() throws Exception {
        /**
         * 向/user发起get请求，其中header的contentType为json，参数自定义
         * 并且期望，返回的http状态码为200，
         * 并且期望，将返回的json解析后，数组长度为3
         */
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/user")
                .param("username", "zx")
                .param("password", "111111")
                .param("age", "32")
                .param("ageTo", "64")
                //分页参数
                .param("size", "15")
                .param("page", "1")
                .param("sort", "age,desc")

                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("username", "zx"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

    //查询用户详情
    @Test
    public void whenGenInfoSuccess() throws Exception {
        String result = mockMvc.perform(MockMvcRequestBuilders.get("/user/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("zx"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

    //创建用户
    @Test
    public void whenCreateSuccess() throws Exception {
        long timestamp = System.currentTimeMillis();

        String content = "{\"username\":\"zx\",\"password\":null,\"birthday\":" + timestamp + "}";
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/user")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

    //修改用户
    @Test
    public void whenUpdateSuccess() throws Exception {
        //获取一个一年后的时间
        Date date = new Date(
                LocalDateTime.now()
                        .plusYears(1)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli());
        long timestamp = date.getTime();

        String content = "{\"id\":\"1\",\"username\":\"zx\",\"password\":null,\"birthday\":" + timestamp + "}";
        String result = mockMvc.perform(MockMvcRequestBuilders.put("/user/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }

    //删除用户
    @Test
    public void whenDeleteSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/user/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
