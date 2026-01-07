package com.chunchun.chunpicturebackend.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import com.chunchun.chunpicturebackend.exception.ErrorCode;
import com.chunchun.chunpicturebackend.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class UrlPictureUpload extends PictureUploadTemplate {
    @Override
    protected void validPicture(Object inputSource) {
        String fileUrl = (String) inputSource;
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR,
                "文件地址不能为空");
        // ... 跟之前的校验逻辑保持一致  
    }

    @Override
    protected String getOriginFilename(Object inputSource) {
        String fileUrl = (String) inputSource;
        // 从 URL 中提取文件名   这里之前有个小 bug getMainName
        return FileUtil.getName(fileUrl);
    }

    @Override
    protected void processFile(Object inputSource, File file) throws Exception {
        String fileUrl = (String) inputSource;
        // 下载文件到临时目录
        log.info(fileUrl);
        try {
            HttpUtil.downloadFile(fileUrl, file);
        } catch (HttpException e) {
            log.error("下载文件失败，状态码异常：{}", e.getMessage());
            // 根据业务需求处理：重试/跳过/记录待处理列表等
        }
    }
}
