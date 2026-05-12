package com.example.blo;

import com.example.api.dto.*;
import com.example.api.dto.common.PageResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IExcelBatchBLO {


    /**
     * 下载 Excel 模板
     */
    void downloadTemplate(HttpServletResponse response);

    /**
     * 批量导入 Excel
     */
    ImportResultVO batchImport(MultipartFile file);

    /**
     * 查询导入记录列表
     */
    PageResult<ImportRecordVO> queryImportList(ImportQueryDTO queryDTO);

}
