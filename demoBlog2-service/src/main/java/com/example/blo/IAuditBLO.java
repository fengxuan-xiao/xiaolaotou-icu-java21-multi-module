package com.example.blo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.api.dto.AuditApproveDTO;
import com.example.api.dto.AuditHistoryDTO;
import com.example.api.dto.TodoTaskDTO;

import java.util.List;

public interface IAuditBLO {

    void submitForAudit(String articleId);

    IPage<TodoTaskDTO> getTodoList(Integer page, Integer pageSize);

    void approve(AuditApproveDTO approveDTO);

    List<AuditHistoryDTO> getAuditHistory(Long articleId);

    void publishArticle(Long articleId);
}
