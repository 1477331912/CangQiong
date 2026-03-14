package com.sky.controller.admin;


import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {

//    /**
//     * 文件上传阿里云
//     * @param file
//     * @return
//     */
//    @PostMapping("/upload")
//    @ApiOperation("文件上传")
//    public Result<String> upload(MultipartFile file) {
//        log.info("文件上传：{}", file);
//
//        return Result.success();
//    }


    @Value("${sky.file.upload-dir}")
    private String uploadDir;

    @Value("${sky.file.access-url}")
    private String accessUrl; // 前端可访问的 URL 前缀

     /**
     * 文件上传
     * @param file
     * @return
     */
    @ApiOperation("文件上传接口")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}", file);        //获取原始文件名
        String filename = file.getOriginalFilename();
        //提取文件扩展名
        String extension = filename.substring(filename.lastIndexOf('.'));
        //生成新文件名
        String newFileName = UUID.randomUUID().toString() + extension;
        //创建目标文件对象
        File dest = new File(uploadDir + File.separator + newFileName);
        try {
            // 将上传的文件保存到指定位置
            file.transferTo(dest);
            // 返回前端可访问的 URL
            String fileUrl = accessUrl + "/" + newFileName;
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
