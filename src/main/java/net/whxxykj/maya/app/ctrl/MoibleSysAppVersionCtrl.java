package net.whxxykj.maya.app.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.trade.entity.SysAppVersion;
import net.whxxykj.maya.trade.service.SysAppVersionService;


/**
* @Description: SysAppVersionCtrl
* @Author: WB
* @Date: 2021-7-20
*/
@RestController
@RequestMapping(value = "/mobile/sys/version")
public class MoibleSysAppVersionCtrl extends BaseCtrl<SysAppVersionService, SysAppVersion> {

	@Autowired
	private SysAppVersionService versionService;
	
	@GetMapping("/findMaxversion")
	public JsonModel findMaxversion() {
		return JsonModel.dataResult(versionService.findMaxversion());
	}
}
