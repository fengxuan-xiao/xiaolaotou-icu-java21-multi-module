package com.example.bpo.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.api.bpo.IRabbitmqBPO;
import com.example.api.dto.SocialTaxMsgDTO;
import com.example.api.dto.TaxProcessDTO;
import com.example.api.dto.TaxReceiveMsgDTO;
import com.example.api.dto.common.PageResult;
import com.example.api.dto.common.Result;
import com.example.blo.IRabbitmqBLO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rabbitmq")
@Slf4j
@Tag(name = "消息队列管理", description = "社保局与税务局消息交互接口")
public class IRabbitmqBPOImpl implements IRabbitmqBPO {

    @Autowired
    private IRabbitmqBLO rabbitmqBLO;

    @Override
    @Operation(summary = "获取社保消息列表", description = "根据状态分页查询社保消息")
    @GetMapping("/social/list")
    public Result<PageResult<SocialTaxMsgDTO>> getSocialList(
            @RequestParam String status,
            @RequestParam int page,
            @RequestParam int size) {

        IPage<SocialTaxMsgDTO> resultPage = rabbitmqBLO.getSocialPage(status, page, size);

        PageResult<SocialTaxMsgDTO> pageResult = PageResult.of(
                resultPage.getRecords(),
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize()
        );

        return Result.success(pageResult);
    }

    @Override
    @Operation(summary = "发送社保消息", description = "发送社保缴费消息到税务局")
    @PostMapping("/social/send/{id}")
    public Result<Void> sendSocial(@PathVariable Long id) {
        try {
            rabbitmqBLO.sendSocial(id);
            return Result.success("发送指令已下达");
        } catch (Exception e) {
            log.error("发送社保消息失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Override
    @Operation(summary = "重发社保消息", description = "重新发送失败的社保消息")
    @PostMapping("/social/resend/{id}")
    public Result<Void> resendSocial(@PathVariable Long id) {
        try {
            rabbitmqBLO.resendSocial(id);
            return Result.success("重发指令已下达");
        } catch (Exception e) {
            log.error("重发社保消息失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Override
    @Operation(summary = "丢弃社保消息", description = "丢弃不需要处理的社保消息")
    @PostMapping("/social/discard/{id}")
    public Result<Void> discardSocial(@PathVariable Long id) {
        try {
            rabbitmqBLO.discardSocial(id);
            return Result.success("消息已丢弃");
        } catch (Exception e) {
            log.error("丢弃社保消息失败", e);
            return Result.error(e.getMessage());
        }
    }

    @Override
    @Operation(summary = "获取税务消息列表", description = "根据状态分页查询税务消息")
    @GetMapping("/tax/list")
    public Result<PageResult<TaxReceiveMsgDTO>> getTaxList(
            @RequestParam String status,
            @RequestParam int page,
            @RequestParam int size) {

        IPage<TaxReceiveMsgDTO> resultPage = rabbitmqBLO.getTaxPage(status, page, size);

        PageResult<TaxReceiveMsgDTO> pageResult = PageResult.of(
                resultPage.getRecords(),
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize()
        );

        return Result.success(pageResult);
    }

    @Override
    @Operation(summary = "处理税务消息", description = "税务局处理社保消息并返回结果")
    @PostMapping("/tax/process/{id}")
    public Result<Void> processTax(@PathVariable Long id, @RequestBody TaxProcessDTO dto) {
        try {
            rabbitmqBLO.processTax(id, dto);
            return Result.success("操作成功，回执已发送");
        } catch (Exception e) {
            log.error("处理税务消息失败", e);
            return Result.error(e.getMessage());
        }
    }
}
