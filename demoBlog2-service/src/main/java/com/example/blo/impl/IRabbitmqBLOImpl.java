package com.example.blo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.api.dto.SocialTaxMsgDTO;
import com.example.api.dto.TaxProcessDTO;
import com.example.api.dto.TaxReceiveMsgDTO;
import com.example.blo.IRabbitmqBLO;
import com.example.entity.SocialTaxMsg;
import com.example.entity.TaxReceiveMsg;
import com.example.service.ISocialTaxMsgService;
import com.example.service.ITaxReceiveMsgService;
import com.example.utils.Idempotent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IRabbitmqBLOImpl implements IRabbitmqBLO {

    @Autowired
    private ISocialTaxMsgService socialTaxMsgService;

    @Autowired
    private ITaxReceiveMsgService taxReceiveMsgService;

    @Override
    public IPage<SocialTaxMsgDTO> getSocialPage(String status, int page, int size) {
        Page<SocialTaxMsg> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<SocialTaxMsg> wrapper = new LambdaQueryWrapper<>();

        if (status != null && !status.isEmpty()) {
            switch (status) {
                case "unsent":
                    wrapper.eq(SocialTaxMsg::getSendStatus, 0);
                    break;
                case "sent_pending":
                    wrapper.in(SocialTaxMsg::getSendStatus, 1, 2);
                    wrapper.eq(SocialTaxMsg::getReceiveStatus, 0);
                    break;
                case "success":
                    wrapper.eq(SocialTaxMsg::getReceiveStatus, 1);
                    break;
                case "failed":
                    wrapper.eq(SocialTaxMsg::getReceiveStatus, 2);
                    break;
                default:
                    break;
            }
        }

        wrapper.orderByDesc(SocialTaxMsg::getCreateTime);

        IPage<SocialTaxMsg> entityPage = socialTaxMsgService.page(pageInfo, wrapper);

        Page<SocialTaxMsgDTO> dtoPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<SocialTaxMsgDTO> dtoList = entityPage.getRecords().stream()
                .map(entity -> {
                    SocialTaxMsgDTO dto = new SocialTaxMsgDTO();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendSocial(Long id) {
        SocialTaxMsg msg = socialTaxMsgService.getById(id);
        if (msg == null) {
            throw new RuntimeException("消息不存在");
        }

        if (msg.getSendStatus() != 0) {
            throw new RuntimeException("消息状态不允许发送");
        }

        msg.setSendStatus((byte) 1);
        msg.setSendTime(LocalDateTime.now());
        msg.setRetryCount(msg.getRetryCount() == null ? 1 : msg.getRetryCount() + 1);

        socialTaxMsgService.updateById(msg);

        log.info("社保消息发送指令已下达，ID: {}, DataNo: {}", id, msg.getDataNo());
    }

    @Override
    @Idempotent(key = "resend_social", expireTime = 10000, message = "重发请求过于频繁，请稍后重试")
    @Transactional(rollbackFor = Exception.class)
    public void resendSocial(Long id) {
        SocialTaxMsg msg = socialTaxMsgService.getById(id);
        if (msg == null) {
            throw new RuntimeException("消息不存在");
        }

        if (msg.getSendStatus() == 0) {
            throw new RuntimeException("消息尚未首次发送，请使用发送功能");
        }

        if (msg.getReceiveStatus() == 1) {
            throw new RuntimeException("消息已成功处理，无需重发");
        }

        msg.setSendStatus((byte) 1);
        msg.setSendTime(LocalDateTime.now());
        msg.setRetryCount(msg.getRetryCount() == null ? 1 : msg.getRetryCount() + 1);
        msg.setReceiveStatus((byte) 0);
        msg.setReceiveMsg(null);
        msg.setReceiveTime(null);
        msg.setTaxSerialNo(null);

        socialTaxMsgService.updateById(msg);

        log.info("社保消息重发指令已下达，ID: {}, DataNo: {}, 重试次数: {}",
                id, msg.getDataNo(), msg.getRetryCount());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void discardSocial(Long id) {
        SocialTaxMsg msg = socialTaxMsgService.getById(id);
        if (msg == null) {
            throw new RuntimeException("消息不存在");
        }

        if (msg.getReceiveStatus() == 1) {
            throw new RuntimeException("消息已成功处理，不能丢弃");
        }

        msg.setSendStatus((byte) 3);
        msg.setStatus((byte) 0);
        msg.setRemark("用户手动丢弃");
        msg.setUpdateTime(LocalDateTime.now());

        socialTaxMsgService.updateById(msg);

        log.info("社保消息已丢弃，ID: {}, DataNo: {}", id, msg.getDataNo());
    }

    @Override
    public IPage<TaxReceiveMsgDTO> getTaxPage(String status, int page, int size) {
        Page<TaxReceiveMsg> pageInfo = new Page<>(page, size);
        LambdaQueryWrapper<TaxReceiveMsg> wrapper = new LambdaQueryWrapper<>();

        if (status != null && !status.isEmpty()) {
            switch (status) {
                case "unprocessed":
                    wrapper.in(TaxReceiveMsg::getReceiveStatus, 0, 1);
                    break;
                case "processed":
                    wrapper.eq(TaxReceiveMsg::getReceiveStatus, 2);
                    break;
                default:
                    break;
            }
        }

        wrapper.orderByDesc(TaxReceiveMsg::getCreateTime);

        IPage<TaxReceiveMsg> entityPage = taxReceiveMsgService.page(pageInfo, wrapper);

        Page<TaxReceiveMsgDTO> dtoPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        List<TaxReceiveMsgDTO> dtoList = entityPage.getRecords().stream()
                .map(entity -> {
                    TaxReceiveMsgDTO dto = new TaxReceiveMsgDTO();
                    BeanUtils.copyProperties(entity, dto);
                    return dto;
                })
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processTax(Long id, TaxProcessDTO dto) {
        TaxReceiveMsg msg = taxReceiveMsgService.getById(id);
        if (msg == null) {
            throw new RuntimeException("消息不存在");
        }

        if (msg.getReceiveStatus() == 2) {
            throw new RuntimeException("消息已处理，不能重复处理");
        }

        msg.setReceiveStatus((byte) 2);
        msg.setProcessResult(dto.getResult().byteValue());
        msg.setProcessMsg(dto.getMsg());
        msg.setProcessTime(LocalDateTime.now());

        if (dto.getResult() == 0) {
            msg.setReplyStatus((byte) 1);
            msg.setReplyTime(LocalDateTime.now());
        }

        taxReceiveMsgService.updateById(msg);

        log.info("税务消息处理完成，ID: {}, DataNo: {}, 结果: {}, 说明: {}",
                id, msg.getDataNo(), dto.getResult(), dto.getMsg());
    }
}
