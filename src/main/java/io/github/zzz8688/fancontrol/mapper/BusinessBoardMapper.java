package io.github.zzz8688.fancontrol.mapper;

import io.github.zzz8688.fancontrol.entity.BusinessBoard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BusinessBoardMapper {

    int count();

    int deleteAll();

    List<BusinessBoard> selectAll();

    int insert(BusinessBoard board);

    int updateTemperature(@Param("slot") int slot,
                          @Param("temperature") double temperature,
                          @Param("updatedAt") LocalDateTime updatedAt);
}
