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
import net.whxxykj.maya.trade.entity.FundPayapply;
import net.whxxykj.maya.trade.entity.VFundPayLead;
import net.whxxykj.maya.trade.entity.VFundPayapplyDetailReport;
import net.whxxykj.maya.trade.service.FundPayapplyService;
import net.whxxykj.maya.trade.service.VFundPayLeadService;
import net.whxxykj.maya.trade.service.VFundPayapplyDetailReportService;


@RestController
@RequestMapping(value = "/mobile/tradem/fundpayapply")
public class MobileFundPayapplyCtrl extends BaseCtrl<FundPayapplyService, FundPayapply> {
    
    @Autowired
    private FundPayapplyService fundPayapplyService;
    @Autowired
    private VFundPayLeadService vFundPayLeadService;
    @Autowired
    private VFundPayapplyDetailReportService vFundPayapplyDetailReportService;
    @Override
    public JsonModel findList(@RequestBody QueryBean queryBean) {
       //查询权限
       DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_JSXG, queryBean);
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
    @PostMapping("/findQuoteList")
    public JsonModel findQuoteList(@RequestBody QueryBean queryBean) {
        //查询权限
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_JSXG, queryBean);
//        DataPermissionUtil.getInstance().getReadPermisssion(queryBean, this.getManagerUser());
        queryBean.defalutParam("dataFundflag", "dataFundflag = '0'");
        Page<VFundPayLead> page = vFundPayLeadService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    
    
    @GetMapping("/updateSubmit")
    public JsonModel updateSubmit(@RequestParam String id, @RequestParam String submit) {
        fundPayapplyService.updateSubmit(id, submit);
        return JsonModel.dataResult("提交成功");
    }
    
    /**
     * 
     * @Title: findDetailReportList   
     * @Description: 付款申请明细表分页查询   
     * @param: @return
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findDetailReport/list")
    public JsonModel findDetailReportList(@RequestBody QueryBean queryBean) {
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_JSXG, queryBean);
//        DataPermissionUtil.getInstance().getReadPermisssion(queryBean, this.getManagerUser());
        Page<VFundPayapplyDetailReport> page = vFundPayapplyDetailReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
}

