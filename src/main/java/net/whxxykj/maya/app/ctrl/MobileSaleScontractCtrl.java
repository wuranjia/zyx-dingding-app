package net.whxxykj.maya.app.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.whxxykj.maya.app.service.MobileSaleScontractService;
import net.whxxykj.maya.base.BaseConstant;
import net.whxxykj.maya.base.common.uitls.DataPermissionNewUtil;
import net.whxxykj.maya.base.entity.SysCompany;
import net.whxxykj.maya.base.entity.SysPrintSetting;
import net.whxxykj.maya.base.service.SysCompanyService;
import net.whxxykj.maya.base.service.SysPrintSettingService;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.common.util.StringUtil;
import net.whxxykj.maya.trade.entity.SaleScontract;
import net.whxxykj.maya.trade.entity.SaleScontractDetail;
import net.whxxykj.maya.trade.entity.VSaleScontractDetailReport;
import net.whxxykj.maya.trade.service.SaleScontractDetailService;
import net.whxxykj.maya.trade.service.SaleScontractService;
import net.whxxykj.maya.trade.service.VSaleScontractDetailReportService;

@RestController
@RequestMapping("/mobile/tradem/contractSale")
public class MobileSaleScontractCtrl extends BaseCtrl<SaleScontractService, SaleScontract> {
    
    @Autowired
    private SaleScontractService saleScontractService;
    @Autowired
    private SaleScontractDetailService saleScontractDetailService; 
    @Autowired
    private VSaleScontractDetailReportService vSaleScontractDetailReportService;
    @Autowired
    private SysCompanyService sysCompanyService;
    @Autowired
    private MobileSaleScontractService mobileSaleScontractService;
    @Autowired
    private SysPrintSettingService sysPrintSettingService;
    
    @Override
    public JsonModel add(@RequestBody SaleScontract mod) {
        SaleScontract newMod = saleScontractService.add(mod);
        SysCompany company = sysCompanyService.getOne(mod.getCompanyCode());
        if("1".equals(company.getIsEsign())) {//启用电子合同是才上传文件
           String printTemplate = newMod.getPrintTemplate(); 
           if(StringUtil.isNotEmpty(printTemplate)) {
               SysPrintSetting sysPrintSetting = sysPrintSettingService.findByPrintName(printTemplate);
               if(null != sysPrintSetting) {
                   String datasBalcorpname = newMod.getDatasBalcorpname();
                   String scontractInmoney = StringUtil.showNumView(newMod.getScontractInmoney());
                   String scontractBillcode = newMod.getScontractBillcode();
                   String fileName = datasBalcorpname+"-"+scontractInmoney+"-"+scontractBillcode;
                   saleScontractService.addUploadFile(newMod.getId(), sysPrintSetting.getPrintUrl(),fileName);   
               }
           } 
        }
        return JsonModel.dataResult(mod, "新增成功");
    }
    
    @GetMapping("/updateSubmit")
    public JsonModel updateSubmit(@RequestParam String id, @RequestParam String submit) {
        saleScontractService.updateSubmit(id, submit);
        return JsonModel.dataResult("提交成功");
    }
    
    @Override
    public JsonModel findList(@RequestBody QueryBean queryBean) {
        //查询权限
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_HTXG, queryBean);
        Page<SaleScontract>  page =  mobileSaleScontractService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    
    
    /** 
    * @Description: 查询销售合同明细(采购计划引用使用)
    * @Param: [queryBean]
    * @returns: net.whxxykj.maya.common.util.JsonModel
    * @Author: lq
    * @Date: 2021/3/5 10:09
    */
    @PostMapping(value = "/findContractSaleDetailList")
    @ResponseBody
    public JsonModel findContractSaleDetailList(@RequestBody QueryBean queryBean) {
        //查询权限
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_HTXG, queryBean);
        Page<SaleScontractDetail> page = saleScontractDetailService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }

    /**
     * 
     * @Title: findDetailQuoteList   
     * @Description: 引用页面查询方法   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findDetailQuoteList")
    public JsonModel findDetailQuoteList(@RequestBody QueryBean queryBean) {
        //查询权限
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_HTXG, queryBean);
        queryBean.defalutParam("dataBillstate_eq", "1");
        queryBean.defalutParam("dataGoodsflag_eq", "0");
        Page<VSaleScontractDetailReport> page = vSaleScontractDetailReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    
    /**
     * 
     * @Title: findDetailReportList   
     * @Description: 销售合同明细分页查询   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findDetailReport/list")
    public JsonModel findDetailReportList(@RequestBody QueryBean queryBean) {
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_HTXG, queryBean);
        Page<VSaleScontractDetailReport> page = vSaleScontractDetailReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    
    /**
     * 
     * @Title: findDetailReportList   
     * @Description: 销售合同汇总分页查询   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findSumReport/list")
    public JsonModel findSumReportList(@RequestBody QueryBean queryBean) {
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_HTXG, queryBean);
//        DataPermissionUtil.getInstance().getReadPermisssion(queryBean, this.getManagerUser());
        Page<VSaleScontractDetailReport> page = vSaleScontractDetailReportService.querySumPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    
    @GetMapping("/checkState")
    public JsonModel checkState(@RequestParam String id) {
        boolean state = saleScontractService.checkState(id);
        if(state) {
            return JsonModel.mkSuccess();
        }
        return JsonModel.mkFaile("合同已完成");
    }
    
    @GetMapping("/getBillCode")
    public JsonModel getBillCode(@RequestParam String billTypeCode,@RequestParam String companyCode) {
        return JsonModel.dataResult(saleScontractService.getBillCode(billTypeCode,companyCode));
    }
    /**
     * 
     * @Title: batchFund   
     * @Description: 实提完成  (完成后  销售合同不能再做提单(调完成方法)  实提单不能删除)
     * @param: @param detailBatchs
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @GetMapping(value = "/batchOk")
    public JsonModel batchOk(@RequestParam String detailBatchs) {
        saleScontractService.editBatchOk(detailBatchs);
        return JsonModel.mkSuccess("操作实提完成成功"); 
    }
}

