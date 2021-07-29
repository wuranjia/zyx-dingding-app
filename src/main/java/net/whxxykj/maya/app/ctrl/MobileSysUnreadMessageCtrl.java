package net.whxxykj.maya.app.ctrl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.whxxykj.maya.base.entity.SysUnreadMessage;
import net.whxxykj.maya.base.service.SysUnreadMessageService;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.util.JsonModel;
/**
 * 
 * @ClassName:  MobileSysUnreadMessageCtrl   
 * @Description:未读消息  
 * @author: HAOKE
 * @date:   2021年7月2日 下午5:02:22      
 * @Copyright:
 */
@RestController
@RequestMapping(value = "/mobile/sysm/unreadMessage")
public class MobileSysUnreadMessageCtrl extends BaseCtrl<SysUnreadMessageService, SysUnreadMessage> {
    @Autowired
    private SysUnreadMessageService sysUnreadMessageService;

    @GetMapping(value = "/findUnreadCountAndInfo")
    public JsonModel findUnreadCountAndInfo() {
        SysUnreadMessage message= sysUnreadMessageService.findUnreadCountAndInfo(getManagerUser().getId());
        return JsonModel.dataResult(message, "查询成功");
    }
    
    @GetMapping(value = "/emptyAll")
    public JsonModel delAll() {
        List<SysUnreadMessage> list=sysUnreadMessageService.findByOperatorCode(getManagerUser().getId());
        if(null!=list && list.size() > 0) {
            sysUnreadMessageService.deleteAll(list);
        }
        return JsonModel.mkSuccess("清空成功");
    }
    @GetMapping(value = "/findUnreadCount")
    public JsonModel findUnreadCount() {
        int count= sysUnreadMessageService.findCountByOperatorCode(getManagerUser().getId());
        return JsonModel.dataResult(count, "查询成功");
    }
}
