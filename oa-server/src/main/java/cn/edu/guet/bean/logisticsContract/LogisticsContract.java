package cn.edu.guet.bean.logisticsContract;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 物流单
 * @TableName logistics_contract
 */
@TableName(value ="logistics_contract")
@Data
public class LogisticsContract implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 物流单合同编号（运输合同编号）
     */
    private String logisticsContractNo;

    /**
     * 销售单合同编号
     */
    private String saleContractNo;
    /**
     * 销售单合同总重量
     */
    private BigDecimal totalWeight;

    /**
     * 货物单位
     */
    private String goodsUnit;

    /**
     * 运费
     */
    private BigDecimal freight;

    /**
     * 物流合同照片
     */
    private String contractPhoto;

    /**
     * 物流合同照片数组
     */
    @TableField(exist = false)
    private List<String> contractPhotoList;

    /**
     * 物流单合同签订时间
     */
    private Date logisticsContractTime;

    /**
     * 榨季
     */
    private String squeezeSeason;

    /**
     * 归档  1显示  0隐藏
     */
    private String pigeonhole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建者名称
     */
    private String createBy;

    /**
     * 最新更新时间
     */
    private Date lastUpdateTime;

    /**
     * 最新更新者名称
     */
    private String lastUpdateBy;


    /**
     * 物流详情表集合
     */
    @TableField(exist = false)
    private List<LogisticsDetail> logisticsDetailList;



}