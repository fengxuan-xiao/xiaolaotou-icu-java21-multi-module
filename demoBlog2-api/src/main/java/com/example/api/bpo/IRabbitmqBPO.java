package com.example.api.bpo;

import com.example.api.dto.SocialTaxMsgDTO;
import com.example.api.dto.TaxProcessDTO;
import com.example.api.dto.TaxReceiveMsgDTO;
import com.example.api.dto.common.PageResult;
import com.example.api.dto.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "rabbitmqBPO", value = "xgh-demoBlog2-service", path = "/rabbitmq")
public interface IRabbitmqBPO {

    @GetMapping("/social/list")
    Result<PageResult<SocialTaxMsgDTO>> getSocialList(
            @RequestParam String status,
            @RequestParam int page,
            @RequestParam int size
    );

    @PostMapping("/social/send/{id}")
    Result<Void> sendSocial(@PathVariable Long id);

    @PostMapping("/social/resend/{id}")
    Result<Void> resendSocial(@PathVariable Long id);

    @PostMapping("/social/discard/{id}")
    Result<Void> discardSocial(@PathVariable Long id);

    @GetMapping("/tax/list")
    Result<PageResult<TaxReceiveMsgDTO>> getTaxList(
            @RequestParam String status,
            @RequestParam int page,
            @RequestParam int size
    );

    @PostMapping("/tax/process/{id}")
    Result<Void> processTax(@PathVariable Long id, @RequestBody TaxProcessDTO dto);
}
