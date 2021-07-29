package net.whxxykj.maya.app.repository;

import org.springframework.stereotype.Repository;

import net.whxxykj.maya.common.repository.BaseRepository;
import net.whxxykj.maya.trade.entity.WarehouseSbill;

@Repository
public interface MobileWarehouseSbillRepository extends BaseRepository<WarehouseSbill, String> {
    
}
