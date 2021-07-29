package net.whxxykj.maya.app.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.entity.ManagerUser;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.trade.entity.VWarehouseGoodsReport;
import net.whxxykj.maya.trade.service.VWarehouseGoodsReportService;
/**
 * 
 * @ClassName:  MobileVWarehouseGoodsReportCtrl   
 * @Description:app库存物资明细报表控制层 
 * @author: HAOKE
 * @date:   2021年6月29日 上午11:23:50      
 * @Copyright:
 */
@RestController
@RequestMapping(value = "/mobile/tradem/VWarehouseGoodsReport")
public class MobileVWarehouseGoodsReportCtrl extends BaseCtrl<VWarehouseGoodsReportService, VWarehouseGoodsReport> {
    
    @Autowired
    private VWarehouseGoodsReportService vWarehouseGoodsReportService;
    
    @Override
    public JsonModel findList(@RequestBody QueryBean queryBean) {
        ManagerUser user = this.getManagerUser();
//        DataPermissionUtil.getInstance().getCompanyReadPermisssion(queryBean, user, "goodsCompany");
//        DataPermissionUtil.getInstance().getDeptReadPermisssion(queryBean, user, "goodsDept");
//        DataPermissionUtil.getInstance().getWareReadPermisssion(queryBean, user);
        return super.findList(queryBean);
    }
    
    /**
     * 
     * @Title: findDetailQuoteList   
     * @Description: 引用页面查询方法（现货库存引用）   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findQuoteList")
    public JsonModel findDetailQuoteList(@RequestBody QueryBean queryBean) {
        ManagerUser user = this.getManagerUser();
//        DataPermissionUtil.getInstance().getCompanyReadPermisssion(queryBean, user, "goodsCompany");
//        DataPermissionUtil.getInstance().getDeptReadPermisssion(queryBean, user, "goodsDept");
//        DataPermissionUtil.getInstance().getWareReadPermisssion(queryBean, user);
        queryBean.defalutParam("goodsSupplyweight", "sql:goodsSupplyweight > 0");
        Page<VWarehouseGoodsReport> page = vWarehouseGoodsReportService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
    
    /**
     * 
     * @Title: findDetailQuoteList   
     * @Description: 引用页面查询方法（现货库存引用）   
     * @param: @param queryBean
     * @param: @return      
     * @return: JsonModel      
     * @throws
     */
    @PostMapping("/findSummaryPage/list")
    public JsonModel findSummaryPage(@RequestBody QueryBean queryBean) {
        ManagerUser user = this.getManagerUser();
//        DataPermissionUtil.getInstance().getCompanyReadPermisssion(queryBean, user, "goodsCompany");
//        DataPermissionUtil.getInstance().getDeptReadPermisssion(queryBean, user, "goodsDept");
//        DataPermissionUtil.getInstance().getWareReadPermisssion(queryBean, user);
        Page<VWarehouseGoodsReport> page = vWarehouseGoodsReportService.querySumPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent(),null,queryBean.getAllSum());
    }
}
