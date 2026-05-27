package com.example.bpo.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.api.bpo.IAuditBPO;
import com.example.api.dto.AuditApproveDTO;
import com.example.api.dto.AuditHistoryDTO;
import com.example.api.dto.TodoTaskDTO;
import com.example.api.dto.common.PageResult;
import com.example.api.dto.common.Result;
import com.example.blo.IAuditBLO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
@Slf4j
@Tag(name = "审核流程", description = "文章审核流程管理接口")
public class IAuditBPOImpl implements IAuditBPO {

    @Autowired
    private IAuditBLO auditBLO;

    @Override
    @Operation(summary = "提交文章审核", description = "作者提交文章进入审核流程")
    @PostMapping("/start/{articleId}")
    public Result<Void> submitForAudit(@PathVariable String articleId) {
        try {
//            if (!StpUtil.isLogin()) {
//                return Result.error("未登录");
//            }

            auditBLO.submitForAudit(articleId);
            return Result.success(null, "提交审核成功");
        } catch (IllegalArgumentException e) {
            log.warn("提交审核失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("提交审核异常", e);
            return Result.error("提交审核失败，请稍后重试");
        }
    }

    @Override
    @Operation(summary = "查询待办任务列表", description = "初审员/复核员查询待审批的任务")
    @GetMapping("/todo/list")
    public Result<PageResult<TodoTaskDTO>> getTodoList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
//            if (!StpUtil.isLogin()) {
//                return Result.error( "未登录");
//            }

            IPage<TodoTaskDTO> resultPage = auditBLO.getTodoList(page, pageSize);

            PageResult<TodoTaskDTO> pageResult = PageResult.of(
                    resultPage.getRecords(),
                    resultPage.getTotal(),
                    (int) resultPage.getCurrent(),
                    (int) resultPage.getSize()
            );

            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("查询待办任务失败", e);
            return Result.error("查询待办任务失败");
        }
    }

    @Override
    @Operation(summary = "审批文章", description = "初审员/复核员审批文章（通过/驳回）")
    @PostMapping("/approve")
    public Result<Void> approve(@RequestBody AuditApproveDTO approveDTO) {
        try {
//            if (!StpUtil.isLogin()) {
//                return Result.error("未登录");
//            }

            auditBLO.approve(approveDTO);
            return Result.success(null, "审批成功");
        } catch (IllegalArgumentException e) {
            log.warn("审批失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("审批异常", e);
            return Result.error("审批失败，请稍后重试");
        }
    }

    @Override
    @Operation(summary = "查询审核历史", description = "查询文章的审核历史记录")
    @GetMapping("/history/{articleId}")
    public Result<List<AuditHistoryDTO>> getAuditHistory(@PathVariable Long articleId) {
        try {
            List<AuditHistoryDTO> history = auditBLO.getAuditHistory(articleId);
            return Result.success(history);
        } catch (Exception e) {
            log.error("查询审核历史失败", e);
            return Result.error("查询审核历史失败");
        }
    }

    @Override
    @Operation(summary = "发布文章", description = "复核通过后发布文章")
    @PostMapping("/publish/{articleId}")
    public Result<Void> publishArticle(@PathVariable Long articleId) {
        try {
//            if (!StpUtil.isLogin()) {
//                return Result.error("未登录");
//            }

            auditBLO.publishArticle(articleId);
            return Result.success(null, "发布成功");
        } catch (IllegalArgumentException e) {
            log.warn("发布失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("发布异常", e);
            return Result.error("发布失败，请稍后重试");
        }
    }
}
