package com.example.api.bpo;

import com.example.api.dto.UserDTO;
import com.example.api.dto.VisualizationQueryDTO;
import com.example.api.dto.VisualizationVO;
import com.example.api.dto.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "visualizationBPO",value = "xgh-demoBlog2-service", path = "/visualization")
public interface IVisualizationBPO {
    @PostMapping("/data")
    public Result<VisualizationVO> getVisualizationData(@RequestBody VisualizationQueryDTO visualizationQueryDTO);

}
