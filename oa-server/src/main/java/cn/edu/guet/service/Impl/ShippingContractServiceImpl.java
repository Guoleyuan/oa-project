package cn.edu.guet.service.Impl;

import cn.edu.guet.bean.*;
import cn.edu.guet.bean.ImportModel.ImportShippingContractModel;
import cn.edu.guet.bean.logisticsContract.LogisticsContract;
import cn.edu.guet.mapper.*;
import cn.edu.guet.service.LogisticsContractService;
import cn.edu.guet.service.ShippingContractService;
import cn.edu.guet.util.ImageUtils;
import cn.edu.guet.util.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author 陶祎祎
 * @description 针对表【shipping_contract】的数据库操作Service实现
 * @createDate 2022-11-07 12:02:08
 */
@Service
public class ShippingContractServiceImpl extends ServiceImpl<ShippingContractMapper, ShippingContract>
        implements ShippingContractService {

    @Autowired
    private ShippingContractMapper shippingContractMapper;

    @Autowired
    private ShippingStateInfoMapper shippingStateInfoMapper;

    @Autowired
    private ShippingDirectorStateMapper shippingDirectorStateMapper;

    @Autowired
    private DirectorMapper directorMapper;

    @Autowired
    private LogisticsContractMapper logisticsContractMapper;

    @Autowired
    private LogisticsContractService logisticsContractService;

    @Autowired
    private CashierShippingMapper cashierShippingMapper;

    @Override
    public Page<ShippingContract> getshippingContractData(int currentPage, int pageSize) {
        QueryWrapper<ShippingContract> qw = new QueryWrapper<>();
        qw.orderByDesc("create_time", "shipping_contract_no");
        Page<ShippingContract> page = new Page<>(currentPage, pageSize);
        page = shippingContractMapper.selectPage(page, qw);
        for (ShippingContract record : page.getRecords()) {
//            获取董事长审核信息，并添加进对象中
            QueryWrapper<ShippingStateView> stateQw = new QueryWrapper<>();
            stateQw.eq("shipping_contract_no", record.getShippingContractNo()).orderByDesc("nick_name");
            record.setShippingDirector(shippingStateInfoMapper.selectList(stateQw));

            //处理图片，形成一个图片数组
            String contractPhoto = record.getContractPhoto();
            String paymentPhoto = record.getPaymentPhoto();
            //有多个照片,合同照片
            if (StringUtils.isNotEmpty(contractPhoto) && contractPhoto.contains(",")) {
                //分割图片字符串，形成一个数组
                List<String> list = ImageUtils.imageSplit(contractPhoto);
                record.setContractPhotoArray(list);
                //取第一个图片的url
                record.setContractPhoto(ImageUtils.getFirstImageUrl(contractPhoto));
            } else {
                record.setContractPhotoArray(Arrays.asList(contractPhoto));
            }
//            付款照片
            if (StringUtils.isNotEmpty(paymentPhoto) && paymentPhoto.contains(",")) {
                //分割图片字符串，形成一个数组
                List<String> list = ImageUtils.imageSplit(paymentPhoto);
                record.setPaymentPhotoArray(list);
                //取第一个图片的url
                record.setPaymentPhoto(ImageUtils.getFirstImageUrl(paymentPhoto));
            } else {
                record.setPaymentPhotoArray(Arrays.asList(paymentPhoto));
            }
        }
        return page;
    }

    @Override
    public Page<ShippingContract> searchShippingContract(int currentPage, int pageSize, String searchWord) {
        QueryWrapper<ShippingContract> qw = new QueryWrapper<>();
        qw.like("shipping_contract_no", searchWord).or().like("logistics_contract_no", searchWord).or().like("own_company_name", searchWord)
                .or().like("principal", searchWord).or().like("packing_location", searchWord).or().like("unpacking_factory", searchWord)
                .or().like("first_container_no", searchWord).or().like("second_container_no", searchWord).or().like("first_seal_no", searchWord)
                .or().like("second_seal_no", searchWord).or().like("tally_clerk", searchWord).or().like("tally_clerk_remark", searchWord)
                .or().like("fleet_manage_name", searchWord).or().like("departure_fleet", searchWord).or().like("carrier_company_name", searchWord)
                .or().like("destination_port_fleet", searchWord).or().like("finance_staff", searchWord).or().like("cashier", searchWord)
                .or().like("create_by", searchWord).orderByDesc("create_time", "shipping_contract_no");
        Page<ShippingContract> page = new Page<>(currentPage, pageSize);
        page = shippingContractMapper.selectPage(page, qw);
        for (ShippingContract record : page.getRecords()) {
            //            获取董事长审核信息，并加入数据库
            QueryWrapper<ShippingStateView> stateQw = new QueryWrapper<>();
            stateQw.eq("shipping_contract_no", record.getShippingContractNo()).orderByAsc("nick_name");
            record.setShippingDirector(shippingStateInfoMapper.selectList(stateQw));

            //处理图片，形成一个图片数组
            String contractPhoto = record.getContractPhoto();
            String paymentPhoto = record.getPaymentPhoto();
            //有多个照片,合同照片
            if (StringUtils.isNotEmpty(contractPhoto) && contractPhoto.contains(",")) {
                //分割图片字符串，形成一个数组
                List<String> list = ImageUtils.imageSplit(contractPhoto);
                record.setContractPhotoArray(list);
                //取第一个图片的url
                record.setContractPhoto(ImageUtils.getFirstImageUrl(contractPhoto));
            } else {
                record.setContractPhotoArray(Arrays.asList(contractPhoto));
            }
//            付款照片
            if (StringUtils.isNotEmpty(paymentPhoto) && paymentPhoto.contains(",")) {
                //分割图片字符串，形成一个数组
                List<String> list = ImageUtils.imageSplit(paymentPhoto);
                record.setPaymentPhotoArray(list);
                //取第一个图片的url
                record.setPaymentPhoto(ImageUtils.getFirstImageUrl(paymentPhoto));
            } else {
                record.setPaymentPhotoArray(Arrays.asList(paymentPhoto));
            }
        }
        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addNewShippingContract(ShippingContract shippingContract, ShippingContract oldShippingContract, int flag) {
        if (ImageUtils.getDBString(shippingContract.getContractPhotoArray()) != "") {
            shippingContract.setContractPhoto(ImageUtils.getDBString(shippingContract.getContractPhotoArray()));
        }
        if (flag == 1) {
            shippingContract.setFinanceStaff(oldShippingContract.getFinanceStaff());
            shippingContract.setFinanceState(oldShippingContract.getFinanceState());
            shippingContract.setCashier(oldShippingContract.getCashier());
            shippingContract.setPaymentCount(oldShippingContract.getPaymentCount());
            shippingContract.setPaymentTime(oldShippingContract.getPaymentTime());
            shippingContract.setPaymentPhoto(oldShippingContract.getPaymentPhoto());
            shippingContract.setCreateTime(oldShippingContract.getCreateTime());
            shippingContract.setCreateBy(oldShippingContract.getCreateBy());
        } else {
            shippingContract.setCreateBy(SecurityUtils.getUsername());
        }
        shippingContract.setLastUpdateBy(SecurityUtils.getUsername());
        int result = shippingContractMapper.insert(shippingContract);

        if (result == 1) {
//            为0，则新增
            if (flag == 0) {
                //        查询出董事会的ID
                QueryWrapper<Director> directorQw = new QueryWrapper<>();
                directorQw.orderByAsc("nick_name").last("limit 3");
                List<Director> directors = directorMapper.selectList(directorQw);

//        循环添加海运董事审核记录
                for (Director director : directors) {
                    ShippingDirectorState shippingDirectorState = new ShippingDirectorState();
                    shippingDirectorState.setShippingContractNo(shippingContract.getShippingContractNo());
                    shippingDirectorState.setUserId(Math.toIntExact(director.getId()));
                    shippingDirectorState.setCreateBy(SecurityUtils.getUsername());
                    shippingDirectorState.setLastUpdateBy(SecurityUtils.getUsername());
                    shippingDirectorStateMapper.insert(shippingDirectorState);
                }
            } else if (flag == 1) {
//                不为0，则更新
                if (shippingContract.getShippingContractNo().equals(oldShippingContract.getShippingContractNo()) == false) {
//                    比较海运单号是否改变，若未改变则无需进行审批记录的修改
                    QueryWrapper<ShippingDirectorState> SDSQw = new QueryWrapper<>();
                    SDSQw.eq("shipping_contract_no", oldShippingContract.getShippingContractNo());
                    List<ShippingDirectorState> shippingDirectorStateList = shippingDirectorStateMapper.selectList(SDSQw);

                    for (int i = 0; i < shippingDirectorStateList.size(); i++) {
                        ShippingDirectorState shippingDirectorState = shippingDirectorStateList.get(i);
                        shippingDirectorState.setShippingContractNo(shippingContract.getShippingContractNo());
                        shippingDirectorStateMapper.updateById(shippingDirectorState);
                    }
                }
            }

            //            获取物流单,修改存在物流付款单标记
            QueryWrapper<LogisticsContract> qw = new QueryWrapper<>();
            qw.eq("logistics_contract_no", shippingContract.getLogisticsContractNo());
            LogisticsContract logisticsContract = logisticsContractMapper.selectOne(qw);
            logisticsContract.setRelationShippingExistState(1);
            logisticsContractMapper.updateById(logisticsContract);
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateShippingContract(ShippingContract shippingContract) {
        int result = 0;

        QueryWrapper<ShippingContract> qw = new QueryWrapper<>();
        qw.eq("shipping_contract_no", shippingContract.getOldShippingContractNo());
        ShippingContract oldShippingContract = shippingContractMapper.selectOne(qw);

//        只修改了图片
        if (shippingContract.getOnlyUpdatePhoto() == 1) {
            if (ImageUtils.getDBString(shippingContract.getContractPhotoArray()) != "") {
                oldShippingContract.setContractPhoto(ImageUtils.getDBString(shippingContract.getContractPhotoArray()));
            } else {
                oldShippingContract.setContractPhoto(null);
            }
            result = shippingContractMapper.updateById(oldShippingContract);
        } else {
            //        0代表是删除，1代表是更新，所以此次选择1，则不删除原图片和审批记录
            if (deleteOneShippingContract(shippingContract.getId(), 1) == 1) {
                //        0代表是新增，1代表是更新，所以此次选择1，则更新原审批记录
                result = addNewShippingContract(shippingContract, oldShippingContract, 1);
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteOneShippingContract(int id, int flag) {
        ShippingContract shippingContract = shippingContractMapper.selectById(id);

        if (flag == 0) {
            //        删除相关审核记录
            QueryWrapper<ShippingDirectorState> directorStateQw = new QueryWrapper<>();
            directorStateQw.eq("shipping_contract_no", shippingContract.getShippingContractNo());
            shippingDirectorStateMapper.delete(directorStateQw);

            ImageUtils.deleteImages(shippingContract.getContractPhoto());
        }

        String logisticsContractNo = shippingContract.getLogisticsContractNo();

        int result = shippingContractMapper.deleteById(id);

        if (result == 1) {
//            查询是否存在其他海运单
            QueryWrapper<ShippingContract> SCQw = new QueryWrapper<>();
            SCQw.eq("logistics_contract_no", logisticsContractNo);
            List<ShippingContract> shippingContractList = shippingContractMapper.selectList(SCQw);

//            若不存在则修改物流单字段
            if (shippingContractList.isEmpty() == true) {
                //            获取物流单
                QueryWrapper<LogisticsContract> qw = new QueryWrapper<>();
                qw.eq("logistics_contract_no", logisticsContractNo);
                LogisticsContract logisticsContract = logisticsContractMapper.selectOne(qw);
                logisticsContract.setRelationShippingExistState(0);
                logisticsContractMapper.updateById(logisticsContract);
            }
        }

        return result;
    }

    @Override
    public ShippingContract getOneShippingContract(int id) {
        ShippingContract shippingContract = shippingContractMapper.selectById(id);

        QueryWrapper<ShippingStateView> stateQw = new QueryWrapper<>();
        stateQw.eq("shipping_contract_no", shippingContract.getShippingContractNo()).orderByAsc("nick_name");
        shippingContract.setShippingDirector(shippingStateInfoMapper.selectList(stateQw));

        return shippingContract;
    }

    @Override
    public Boolean checkContainerNo(String containerNo) {
        List<ShippingContract> shippingContracts = shippingContractMapper.checkContainerNo(containerNo);
        return shippingContracts.size() != 0;
    }

    @Override
    public int handleImportShippingContractModel(ImportShippingContractModel importShippingContractModel) {
        ShippingContract shippingContract = new ShippingContract();
//        海运合同号
        if (importShippingContractModel.getShippingContractNo() == null) {
            return 0;
        } else {
//            检验是否重复
            if (checkShippingContractNo(importShippingContractModel.getShippingContractNo())) {
                return 0;
            } else {
                shippingContract.setShippingContractNo(importShippingContractModel.getShippingContractNo());
            }
        }
//        物流合同号
        if (importShippingContractModel.getLogisticsContractNo() == null) {
            return 0;
        } else {
//            检验是否存在
            if (logisticsContractService.checkLogisticsContractNo(importShippingContractModel.getLogisticsContractNo())) {
                shippingContract.setLogisticsContractNo(importShippingContractModel.getLogisticsContractNo());
            } else {
                return 0;
            }
        }

        if (importShippingContractModel.getOwnCompanyName() != null) {
            shippingContract.setOwnCompanyName(importShippingContractModel.getOwnCompanyName());
        }
        if (importShippingContractModel.getPrincipal() != null) {
            shippingContract.setPrincipal(importShippingContractModel.getPrincipal());
        }
        if (importShippingContractModel.getPackingTime() != null) {
            shippingContract.setPackingTime(importShippingContractModel.getPackingTime());
        }
        if (importShippingContractModel.getPackingLocation() != null) {
            shippingContract.setPackingLocation(importShippingContractModel.getPackingLocation());
        }
        if (importShippingContractModel.getUnpackingFactory() != null) {
            shippingContract.setUnpackingFactory(importShippingContractModel.getUnpackingFactory());
        }
        if (importShippingContractModel.getFirstContainerNo() != null) {
            shippingContract.setFirstContainerNo(importShippingContractModel.getFirstContainerNo());
        }
        if (importShippingContractModel.getSecondContainerNo() != null) {
            shippingContract.setSecondContainerNo(importShippingContractModel.getSecondContainerNo());
        }
        if (importShippingContractModel.getFirstSealNo() != null) {
            shippingContract.setFirstSealNo(importShippingContractModel.getFirstSealNo());
        }
        if (importShippingContractModel.getSecondSealNo() != null) {
            shippingContract.setSecondSealNo(importShippingContractModel.getSecondSealNo());
        }
        if (importShippingContractModel.getRough() != null) {
            shippingContract.setRough(importShippingContractModel.getRough());
        }
        if (importShippingContractModel.getTare() != null) {
            shippingContract.setTare(importShippingContractModel.getTare());
        }
        if (importShippingContractModel.getSuttle() != null) {
            shippingContract.setSuttle(importShippingContractModel.getSuttle());
        }
        if (importShippingContractModel.getTallyClerk() != null) {
            shippingContract.setTallyClerk(importShippingContractModel.getTallyClerk());
        }
        if (importShippingContractModel.getTallyClerkPrice() != null) {
            shippingContract.setTallyClerkPrice(importShippingContractModel.getTallyClerkPrice());
        }
        if (importShippingContractModel.getTallyClerkRemark() != null) {
            shippingContract.setTallyClerkRemark(importShippingContractModel.getTallyClerkRemark());
        }
        if (importShippingContractModel.getFleetManageName() != null) {
            shippingContract.setFleetManageName(importShippingContractModel.getFleetManageName());
        }
        if (importShippingContractModel.getDepartureFleet() != null) {
            shippingContract.setDepartureFleet(importShippingContractModel.getDepartureFleet());
        }
        if (importShippingContractModel.getDeparturePrice() != null) {
            shippingContract.setDeparturePrice(importShippingContractModel.getDeparturePrice());
        }
        if (importShippingContractModel.getCarrierCompanyName() != null) {
            shippingContract.setCarrierCompanyName(importShippingContractModel.getCarrierCompanyName());
        }
        if (importShippingContractModel.getCarrierCompanyPrice() != null) {
            shippingContract.setCarrierCompanyPrice(importShippingContractModel.getCarrierCompanyPrice());
        }
        if (importShippingContractModel.getDestinationPortFleet() != null) {
            shippingContract.setDestinationPortFleet(importShippingContractModel.getDestinationPortFleet());
        }
        if (importShippingContractModel.getDestinationPortPrice() != null) {
            shippingContract.setDestinationPortPrice(importShippingContractModel.getDestinationPortPrice());
        }
        if (importShippingContractModel.getExpenses() != null) {
            shippingContract.setExpenses(importShippingContractModel.getExpenses());
        }

        return addNewShippingContract(shippingContract, null, 0);
    }

    @Override
    public Boolean checkShippingContractNo(String shippingContractNo) {
        QueryWrapper<ShippingContract> qw = new QueryWrapper<>();
        qw.eq("shipping_contract_no", shippingContractNo).orderByDesc("create_time", "shipping_contract_no");
        List<ShippingContract> shippingContracts = shippingContractMapper.selectList(qw);
        return !shippingContracts.isEmpty();
    }

    @Override
    public Page<CashierShipping> getCashierShipping(int currentPage, int pageSize) {
        QueryWrapper<CashierShipping> qw = new QueryWrapper<>();
        qw.isNotNull("finance_staff").isNotNull("finance_state").eq("director_state", "1,1,1").orderByDesc("create_time", "shipping_contract_no");
        Page<CashierShipping> page = new Page<>(currentPage, pageSize);
        page = cashierShippingMapper.selectPage(page, qw);
        Iterator<CashierShipping> iterator = page.getRecords().iterator();

        while (iterator.hasNext()) {
            CashierShipping record = iterator.next();
//            获取董事长审核信息，并添加进对象中
            QueryWrapper<ShippingStateView> stateQw = new QueryWrapper<>();
            stateQw.eq("shipping_contract_no", record.getShippingContractNo()).isNotNull("state").orderByDesc("nick_name");
            List<ShippingStateView> shippingStateViews = shippingStateInfoMapper.selectList(stateQw);

            record.setShippingDirector(shippingStateViews);

            //处理图片，形成一个图片数组
            String contractPhoto = record.getContractPhoto();
            String paymentPhoto = record.getPaymentPhoto();
            //有多个照片,合同照片
            if (StringUtils.isNotEmpty(contractPhoto) && contractPhoto.contains(",")) {
                //分割图片字符串，形成一个数组
                List<String> list = ImageUtils.imageSplit(contractPhoto);
                record.setContractPhotoArray(list);
                //取第一个图片的url
                record.setContractPhoto(ImageUtils.getFirstImageUrl(contractPhoto));
            } else {
                record.setContractPhotoArray(Arrays.asList(contractPhoto));
            }
            //            付款照片
            if (StringUtils.isNotEmpty(paymentPhoto) && paymentPhoto.contains(",")) {
                //分割图片字符串，形成一个数组
                List<String> list = ImageUtils.imageSplit(paymentPhoto);
                record.setPaymentPhotoArray(list);
                //取第一个图片的url
                record.setPaymentPhoto(ImageUtils.getFirstImageUrl(paymentPhoto));
            } else {
                record.setPaymentPhotoArray(Arrays.asList(paymentPhoto));
            }
        }
        return page;
    }

    @Override
    public Page<CashierShipping> searchCashierShipping(int currentPage, int pageSize, String searchWord) {
        QueryWrapper<CashierShipping> qw = new QueryWrapper<>();
        qw.isNotNull("finance_staff").isNotNull("finance_state").eq("director_state", "1,1,1").and(q ->
                q.like("shipping_contract_no", searchWord).or().like("logistics_contract_no", searchWord).or().like("own_company_name", searchWord)
                        .or().like("principal", searchWord).or().like("packing_location", searchWord).or().like("unpacking_factory", searchWord)
                        .or().like("first_container_no", searchWord).or().like("second_container_no", searchWord).or().like("first_seal_no", searchWord)
                        .or().like("second_seal_no", searchWord).or().like("tally_clerk", searchWord).or().like("tally_clerk_remark", searchWord)
                        .or().like("fleet_manage_name", searchWord).or().like("departure_fleet", searchWord).or().like("carrier_company_name", searchWord)
                        .or().like("destination_port_fleet", searchWord).or().like("finance_staff", searchWord).or().like("cashier", searchWord)
                        .or().like("create_by", searchWord)
        ).orderByDesc("create_time", "shipping_contract_no");
        Page<CashierShipping> page = new Page<>(currentPage, pageSize);
        page = cashierShippingMapper.selectPage(page, qw);
        Iterator<CashierShipping> iterator = page.getRecords().iterator();
        while (iterator.hasNext()) {
            CashierShipping record = iterator.next();
//            获取董事长审核信息，并添加进对象中
            QueryWrapper<ShippingStateView> stateQw = new QueryWrapper<>();
            stateQw.eq("shipping_contract_no", record.getShippingContractNo()).isNotNull("state").orderByDesc("nick_name");
            List<ShippingStateView> shippingStateViews = shippingStateInfoMapper.selectList(stateQw);

            record.setShippingDirector(shippingStateViews);

            //处理图片，形成一个图片数组
            String contractPhoto = record.getContractPhoto();
            String paymentPhoto = record.getPaymentPhoto();
            //有多个照片,合同照片
            if (StringUtils.isNotEmpty(contractPhoto) && contractPhoto.contains(",")) {
                //分割图片字符串，形成一个数组
                List<String> list = ImageUtils.imageSplit(contractPhoto);
                record.setContractPhotoArray(list);
                //取第一个图片的url
                record.setContractPhoto(ImageUtils.getFirstImageUrl(contractPhoto));
            } else {
                record.setContractPhotoArray(Arrays.asList(contractPhoto));
            }
            //            付款照片
            if (StringUtils.isNotEmpty(paymentPhoto) && paymentPhoto.contains(",")) {
                //分割图片字符串，形成一个数组
                List<String> list = ImageUtils.imageSplit(paymentPhoto);
                record.setPaymentPhotoArray(list);
                //取第一个图片的url
                record.setPaymentPhoto(ImageUtils.getFirstImageUrl(paymentPhoto));
            } else {
                record.setPaymentPhotoArray(Arrays.asList(paymentPhoto));
            }
        }
        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int uploadCashierShipping(ShippingContract shippingContract) {
        ShippingContract oldShippingContract = shippingContractMapper.selectById(shippingContract.getId());
        String paymentPhotos = ImageUtils.getDBString(shippingContract.getPaymentPhotoArray());
        if (paymentPhotos != "") {
            oldShippingContract.setPaymentPhoto(paymentPhotos);
        }
        oldShippingContract.setCashier(SecurityUtils.getUsername());
        oldShippingContract.setLastUpdateBy(SecurityUtils.getUsername());
        oldShippingContract.setPaymentCount(oldShippingContract.getExpenses());
        oldShippingContract.setPaymentTime(shippingContract.getPaymentTime());
        return shippingContractMapper.updateById(oldShippingContract);
    }

    @Override
    public Page<CashierShipping> getDirectorSC(int currentPage, int pageSize, int userId, int type) {
        QueryWrapper<CashierShipping> qw = new QueryWrapper<>();
//        该处的-是为了模糊匹配时匹配到完整的director_id
//        避免出现查userId=1，却查出userId=11这样的情况
        if(type==0){
            qw.isNotNull("finance_staff").isNotNull("finance_state").and(q->
                    q.like("director_id",userId+"-").and(s->
                            s.notLike("concat_director_state",userId+"-1")).or().isNull("concat_director_state")).orderByDesc("create_time","id");
        }else if(type==1){
            qw.isNotNull("finance_staff").isNotNull("finance_state").and(q->
                    q.like("director_id",userId+"-").like("concat_director_state",userId+"-1").ne("director_state","1,1,1")).orderByDesc("create_time","id");
        }else if(type==2){
            qw.isNotNull("finance_staff").isNotNull("finance_state").and(q->
                    q.like("director_id",userId+"-").like("concat_director_state",userId+"-1").eq("director_state","1,1,1")).orderByDesc("create_time","id");
        }
        Page<CashierShipping> page = new Page<>(currentPage, pageSize);
        page = cashierShippingMapper.selectPage(page, qw);
        Iterator<CashierShipping> iterator = page.getRecords().iterator();
        while (iterator.hasNext()) {
            CashierShipping record = iterator.next();
//            获取董事长审核信息，并加入对象中
            QueryWrapper<ShippingStateView> stateQw = new QueryWrapper<>();
            stateQw.eq("shipping_contract_no", record.getShippingContractNo()).orderByDesc("nick_name");
            List<ShippingStateView> shippingStateViews = shippingStateInfoMapper.selectList(stateQw);
            record.setShippingDirector(shippingStateViews);

            //处理图片，形成一个图片数组
            String contractPhoto = record.getContractPhoto();
            String paymentPhoto = record.getPaymentPhoto();
            //有多个照片,合同照片
            if (StringUtils.isNotEmpty(contractPhoto) && contractPhoto.contains(",")) {
                //分割图片字符串，形成一个数组
                List<String> list = ImageUtils.imageSplit(contractPhoto);
                record.setContractPhotoArray(list);
                //取第一个图片的url
                record.setContractPhoto(ImageUtils.getFirstImageUrl(contractPhoto));
            } else {
                record.setContractPhotoArray(Arrays.asList(contractPhoto));
            }
//            付款照片
            if (StringUtils.isNotEmpty(paymentPhoto) && paymentPhoto.contains(",")) {
                //分割图片字符串，形成一个数组
                List<String> list = ImageUtils.imageSplit(paymentPhoto);
                record.setPaymentPhotoArray(list);
                //取第一个图片的url
                record.setPaymentPhoto(ImageUtils.getFirstImageUrl(paymentPhoto));
            } else {
                record.setPaymentPhotoArray(Arrays.asList(paymentPhoto));
            }
        }
        return page;
    }

    @Override
    public ShippingContract getOneDirectorSC(int id) {
        ShippingContract shippingContract = shippingContractMapper.selectById(id);
        QueryWrapper<ShippingStateView> stateQw = new QueryWrapper<>();
        stateQw.eq("shipping_contract_no", shippingContract.getShippingContractNo()).orderByDesc("nick_name");
        shippingContract.setShippingDirector(shippingStateInfoMapper.selectList(stateQw));
        //处理图片，形成一个图片数组
        String contractPhoto = shippingContract.getContractPhoto();
        String paymentPhoto = shippingContract.getPaymentPhoto();
//            付款照片
        if (StringUtils.isNotEmpty(contractPhoto) && contractPhoto.contains(",")) {
            //分割图片字符串，形成一个数组
            List<String> list = ImageUtils.imageSplit(contractPhoto);
            shippingContract.setContractPhotoArray(list);
            //取第一个图片的url
            shippingContract.setContractPhoto(ImageUtils.getFirstImageUrl(contractPhoto));
        } else {
            shippingContract.setContractPhotoArray(Arrays.asList(contractPhoto));
        }
        if (StringUtils.isNotEmpty(paymentPhoto) && paymentPhoto.contains(",")) {
            //分割图片字符串，形成一个数组
            List<String> list = ImageUtils.imageSplit(paymentPhoto);
            shippingContract.setPaymentPhotoArray(list);
            //取第一个图片的url
            shippingContract.setPaymentPhoto(ImageUtils.getFirstImageUrl(paymentPhoto));
        } else {
            shippingContract.setPaymentPhotoArray(Arrays.asList(paymentPhoto));
        }
        return shippingContract;
    }

    @Override
    public Page<CashierShipping> searchDirectorSC(int currentPage, int pageSize, String searchWord, int userId) {
        QueryWrapper<CashierShipping> qw = new QueryWrapper<>();
        qw.isNotNull("finance_staff").isNotNull("finance_state").and(w->w.like("director_id",userId+"-").and(q ->
                q.like("shipping_contract_no", searchWord).or().like("logistics_contract_no", searchWord).or().like("own_company_name", searchWord)
                        .or().like("principal", searchWord).or().like("packing_location", searchWord).or().like("unpacking_factory", searchWord)
                        .or().like("first_container_no", searchWord).or().like("second_container_no", searchWord).or().like("first_seal_no", searchWord)
                        .or().like("second_seal_no", searchWord).or().like("tally_clerk", searchWord).or().like("tally_clerk_remark", searchWord)
                        .or().like("fleet_manage_name", searchWord).or().like("departure_fleet", searchWord).or().like("carrier_company_name", searchWord)
                        .or().like("destination_port_fleet", searchWord).or().like("finance_staff", searchWord).or().like("cashier", searchWord)
                        .or().like("create_by", searchWord)
        )).orderByDesc("create_time", "shipping_contract_no");
        Page<CashierShipping> page = new Page<>(currentPage, pageSize);
        page = cashierShippingMapper.selectPage(page, qw);
        Iterator<CashierShipping> iterator = page.getRecords().iterator();
        while (iterator.hasNext()) {
            CashierShipping record = iterator.next();
//            获取董事长审核信息，并添加进对象中
            QueryWrapper<ShippingStateView> stateQw = new QueryWrapper<>();
            stateQw.eq("shipping_contract_no", record.getShippingContractNo()).orderByDesc("nick_name");
            List<ShippingStateView> shippingStateViews = shippingStateInfoMapper.selectList(stateQw);
            record.setShippingDirector(shippingStateViews);

            //处理图片，形成一个图片数组
            String contractPhoto = record.getContractPhoto();
            String paymentPhoto = record.getPaymentPhoto();
            //有多个照片,合同照片
            if (StringUtils.isNotEmpty(contractPhoto) && contractPhoto.contains(",")) {
                //分割图片字符串，形成一个数组
                List<String> list = ImageUtils.imageSplit(contractPhoto);
                record.setContractPhotoArray(list);
                //取第一个图片的url
                record.setContractPhoto(ImageUtils.getFirstImageUrl(contractPhoto));
            } else {
                record.setContractPhotoArray(Arrays.asList(contractPhoto));
            }
            //            付款照片
            if (StringUtils.isNotEmpty(paymentPhoto) && paymentPhoto.contains(",")) {
                //分割图片字符串，形成一个数组
                List<String> list = ImageUtils.imageSplit(paymentPhoto);
                record.setPaymentPhotoArray(list);
                //取第一个图片的url
                record.setPaymentPhoto(ImageUtils.getFirstImageUrl(paymentPhoto));
            } else {
                record.setPaymentPhotoArray(Arrays.asList(paymentPhoto));
            }

        }
        return page;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int changeFinanceState(String shippingContractNo, String financeStaff) {
        QueryWrapper<ShippingContract> qw = new QueryWrapper<>();
        qw.eq("shipping_contract_no", shippingContractNo);
        ShippingContract shippingContract = shippingContractMapper.selectOne(qw);
        shippingContract.setFinanceStaff(financeStaff);
        shippingContract.setFinanceState(1);

        int result = shippingContractMapper.updateById(shippingContract);

        if (result == 1) {
            QueryWrapper<LogisticsContract> LCqw = new QueryWrapper<>();
            LCqw.eq("logistics_contract_no", shippingContract.getLogisticsContractNo());
            LogisticsContract logisticsContract = logisticsContractMapper.selectOne(LCqw);
            logisticsContract.setRelationShippingAuditState(1);
            logisticsContractMapper.updateById(logisticsContract);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int changeDirectorState(String shippingContractNo, int userId) {
        UpdateWrapper<ShippingDirectorState> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("shipping_contract_no", shippingContractNo).eq("user_id", userId).set("state", 1);
        return shippingDirectorStateMapper.update(null, updateWrapper);
    }
}




