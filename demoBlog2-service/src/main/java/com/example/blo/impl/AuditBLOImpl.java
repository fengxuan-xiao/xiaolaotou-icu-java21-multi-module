package com.example.blo.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.dto.AuditApproveDTO;
import com.example.api.dto.AuditHistoryDTO;
import com.example.api.dto.TodoTaskDTO;
import com.example.blo.IAuditBLO;
import com.example.entity.Article;
import com.example.entity.User;
import com.example.mapper.ArticleMapper;
import com.example.mapper.UserMapper;
import com.example.utils.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuditBLOImpl implements IAuditBLO {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void submitForAudit(String articleId) {
        log.info("提交文章审核，articleId: {}", articleId);

        Article article = articleMapper.selectById(articleId);

        if (article == null) {
            log.warn("提交审核失败：文章不存在，articleId: {}", articleId);
            throw new IllegalArgumentException("文章不存在");
        }

        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (!article.getAuthorId().equals(currentUserId)) {
            log.warn("提交审核失败：非文章作者，articleId: {}, userId: {}", articleId, currentUserId);
            throw new IllegalArgumentException("只能提交自己的文章进行审核");
        }

        if (article.getAuditStatus() != null && article.getAuditStatus() >= 1) {
            log.warn("提交审核失败：文章已提交审核，articleId: {}, auditStatus: {}", articleId, article.getAuditStatus());
            throw new IllegalArgumentException("文章已提交审核，请勿重复提交");
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("articleId", articleId);
        variables.put("authorId", article.getAuthorId());
        variables.put("submitter", currentUserId);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "article_audit_process",
                "article_" + articleId,
                variables
        );

        article.setProcessInstanceId(processInstance.getId());
        article.setAuditStatus((byte) 1);

        Task currentTask = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .singleResult();

        if (currentTask != null) {
            article.setTaskId(currentTask.getId());
        }

        articleMapper.updateById(article);

        redisCacheUtil.evictArticleCache(Long.valueOf(articleId));

        log.info("文章提交审核成功，articleId: {}, processInstanceId: {}", articleId, processInstance.getId());
    }

    @Override
    public IPage<TodoTaskDTO> getTodoList(Integer page, Integer pageSize) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        log.info("查询待办任务列表，userId: {}, page: {}, pageSize: {}", currentUserId, page, pageSize);

        Object cachedTodoList = redisCacheUtil.getCachedTodoList(currentUserId);
        if (cachedTodoList != null && page == 1) {
            log.info("待办列表缓存命中，userId: {}", currentUserId);
            IPage<TodoTaskDTO> cachedPage = (IPage<TodoTaskDTO>) cachedTodoList;
            return cachedPage;
        }

        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned(String.valueOf(currentUserId))
                .orderByTaskCreateTime()
                .desc()
                .listPage((page - 1) * pageSize, pageSize);

        long total = taskService.createTaskQuery()
                .taskCandidateOrAssigned(String.valueOf(currentUserId))
                .count();

        List<TodoTaskDTO> todoList = tasks.stream()
                .map(task -> convertToTodoTaskDTO(task))
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

        Page<TodoTaskDTO> resultPage = new Page<>(page, pageSize);
        resultPage.setRecords(todoList);
        resultPage.setTotal(total);

        if (page == 1) {
            redisCacheUtil.cacheTodoList(currentUserId, resultPage, 5, TimeUnit.MINUTES);
        }

        log.info("查询待办任务列表成功，userId: {}, total: {}", currentUserId, total);
        return resultPage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approve(AuditApproveDTO approveDTO) {
        log.info("审批文章，taskId: {}, approved: {}", approveDTO.getTaskId(), approveDTO.getApproved());

        Task task = taskService.createTaskQuery()
                .taskId(approveDTO.getTaskId())
                .singleResult();

        if (task == null) {
            log.warn("审批失败：任务不存在或已完成，taskId: {}", approveDTO.getTaskId());
            throw new IllegalArgumentException("任务不存在或已完成");
        }

        Long currentUserId = StpUtil.getLoginIdAsLong();

        Map<String, Object> variables = new HashMap<>();
        variables.put("approved", approveDTO.getApproved());
        variables.put("approver", currentUserId);
        variables.put("comment", approveDTO.getComment());

        taskService.complete(approveDTO.getTaskId(), variables);

        Article article = findArticleByProcessInstanceId(approveDTO.getProcessInstanceId());

        if (article == null) {
            log.error("审批失败：找不到对应的文章，processInstanceId: {}", approveDTO.getProcessInstanceId());
            throw new IllegalArgumentException("找不到对应的文章");
        }

        if (approveDTO.getApproved()) {
            Task nextTask = taskService.createTaskQuery()
                    .processInstanceId(approveDTO.getProcessInstanceId())
                    .singleResult();

            if (nextTask != null) {
                article.setTaskId(nextTask.getId());

                if ("recheck_task".equals(nextTask.getTaskDefinitionKey())) {
                    article.setAuditStatus((byte) 3);
                    log.info("初审通过，进入复核阶段，articleId: {}", article.getId());
                } else if ("publish_task".equals(nextTask.getTaskDefinitionKey())) {
                    article.setAuditStatus((byte) 4);
                    log.info("复核通过，等待发布，articleId: {}", article.getId());
                }
            } else {
                article.setAuditStatus((byte) 4);
                article.setTaskId(null);
                log.info("审批流程结束，articleId: {}", article.getId());
            }
        } else {
            article.setAuditStatus((byte) 5);
            article.setAuditRemark(approveDTO.getComment());
            article.setTaskId(null);
            log.info("审批驳回，articleId: {}, reason: {}", article.getId(), approveDTO.getComment());
        }

        articleMapper.updateById(article);

        redisCacheUtil.evictArticleCache(article.getId());
        redisCacheUtil.evictTodoListCache(currentUserId);

        log.info("审批完成，taskId: {}, articleId: {}, 审批结果: {}",
                approveDTO.getTaskId(), article.getId(),
                approveDTO.getApproved() ? "通过" : "驳回");
    }

    @Override
    public List<AuditHistoryDTO> getAuditHistory(Long articleId) {
        log.info("查询审核历史，articleId: {}", articleId);

        Article article = articleMapper.selectById(articleId);

        if (article == null || article.getProcessInstanceId() == null) {
            log.warn("审核历史查询失败：文章不存在或未启动流程，articleId: {}", articleId);
            return Collections.emptyList();
        }

        List<HistoricActivityInstance> activities = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(article.getProcessInstanceId())
                .activityType("userTask")
                .finished()
                .orderByHistoricActivityInstanceEndTime()
                .asc()
                .list();

        List<HistoricTaskInstance> historicTasks = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(article.getProcessInstanceId())
                .finished()
                .orderByHistoricTaskInstanceEndTime()
                .asc()
                .list();

        Map<String, HistoricTaskInstance> taskMap = historicTasks.stream()
                .collect(Collectors.toMap(
                        HistoricTaskInstance::getId,
                        task -> task
                ));

        List<AuditHistoryDTO> history = activities.stream()
                .map(activity -> convertToAuditHistoryDTO(activity, taskMap))
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

        log.info("查询审核历史成功，articleId: {}, 记录数: {}", articleId, history.size());
        return history;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void publishArticle(Long articleId) {
        log.info("发布文章，articleId: {}", articleId);

        Article article = articleMapper.selectById(articleId);

        if (article == null) {
            log.warn("发布失败：文章不存在，articleId: {}", articleId);
            throw new IllegalArgumentException("文章不存在");
        }

        if (article.getAuditStatus() == null || article.getAuditStatus() != 4) {
            log.warn("发布失败：文章未通过复核，articleId: {}, auditStatus: {}", articleId, article.getAuditStatus());
            throw new IllegalArgumentException("文章未通过复核，无法发布");
        }

        article.setStatus((byte) 1);
        article.setUpdateTime(LocalDateTime.now());

        articleMapper.updateById(article);

        redisCacheUtil.evictArticleCache(articleId);

        log.info("文章发布成功，articleId: {}", articleId);
    }

    private TodoTaskDTO convertToTodoTaskDTO(Task task) {
        try {
            Object articleIdObj = runtimeService.getVariable(
                    task.getProcessInstanceId(),
                    "articleId"
            );

            if (articleIdObj == null) {
                log.warn("转换待办任务DTO失败：articleId为空，processInstanceId: {}", task.getProcessInstanceId());
                return null;
            }

            Long articleId = Long.valueOf(articleIdObj.toString());
            Article article = articleMapper.selectById(articleId);

            if (article == null) {
                log.warn("转换待办任务DTO失败：文章不存在，articleId: {}", articleId);
                return null;
            }

            TodoTaskDTO dto = new TodoTaskDTO();
            dto.setId(article.getId());
            dto.setArticleTitle(article.getTitle());
            dto.setAuthorName(article.getAuthorName());
            dto.setProcessInstanceId(task.getProcessInstanceId());
            dto.setTaskId(task.getId());
            dto.setAuditStatus(article.getAuditStatus() != null ? article.getAuditStatus().intValue() : null);
            dto.setCreateTime(LocalDateTime.ofInstant(
                    task.getCreateTime().toInstant(),
                    ZoneId.systemDefault()
            ));

            return dto;
        } catch (Exception e) {
            log.error("转换待办任务DTO失败，taskId: {}", task.getId(), e);
            return null;
        }
    }

    private Article findArticleByProcessInstanceId(String processInstanceId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getProcessInstanceId, processInstanceId);
        return articleMapper.selectOne(wrapper);
    }

    private AuditHistoryDTO convertToAuditHistoryDTO(
            HistoricActivityInstance activity,
            Map<String, HistoricTaskInstance> taskMap) {

        try {
            HistoricTaskInstance task = taskMap.get(activity.getActivityId());

            if (task == null) {
                return null;
            }

            AuditHistoryDTO dto = new AuditHistoryDTO();

            String assignee = task.getAssignee();
            if (assignee != null) {
                try {
                    Long userId = Long.valueOf(assignee);
                    User user = userMapper.selectById(userId);
                    if (user != null) {
                        dto.setOperatorName(user.getNickname() != null ?
                                user.getNickname() : user.getUsername());
                    } else {
                        dto.setOperatorName("用户" + userId);
                    }
                } catch (NumberFormatException e) {
                    dto.setOperatorName(assignee);
                }
            }

            String taskName = task.getName();
            String action = determineAction(taskName, activity.getActivityId());
            dto.setAction(action);

            String comment = task.getDescription();
            dto.setComment(comment);

            if (task.getEndTime() != null) {
                dto.setCreateTime(LocalDateTime.ofInstant(
                        task.getEndTime().toInstant(),
                        ZoneId.systemDefault()
                ));
            }

            return dto;
        } catch (Exception e) {
            log.error("转换审核历史DTO失败，activityId: {}", activity.getActivityId(), e);
            return null;
        }
    }

    private String determineAction(String taskName, String activityId) {
        if (activityId != null) {
            if (activityId.contains("first_check")) {
                return "初审";
            } else if (activityId.contains("recheck")) {
                return "复核";
            }
        }

        if (taskName != null) {
            return taskName;
        }

        return "未知操作";
    }
}
