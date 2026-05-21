package com.example.blo.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.dto.*;
import com.example.api.dto.common.PageResult;
import com.example.blo.IExcelBatchBLO;
import com.example.entity.ImportRecord;
import com.example.mapper.ImportRecordMapper;
import com.example.utils.ArticleDataDTO;
import com.example.utils.ExcelTemplateService;
import com.example.utils.UserContextUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IExcelBatchBLOImpl implements IExcelBatchBLO {

    @Resource
    private ImportRecordMapper importRecordMapper;

    @Resource
    private ExcelTemplateService excelTemplateService;

    @Override
    public void downloadTemplate(HttpServletResponse response) {
        excelTemplateService.downloadTemplate(response, "批量导入001模板.xlsx");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportResultVO batchImport(MultipartFile file) {
        //, String operator
        String operator1 = UserContextUtil.getCurrentUserIdStr();
        String userName = UserContextUtil.requireCurrentUserName();
//        String currentUserName = UserContextUtil.getCurrentUserName();
        String operator = "admin";
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            throw new IllegalArgumentException("文件格式不正确，仅支持 .xlsx 或 .xls");
        }

        try {
            List<ArticleDataDTO> dataList = new ArrayList<>();
            List<ImportResultVO.ImportErrorDetail> errorDetails = new ArrayList<>();

            // 使用 EasyExcel 读取文件
            EasyExcel.read(file.getInputStream(), ArticleDataDTO.class, new ReadListener<ArticleDataDTO>() {
                private int rowIndex = 0;

                @Override
                public void invoke(ArticleDataDTO data, AnalysisContext context) {
                    rowIndex++;

                    // 跳过标题行
                    if (rowIndex == 1) {
                        return;
                    }

                    // 数据校验
                    String error = validateData(data);
                    if (error != null) {
                        errorDetails.add(ImportResultVO.ImportErrorDetail.builder()
                                .rowIndex(rowIndex)
                                .reason(error)
                                .rawDataSnapshot(data.toString())
                                .build());
                    } else {
                        dataList.add(data);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("Excel 解析完成，共解析 {} 行", rowIndex);
                }
            }).sheet().doRead();

            // 保存成功数据到数据库
            int successCount = saveSuccessRecords(dataList, operator);
            int failCount = errorDetails.size();
            int totalCount = successCount + failCount;

            // 保存失败记录到数据库
            saveFailRecords(errorDetails, operator);

            log.info("导入完成，总数: {}, 成功: {}, 失败: {}", totalCount, successCount, failCount);

            return ImportResultVO.builder()
                    .totalCount(totalCount)
                    .successCount(successCount)
                    .failCount(failCount)
                    .failDetails(errorDetails)
                    .build();

        } catch (Exception e) {
            log.error("导入失败", e);
            throw new RuntimeException("导入失败: " + e.getMessage());
        }
    }

    @Override
    public PageResult<ImportRecordVO> queryImportList(ImportQueryDTO queryDTO) {
        // 构建分页对象
        Page<ImportRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<ImportRecord> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(ImportRecord::getAuthorName, queryDTO.getKeyword())
                    .or()
                    .like(ImportRecord::getContent, queryDTO.getKeyword()));
        }

        // 状态筛选
        if (queryDTO.getStatus() != null && !"ALL".equals(queryDTO.getStatus())) {
            wrapper.eq(ImportRecord::getStatus, queryDTO.getStatus());
        }

        // 时间范围筛选
        if (queryDTO.getStartDate() != null && !queryDTO.getStartDate().isEmpty()) {
            LocalDateTime start = LocalDateTime.parse(queryDTO.getStartDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.ge(ImportRecord::getImportTime, start);
        }
        if (queryDTO.getEndDate() != null && !queryDTO.getEndDate().isEmpty()) {
            LocalDateTime end = LocalDateTime.parse(queryDTO.getEndDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            wrapper.le(ImportRecord::getImportTime, end);
        }

        // 按导入时间倒序
        wrapper.orderByDesc(ImportRecord::getImportTime);

        // 执行查询
        IPage<ImportRecord> recordPage = importRecordMapper.selectPage(page, wrapper);

        // 转换为 VO
        List<ImportRecordVO> voList = recordPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return PageResult.<ImportRecordVO>builder()
                .total(recordPage.getTotal())
                .pages(recordPage.getPages())
                .current(recordPage.getCurrent())
                .pageSize((int) recordPage.getSize())
                .list(voList)
                .build();
    }

    /**
     * 数据校验
     */
    private String validateData(ArticleDataDTO data) {
        if (data.getTitle() == null || data.getTitle().trim().isEmpty()) {
            return "文章标题不能为空";
        }
        if (data.getContent() == null || data.getContent().trim().isEmpty()) {
            return "文章内容不能为空";
        }
        if (data.getAuthorName() == null || data.getAuthorName().trim().isEmpty()) {
            return "作者名称不能为空";
        }
        if (data.getTags() == null || data.getTags().trim().isEmpty()) {
            return "标签不能为空";
        }
        return null;
    }

    /**
     * 保存成功记录
     */
    private int saveSuccessRecords(List<ArticleDataDTO> dataList, String operator) {
        int count = 0;
        for (ArticleDataDTO data : dataList) {
            ImportRecord record = new ImportRecord();
            record.setContent(data.getContent());
            record.setTitle(data.getTitle());
            record.setAuthorName(data.getAuthorName());
            record.setTags(data.getTags());
            record.setStatus("SUCCESS");
            record.setImportTime(LocalDateTime.now());
            record.setOperator(operator);
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());

            importRecordMapper.insert(record);
            count++;
        }
        return count;
    }

    /**
     * 保存失败记录
     */
    private void saveFailRecords(List<ImportResultVO.ImportErrorDetail> errorDetails, String operator) {
        for (ImportResultVO.ImportErrorDetail error : errorDetails) {
            ImportRecord record = new ImportRecord();
            record.setStatus("FAIL");
            record.setFailReason(error.getReason());
            record.setRowIndex(error.getRowIndex());
            record.setImportTime(LocalDateTime.now());
            record.setOperator(operator);
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());

            importRecordMapper.insert(record);
        }
    }

    /**
     * 实体转 VO
     */
    private ImportRecordVO convertToVO(ImportRecord record) {
        ImportRecordVO vo = new ImportRecordVO();
        vo.setId(record.getId());
        vo.setAuthorName(record.getAuthorName());
        vo.setTitle(record.getTitle());
        vo.setContent(record.getContent());
        vo.setTags(record.getTags());
        vo.setStatus(record.getStatus());
        vo.setFailReason(record.getFailReason());
        vo.setRowIndex(record.getRowIndex());
        vo.setImportTime(record.getImportTime());
        vo.setOperator(record.getOperator());
        return vo;
    }
}
