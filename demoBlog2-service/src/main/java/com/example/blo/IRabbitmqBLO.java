package com.example.blo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.api.dto.SocialTaxMsgDTO;
import com.example.api.dto.TaxProcessDTO;
import com.example.api.dto.TaxReceiveMsgDTO;

public interface IRabbitmqBLO {

    IPage<SocialTaxMsgDTO> getSocialPage(String status, int page, int size);

    void sendSocial(Long id);

    void resendSocial(Long id);

    void discardSocial(Long id);

    IPage<TaxReceiveMsgDTO> getTaxPage(String status, int page, int size);

    void processTax(Long id, TaxProcessDTO dto);
}
