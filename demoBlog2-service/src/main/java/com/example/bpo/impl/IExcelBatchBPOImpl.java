package com.example.bpo.impl;

import com.example.api.bpo.IExcelBatchBPO;
import com.example.api.dto.*;
import com.example.api.dto.common.PageResult;
import com.example.api.dto.common.Result;
import com.example.blo.IExcelBatchBLO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/excelbatch")
@Slf4j
@Tag(name = "Excel批量导入", description = "Excel批量导入相关接口")
public class IExcelBatchBPOImpl implements IExcelBatchBPO {

    @Resource
    private IExcelBatchBLO iExcelBatchBLO;

    @Override
    @Operation(summary = "下载Excel模板", description = "下载Excel导入模板文件")
    @GetMapping("/import/template/download")
    public void downloadExcelTemplate() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        log.info("下载Excel模板");
        iExcelBatchBLO.downloadTemplate(response);
    }


    @Override
    @Operation(summary = "批量导入Excel", description = "批量导入Excel数据")
    @PostMapping(value = "/import/batch")
    public Result<ImportResultVO> batchImport(@RequestPart("file") MultipartFile file) {
        //log.info("批量导入Excel，操作人: {}", operator);

        ImportResultVO result = iExcelBatchBLO.batchImport(file);

        return Result.success(result);
    }

    @Override
    @Operation(summary = "查询导入记录列表", description = "分页查询导入记录")
    @GetMapping("/import/list")
    public Result<PageResult<ImportRecordVO>> queryImportList(@ModelAttribute ImportQueryDTO queryDTO) {
        log.info("查询导入记录列表，参数: {}", queryDTO);

        PageResult<ImportRecordVO> result = iExcelBatchBLO.queryImportList(queryDTO);

        return Result.success(result);
    }
}
