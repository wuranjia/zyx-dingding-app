package net.whxxykj.maya.app.ctrl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.whxxykj.maya.base.BaseConstant;
import net.whxxykj.maya.base.common.uitls.DataPermissionNewUtil;
import net.whxxykj.maya.base.entity.SysCompany;
import net.whxxykj.maya.base.entity.SysDataPerm;
import net.whxxykj.maya.base.service.SysCompanyBillService;
import net.whxxykj.maya.base.service.SysCompanyService;
import net.whxxykj.maya.base.service.SysDataPermService;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.entity.ManagerUser;
import net.whxxykj.maya.common.exception.BaseException;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.ExcelUtilNew;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.common.util.StringUtil;

@RestController
@RequestMapping("/mobile/sysm/cpmpany")
public class MobileCompanyCtrl extends BaseCtrl<SysCompanyService, SysCompany>{

    @Autowired
    private SysCompanyService sysCompanyService;
    @Autowired
    private SysDataPermService sysDataPermService;
    @Autowired
    private SysCompanyBillService sysCompanyBillService;

    /**
     * 此接口为查询单位列表
     * @param queryBean 通用查询对象
     * @return
     */
    @PostMapping("/findListAll")
    public JsonModel findListAll(@RequestBody QueryBean queryBean){
        ManagerUser user = this.getManagerUser();
        //系统管理员能看到所有的数据，非系统管理员只能看到自己公司的数据
        queryBean.setDirStr("asc");
        queryBean.setSortStr("id");
        
//        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_FPXG, queryBean);
//        DataPermissionUtil.getInstance().getCompanyReadPermisssion(queryBean, user, "id");
        String companyId = user.getCompanyId();
        Map<String,Object> searchFileds = queryBean.getSearchFileds();
        List<String> companyIds = new ArrayList<String>();
        if(searchFileds.containsKey("id_in")) {
            companyIds.addAll((List<String>)searchFileds.getOrDefault("id_in", new ArrayList<String>()));
            companyIds.add(companyId);
            queryBean.defalutParam("id_in", companyIds);
        }
        Page<SysCompany> page = sysCompanyService.findListAll(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }
    
    
    @PostMapping("/findCompany")
    public JsonModel findCompany(@RequestBody QueryBean queryBean) {
        ManagerUser user = this.getManagerUser() ;
        queryBean.setDirStr("asc");
        queryBean.setSortStr("coCode");
//        DataPermissionUtil.getInstance().getCompanyReadPermisssion(queryBean, user, "id");
        Page<SysCompany> page = sysCompanyService.findListAll(queryBean);;
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }
    
    @PostMapping("/findCoAbbreviatePage")
    public JsonModel findCoAbbreviatePage(@RequestBody QueryBean queryBean) {
        ManagerUser user = this.getManagerUser() ;
//        DataPermissionUtil.getInstance().getCompanyReadPermisssion(queryBean, user, "id");
        Page<Map<String, String>> page = sysCompanyService.findCoAbbreviatePage(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }   

    /**
     * 此接口为新增单位信息
     * @param sysCompany 单位对象
     * @return
     */
    @PostMapping("/addSysCompany")
    public JsonModel addSysCompany(@RequestBody  SysCompany sysCompany){
        sysCompanyService.addSysCompany(sysCompany);
        return JsonModel.mkSuccess("保存成功");
    }

    /**
     * 此接口为编辑单位信息
     * @param sysCompany 单位对象
     * @return
     */
    
    @PostMapping("/editSysCompany")
    public JsonModel editSysCompany(@RequestBody  SysCompany sysCompany){
        sysCompanyService.editSysCompany(sysCompany);
        return JsonModel.mkSuccess("保存成功");
    }
    
    /*
    
    @GetMapping("/delSysCompany")
    public JsonModel delSysCompany(@RequestParam String id){
        try {
            sysCompanyService.delSysCompany(id);
        } catch (Exception e) {
            return JsonModel.mkFaile("删除失败");
        }
        return JsonModel.mkSuccess("删除成功");
    }*/

    /**
     * 此接口为根据单位Id查询单位对象信息
     * @param id 单位ID
     * @return
     */
    
    
    @GetMapping("/findById")
    public JsonModel findById(@RequestParam String id){
        SysCompany sysCompany = sysCompanyService.findById(id);
        return JsonModel.dataResult(sysCompany);
    }

    /**
     * 此接口为查询所有单位记录
     * @return
     */
    
    @GetMapping("/findByType")
    public JsonModel findByType(){
        /**
         * 超级管理员可以给所有单位新增，非超级管理员只能给自己单位新增
         */
        ManagerUser user = this.getManagerUser();
        Integer optIdentity  = user.getOptIdentity();
        List<SysCompany> list = null ;
        if(Integer.valueOf(2).compareTo(optIdentity) != 0) {
            list = new LinkedList<SysCompany>();
            list.add(sysCompanyService.findById(user.getCompanyId()));
        }else {
            list = sysCompanyService.findByType();
        }
        return JsonModel.dataResult(list.size(), list);
    }

    /**
     * 此接口为删除单位信息
     * @param ids 单位Id
     * @return
     */
    
    
    @GetMapping("/delBatchCompany")
    public JsonModel delBatchCompany(String ids) {
        try {
            String msg = sysCompanyService.deleteSysCompanys(ids);
            if (msg != null) {
                return JsonModel.mkFaile(msg);
            }
            return JsonModel.mkSuccess("删除成功");
        } catch (Exception e) {
            logger.error("删除异常", e);
            return JsonModel.mkFaile(e.getMessage());
        }
    }
    
    @GetMapping(value = "/getCompanyRela")
    public  JsonModel getCompanyRela() {
        return JsonModel.dataResult(sysCompanyService.queryCompanyRela());
    }
    
    @GetMapping(value = "/findSelectList")
    public  JsonModel findSelectList(String id) {
        return JsonModel.dataResult(sysCompanyService.findSelectList(id));
    }
    
    @GetMapping(value = "/getCompanyRelaLegal")
    public  JsonModel getCompanyRelaLegal() {
        return JsonModel.dataResult(sysCompanyService.queryCompanyRelaLegal());
    }
    
    @GetMapping(value = "/updateCoParten")
    public  JsonModel updateCoParten(String id,String pid) {
        sysCompanyService.editCoParten(id,pid);
        return JsonModel.mkSuccess();
    }

    /**
     * 此接口为查询所有公司列表
     * @return
     */
    
    @GetMapping("/findCompanyList")
    public JsonModel findCompanyList(){
        List<SysCompany> list = sysCompanyService.findAll();
        return JsonModel.dataResult(list);
    }
    
      /**
       * 上级管理单位 本身和下级被排除
       * @param queryBean
       * @return
       */
     @PostMapping("/findSubCompanyList")
    public JsonModel findSubCompanyList(@RequestBody QueryBean queryBean) {
        Map<String,Object> map = queryBean.getSearchFileds();
        if(map.containsKey("id_eq")) {
            String id = (String) map.get("id_eq");
            Set<String> ids = this.sysCompanyService.findChildCompanyIdsByCoParten(id);
            if(!ids.isEmpty()) {
                map.put("id_notin", ids);
            }
            map.remove("id_eq");
        }
        Page<SysCompany> page = sysCompanyService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }
    
    
    /**
     * 获取上级法人单位，本级及下级需排除
     * @param queryBean
     * @return
     */
     @PostMapping("/findSubLegalList")
    public JsonModel findSubLegalList(@RequestBody QueryBean queryBean) {
        Map<String,Object> map = queryBean.getSearchFileds();
        if(map.containsKey("id_eq")) {
            String id = (String) map.get("id_eq");
            Set<String> ids = this.sysCompanyService.findIdsBySupLegalId(id);
            map.put("id_notin", ids);
            map.remove("id_eq");
        }
        Page<SysCompany> page = sysCompanyService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }
    
    /**
     * @Title: findChildCompanyList
     * @Description: 获取下级法人或管理单位，上级需排除<br>
     * @Parmaters: @param queryBean
     * @Return: JsonModel
     * @Author:lijun
     * @CreateDate:2020年6月19日 下午5:13:42
     * @ModifyLog:2020年6月19日 下午5:13:42
     */
    @PostMapping("/findChildCompanyList")
    public JsonModel findChildCompanyList(@RequestBody QueryBean queryBean) {
        Set<String> idSet = sysCompanyService.findChildCompanyList();
        return JsonModel.dataResult(idSet.size(),idSet);
    }

    @PostMapping("/expCompany")
    public void expCompany(@RequestBody QueryBean queryBean, HttpServletRequest request, HttpServletResponse response)throws Exception {
        Page<SysCompany> page = sysCompanyService.queryPageList(queryBean);
        if(null != page) {
            for(SysCompany co:page.getContent()) {
                if(null != co.getCoType() && 1== co.getCoType()) {
                    co.setCoTypeStr("个人");
                }else if(null != co.getCoType() && 0== co.getCoType()) {
                    co.setCoTypeStr("单位");
                }
                
                if(null != co.getCoState() && 1== co.getCoState()) {
                    co.setCoStateStr("启用");
                }else if(null != co.getCoState() && 0== co.getCoState()) {
                    co.setCoStateStr("停用");
                }
                
                if(null != co.getCoSource() && 1== co.getCoSource()) {
                    co.setCoSourceStr("注册");
                }else if(null != co.getCoSource() && 2== co.getCoSource()) {
                    co.setCoSourceStr("手工新增");
                }
                
            }
        }
       
        String []headerField = {"coName","coAbbreviate","coMnemcode","coTypeStr","coCorporation","coTel","coStateStr","coSourceStr"};
        String []headerName = {"机构名称","机构简称","助记码","机构类型","法人","电话","状态","来源 "};
        String excelName =  "机构设置";
        ExcelUtilNew.bindExcelNew(request, response,excelName, headerField, headerName, page.getContent(), true);
    }
    
    
    @PostMapping("/findListAll1")
    public JsonModel findListAll1(@RequestBody QueryBean queryBean){
        Map<String, Object> map = queryBean.getSearchFileds();
        String operatorId = "";
        if(null != map.get("operatorId_eq")) {
            operatorId =  map.get("operatorId_eq").toString();
            map.remove("operatorId_eq");
        }
        if(StringUtil.isEmpty(operatorId)) {
            throw new BaseException("请选择一条数据");
        }
        //系统管理员能看到所有的数据，非系统管理员只能看到自己公司的数据
        queryBean.setDirStr("asc");
        queryBean.setSortStr("coName");
        ManagerUser user = this.getManagerUser() ;
        Integer optIdentity  = user.getOptIdentity();
        Page<SysCompany> page = null;
        if(Integer.valueOf(2).compareTo(optIdentity) != 0) {
            // 非系统管理员展示当前用户本机构或下属机构列表
            page = sysCompanyService.findChildCompanyList(queryBean);
        }else {
            page = sysCompanyService.findListAll(queryBean);
        }
        if(null != page) {
            StringBuffer companyIds = new StringBuffer();
            List<SysDataPerm> allot = sysDataPermService.findByEntIdAndAllot(operatorId,"1");
//            allot.forEach(dataPerm -> {
//                companyIds.append(dataPerm.getCompanyId()).append(",");
//            });
            List<SysCompany> list = page.getContent();
            if(StringUtil.isNotEmpty(companyIds)) {
                for(SysCompany company:list) {
                    if(companyIds.indexOf(company.getId()) != -1) {
                        company.setDataPermCheck(true);
                    }else {
                        company.setDataPermCheck(false);
                    }
                }
            }
        }
    
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }
    
    @PostMapping("/findSubLegalList1")
    public JsonModel findSubLegalList1(@RequestBody QueryBean queryBean) {
        ManagerUser user = this.getManagerUser();
//        DataPermissionUtil.getInstance().getCompanyReadPermisssion(queryBean, user, "id");
        Page<SysCompany> page = sysCompanyService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }
    
    /**
     * 查询所有直管单位
     * @param queryBean
     * @return
     */
    @PostMapping("/findChildCompanyByCoParten")
    public JsonModel findChildCompanyByCoParten(@RequestBody QueryBean queryBean) {
        ManagerUser user = this.getManagerUser();
        queryBean.defalutParam("id|coParten_eq", user.getCompanyId());
        Page<SysCompany> page= sysCompanyService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }
    
    /**
     * 查询所有单位 (包含直管项目和下级所有项目)
     * @param queryBean
     * @return
     */
    @PostMapping("/findAllChildCompany")
    public JsonModel findChildCompanyByCoPartenAndSupLegal(@RequestBody QueryBean queryBean) {
        ManagerUser user = this.getManagerUser();
        queryBean.defalutParam("coPartenCode|coSupLegalCode_like", user.getCompanyId());
        Page<SysCompany> page= sysCompanyService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }
    
    @GetMapping("/test")
    public JsonModel test(@RequestParam String companyId) {
        sysCompanyBillService.addSysCompanyBillBatch(companyId);
        return JsonModel.mkSuccess();
    }
}
