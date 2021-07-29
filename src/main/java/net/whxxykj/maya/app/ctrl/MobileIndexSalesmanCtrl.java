package net.whxxykj.maya.app.ctrl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.format.FormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import antlr.StringUtils;
import net.whxxykj.maya.app.service.MobileIndexSalesmanService;
import net.whxxykj.maya.app.service.MobileIndexService;
import net.whxxykj.maya.base.BaseConstant;
import net.whxxykj.maya.base.common.uitls.DataPermissionNewUtil;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.DataValUtil;
import net.whxxykj.maya.common.util.DateUtil;
import net.whxxykj.maya.common.util.Format;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.trade.entity.InterestBankroll;
import net.whxxykj.maya.trade.entity.VIndexProfitReport;
import net.whxxykj.maya.trade.entity.WarehouseSbill;
import net.whxxykj.maya.trade.service.TradeIndexService;

/**
 * 
 * @ClassName:  MobileIndexSalesmanCtrl   
 * @Description: 销售看板   
 * @author: YEJUN
 * @date:   2021年6月23日 下午9:01:49      
 * @Copyright:
 */
@RestController
@RequestMapping(value = "/mobile/index/salesman")
public class MobileIndexSalesmanCtrl extends BaseCtrl<MobileIndexSalesmanService, WarehouseSbill> {
    
    @Autowired
    private MobileIndexSalesmanService mobileIndexSalesmanService;
    
    @Autowired
    private MobileIndexService mobileIndexService;
    
    @Autowired
    private TradeIndexService tradeIndexService;
    
    @GetMapping("/getData")
   public JsonModel getData() {
        Double saleGoodsWeightToday = mobileIndexSalesmanService.getSaleGoodsWeightToday();//今日销量
        Double saleGoodsWeightYesterday = mobileIndexSalesmanService.getSaleGoodsWeightYesterday();//昨日销量
        Double purchaseGoodsWeightToday = mobileIndexSalesmanService.getPurchaseGoodsWeightToday();//今日采购量
        Double purchaseGoodsWeightYesterday = mobileIndexSalesmanService.getPurchaseGoodsWeightYesterday();//昨日采购量
        List<Map<String,Object>> saleSumGoodsWeightTop5 = mobileIndexSalesmanService.getSaleSumGoodsWeightTop5();//商品销售量top5
        List<Map<String,Object>> saleSumGoodsMoneyTop5 = mobileIndexSalesmanService.getSaleSumGoodsMoneyTop5(); //商品销售额top5
        List<Map<String,Object>> purchaseSumGoodsWeightTop5 = mobileIndexSalesmanService.getPurchaseSumGoodsWeightTop5();//商品采购量top5
        List<Map<String,Object>> purchaseSumGoodsMoneyTop5 = mobileIndexSalesmanService.getPurchaseSumGoodsMoneyTop5();//商品采购额top5
        List<Map<String,Object>> customerGoodsWeightTop5 = mobileIndexSalesmanService.getCustomerGoodsWeightTop5();//客户销售重量top5
        List<Map<String,Object>> customerGoodsMoneyTop5 = mobileIndexSalesmanService.getCustomerGoodsMoneyTop5();//客户销售金额top5
        List<Map<String,Object>> supplierGoodsWeightTop5 = mobileIndexSalesmanService.getSupplierGoodsWeightTop5();//供应商采购重量top
        List<Map<String,Object>> supplierGoodsMoneyTop5 = mobileIndexSalesmanService.getSupplierGoodsMoneyTop5();//供应商采购金额top
        List<Map<String,Object>> recMoneyTop6 = mobileIndexSalesmanService.getRecMoneyTop6();//应收账款排名top6
        List<Map<String,Object>> payMoneyTop6 = mobileIndexSalesmanService.getPayMoneyTop6();//应付账款排名top6
        
        //资金占用
        QueryBean queryBean = new QueryBean();
        //获取前一天资金占用情况
        queryBean.defalutParam("interestDate_eq",DateUtil.getBeforeDate(getSysDate(), 1));
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_JSXG, queryBean);
        InterestBankroll mod = mobileIndexSalesmanService.getInterestBankroll(queryBean);
        
        VIndexProfitReport indexProfit = mobileIndexSalesmanService.findIndexProfitMoneyOfEmp();//业务员 毛利 需要变更为权限方法
        Map<String,Object> object = mobileIndexService.getRecMoneyTotal();//获取应收总金额 需要变更为权限方法
        
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("saleGoodsWeightToday", Format.format2Number(saleGoodsWeightToday));
        resultMap.put("saleGoodsWeightYesterday", Format.format2Number(saleGoodsWeightYesterday));
        resultMap.put("purchaseGoodsWeightToday", Format.format2Number(purchaseGoodsWeightToday));
        resultMap.put("purchaseGoodsWeightYesterday", Format.format2Number(purchaseGoodsWeightYesterday));
        
        resultMap.put("saleSumGoodsWeightTop5", saleSumGoodsWeightTop5);
        resultMap.put("saleSumGoodsMoneyTop5", saleSumGoodsMoneyTop5);
        resultMap.put("purchaseSumGoodsWeightTop5", purchaseSumGoodsWeightTop5);
        resultMap.put("purchaseSumGoodsMoneyTop5", purchaseSumGoodsMoneyTop5);
        resultMap.put("customerGoodsWeightTop5", customerGoodsWeightTop5);
        resultMap.put("customerGoodsMoneyTop5", customerGoodsMoneyTop5);
        resultMap.put("supplierGoodsWeightTop5", supplierGoodsWeightTop5);
        resultMap.put("supplierGoodsMoneyTop5", supplierGoodsMoneyTop5);
        resultMap.put("recMoneyTop6", recMoneyTop6);
        resultMap.put("payMoneyTop6", payMoneyTop6);
        
        Object warehouseGoodsWeight = mobileIndexService.getWarehouseGoodsWeight();
        
        resultMap.put("interestBankroll", mod);//资金占用
        resultMap.put("empProfit", indexProfit);//业务员毛利
        resultMap.put("recMoneyTotal", object);//业务员毛利
        resultMap.put("warehouseGoodsWeight", warehouseGoodsWeight);//库存信息
        
        return JsonModel.dataResult(resultMap);
   }
    
    
    //TODO 需要权限方法，  
    @GetMapping("/getWarehouseGoodsPercent")
    public JsonModel  getWarehouseGoodsPercent(@RequestParam(required = false) String warehouseCode){
        List<Map<String,Object>> percent =  mobileIndexService.getWarehouseGoodsPercent(warehouseCode);
        return JsonModel.dataResult(percent);
    }
    
    //TODO 需要权限方法
    //销售分析
    @PostMapping("/getSaleAnalysis")
    public JsonModel  getSaleAnalysis(@RequestBody QueryBean querybean){
        Object object = mobileIndexService.getSaleAnalysis(querybean);
        return JsonModel.dataResult(object);
    }

    
}
