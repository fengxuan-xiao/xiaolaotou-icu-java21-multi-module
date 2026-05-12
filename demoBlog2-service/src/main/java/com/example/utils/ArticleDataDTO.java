package com.example.utils;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ArticleDataDTO {


    @ExcelProperty(value = "文章标题", index = 0)
    private String title;
    @ExcelProperty(value = "文章内容", index = 1)
    private String content;
    @ExcelProperty(value = "作者名称", index = 2)
    private String authorName;
    @ExcelProperty(value = "标签", index = 3)
    private String tags;
}
