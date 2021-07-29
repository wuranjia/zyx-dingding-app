package net.whxxykj.maya.app.ctrl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.whxxykj.maya.base.entity.SysEmployee;
import net.whxxykj.maya.base.entity.SysOperator;
import net.whxxykj.maya.base.entity.SysRole;
import net.whxxykj.maya.base.entity.TreeBean;
import net.whxxykj.maya.base.service.SysCompanyService;
import net.whxxykj.maya.base.service.SysOperatorService;
import net.whxxykj.maya.base.service.SysRoleService;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.entity.ManagerUser;
import net.whxxykj.maya.common.exception.BaseException;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.common.util.StringUtil;

@RestController
@RequestMapping("/mobile/sysm/operator")
public class MoibleSysOperatorCtrl extends BaseCtrl<SysOperatorService, SysOperator>{

    @Autowired
    private SysOperatorService sysOperatorService;
    
    @Autowired
    private SysCompanyService sysCompanyService;
    
    @Autowired
    private SysRoleService sysRoleService;
    
    /**
     * 此接口为查询操作员数据
     * @param queryBean 通用查询对象
     * @return
     */
    @PostMapping("/findListAll")
    public JsonModel findListAll(@RequestBody QueryBean queryBean){
        //系统管理员能看到所有的数据，非系统管理员只能看到自己公司的数据
        ManagerUser user = this.getManagerUser() ;
//      Integer optIdentity  = user.getOptIdentity();
//      if(Integer.valueOf(2).compareTo(optIdentity) != 0) {
//          // 非系统管理员查看该机构或下属机构部门信息
//          Set<String> idSet = sysCompanyService.findIdsBySupLegalId(user.getCompanyId());
//          Map<String, Object> searchFileds = queryBean.getSearchFileds();
//          searchFileds.put("companyId_in", idSet);
//      }
        queryBean.setSortStr("id");
        queryBean.setDirStr("desc");
//        DataPermissionUtil.getInstance().getCompanyReadPermisssion(queryBean, user, "companyId");
//        DataPermissionUtil.getInstance().getDeptReadPermisssion(queryBean, user, "deptId");
        Page<SysOperator> page = sysOperatorService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }
    @PostMapping("/findListAll1")
    public JsonModel findListAll1(){
        //系统管理员能看到所有的数据，非系统管理员只能看到自己公司的数据
        QueryBean queryBean = new QueryBean();
        ManagerUser user = this.getManagerUser();
        Integer optIdentity  = user.getOptIdentity();
        if(Integer.valueOf(2).compareTo(optIdentity) != 0) {
//            DataPermissionUtil.getInstance().getCompanyReadPermisssion(queryBean, user, "companyId");
//            DataPermissionUtil.getInstance().getDeptReadPermisssion(queryBean, user, "deptId");
        }
        queryBean.setSortStr("id");
        queryBean.setDirStr("desc");
        List<TreeBean> list = sysOperatorService.queryTreeList(queryBean);
        List<SysRole> sysRoleList  = sysRoleService.findAll();
        if(CollectionUtils.isNotEmpty(sysRoleList)) {
            TreeBean treeBean = new TreeBean();
            treeBean.setId("role");
            treeBean.setText("角色");
            List<TreeBean> roleList = new ArrayList<TreeBean>();
            for(SysRole role : sysRoleList) {
                TreeBean treeBeanChild = new TreeBean(); 
                treeBeanChild.setId(role.getId());
                treeBeanChild.setText(role.getRoleName());
                treeBeanChild.setLeaf(true);
                treeBeanChild.setType("role");
                roleList.add(treeBeanChild);
            }
            treeBean.setChildren(roleList);
            list.add(treeBean);
        }
        return JsonModel.dataResult(list.size(),list);
    }
    
    

    /**
     * 此接口为查询操作员数据
     * @param queryBean 通用查询对象
     * @return
     */
    @PostMapping("/findOptByEmpId")
    public JsonModel findOptByEmpId(@RequestBody QueryBean queryBean){
        ManagerUser managerUser = this.getManagerUser();
        if (null == managerUser) {
            throw new BaseException("用户信息失效,请重新登录");
        }
        if (null == queryBean.getSearchFileds().get("empId_eq")) {
            queryBean.defalutParam("empId_eq", managerUser.getEmpId());
        }
        List<SysOperator> optList = sysOperatorService.queryList(queryBean);
        return JsonModel.dataResult(optList.size(), optList);
    }
    
    /**
     * 此接口为新增操作员数据
     * @param sysOperator 操作员对象
     * @return
     */
    @PostMapping("/addSysOperator")
    public JsonModel addSysOperator(@RequestBody SysOperator sysOperator){
        sysOperatorService.addOpt(sysOperator);
        return JsonModel.mkSuccess("新增成功");
    }

    /**
     * 此接口为编辑操作员数据
     * @param sysOperator 操作员对象
     * @return
     */
    @PostMapping("/editSysOperator")
    public JsonModel editSysOperator(@RequestBody SysOperator sysOperator){
        sysOperatorService.editOpt(sysOperator);
        return JsonModel.mkSuccess("编辑成功");
    }

    /**
     * 此接口为根据操作员ID查询操作员数据
     * @param id 操作员ID
     * @return
     */
    @GetMapping("/findById")
    public JsonModel findById(@RequestParam String id){
        SysOperator sysOperator = sysOperatorService.findById(id);
        return JsonModel.dataResult(sysOperator);
    }

    /**
     * 此接口为修改操作员状态
     * @param state 操作员状态
     * @param userid 操作员账号
     * @return
     */
    @GetMapping("/updateByState")
    public JsonModel updateByState(@RequestParam int state, @RequestParam String userid){
        sysOperatorService.updateByStateAndUserId(state, userid);
        return JsonModel.mkSuccess("操作成功");
    }

    /**
     * 此接口为重置操作员错误密码次数
     * @param userid 操作员账号
     * @return
     */
    @GetMapping("/updateByFailunm")
    public JsonModel updateByFailunm(@RequestParam String userid){
        sysOperatorService.updateByFailunm(userid);
        return JsonModel.mkSuccess("操作成功");
    }

    /**
     * 此接口为修改操作员密码
     * @param password 操作员密码
     * @param userid 操作员账号
     * @return
     */
    @GetMapping("/updateByPassword")
    public JsonModel updateByPassword(@RequestParam String password, @RequestParam String userid){
        sysOperatorService.updateByPassword(password, userid);
        return JsonModel.mkSuccess("操作成功");
    }

    /**
     * 此接口为查询全部操作员用户
     * @return
     */
    @GetMapping("/findAllOpt")
    public JsonModel findListAll(){
        List<SysOperator> list = sysOperatorService.findAll();
        return JsonModel.dataResult(list.size(), list);
    }

    /**
     * 此接口为根据操作员Ids删除操作员数据
     * @param ids 操作员Ids
     * @return
     */
    @GetMapping("/delBatchOperator")
    public JsonModel delBatchOperator(@RequestParam String ids) {
        try {
            sysOperatorService.deleteBatchByIds(ids);
        } catch (Exception e) {
            logger.error("删除异常", e);
            return JsonModel.mkFaile(e.getMessage());
        }
        return JsonModel.mkSuccess("删除成功");
    }

    /**
     * 此接口为修改当前用户密码
     * @param password 操作员密码
     * @return
     */
    @GetMapping("/updatePassword")
    public JsonModel updatePassword(@RequestParam String password){
        ManagerUser managerUser = this.getManagerUser();
        if(managerUser != null) {
            sysOperatorService.updateByPassword(password,managerUser.getOptUserid());
            return JsonModel.mkSuccess("操作成功");
        }
        return JsonModel.mkFaile("操作失败：请登入后操作");
    }
    
    /**
     * @Title: createMnemonicCode
     * @Description: 根据员工姓名生成助记码<br>
     * @Parmaters: @param empName
     * @Parmaters: @return   
     * @Return: JsonModel
     * @Throws:
     * @Author:lijun
     * @CreateDate:2020年6月24日 上午11:12:13
     * @ModifyLog:2020年6月24日 上午11:12:13
     */
    @GetMapping("/createMnemonicCode")
    public JsonModel createMnemonicCode(@RequestParam String empName){
        return JsonModel.dataResult(StringUtil.getPinYin(empName));
    }

    /**
     * 查询机构单位下的操作员信息
     * @param queryBean
     * @return
     */
    @PostMapping("/findListByCompanyId")
    public JsonModel findListByCompanyId(@RequestBody QueryBean queryBean){
        Page<SysOperator> page = sysOperatorService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }
    
       @PostMapping("/findEmployeeForOperator")
        public JsonModel findEmployeeForOperator(@RequestBody QueryBean queryBean){
         //数据权限控制
           ManagerUser user = this.getManagerUser();
//            DataPermissionUtil.getInstance().getCompanyReadPermisssion(queryBean, user, "companyId");
//            DataPermissionUtil.getInstance().getDeptReadPermisssion(queryBean, user, "deptId");
            Page<SysEmployee> page = sysOperatorService.queryPageEmployeeForOperator(queryBean);
            return JsonModel.dataResult(page.getTotalElements(), page.getContent());
        }
}
