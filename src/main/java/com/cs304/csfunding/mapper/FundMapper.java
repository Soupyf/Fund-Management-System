package com.cs304.csfunding.mapper;

import com.cs304.csfunding.api.FundDTO;
import com.cs304.csfunding.entity.Fund;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FundMapper {

    @Insert("insert into fund(FundNumber, FundName, Sum, Balance, RemainDays, StartTime, EndTime) values (#{FundNumber}, #{FundName}, #{Sum}, #{Balance}, #{RemainDays}, #{StartTime}, #{EndTime})")
    void addFund(Fund fund);

    @Select("select * from fund")
    List<Fund> getAllFund();

    @Select("select * from fund where UUID=#{UUID}")
    Fund getFundByID(@Param("UUID") int uuid);

    @Update("update fund set FundNumber=#{FundNumber}, FundName=#{FundName}, Sum=#{Sum}, Balance=#{Balance}, RemainDays=#{RemainDays}, StartTime=#{StartTime}, EndTime=#{EndTime} where UUID=#{uuid}")
    void modifyFund(Fund fund);

    @Delete("delete from fund where UUID=#{UUID}")
    void deleteFundByID(@Param("UUID") int uuid);
}
