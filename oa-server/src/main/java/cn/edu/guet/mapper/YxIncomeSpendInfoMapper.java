package cn.edu.guet.mapper;

import cn.edu.guet.bean.YxIncomeSpendView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.util.List;

/**
* @author 陶祎祎
* @description 针对表【yx_income_spend_info】的数据库操作Mapper
* @createDate 2022-11-17 14:27:25
* @Entity generator.bean.YxIncomeSpendView
*/
@Mapper
public interface YxIncomeSpendInfoMapper extends BaseMapper<YxIncomeSpendView> {
    List<YxIncomeSpendView> getYxIncomeSpendData(@Param("startDate")Date startDate, @Param("endDate")Date endDate);
}



