package com.example.bpo.impl;

import com.example.api.bpo.ILoginBPO;
import com.example.api.bpo.IVisualizationBPO;
import com.example.api.dto.UserDTO;
import com.example.api.dto.VisualizationQueryDTO;
import com.example.api.dto.VisualizationVO;
import com.example.api.dto.common.Result;
import com.example.blo.ILoginBLO;
import com.example.blo.IVisualizationBLO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visualization")
@Slf4j
@Tag(name = "可视化界面", description = "可视化接口")
public class IVisualizationBPOImpl implements IVisualizationBPO {

    @Autowired
    private IVisualizationBLO iVisualizationBLO;
    @Override
    @Operation(summary = "可视化界面", description = "可视化接口")
    @PostMapping(value = "/data")
    public Result<VisualizationVO> getVisualizationData(@RequestBody VisualizationQueryDTO visualizationQueryDTO) {


        VisualizationVO visualizationVO = iVisualizationBLO.getDate(visualizationQueryDTO);


        return Result.success(visualizationVO);
    }

}
