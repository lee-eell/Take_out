package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

//    log.info("进来了吗？ upload？？");

    @Value("${reggie.path}")    //从设置中获取配置
    private String basePath;    //设置默认路径

    /**
     * 上传图片功能
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info("我进来上传文件了");
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //通过subString拿到.后面的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名重复而被覆盖
        String fileName = UUID.randomUUID().toString() + suffix;

        //判断basePath路径的文件夹是否存在，不存在则创建
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        //这里为什么要try catch？
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        log.info("我进来拿图片了");
        log.info(name);
        try {
            //获取文件对象
            File file = new File(basePath + name);
            log.info(file.getPath());
            //创建输入流，通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(file);
            //浏览器带有输出流，获取浏览器输出流 将文件回写浏览器里
            ServletOutputStream fileOutputStream = response.getOutputStream();
            //设置response响应的类型
            response.setContentType("image/jpeg");

            //开始用io流 字节方式读字节
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){  //这里之前开发忘记了len=    一定要保证读的长度进行判断
                fileOutputStream.write(bytes,0,len);
                fileOutputStream.flush();
            }
            //别忘记关闭IO流，释放资源
            fileOutputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

