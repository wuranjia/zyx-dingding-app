package net.whxxykj.maya.app.ctrl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.whxxykj.maya.app.service.MobileIndexService;
import net.whxxykj.maya.app.service.MobileWarehouseSbillService;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.Format;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.trade.entity.WarehouseSbill;

/**
 * 
 * @ClassName:  MobileHomeCtrl   
 * @Description:app首页   
 * @author: YEJUN
 * @date:   2021年6月24日 下午2:19:28      
 * @Copyright:
 */
@RestController
@RequestMapping(value = "/mobile/home")
public class MobileHomeCtrl extends BaseCtrl<MobileWarehouseSbillService, WarehouseSbill> {
    
    @Autowired
    private MobileIndexService mobileIndexService;
    
    @GetMapping("/getData")
    public JsonModel getData() {
        Double saleGoodsWeightToday = mobileIndexService.getSaleGoodsWeightToday();//今日销售金额
        Double saleGoodsMoneyToday = mobileIndexService.getSaleGoodsMoneyToday();//今日销售金额
        Double purchaseGoodsWeightToday = mobileIndexService.getPurchaseGoodsWeightToday();//今日采购重量
        Double purchaseGoodsMoneyToday = mobileIndexService.getPurchaseGoodsMoneyToday();//今日采购金额
        Long saleScontractToday = mobileIndexService.getSaleScontractToday();//今日开单
        Long needInvoiceSaleinv = mobileIndexService.getNeedInvoiceSaleinv();//销售未开票
        Long needInvoiceMakeinv = mobileIndexService.getNeedInvoiceMakeinv();//采购未开票
        Long needPaymoney = mobileIndexService.getNeedPaymoney();//待付款
        Long needRecmoney = mobileIndexService.getNeedRecmoney();//待收款
        Long needRefund = mobileIndexService.getNeedRefund();//待退款
        Long needApply = mobileIndexService.getNeedApply();//待审批
        
        QueryBean queryBean  = new QueryBean();
        mobileIndexService.queryWarehousegoods(queryBean);//库存概况
        Map<String, Object> allSum = queryBean.getAllSum();
        Double goodsSupplyweight = MapUtils.getDouble(allSum, "goodsSupplyweight", 0D);
        Double goodsWeight = MapUtils.getDouble(allSum, "goodsWeight", 0D);
        Double goodsSlockweight = MapUtils.getDouble(allSum, "goodsSlockweight", 0D);
        Double goodsOrdweight = MapUtils.getDouble(allSum, "goodsOrdweight", 0D);
        Double goodsBillweight = MapUtils.getDouble(allSum, "goodsBillweight", 0D);
        Double goodsMoweight = MapUtils.getDouble(allSum, "goodsBillweight", 0D);
        
        Map<String,Object>   map = new HashMap<String,Object>();
        map.put("needApply",  Integer.toString(Format.long2int(needApply)));//待审批
        map.put("needPaymoney", Integer.toString(Format.long2int(needPaymoney)));//待付款
        map.put("needRecmoney", Integer.toString(Format.long2int(needRecmoney)));//待收款
        map.put("needRefund", Integer.toString(Format.long2int(needRefund)));//待退款
        map.put("saleScontractToday", Integer.toString(Format.long2int(saleScontractToday)));
        map.put("saleGoodsWeightToday", Format.format2Number(saleGoodsWeightToday));
        map.put("saleGoodsMoneyToday", Format.format2Price(saleGoodsMoneyToday));
        map.put("purchaseGoodsWeightToday", Format.format2Number(purchaseGoodsWeightToday));
        map.put("purchaseGoodsMoneyToday", Format.format2Price(purchaseGoodsMoneyToday));
        map.put("needInvoiceSaleinv", Integer.toString(Format.long2int(needInvoiceSaleinv)));
        map.put("needInvoiceMakeinv", Integer.toString(Format.long2int(needInvoiceMakeinv)));
        map.put("goodsSupplyweight", Format.format2Number(goodsSupplyweight));
        map.put("goodsWeight", Format.format2Number(goodsWeight));
        map.put("goodsSlockweight", Format.format2Number(goodsSlockweight));
        map.put("goodsOrdweight", Format.format2Number(goodsOrdweight));
        map.put("goodsBillweight", Format.format2Number(goodsBillweight));
        map.put("goodsMoweight", Format.format2Number(goodsMoweight));
        
        return JsonModel.dataResult(map);
    }
    
}
