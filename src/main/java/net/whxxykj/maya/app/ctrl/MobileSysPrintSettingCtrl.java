package net.whxxykj.maya.app.ctrl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.whxxykj.maya.base.entity.SysPrintSetting;
import net.whxxykj.maya.base.service.SysPrintSettingService;
import net.whxxykj.maya.common.ctrl.BaseCtrl;

/**
 * 此类为打印配置接口
 */
@RestController
@RequestMapping("/mobile/sysm/printSetting")
public class MobileSysPrintSettingCtrl extends BaseCtrl<SysPrintSettingService, SysPrintSetting> {
	
}
