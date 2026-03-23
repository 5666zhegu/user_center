package com.geek.usercenter.controller;

import com.geek.usercenter.common.BaseResponse;
import com.geek.usercenter.common.ErrorCode;
import com.geek.usercenter.common.ResultUtils;
import com.geek.usercenter.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;


@Slf4j
@RequestMapping("/file")
@RestController
public class FileUploadController {

    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if(file.isEmpty()){
            throw new BusinessException(ErrorCode.NULL_ERROR,"传入的不是有效文件");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件名不能为空");
        }
        int lastIndexOf = originalFilename.lastIndexOf(".");
        if(lastIndexOf == -1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"传入文件格式错误");
        }
        String suffix = originalFilename.substring(lastIndexOf);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String newFileName = uuid.substring(0,8) + suffix;

        String uploadDir = "D:/code/user-center/";
        File dest = new File(uploadDir,newFileName);

        if(!dest.getParentFile().exists()){
            dest.getParentFile().mkdirs();
        }

        file.transferTo(dest);

        String url = "http://localhost:8080/api/upload/" + newFileName;
        return ResultUtils.success(url);
    }


}
