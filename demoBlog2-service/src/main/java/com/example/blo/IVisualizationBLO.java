package com.example.blo;

import com.example.api.dto.UserDTO;
import com.example.api.dto.VisualizationQueryDTO;
import com.example.api.dto.VisualizationVO;
import com.example.api.dto.common.Result;

public interface IVisualizationBLO {


    VisualizationVO getDate(VisualizationQueryDTO visualizationQueryDTO);
}
