package net.whxxykj.maya.app.service;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import net.whxxykj.maya.base.service.SysFileService;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.service.BaseService;
import net.whxxykj.maya.trade.entity.SaleScontract;
import net.whxxykj.maya.trade.repository.SaleScontractRepository;
import net.whxxykj.maya.trade.service.SaleScontractService;

@Service
public class MobileSaleScontractService extends BaseService<SaleScontractRepository, SaleScontract> {
    
    @Autowired
    private SaleScontractService saleScontractService;
    @Autowired
    private SysFileService sysFileService;
    
    @Override
    public Page<SaleScontract> queryPageList(QueryBean queryBean) {
        Page<SaleScontract> page =   saleScontractService.queryPageList(queryBean);
        List<SaleScontract> list =  page.getContent();
        if(CollectionUtils.isNotEmpty(list)){
            for(SaleScontract scontract : list ) {
                scontract = saleScontractService.getOne(scontract.getId());
            }
        }
        return page;
    }
}
