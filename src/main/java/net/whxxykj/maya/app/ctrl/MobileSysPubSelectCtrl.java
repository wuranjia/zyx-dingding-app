package net.whxxykj.maya.app.ctrl;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.whxxykj.maya.base.entity.SysBillSetting;
import net.whxxykj.maya.base.entity.SysBpmnSetting;
import net.whxxykj.maya.base.entity.SysButton;
import net.whxxykj.maya.base.entity.SysCode;
import net.whxxykj.maya.base.entity.SysCompany;
import net.whxxykj.maya.base.entity.SysCurrency;
import net.whxxykj.maya.base.entity.SysDept;
import net.whxxykj.maya.base.entity.SysEmployee;
import net.whxxykj.maya.base.entity.SysFeeitem;
import net.whxxykj.maya.base.entity.SysInneraccounts;
import net.whxxykj.maya.base.entity.SysMenu;
import net.whxxykj.maya.base.entity.SysMenuDir;
import net.whxxykj.maya.base.entity.SysOrg;
import net.whxxykj.maya.base.entity.SysOrgAccounts;
import net.whxxykj.maya.base.entity.SysOrgBank;
import net.whxxykj.maya.base.entity.SysOrgLinkman;
import net.whxxykj.maya.base.entity.SysPermAllot;
import net.whxxykj.maya.base.entity.SysPntree;
import net.whxxykj.maya.base.entity.SysProject;
import net.whxxykj.maya.base.entity.SysSubSite;
import net.whxxykj.maya.base.entity.SysTermcode;
import net.whxxykj.maya.base.entity.SysWarehouse;
import net.whxxykj.maya.base.entity.SysWareplace;
import net.whxxykj.maya.base.service.SysBillSettingService;
import net.whxxykj.maya.base.service.SysBpmnSettingService;
import net.whxxykj.maya.base.service.SysButtonService;
import net.whxxykj.maya.base.service.SysCodeService;
import net.whxxykj.maya.base.service.SysCompanyService;
import net.whxxykj.maya.base.service.SysCurrencyService;
import net.whxxykj.maya.base.service.SysDeptService;
import net.whxxykj.maya.base.service.SysEmployeeService;
import net.whxxykj.maya.base.service.SysFeeitemService;
import net.whxxykj.maya.base.service.SysGoodscodeService;
import net.whxxykj.maya.base.service.SysInneraccountsService;
import net.whxxykj.maya.base.service.SysMenuService;
import net.whxxykj.maya.base.service.SysOrgAccountsService;
import net.whxxykj.maya.base.service.SysOrgBankService;
import net.whxxykj.maya.base.service.SysOrgLinkmanService;
import net.whxxykj.maya.base.service.SysOrgService;
import net.whxxykj.maya.base.service.SysPartsnameService;
import net.whxxykj.maya.base.service.SysPntreeService;
import net.whxxykj.maya.base.service.SysProCoService;
import net.whxxykj.maya.base.service.SysProjectService;
import net.whxxykj.maya.base.service.SysSubSiteService;
import net.whxxykj.maya.base.service.SysTermcodeService;
import net.whxxykj.maya.base.service.SysWarehouseService;
import net.whxxykj.maya.base.service.SysWareplaceService;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.entity.ManagerUser;
import net.whxxykj.maya.common.exception.BaseException;
import net.whxxykj.maya.common.exception.ExcelException;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.JsonModel;

@RestController
@RequestMapping("/mobile/sysm/pubselect")
public class MobileSysPubSelectCtrl extends BaseCtrl<SysCodeService, SysCode> {

    @Autowired
    private SysCodeService sysCodeService;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private SysButtonService sysButtonService;

    @Autowired
    private SysCompanyService sysCompanyService;

    @Autowired
    private SysDeptService sysDeptService;

    @Autowired
    private SysSubSiteService sysSubSiteService;

    @Autowired
    private SysProjectService sysProjectService;

    @Autowired
    private SysProCoService sysProCoService;

    @Autowired
    private SysEmployeeService sysEmployeeService;

    @Autowired
    private SysOrgService sysOrgService;

    @Autowired
    private SysOrgAccountsService sysOrgAccountsService;

    @Autowired
    private SysOrgBankService sysOrgBankService;

    @Autowired
    private SysOrgLinkmanService sysOrgLinkmanService;

    @Autowired
    private SysWarehouseService sysWarehouseService;

    @Autowired
    private SysGoodscodeService sysGoodscodeService;

    @Autowired
    private SysInneraccountsService sysInneraccountsService;

    @Autowired
    private SysFeeitemService sysFeeitemService;

    @Autowired
    private SysPartsnameService sysPartsnameService;

    private SysBillSettingService sysBillSettingService;

    @Autowired
    private SysPntreeService sysPntreeService;

    @Autowired
    private SysWareplaceService sysWareplaceService;

    @Autowired
    private SysTermcodeService sysTermcodeService;

    @Autowired
    private SysBpmnSettingService sysBpmnSettingService;
    @Autowired
    private SysCurrencyService sysCurrencyService;
    /**
     * 此接口为根据子系统Id查询菜单
     * 
     * @param subsiteId
     *            子系统ID
     * @return
     */
    @GetMapping("/loadMenu")
    public JsonModel loadMenu(@RequestParam String subsiteId) {
        List<SysMenuDir> list = sysMenuService.findMenuDir(subsiteId);
        return JsonModel.dataResult(list.size(), list);
    }

    @GetMapping("/loadMenu2")
    public JsonModel loadMenu2(@RequestParam String siteId) {
        List<SysSubSite> list = sysSubSiteService.findBySiteId(siteId);
        for (SysSubSite sysSubSite : list) {
            List<SysMenuDir> menuDirList = sysMenuService.findMenuDir(sysSubSite.getId());
            Collections.sort(menuDirList, new Comparator<SysMenuDir>() {
                @Override
                public int compare(SysMenuDir o1, SysMenuDir o2) {
                    return o2.getMenuDirState() - o1.getMenuDirState();
                }

            });
            sysSubSite.setSysMenuDirList(menuDirList);
        }
        return JsonModel.dataResult(list.size(), list);
    }

    /**
     * t通过目录Id 查询所对应的菜单
     * 
     * @param menuDirId
     * @return
     */
    @GetMapping("/findMenuByMenuDirId")
    public JsonModel findMenuByMenuDirId(@RequestParam String menuDirId) {
        List<SysMenu> sysMenus = sysMenuService.findMenuByMenuDirId(menuDirId);
        return JsonModel.dataResult(sysMenus);
    }

    // 根据多个常用代码类型查询
    @GetMapping("/getCodeByTypes")
    public JsonModel getCodeByTypes(String types) {
        Map<String, List<SysCode>> map = sysCodeService.findByTypesState(types, null);
        return JsonModel.dataResult(map);
    }

    /**
     * 此接口为根据站点ID和菜单ID查询按钮
     * 
     * @param subsiteId
     *            子系统ID
     * @param menuId
     *            菜单ID
     * @return
     */
    @GetMapping("/findButtonBySiteAndMenu")
    public JsonModel findButtonBySiteAndMenu(@RequestParam String subsiteId, @RequestParam String menuId) {
        List<SysButton> list = sysButtonService.findBySubsiteAndMenu(subsiteId, menuId);
        return JsonModel.dataResult(list.size(), list);
    }

    /**
     * 此接口为根据站点查询当前登录会员已拥有的权限
     * 
     * @param subsiteId
     *            子系统ID
     * @param menuId
     *            菜单ID
     * @return
     */
    @GetMapping("/findButtonQx")
    public JsonModel findButtonQx(@RequestParam String subsiteId, @RequestParam String menuId) {
        ManagerUser user = this.getManagerUser();
        if (user == null) {
            throw new BaseException("请重新登录");
        }
        Integer optIdentity = user.getOptIdentity();
        String companyId = user.getCompanyId();
        
        List<SysButton> list = null;
        if (Integer.valueOf(2).compareTo(optIdentity) == 0) {
            list = sysButtonService.findAllBySubsiteIdAndMenuId(subsiteId, menuId);
        } else {
            list = sysButtonService.findButtonQx(user.getId(), SysPermAllot.EntType.TYPE1, subsiteId, menuId);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            //查询是否配置审核
            SysBpmnSetting  sysBpmnSetting = sysBpmnSettingService.findBpmnByCompanyIdAndMenuId(companyId,menuId);
            if (sysBpmnSetting == null) {
                Iterator<SysButton> it = list.iterator();
                while (it.hasNext()) {
                    SysButton button = it.next();
                    Integer btnType = button.getBtnType();
                    if (btnType != null) {
                        if((btnType == 9 || btnType == 11 || btnType == 12) && sysBpmnSetting == null) {
                            it.remove();
                        }
                    }
                }
            }
        }
        return JsonModel.dataResult(list.size(), list);
    }

    /**
     * 此接口为获取系统时间接口
     * 
     * @return
     */

    @GetMapping("/getSysDateTime")
    public JsonModel getSysDateTime() {
        return JsonModel.dataResult(sysCodeService.getSysDate());
    }

    // 填报单位（项目部）-下拉翻页
    /**
     * @Title: findChildCompanyListByOpt
     * @Description: 根据操作员机构，展示下属外部项目部、内部项目部<br>
     * @Parmaters: @param queryBean
     * @Parmaters: @return
     * @Return: JsonModel
     * @Throws:
     * @Author:lijun
     * @CreateDate:2020年6月27日 上午11:59:14
     * @ModifyLog:2020年6月27日 上午11:59:14
     */
    @PostMapping("/findChildCompanyListByOpt")
    public JsonModel findChildCompanyListByOpt(@RequestBody QueryBean queryBean) {
        // 系统管理员能看到所有的数据，非系统管理员只能看到自己公司的数据
        queryBean.setDirStr("asc");
        queryBean.setSortStr("coCode");
        ManagerUser user = this.getManagerUser();
        Integer optIdentity = user.getOptIdentity();
        Page<SysCompany> page = null;
        if (Integer.valueOf(2).compareTo(optIdentity) != 0) {
            // 非系统管理员展示当前用户本机构或下属机构列表
            List<String> permCompanyList = user.getDataPermCompanyList();
            queryBean.defalutParam("id_in", permCompanyList);
        }
        page = sysCompanyService.findListAll(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 分页查询 工程-下拉翻页 （查询条件专用）
     * 
     * @param queryBean
     * @return
     * @throws ExcelException
     * @throws IOException
     */
    @PostMapping(value = "/findProjectListAll")
    @ResponseBody
    public JsonModel findProjectListAll(@RequestBody QueryBean queryBean) {
        queryBean.setSortStr("cDate");
        queryBean.setDirStr("desc");
        ManagerUser user = this.getManagerUser();
        Integer optIdentity = user.getOptIdentity();
        if (Integer.valueOf(2).compareTo(optIdentity) != 0) {
            // 非管理员查询已分配的数据权限的机构
            List<String> permCompanyList = user.getDataPermCompanyList();
            Set<String> proIds = sysProCoService.findByCoIds(permCompanyList).stream().map(x -> x.getProId())
                .collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(proIds)) {
                queryBean.defalutParam("id_in", proIds);
            } else {
                queryBean.defalutParam("id_in", "");
            }
        }
        Page<SysProject> page = sysProjectService.findProjectListAll(queryBean);
        return new JsonModel(page.getTotalElements(), page.getContent());
    }

    /**
     * @Title: findListAll
     * @Description: 查询操作员所在机构的下属所有机构<br>
     * @Parmaters: @param queryBean
     * @Return: JsonModel
     * @Throws:
     * @Author:lijun
     * @CreateDate:2020年6月27日 下午4:12:39
     * @ModifyLog:2020年6月27日 下午4:12:39
     */
    @PostMapping("/findChildCompanyListAll")
    public JsonModel findChildCompanyListAll(@RequestBody QueryBean queryBean) {
        // 系统管理员能看到所有的数据，非系统管理员只能看到自己公司的数据
        queryBean.setDirStr("asc");
        queryBean.setSortStr("coCode");
        ManagerUser user = this.getManagerUser();
        Integer optIdentity = user.getOptIdentity();
        Page<SysCompany> page = null;
        if (Integer.valueOf(2).compareTo(optIdentity) != 0) {
            // 非系统管理员展示当前用户本机构或下属机构列表
            page = sysCompanyService.findChildCompanyList(queryBean);
        } else {
            page = sysCompanyService.findListAll(queryBean);
        }
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 通过上下级关系（法人单位）查询 下级机构信息
     * 
     * @param queryBean
     * @return
     */
    @PostMapping("/findChildCompanyList")
    public JsonModel findChildCompanyList(@RequestBody QueryBean queryBean) {
        ManagerUser user = this.getManagerUser();
        queryBean.defalutParam("coSupLegalId_eq", user.getCompanyId());
        Page<SysCompany> page = sysCompanyService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: findCompanyList @Description: 机构分页查询 （含权限） @param: @param queryBean @param: @return @return:
     * JsonModel @throws
     */
    @PostMapping("/findCompanyList")
    public JsonModel findCompanyList(@RequestBody QueryBean queryBean) {
        // 查询权限
        ManagerUser user = this.getManagerUser();
//        DataPermissionUtil.getInstance().getCompanyReadPermisssion(queryBean, user, "id");
        Page<SysCompany> page = sysCompanyService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: findCompanyListNoPermission 
     * @Description: 机构分页查询 （无权限控制） 
     * @param: queryBean 
     * @return: JsonModel 
     * @throws
     */
    @PostMapping("/findCompanyListNoPermission")
    public JsonModel findCompanyListNoPermission(@RequestBody QueryBean queryBean) {
        Page<SysCompany> page = sysCompanyService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: findDeptListNoPermission   
     * @Description: 部门查询 无权限控制   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findDeptListNoPermission")
    public JsonModel findDeptListNoPermission(@RequestBody QueryBean queryBean) {
        Page<SysDept> page = sysDeptService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: findDeptList   
     * @Description: 部门分页查询 （含权限）   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findDeptList")
    public JsonModel findDeptList(@RequestBody QueryBean queryBean) {
        // 查询权限
        ManagerUser user = this.getManagerUser();
//        DataPermissionUtil.getInstance().getDeptReadPermisssion(queryBean, user, "id");
        Page<SysDept> page = sysDeptService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 职员下拉列表
     * 
     * @param queryBean
     *            通用查询对象
     * @return
     */
    @PostMapping("/findEmployeeList")
    public JsonModel findEmployeeList(@RequestBody QueryBean queryBean) {
        queryBean.setSortStr("id");
        queryBean.setDirStr("desc");
        queryBean.defalutParam("empWorking_eq", "1");
        // queryBean.defalutParam("empState_eq", 1);
        // 查询权限
        ManagerUser user = this.getManagerUser();
//        DataPermissionUtil.getInstance().getCompanyReadPermisssion(queryBean, user, "companyId");
//        DataPermissionUtil.getInstance().getDeptReadPermisssion(queryBean, user, "deptId");
        Page<SysEmployee> page = sysEmployeeService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 内部机构下拉列表
     * 
     * @param queryBean
     *            通用查询对象
     * @return
     */
    @PostMapping("/findInnerCompanyList")
    public JsonModel findInnerCompanyList(@RequestBody QueryBean queryBean) {
        ManagerUser user = this.getManagerUser();
        queryBean.defalutParam("coSupLegalCode_startlike", user.getCoSupLegalCode());
        Page<SysCompany> page = sysCompanyService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 上级单位列表
     * 
     * @param queryBean
     *            通用查询对象
     * @return
     */
    @PostMapping("/findOrgList")
    public JsonModel findOrgList(@RequestBody QueryBean queryBean) {
//        DataPermissionUtil.getInstance().getOrgCodeReadPermission(queryBean, this.getManagerUser(), "id");
        Page<SysOrg> page = sysOrgService.findCombo(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: findOrgAccountsList   
     * @Description: 往来单位账号查询   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findOrgAccountsList")
    public JsonModel findOrgAccountsList(@RequestBody QueryBean queryBean) {
        Page<SysOrgAccounts> page = sysOrgAccountsService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: findOrgBankList @Description: 往来单位银行 @param: @param queryBean @param: @return @return: JsonModel @throws
     */
    @PostMapping("/findOrgBankList")
    public JsonModel findOrgBankList(@RequestBody QueryBean queryBean) {
        Page<SysOrgBank> page = sysOrgBankService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: findOrgBankList 
     * @Description: 往来单位联系人 
     * @param: @param queryBean 
     * @param: @return 
     * @return: JsonModel 
     * @throws
     */
    @PostMapping("/findOrgLinkmanList")
    public JsonModel findsysOrgLinkmanList(@RequestBody QueryBean queryBean) {
        Page<SysOrgLinkman> page = sysOrgLinkmanService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: findTermsettingList 
     * @Description: 条款模板下拉选择 
     * @param: @param queryBean 
     * @param: @return 
     * @return:JsonModel 
     * @throws
     */
    @GetMapping("/findTermsettingList")
    public JsonModel findTermsettingList(@RequestParam String billtypeCode) {
        List<SysTermcode> list = sysTermcodeService.findByBilltypeCode(billtypeCode);
        return JsonModel.dataResult(list);
    }

    /**
     * 
     * @Title: findTermsettingList @Description: 条款模板下拉选择 @param: @param queryBean @param: @return @return:
     * JsonModel @throws
     */
    @GetMapping("/querySysTermsettingList")
    public JsonModel querySysTermsettingList(@RequestParam String billtypeCode, String termsettingCode) {
        List<SysTermcode> termcodeList = sysTermcodeService.findByBilltypeCode(billtypeCode);
        return JsonModel.dataResult(termcodeList);
    }

    /**
     * 
     * @Title: findTermsettingList @Description: 仓库下拉选择 @param: @param queryBean @param: @return @return:
     * JsonModel @throws
     */
    @PostMapping("/findWarehouseList")
    public JsonModel findWarehouseList(@RequestBody QueryBean queryBean) {
        // 查询权限
        ManagerUser user = this.getManagerUser();
//        DataPermissionUtil.getInstance().getWareReadPermisssion(queryBean, user);
        Page<SysWarehouse> page = sysWarehouseService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: findWarehouseListNoPermission @Description: 仓库下拉选择，无权限控制 @param: @param
     * queryBean @param: @return @return: JsonModel @throws
     */
    @PostMapping("/findWarehouseListNoPermission")
    public JsonModel findWarehouseListNoPermission(@RequestBody QueryBean queryBean) {
        Page<SysWarehouse> page = sysWarehouseService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: findGoodsMaterialList @Description: 牌号下拉选择 @param: @param queryBean @param: @return @return:
     * JsonModel @throws
     */
    @PostMapping("/findGoodsMaterialList")
    public JsonModel findGoodsMaterialList(@RequestBody QueryBean queryBean) {
        Page<Map<String, String>> page = sysGoodscodeService.queryGoodsMaterialList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: findInneraccountsList @Description: 查询机构付款账号 @param: @param queryBean @param: @return @return:
     * JsonModel @throws
     */
    @PostMapping("/findInneraccountsList")
    public JsonModel findInneraccountsList(@RequestBody QueryBean queryBean) {
        Page<SysInneraccounts> page = sysInneraccountsService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: findFeeitemList @Description: 查询费用项目 @param: @param queryBean @param: @return @return: JsonModel @throws
     */
    @PostMapping("/findFeeitemList")
    public JsonModel findFeeitemList(@RequestBody QueryBean queryBean) {
        Page<SysFeeitem> page = sysFeeitemService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: findPartsnameList   
     * @Description: 品名下拉选择   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
   @PostMapping("/findPartsnameList")
   public JsonModel findPartsnameList (@RequestBody QueryBean queryBean){
       Page<Map<String, String>> page = sysPartsnameService.queryPartsnameList(queryBean);
       return JsonModel.dataResult(page.getTotalElements(), page.getContent());
   }

    /**
     * 
     * @Title: findGoodsMaterialList @Description: 产地下拉选择 @param: @param queryBean @param: @return @return:
     * JsonModel @throws
     */
    @PostMapping("/findProductareaList")
    public JsonModel findProductareaList(@RequestBody QueryBean queryBean) {
        Page<Map<String, String>> page = sysGoodscodeService.findProductareaList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: findGoodsMaterialList @Description: 品名大类 类别下拉选择 @param: @param queryBean @param: @return @return:
     * JsonModel @throws
     */
    @PostMapping("/findSysPntreeList")
    public JsonModel findSysPntreeList(@RequestBody QueryBean queryBean) {
        // Page<SysPntree> page = sysPntreeService.queryPageList(queryBean);
        List<SysPntree> list = sysPntreeService.findByPntreeParentcode();
        return JsonModel.dataResult(list.size(), list);
    }
    /**
     * 
     * @Title: findGoodsMaterialList   
     * @Description: 品名大类  品名 牌号  厂家筛选
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findSysPntreeList1")
    public JsonModel findSysPntreeList1 (@RequestBody QueryBean queryBean){
        Page<Map<String, String>> page  = sysGoodscodeService.queryPntreeNameList(queryBean);
//      List<SysPntree> list = sysPntreeService.findByPntreeParentcode();
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    @PostMapping("/findSysWareplaceList")
    public JsonModel findSysWareplaceList(@RequestBody QueryBean queryBean) {
        queryBean.defalutParam("warehouseWareplace_eq", "1");// 查询启用库位的
        Page<SysWareplace> page = sysWareplaceService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 
     * @Title: getBillTypeCode @Description: 获取单据号生成模式（0手动输入，1自动生成） @param: @param type @param: @return @return:
     * JsonModel @throws
     */
    @GetMapping("/getBillCodeMode/{type}")
    public JsonModel getBillTypeCode(@PathVariable(value = "type") String type) {
        SysBillSetting setting = sysBillSettingService.findByBillTypeCode(type);
        return JsonModel.dataResult(setting.getBillCodeMode());
    }
    
    @PostMapping("/findSysCurrencyList")
    public JsonModel findSysCurrencyList(@RequestBody QueryBean queryBean) {
        Page<SysCurrency> page = sysCurrencyService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }
}
