package cn.edu.guet.service.Impl;

import cn.edu.guet.bean.purchaseContract.PurchaseContractView;
import cn.edu.guet.mapper.PurchaseContractViewMapper;
import cn.edu.guet.service.PurchaseContractViewService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 陶祎祎
* @description 针对表【purchase_contract_info】的数据库操作Service实现
* @createDate 2022-11-02 09:45:29
*/
@Service
public class PurchaseContractViewServiceImpl extends ServiceImpl<PurchaseContractViewMapper, PurchaseContractView>
    implements PurchaseContractViewService {

    @Autowired
    private PurchaseContractViewMapper purchaseContractViewMapper;

    @Override
    public Page<PurchaseContractView> getTPurchaseContractData(int currentPage, int pageSize) {
        QueryWrapper<PurchaseContractView> qw= new QueryWrapper<>();
        qw.eq("pigeonhole",1).orderByDesc("create_time");
        Page<PurchaseContractView> page =new Page<>(currentPage,pageSize);
        page=purchaseContractViewMapper.selectPage(page,qw);
        return page;
    }

    @Override
    public Page<PurchaseContractView> getFPurchaseContractData(int currentPage, int pageSize) {
        QueryWrapper<PurchaseContractView> qw= new QueryWrapper<>();
        qw.eq("pigeonhole",0).orderByDesc("create_time");
        Page<PurchaseContractView> page =new Page<>(currentPage,pageSize);
        page=purchaseContractViewMapper.selectPage(page,qw);
        return page;
    }

    @Override
    public Page<PurchaseContractView> searchPurchaseContract(int currentPage, int pageSize,String searchWord) {
        QueryWrapper<PurchaseContractView> qw= new QueryWrapper<>();
        qw.like("purchase_contract_no",searchWord).or().like("customer_enterprise_name",searchWord).or()
                .like("own_company_name",searchWord).or().like("squeeze_season",searchWord).or()
                .like("goods_name",searchWord).or().like("create_by",searchWord).orderByDesc("create_time");
        Page<PurchaseContractView> page =new Page<>(currentPage,pageSize);
        page=purchaseContractViewMapper.selectPage(page,qw);
        return page;
    }
}




