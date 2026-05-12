package com.example.api.bpo;

import com.example.api.dto.*;
import com.example.api.dto.common.PageResult;
import com.example.api.dto.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(contextId = "excelbatchBPO",value = "xgh-demoBlog2-service", path = "/excelbatch")
public interface IExcelBatchBPO {
    /**
     * 下载 Excel 模板
     */
    @GetMapping("/import/template/download")
    void downloadExcelTemplate();

    /**
     * 批量导入 Excel
     */
    @PostMapping(value = "/import/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<ImportResultVO> batchImport(@RequestPart("file") MultipartFile file);

    /**
     * 查询导入记录列表
     */
    @GetMapping("/import/list")
    Result<PageResult<ImportRecordVO>> queryImportList(@SpringQueryMap ImportQueryDTO queryDTO);

}
