package net.whxxykj.maya.app.ctrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.whxxykj.maya.base.BaseConstant;
import net.whxxykj.maya.base.entity.SysOperator;
import net.whxxykj.maya.base.entity.SysOrg;
import net.whxxykj.maya.base.entity.VPermissionAllotData;
import net.whxxykj.maya.base.service.SysOperatorService;
import net.whxxykj.maya.base.service.SysOrgService;
import net.whxxykj.maya.base.service.VPermissionAllotDataService;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.common.util.StringUtil;

@RestController
@RequestMapping(value = "/mobile/sysm/org")
public class MobileSysOrgCtrl extends BaseCtrl<SysOrgService, SysOrg> {

    @Autowired
    private SysOrgService sysOrgService;
    @Autowired
    private SysOperatorService operatorService;
    @Autowired
    private VPermissionAllotDataService vPermissionAllotDataService;
    
    @Override
    public JsonModel findList(@RequestBody QueryBean queryBean) {
      List<SysOrg> orgList = sysOrgService.findAll();
      if(CollectionUtils.isNotEmpty(orgList)) {
           for(SysOrg org : orgList) {
              if(StringUtil.isEmpty(org.getOrgMnemcode())) {
                 try {
                     sysOrgService.edit(org);   
                 }catch(Exception e) {
                     logger.error("单位ID{}",org.getId());
                     e.printStackTrace();
                 } 
              }
           }
      }
      return super.findList(queryBean);
    }
    
    @Override
    public JsonModel add(@RequestBody SysOrg mod) {
        SysOrg newMod = sysOrgService.add(mod);
        
        String employeeCode = mod.getEmployeeCode();
        List<SysOperator> operatorList =   operatorService.findByEmpId(employeeCode);
        if(CollectionUtils.isNotEmpty(operatorList)) {//自动给业务员分配数据权限
            SysOperator operator = operatorList.get(0);
            String optId = operator.getId();
            VPermissionAllotData newAllotData = new VPermissionAllotData();
            newAllotData.setDataperCode(BaseConstant.DataperCode.ORG_CODE);
            newAllotData.setDataperItem(newMod.getId());
            newAllotData.setOperatorCode(operator.getId());
            newAllotData.setPermissionFlag("0");
            newAllotData.setDataperRstate("1");
            newAllotData.setDataperWstate("1");
            vPermissionAllotDataService.updateOpDataper(newAllotData);
            vPermissionAllotDataService.updateRedis(optId, BaseConstant.DataperCode.ORG_CODE, "0");
        }
        
        return JsonModel.dataResult(newMod, "新增成功");
    }
    
    @Override
    public JsonModel edit(@RequestBody SysOrg mod) {
        SysOrg bean = sysOrgService.getOne(mod.getId());
        String newEmployeeCode = mod.getEmployeeCode();
        String oldEmployeeCode = bean.getEmployeeCode();
        sysOrgService.edit(mod);
        
        if(!newEmployeeCode.equals(oldEmployeeCode)) {//业务员发生变化,数据权限发生对应变化
            List<SysOperator> newOperatorList = operatorService.findByEmpId(newEmployeeCode);
            if(CollectionUtils.isNotEmpty(newOperatorList)) {
                SysOperator operator = newOperatorList.get(0);
                String optId = operator.getId();
                //自动分配数据权限
                VPermissionAllotData newAllotData = new VPermissionAllotData();
                newAllotData.setDataperCode(BaseConstant.DataperCode.ORG_CODE);
                newAllotData.setDataperItem(mod.getId());
                newAllotData.setOperatorCode(operator.getId());
                newAllotData.setPermissionFlag("0");
                newAllotData.setDataperRstate("1");
                newAllotData.setDataperWstate("1");
                vPermissionAllotDataService.updateOpDataper(newAllotData);
                vPermissionAllotDataService.updateRedis(optId, BaseConstant.DataperCode.ORG_CODE, "0");
            }
            List<SysOperator> oldOperatorList = operatorService.findByEmpId(oldEmployeeCode);
            if(CollectionUtils.isNotEmpty(oldOperatorList)) {
                SysOperator operator = oldOperatorList.get(0);
                String optId = operator.getId();
                //自动分配数据权限
                VPermissionAllotData newAllotData = new VPermissionAllotData();
                newAllotData.setDataperCode(BaseConstant.DataperCode.ORG_CODE);
                newAllotData.setDataperItem(mod.getId());
                newAllotData.setOperatorCode(operator.getId());
                newAllotData.setPermissionFlag("0");
                newAllotData.setDataperRstate("0");
                newAllotData.setDataperWstate("0");
                vPermissionAllotDataService.updateOpDataper(newAllotData);
                vPermissionAllotDataService.updateRedis(optId, BaseConstant.DataperCode.ORG_CODE, "0");
            }
        }
        
        return JsonModel.dataResult(mod, "编辑成功");
    }

    /**
     * 数据提交
     * @param id
     * @author min.jiang
     * @date 2021/3/2
     * @return net.whxxykj.maya.common.util.JsonModel
     */
    @GetMapping("/submit")
    public JsonModel submit(@RequestParam String id) {
        sysOrgService.dataSubmit(id);
        return JsonModel.mkSuccess("操作成功");
    }
    
    /**
     * @Description 导入数据校验
     * @param detailList
     * @return
     */
    @PostMapping(value = "/importOrg" )
    @ResponseBody
    public JsonModel importInitgoods(@RequestBody Map<String,List<SysOrg>> map) {
        List<SysOrg> detailList = new ArrayList<SysOrg>();
        for (String key : map.keySet()) {
            detailList = map.get(key);
            detailList =  sysOrgService.checkData(detailList);
        }
        return JsonModel.dataResult(detailList);
    }

    /**
     * description:批量新增
     * @param detailList
     * @return
     */
    @PostMapping(value = "/addlist" )
    @ResponseBody
    public JsonModel addList(@RequestBody Map<String,List<SysOrg>> map) {
        for (String key : map.keySet()) {
            List<SysOrg> detailList = map.get(key);
            detailList.forEach((detail)->{
                sysOrgService.add(detail);
            });
        }
        return JsonModel.mkSuccess();
    }

    /**
     * 数据启用
     * @param id
     * @author min.jiang
     * @date 2021/3/2
     * @return net.whxxykj.maya.common.util.JsonModel
     */
    @GetMapping("/enable")
    public JsonModel enable(@RequestParam String id) {
        sysOrgService.dataEnable(id);
        return JsonModel.mkSuccess("操作成功");
    }
}
