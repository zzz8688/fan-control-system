package io.github.zzz8688.fancontrol.mapper;

import io.github.zzz8688.fancontrol.domain.FanBoardModeType;
import io.github.zzz8688.fancontrol.domain.FanSpeed;
import io.github.zzz8688.fancontrol.entity.FanBoard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface FanBoardMapper {

    int count();

    int deleteAll();

    List<FanBoard> selectAll();

    FanBoard selectBySlot(@Param("slot") int slot);

    int insert(FanBoard board);

    /** 手动调速（不限模式，模式互斥由 Service 层保证） */
    int updateSpeed(@Param("slot") int slot,
                    @Param("speed") FanSpeed speed,
                    @Param("updatedAt") LocalDateTime updatedAt);

    /** 自动调速：仅当当前模式为 AUTOMATIC 才生效，避免与手动切换发生覆盖竞态 */
    int updateSpeedIfAutomatic(@Param("slot") int slot,
                               @Param("speed") FanSpeed speed,
                               @Param("updatedAt") LocalDateTime updatedAt);

    int updateMode(@Param("slot") int slot,
                   @Param("mode") FanBoardModeType mode,
                   @Param("updatedAt") LocalDateTime updatedAt);
}
