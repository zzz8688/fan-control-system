package io.github.zzz8688.fancontrol.mapper;

import io.github.zzz8688.fancontrol.entity.AlarmRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlarmRecordMapper {

    int insert(AlarmRecord record);

    int deleteAll();

    List<AlarmRecord> selectRecent(@Param("limit") int limit);
}
