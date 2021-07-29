package net.whxxykj.maya.app.ctrl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.whxxykj.maya.base.entity.SysTermlist;
import net.whxxykj.maya.base.service.SysTermlistService;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.JsonModel;

/**
 * 
 * @ClassName:  MobileSysTermsettingCtrl   
 * @Description: 条款设置   
 * @author: YEJUN
 * @date:   2021年6月28日 下午3:02:07      
 * @Copyright:
 */
@RestController
@RequestMapping(value = "/mobile/sysm/termlist")
public class MobileSysTermlistCtrl extends BaseCtrl<SysTermlistService, SysTermlist> {
    
    @Autowired
    private SysTermlistService sysTermlistService;

    @PostMapping(value = "/queryList")
    public JsonModel queryTermList(@RequestBody QueryBean queryBean) {
        List<SysTermlist> list = sysTermlistService.queryTermList(queryBean);
        return JsonModel.dataResult(list);
    }

}
