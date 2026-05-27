package com.example.api.bpo;

import com.example.api.dto.AuditApproveDTO;
import com.example.api.dto.AuditHistoryDTO;
import com.example.api.dto.TodoTaskDTO;
import com.example.api.dto.common.PageResult;
import com.example.api.dto.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(contextId = "auditBPO", value = "xgh-demoBlog2-service", path = "/audit")
public interface IAuditBPO {

    @PostMapping("/start/{articleId}")
    Result<Void> submitForAudit(@PathVariable String articleId);

    @GetMapping("/todo/list")
    Result<PageResult<TodoTaskDTO>> getTodoList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    );

    @PostMapping("/approve")
    Result<Void> approve(@RequestBody AuditApproveDTO approveDTO);

    @GetMapping("/history/{articleId}")
    Result<List<AuditHistoryDTO>> getAuditHistory(@PathVariable Long articleId);

    @PostMapping("/publish/{articleId}")
    Result<Void> publishArticle(@PathVariable Long articleId);
}
