package net.whxxykj.maya.app.ctrl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.whxxykj.maya.base.BaseConstant;
import net.whxxykj.maya.base.common.uitls.DataPermissionNewUtil;
import net.whxxykj.maya.base.service.SysBillSettingService;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.common.util.StringUtil;
import net.whxxykj.maya.trade.entity.VWarehouseSbillDetailReport;
import net.whxxykj.maya.trade.entity.WarehouseSbill;
import net.whxxykj.maya.trade.service.VWarehouseSbillDetailReportService;
import net.whxxykj.maya.trade.service.WarehouseSbillDetailService;
import net.whxxykj.maya.trade.service.WarehouseSbillService;

@RestController
@RequestMapping(value = "/mobile/tradem/warehouseSbill")
public class MobileWarehouseSbillCtrl extends BaseCtrl<WarehouseSbillService, WarehouseSbill> {

    @Autowired
    private WarehouseSbillService warehouseSbillService;
    
    @Autowired
    private WarehouseSbillDetailService warehouseSbillDetailService;
    
    @Autowired
    private VWarehouseSbillDetailReportService vWarehouseSbillDetailReportService;
    
    @Autowired
    private SysBillSettingService billSettingService;
    
    @Override
    public JsonModel add(@RequestBody WarehouseSbill mod) {
        mod.setBilltypeCode(BaseConstant.BillTypeCode.XHTD);
        return super.add(mod);
    }
    
    @Override
    public JsonModel findList(@RequestBody QueryBean queryBean) {
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_XSXG, queryBean);
        Map<String, Object>  searchFileds =  queryBean.getSearchFileds();
        searchFileds.put("billtypeCode_eq", BaseConstant.BillTypeCode.XHTD);
        return super.findList(queryBean);
    }
    

    @GetMapping("/updateSubmit")
    public JsonModel updateSubmit(@RequestParam String id, @RequestParam String submit) {
        warehouseSbillService.updateSubmit(id, submit);
        return JsonModel.dataResult("提交成功");
    }
    
    /**
     * @Title: findAllList   
     * @Description:查询所有提单
     * @param: @param queryBean
     * @param: @return
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findAllList/list")
    @ResponseBody
    public JsonModel findAllList(@RequestBody QueryBean queryBean) {
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_XSXG, queryBean);
        return super.findList(queryBean);
    }
    
    /**
     * 
     * @Title: findDetailReportList   
     * @Description: 提单明细分页查询   
     * @param: @return
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findDetailReport/list")
    public JsonModel findDetailReportList(@RequestBody QueryBean queryBean) {
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_XSXG, queryBean);
        Page<VWarehouseSbillDetailReport> page = vWarehouseSbillDetailReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    /**
     * 
     * @Title: findDetailReportList   
     * @Description: 提单汇总分页查询   
     * @param: @return
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findSumReport/list")
    public JsonModel findSumReportList(@RequestBody QueryBean queryBean) {
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_XSXG, queryBean);
        Page<VWarehouseSbillDetailReport> page = vWarehouseSbillDetailReportService.querySumPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    
    
    /**
     * 
     * @Title: findDetailStockQuoteList   
     * @Description: 待页面方法(现货)  
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findDetailStockQuoteList")
    public JsonModel findDetailStockQuoteList(@RequestBody QueryBean queryBean) {
        //数据权限
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_XSXG, queryBean);
        String billtypeCode = BaseConstant.BillTypeCode.XHTD;
        if(billSettingService.getBillVerify(billtypeCode)) { //启用审核
            Map<String, Object> searchFileds = queryBean.getSearchFileds();
            searchFileds.put("dataAudit_eq", BaseConstant.AuditStatus.YS); //查询已审核通过的
        }
        queryBean.defalutParam("billtypeCode_eq", billtypeCode); //查询现货提单
        queryBean.defalutParam("dataGoodsflag_ne", BaseConstant.DataGoodsflag.flag_1); //货齐的不查询
        Page<VWarehouseSbillDetailReport> page = vWarehouseSbillDetailReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    
    /**
     * 
     * @Title: findDetailContractQuoteList   
     * @Description: 待页面方法(配货)  
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findDetailContractQuoteList")
    public JsonModel findDetailQuoteList(@RequestBody QueryBean queryBean) {
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_XSXG, queryBean);
        String billtypeCode = BaseConstant.BillTypeCode.PHTD;
        if(billSettingService.getBillVerify(billtypeCode)) { //启用审核
            Map<String, Object> searchFileds = queryBean.getSearchFileds();
            searchFileds.put("dataAudit_eq", BaseConstant.AuditStatus.YS); //查询已审核通过的
        }
        queryBean.defalutParam("billtypeCode_eq", billtypeCode); //查询配货提单
        queryBean.defalutParam("dataGoodsflag_ne", BaseConstant.DataGoodsflag.flag_1); //货齐的不查询
        Page<VWarehouseSbillDetailReport> page = vWarehouseSbillDetailReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    
    /**
     * 
     * @Title: findDetailReturnQuoteList   
     * @Description: 退货入库待页面查询   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findDetailReturnQuoteList")
    public JsonModel findDetailReturnQuoteList(@RequestBody QueryBean queryBean) {
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_XSXG, queryBean);
        String billtypeCode = BaseConstant.BillTypeCode.TDTH;
        if(billSettingService.getBillVerify(billtypeCode)) { //启用审核
            Map<String, Object> searchFileds = queryBean.getSearchFileds();
            searchFileds.put("dataAudit_eq", BaseConstant.AuditStatus.YS); //查询已审核通过的
        }
        queryBean.defalutParam("billtypeCode_eq", billtypeCode); //查询退货提单
        queryBean.defalutParam("dataGoodsflag_ne", BaseConstant.DataGoodsflag.flag_1);
        //searchFileds.put("sbillDetailOkweight_gt", 0); //实提重量大于0
        Page<VWarehouseSbillDetailReport> page = vWarehouseSbillDetailReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    
    /**
     * 
     * @Title: findDetailQuoteList   
     * @Description: 待页面方法   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findDetailReturnList")
    public JsonModel findDetailReturnList(@RequestBody QueryBean queryBean) {
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_XSXG, queryBean);
        Map<String, Object> searchFileds = queryBean.getSearchFileds();
        String billtypeCode =  (String)searchFileds.get("billtypeCode_eq");
        if(StringUtil.isEmpty(billtypeCode)) { //单据类型为空
            searchFileds.put("billtypeCode_ne", BaseConstant.BillTypeCode.TDTH); //查询除退货之外的 所有 提单(现货\配货)
        }
        searchFileds.put("sbillDetailOkweight_gt", 0); //实提重量大于0
        Page<VWarehouseSbillDetailReport> page = vWarehouseSbillDetailReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    /**
     * 
     * @Title: updateGoodsPrice   
     * @Description: 明细表  统设单价 
     * @param: @param batchs 被修改的明细批号
     * @param: @param type 修改方式   0:统一单价 1:统一加 2:统一减
     * @param: @param price 
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @GetMapping("/updateGoodsBinprice")
    public JsonModel updateGoodsBinprice(@RequestParam String batchs,@RequestParam String type,@RequestParam Double price) {
        int i = warehouseSbillDetailService.updateGoodsBinprice(batchs,type,price);
        return JsonModel.mkSuccess("修改成功");
    }
}
