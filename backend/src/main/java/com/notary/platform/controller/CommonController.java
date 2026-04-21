package com.notary.platform.controller;

import com.notary.platform.dto.Result;
import com.notary.platform.enums.IndustryType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 通用控制器 - 文件上传、行业类型等
 */
@Tag(name = "通用接口", description = "文件上传、行业类型字典等公共接口")
@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class CommonController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }
        // 确保目录存在
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        File dest = new File(dir, fileName);
        file.transferTo(dest);

        Map<String, String> result = new HashMap<>();
        result.put("fileName", fileName);
        result.put("url", "/uploads/" + fileName);
        return Result.success(result);
    }

    @Operation(summary = "获取行业类型字典")
    @GetMapping("/industry-types")
    public Result<List<Map<String, String>>> getIndustryTypes() {
        List<Map<String, String>> list = new ArrayList<>();
        for (IndustryType type : IndustryType.values()) {
            Map<String, String> map = new HashMap<>();
            map.put("code", type.getCode());
            map.put("desc", type.getDesc());
            list.add(map);
        }
        return Result.success(list);
    }
}
