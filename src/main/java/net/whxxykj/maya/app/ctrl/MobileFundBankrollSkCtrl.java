package net.whxxykj.maya.app.ctrl;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
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
import net.whxxykj.maya.trade.entity.FundBankroll;
import net.whxxykj.maya.trade.entity.VFundBankrollDetailReport;
import net.whxxykj.maya.trade.entity.VFundGatheringLead;
import net.whxxykj.maya.trade.service.FundBankrollService;
import net.whxxykj.maya.trade.service.VFundBankrollDetailReportService;
import net.whxxykj.maya.trade.service.VFundGatheringLeadService;

@RestController
@RequestMapping("/mobile/tradem/fundBankrollSk")
public class MobileFundBankrollSkCtrl extends BaseCtrl<FundBankrollService, FundBankroll> {
    
    @Autowired
    private FundBankrollService fundBankrollService;

    @Autowired
    private VFundGatheringLeadService vFundGatheringLeadService;
    
    @Autowired
    private VFundBankrollDetailReportService vFundBankrollDetailReportService;
    @Override
    public JsonModel add(@RequestBody FundBankroll mod) {
        mod.setBilltypeCode(BaseConstant.BillTypeCode.SK);
        return super.add(mod);
    }
    
    @Override
    public JsonModel edit(@RequestBody FundBankroll mod) {
        mod.setBilltypeCode(BaseConstant.BillTypeCode.SK);
        return super.edit(mod);
    }
    @Override
    public JsonModel findList(@RequestBody QueryBean queryBean) {
        //查询权限
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_JSXG, queryBean);
        queryBean.defalutParam("billtypeCode_eq", BaseConstant.BillTypeCode.SK);
        return super.findList(queryBean);
    }
    

    @GetMapping("/updateSubmit")
    public JsonModel updateSubmit(@RequestParam String id, @RequestParam String submit) {
        fundBankrollService.updateSubmit(id, submit);
        return JsonModel.dataResult("提交成功");
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
    @PostMapping("/findQuoteList")
    public JsonModel findQuoteList(@RequestBody QueryBean queryBean) {
        Map<String, Object> searchFileds  = queryBean.getSearchFileds();
        Integer overdueDaysGt = MapUtils.getInteger(searchFileds, "overdueDays_gt", 0);
        if(overdueDaysGt.equals(0)) {
            searchFileds.remove("overdueDays_gt");
            queryBean.defalutParam("overdueDays_eq", 0);
        }
        //查询权限
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_JSXG, queryBean);
        Page<VFundGatheringLead> page = vFundGatheringLeadService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    
    /**
     * 
     * @Title: findDetailReportList   
     * @Description: 明细表分页查询   
     * @param: @return
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findDetailReport/list")
    public JsonModel findDetailReportList(@RequestBody QueryBean queryBean) {
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_JSXG, queryBean);
        queryBean.defalutParam("billtypeCode_eq", BaseConstant.BillTypeCode.SK);
        Page<VFundBankrollDetailReport> page = vFundBankrollDetailReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
}
