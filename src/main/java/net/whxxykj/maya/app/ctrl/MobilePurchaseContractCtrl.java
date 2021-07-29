package net.whxxykj.maya.app.ctrl;

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
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.trade.entity.PurchaseContract;
import net.whxxykj.maya.trade.entity.VPurchaseContractDetailReport;
import net.whxxykj.maya.trade.service.PurchaseContractService;
import net.whxxykj.maya.trade.service.VPurchaseContractDetailReportService;

@RestController
@RequestMapping(value = "/mobile/tradem/purchaseContract")
public class MobilePurchaseContractCtrl extends BaseCtrl<PurchaseContractService, PurchaseContract> {
    
    @Autowired
    private VPurchaseContractDetailReportService  vPurchaseContractDetailReportService;
    @Autowired
    private PurchaseContractService purchaseContractService;
    
    @Override
    public JsonModel add(@RequestBody PurchaseContract mod) {
      PurchaseContract newMod = purchaseContractService.add(mod);
      purchaseContractService.addUploadFile(newMod.getId(),"wscght");
      return JsonModel.dataResult(newMod, "新增成功");
    }
    
    @GetMapping("/updateSubmit")
    public JsonModel updateSubmit(@RequestParam String id, @RequestParam String submit) {
        purchaseContractService.updateSubmit(id, submit);
        return JsonModel.dataResult("提交成功");
    }
    
    @Override
    public JsonModel findList(@RequestBody QueryBean queryBean) {
       //查询权限
       DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_HTXG, queryBean);
       return super.findList(queryBean);
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
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_HTXG, queryBean);//数据权限
        queryBean.defalutParam("dataBillstate_eq", BaseConstant.DataBillState.EFFECT);//查询已生效的
//        queryBean.defalutParam("saleContract_null", ""); //查询销售合同号为空的。
        queryBean.defalutParam("goodsWeight", "sql:goodsWeight >= contractDetailEweight");
        Page<VPurchaseContractDetailReport> page = vPurchaseContractDetailReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    
    /**
     * 
     * @Title: findDetailReportList   
     * @Description: 采购合同明细分页查询   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findDetailReport/list")
    public JsonModel findDetailReportList(@RequestBody QueryBean queryBean) {
        //查询权限
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_HTXG, queryBean);
        Page<VPurchaseContractDetailReport> page = vPurchaseContractDetailReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    /**
     * 
     * @Title: findDetailReportList   
     * @Description: 采购合同汇总分页查询   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findSumReport/list")
    public JsonModel findSumReport(@RequestBody QueryBean queryBean) {
        //查询权限
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_HTXG, queryBean);
//        DataPermissionUtil.getInstance().getReadPermisssion(queryBean, this.getManagerUser());
        Page<VPurchaseContractDetailReport> page = vPurchaseContractDetailReportService.querySumPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    
    @GetMapping("/getBillCode")
    public JsonModel getBillCode(@RequestParam String billTypeCode,@RequestParam String companyCode) {
        return JsonModel.dataResult(purchaseContractService.getBillCode(billTypeCode,companyCode));
    }
}
