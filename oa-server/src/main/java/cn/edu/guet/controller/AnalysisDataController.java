package cn.edu.guet.controller;

import cn.edu.guet.http.HttpResult;
import cn.edu.guet.http.ResultUtils;
import cn.edu.guet.service.AnalysisDataService;
import cn.edu.guet.service.IncomeSpendInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;

/**
 * @author 陶祎祎
 */
@RestController
@RequestMapping("/finance")
public class AnalysisDataController {

    @Autowired
    private AnalysisDataService analysisDataService;

    @Autowired
    private IncomeSpendInfoService incomeSpendInfoService;

    @RequestMapping("/getGoodsNameData")
    public HttpResult getGoodsNameData(){
        return ResultUtils.success("获取成功",analysisDataService.getGoodsNameData());
    }

    @RequestMapping("/getPriceTrendData")
    public HttpResult getPriceTrendData(String goodsName, Date startDate, Date endDate){
       return ResultUtils.success("获取成功",analysisDataService.getPriceTrendData(goodsName,startDate,endDate));
    }

    @RequestMapping("/getIncomeSpendData")
    public HttpResult getIncomeSpendData(String dataType, String dataCompany, Date startDate, Date endDate){
       return ResultUtils.success("获取成功",incomeSpendInfoService.getIncomeSpendData(dataType,dataCompany,startDate,endDate));
    }
}