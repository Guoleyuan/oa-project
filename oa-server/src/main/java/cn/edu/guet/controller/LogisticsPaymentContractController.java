package cn.edu.guet.controller;

import cn.edu.guet.bean.LogisticsPaymentContract;
import cn.edu.guet.http.HttpResult;
import cn.edu.guet.http.ResultUtils;
import cn.edu.guet.service.LogisticsContractService;
import cn.edu.guet.service.LogisticsPaymentContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 陶祎祎
 */
@RestController
@RequestMapping("/logisticsPaymentContract")
public class LogisticsPaymentContractController {

    @Autowired
    private LogisticsPaymentContractService logisticsPaymentContractService;

    @Autowired
    private LogisticsContractService logisticsContractService;

    //    获取采购付款单数据
    @RequestMapping("/getLogisticsPaymentContractData")
    public HttpResult getLogisticsPaymentContractData(int current, int page){
        return ResultUtils.success("查询成功",logisticsPaymentContractService.getLogisticsPaymentContractData(current,page));
    }

    @RequestMapping("/searchLogisticsPaymentContract")
    public HttpResult searchLogisticsPaymentContract(int current,int page,String searchWord){
        return ResultUtils.success("查询成功",logisticsPaymentContractService.searchLogisticsPaymentContract(current,page,searchWord));
    }

    @DeleteMapping("/deleteOneLogisticsPaymentContract/{id}")
    public HttpResult deleteOneLogisticsPaymentContract(@PathVariable("id") Integer id){
        System.out.println("所要删除的ID为"+id);
        return ResultUtils.success("删除成功",logisticsPaymentContractService.deleteOneLogisticsPaymentContract(id));
    }

    @RequestMapping("/addNewLogisticsPaymentContract")
    public HttpResult addNewLogisticsPaymentContract(@RequestBody LogisticsPaymentContract logisticsPaymentContract){
        return ResultUtils.success("新增成功",logisticsPaymentContractService.addNewLogisticsPaymentContract(logisticsPaymentContract));
    }

    @RequestMapping("/checkLogisticsContractNo")
    public HttpResult checkLogisticsContractNo(String logisticsContractNo){
        return ResultUtils.success("获取成功",logisticsContractService.checkLogisticsContractNo(logisticsContractNo));
    }
}