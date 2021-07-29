package net.whxxykj.maya.app.ctrl;

import java.util.Map;

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
import net.whxxykj.maya.base.service.SysBillSettingService;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.trade.entity.VWarehouseDeliveryDetailReport;
import net.whxxykj.maya.trade.entity.WarehouseDelivery;
import net.whxxykj.maya.trade.service.VWarehouseDeliveryDetailReportService;
import net.whxxykj.maya.trade.service.WarehouseDeliveryService;

/**
 * 
 * @ClassName:  WarehouseDeliveryCtrl   
 * @Description: 配货实提  
 * @author: YEJUN
 * @date:   2021年4月19日 上午10:18:07      
 * @Copyright:
 */
@RestController
@RequestMapping(value = "/mobile/tradem/warehouseDelivery/contract")
public  class MobileWarehouseDeliveryContractCtrl extends BaseCtrl<WarehouseDeliveryService, WarehouseDelivery> {
    
    @Autowired
    private WarehouseDeliveryService warehouseDeliveryService;
    
    @Autowired
    private VWarehouseDeliveryDetailReportService vWarehouseDeliveryDetailReportService;
    
    @Autowired
    private SysBillSettingService billSettingService;
    
    private String billtypeCode = BaseConstant.BillTypeCode.PHST;
    
    @Override
    public JsonModel findList(@RequestBody QueryBean queryBean) {
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_KCXG, queryBean);
        Map<String, Object>  searchFileds =  queryBean.getSearchFileds();
        searchFileds.put("billtypeCode_eq", this.getBilltypeCode());
        return super.findList(queryBean);
    }

    @GetMapping("/updateSubmit")
    public JsonModel updateSubmit(@RequestParam String id, @RequestParam String submit) {
        warehouseDeliveryService.updateSubmit(id, submit);
        return JsonModel.dataResult("提交成功");
    }
    
    @Override
    public JsonModel add(@RequestBody WarehouseDelivery mod) {
        mod.setBilltypeCode(billtypeCode);
        return super.add(mod);
    }
    
    /**
     * 
     * @Title: findDetailReportList   
     * @Description: 采购入库明细分页查询   
     * @param: @return
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findDetailReport/list")
    public JsonModel findDetailReportList(@RequestBody QueryBean queryBean) {
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_KCXG, queryBean);
        Page<VWarehouseDeliveryDetailReport> page = vWarehouseDeliveryDetailReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    
    /**
     * 
     * @Title: findDetailQuoteList   
     * @Description: 销售补差带页面引入   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findDetailQuote/list")
    public JsonModel findDetailQuoteList(@RequestBody QueryBean queryBean) {
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_KCXG, queryBean);
        String sqlXhtd = null;
        String sqlPhtd = null;
        String sqlXSth = null;
        if(billSettingService.getBillVerify(BaseConstant.BillTypeCode.XHTD)) { //启用审核
            sqlXhtd =  "(billtypeCode = '"+BaseConstant.BillTypeCode.XHTD+"' and dataAudit = '1')";
        }else {
            sqlXhtd =  "(billtypeCode = '"+BaseConstant.BillTypeCode.XHTD+"')";
        }
        if(billSettingService.getBillVerify(BaseConstant.BillTypeCode.PHTD)) { //启用审核
            sqlPhtd =  "(billtypeCode = '"+BaseConstant.BillTypeCode.PHTD+"' and dataAudit = '1')";
        }else {
            sqlPhtd =  "(billtypeCode = '"+BaseConstant.BillTypeCode.PHTD+"')";
        }
        if(billSettingService.getBillVerify(BaseConstant.BillTypeCode.XSTH)) { //启用审核
            sqlXSth =  "(billtypeCode = '"+BaseConstant.BillTypeCode.XSTH+"' and dataAudit = '1')";
        }else {
            sqlXSth =  "(billtypeCode = '"+BaseConstant.BillTypeCode.XSTH+"')";
        }
        
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("( ").append(sqlXhtd).append(" or ").append(sqlPhtd).append(" or ").append(sqlXSth).append(")");
        queryBean.defalutParam("wheresql", "sql:"+sqlBuilder.toString());
        
        Page<VWarehouseDeliveryDetailReport> page = vWarehouseDeliveryDetailReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }

    public String getBilltypeCode() {
        return billtypeCode;
    }

    public void setBilltypeCode(String billtypeCode) {
        this.billtypeCode = billtypeCode;
    }
    
}
