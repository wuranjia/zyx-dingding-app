package net.whxxykj.maya.app.ctrl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.whxxykj.maya.app.service.MobileIndexService;
import net.whxxykj.maya.app.service.MobileWarehouseSbillService;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.Format;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.trade.entity.InterestBankroll;
import net.whxxykj.maya.trade.entity.WarehouseSbill;
import net.whxxykj.maya.trade.repository.VWarehouseGoodsReportRepository;

/**
 * 
 * @ClassName:  MobileIndexCtrl   
 * @Description: 老板看板   
 * @author: YEJUN
 * @date:   2021年6月23日 下午9:01:32      
 * @Copyright:
 */
@RestController
@RequestMapping(value = "/mobile/index/boss")
public class MobileIndexBossCtrl extends BaseCtrl<MobileWarehouseSbillService, WarehouseSbill> {
    
    @Autowired
    private MobileIndexService mobileIndexService;
    
    @Autowired
    private VWarehouseGoodsReportRepository vWarehouseGoodsReportRepository;
    
    @GetMapping("/getData")
    public JsonModel getData() {
        Double saleGoodsWeightToday = mobileIndexService.getSaleGoodsWeightToday();//今日销量
        Double saleGoodsWeightYesterday = mobileIndexService.getSaleGoodsWeightYesterday();//昨日销量
        Double purchaseGoodsWeightToday = mobileIndexService.getPurchaseGoodsWeightToday();//今日采购量
        Double purchaseGoodsWeightYesterday = mobileIndexService.getPurchaseGoodsWeightYesterday();//昨日采购量
        List<Map<String,Object>> saleSumGoodsWeightTop5 = mobileIndexService.getSaleSumGoodsWeightTop5();//商品销售量top5
        List<Map<String,Object>> saleSumGoodsMoneyTop5 = mobileIndexService.getSaleSumGoodsMoneyTop5();//商品销售额top5
        List<Map<String,Object>> purchaseSumGoodsWeightTop5 = mobileIndexService.getPurchaseSumGoodsWeightTop5();//商品采购量top5
        List<Map<String,Object>> purchaseSumGoodsMoneyTop5 = mobileIndexService.getPurchaseSumGoodsMoneyTop5();//商品采购额top5
        List<Map<String,Object>> customerGoodsWeightTop5 = mobileIndexService.getCustomerGoodsWeightTop5();
        List<Map<String,Object>> customerGoodsMoneyTop5 = mobileIndexService.getCustomerGoodsMoneyTop5();//客户销售金额top5
        List<Map<String,Object>> supplierGoodsMoneyTop5 = mobileIndexService.getSupplierGoodsMoneyTop5();//供应商采购金额top5
        List<Map<String,Object>> supplierGoodsWeightTop5 = mobileIndexService.getSupplierGoodsWeightTop5();//供应商采购重量top5
        List<Map<String,Object>> recMoneyTop6 = mobileIndexService.getRecMoneyTop6();//应收账款排名top6
        List<Map<String,Object>> payMoneyTop6 = mobileIndexService.getPayMoneyTop6();//应收账款排名top6
        Map<String,Object> RecMoneyTotal = mobileIndexService.getRecMoneyTotal();//获取应付总金额
        Map<String,Object> warehouseGoodsWeight = mobileIndexService.getWarehouseGoodsWeight();
        
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
        resultMap.put("supplierGoodsMoneyTop5", supplierGoodsMoneyTop5);
        resultMap.put("supplierGoodsWeightTop5", supplierGoodsWeightTop5);
        resultMap.put("recMoneyTop6", recMoneyTop6);
        resultMap.put("payMoneyTop6", payMoneyTop6);
        resultMap.put("RecMoneyTotal", RecMoneyTotal);
        resultMap.put("warehouseGoodsWeight", warehouseGoodsWeight);
        
        return JsonModel.dataResult(resultMap);
    }
    
    //库存占比(当日库存状态)
    @GetMapping("/getWarehouseGoodsPercent")
    public JsonModel  getWarehouseGoodsPercent(@RequestParam(required = false) String warehouseCode){
        List<Map<String,Object>> percent =  mobileIndexService.getWarehouseGoodsPercent(warehouseCode);
        return JsonModel.dataResult(percent);
    }
    
    //销售分析
    @PostMapping("/getSaleAnalysis")
    public JsonModel  getSaleAnalysis(@RequestBody QueryBean querybean){
        Object object = mobileIndexService.getSaleAnalysis(querybean);
        return JsonModel.dataResult(object);
    }
    
    //采购分析
    @PostMapping("/getPurchaseAnalysis")
    public JsonModel  getPurchaseAnalysis(@RequestBody QueryBean querybean){
        Object object = mobileIndexService.getPurchaseAnalysis(querybean);
        return JsonModel.dataResult(object);
    }
    
    //资金占用
    @PostMapping("/getInterestBankroll")
    public JsonModel  getInterestBankroll(@RequestBody QueryBean querybean){
        InterestBankroll bankroll = mobileIndexService.getInterestBankroll(querybean);
        return JsonModel.dataResult(bankroll);
    }
    
}
