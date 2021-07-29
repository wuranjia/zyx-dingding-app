package net.whxxykj.maya.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.whxxykj.maya.app.repository.MobileWarehouseSbillRepository;
import net.whxxykj.maya.common.service.BaseService;
import net.whxxykj.maya.trade.entity.WarehouseSbill;

@Service
public class MobileWarehouseSbillService extends BaseService<MobileWarehouseSbillRepository, WarehouseSbill> {
    
    @Autowired
    private MobileWarehouseSbillRepository mobileWarehouseSbillRepository;
    
}
