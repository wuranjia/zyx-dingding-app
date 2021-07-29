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
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.trade.entity.FundBankroll;
import net.whxxykj.maya.trade.entity.VFundBankrollDetailReport;
import net.whxxykj.maya.trade.entity.VFundPayapplyDetailReport;
import net.whxxykj.maya.trade.service.FundBankrollService;
import net.whxxykj.maya.trade.service.VFundBankrollDetailReportService;
import net.whxxykj.maya.trade.service.VFundPayapplyDetailReportService;

@RestController
@RequestMapping("/mobile/tradem/fundBankroll")
public class MobileFundBankrollCtrl extends BaseCtrl<FundBankrollService, FundBankroll> {
    
    @Autowired
    private FundBankrollService fundBankrollService;

    @Autowired
    private VFundPayapplyDetailReportService vFundPayapplyDetailReportService;
    
    @Autowired
    private VFundBankrollDetailReportService vFundBankrollDetailReportService;
    @Override
    public JsonModel add(@RequestBody FundBankroll mod) {
        mod.setBilltypeCode(BaseConstant.BillTypeCode.FK);
        return super.add(mod);
    }
    
    @GetMapping("/updateSubmit")
    public JsonModel updateSubmit(@RequestParam String id, @RequestParam String submit) {
        fundBankrollService.updateSubmit(id, submit);
        return JsonModel.dataResult("提交成功");
    }
    
    @Override
    public JsonModel edit(@RequestBody FundBankroll mod) {
        mod.setBilltypeCode(BaseConstant.BillTypeCode.FK);
        return super.edit(mod);
    }
    @Override
    public JsonModel findList(@RequestBody QueryBean queryBean) {
        //查询权限
//        DataPermissionUtil.getInstance().getReadPermisssion(queryBean, this.getManagerUser());
        queryBean.defalutParam("billtypeCode_eq", BaseConstant.BillTypeCode.FK);
        return super.findList(queryBean);
    }
    /**
     * @Title: findDetailQuoteList   
     * @Description: 引用页面查询方法   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findQuoteList")
    public JsonModel findQuoteList(@RequestBody QueryBean queryBean) {
        //查询权限
//        DataPermissionUtil.getInstance().getReadPermisssion(queryBean, this.getManagerUser());
        Page<VFundPayapplyDetailReport> page = vFundPayapplyDetailReportService.queryPageList(queryBean);
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
//        DataPermissionUtil.getInstance().getReadPermisssion(queryBean, this.getManagerUser());
        queryBean.defalutParam("billtypeCode_eq", BaseConstant.BillTypeCode.FK);
        Page<VFundBankrollDetailReport> page = vFundBankrollDetailReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
}