package cn.edu.guet.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.edu.guet.bean.ImportModel.ImportPurchaseContractModel;
import cn.edu.guet.bean.purchaseContract.ExportOutPurchaseContract;
import cn.edu.guet.bean.purchaseContract.PurchaseContract;
import cn.edu.guet.bean.purchaseContract.PurchaseExportParams;
import cn.edu.guet.http.HttpResult;
import cn.edu.guet.http.ResultUtils;
import cn.edu.guet.service.PurchaseContractService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson.JSON;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.guet.util.ExcelUtils.downloadExcel;

/**
 * @author 陶祎祎
 */
@RestController
@RequestMapping("/purchaseContract")
public class PurchaseContractController {

    @Autowired
    private PurchaseContractService purchaseContractService;

//    获取非归档数据（显示）
    @RequestMapping("/getTPurchaseContractData")
    public HttpResult getTPurchaseContractData(int current,int page){
        return ResultUtils.success("查询成功",purchaseContractService.getTPurchaseContractData(current,page));
    }

//    获取归档数据（隐藏）
    @RequestMapping("/getFPurchaseContractData")
    public HttpResult getFPurchaseContractData(int current,int page){
        return ResultUtils.success("查询成功",purchaseContractService.getFPurchaseContractData(current,page));
    }

    @RequestMapping("/searchPurchaseContract")
    public HttpResult searchPurchaseContract(int current, int page, String searchWord, boolean showPigeonhole, Date startDate, Date endDate){
        return ResultUtils.success("查询成功",purchaseContractService.searchPurchaseContract(current,page,searchWord,showPigeonhole,startDate,endDate));
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping("/purchaseImportExcel")
    public HttpResult purchaseImportExcel(@RequestBody MultipartFile file) throws IOException {
        List<String> errorList=new ArrayList<>();
        EasyExcel.read(file.getInputStream(), ImportPurchaseContractModel.class, new PageReadListener<ImportPurchaseContractModel>(dataList -> {
            System.out.println(dataList.size());
            for (ImportPurchaseContractModel importPurchaseContractModel : dataList) {
                if(importPurchaseContractModel.getPurchaseContractNo()==null){
                    break;
                }
                int result=purchaseContractService.handleImportPurchaseContractModel(importPurchaseContractModel);
                if(result==0){
                    String msg="采购合同号（"+importPurchaseContractModel.getPurchaseContractNo()+"）已重复";
                    if(!errorList.contains(msg)){
                        errorList.add(msg);
                    }
                }
            }
        })).sheet().doRead();
        if(!errorList.isEmpty()){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtils.error("批量插入采购单失败",errorList);
        }
        return ResultUtils.success("批量插入采购单成功");
    }

    @RequestMapping("/sendExportParm")
    public HttpResult sendExportParm(HttpServletRequest request, @RequestBody PurchaseExportParams purchaseExportParams){
        request.getServletContext().setAttribute("purchaseExportParams", purchaseExportParams);
        System.out.println("存进的全局属性："+ purchaseExportParams);
        return ResultUtils.success("传递参数成功");
    }

    @RequestMapping("/purchaseExportExcel")
    public void purchaseExportExcel(HttpServletResponse response, HttpServletRequest request) throws Exception {
        PurchaseExportParams purchaseExportParams = (PurchaseExportParams) request.getServletContext().getAttribute("purchaseExportParams");
        System.out.println("拿到的全局数据:"+purchaseExportParams);
        // 导出
        String fileName = "采购单.xlsx";
        ExportParams exportParams = new ExportParams();
        exportParams.setType(ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, ExportOutPurchaseContract.class, purchaseContractService.purchaseExportExcel(purchaseExportParams.getSearchWord(),purchaseExportParams.isShowPigeonhole(),purchaseExportParams.getStartDate(),purchaseExportParams.getEndDate()));
        downloadExcel(fileName, workbook, response);
    }

    @DeleteMapping("/deleteOnePurchaseContract/{id}")
    public HttpResult deleteOnePurchaseContract(@PathVariable("id") Integer id){
        System.out.println("所要删除的ID为"+id);
        return ResultUtils.success("删除成功",purchaseContractService.deleteOnePurchaseContract(id,0));
    }

    @RequestMapping("/deleteMorePurchaseContract")
    public HttpResult deleteMorePurchaseContract(@RequestBody String pram){
        return ResultUtils.success("删除成功",purchaseContractService.deleteMorePurchaseContract(JSON.parseArray(pram.substring(8).split("\"")[0])));
    }

    @RequestMapping("/setPurchaseContractPigeonhole")
    public HttpResult setPurchaseContractPigeonhole(int id,int pigeonhole){
        return ResultUtils.success("修改成功",purchaseContractService.setPurchaseContractPigeonhole(id,pigeonhole));
    }

    @RequestMapping("/addNewPurchaseContract")
    public HttpResult addNewPurchaseContract(@RequestBody PurchaseContract purchaseContract){
        return ResultUtils.success("新增成功",purchaseContractService.addNewPurchaseContract(purchaseContract,null,0));
    }

    @RequestMapping("/getPurchaseContractInboundData")
    public HttpResult getPurchaseContractInboundData(String purchaseContractNo){
        return ResultUtils.success("获取成功",purchaseContractService.getPurchaseContractInboundData(purchaseContractNo));
    }

    @RequestMapping("/updatePurchaseContract")
    public HttpResult updatePurchaseContract(@RequestBody PurchaseContract purchaseContract){
        return ResultUtils.success("新增成功",purchaseContractService.updatePurchaseContract(purchaseContract));
    }

//    获取详情数据
    @RequestMapping("/getPurchaseDetail")
    public HttpResult getPurchaseDetail(String purchaseContractNo,String goodsName){
        return ResultUtils.success("查询成功",purchaseContractService.getPurchaseDetail(purchaseContractNo,goodsName));
    }
}
