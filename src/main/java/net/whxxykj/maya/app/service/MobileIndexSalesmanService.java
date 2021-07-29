package net.whxxykj.maya.app.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.whxxykj.maya.app.repository.MobileIndexRepository;
import net.whxxykj.maya.app.repository.MobileIndexSalesmanRepository;
import net.whxxykj.maya.base.BaseConstant;
import net.whxxykj.maya.base.common.uitls.DataPermissionNewUtil;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.service.BaseService;
import net.whxxykj.maya.common.util.DataValUtil;
import net.whxxykj.maya.common.util.DateUtil;
import net.whxxykj.maya.trade.entity.InterestBankroll;
import net.whxxykj.maya.trade.entity.VIndexProfitReport;
import net.whxxykj.maya.trade.entity.WarehouseSbill;
import net.whxxykj.maya.trade.service.InterestBankrollService;
import net.whxxykj.maya.trade.service.TradeIndexSalemanService;
import net.whxxykj.maya.trade.service.TradeIndexService;

@Service
public class MobileIndexSalesmanService extends BaseService<MobileIndexRepository, WarehouseSbill> {
    
    @Autowired
    private MobileIndexSalesmanRepository mobileIndexSalesmanRepository;
    
    @Autowired
    private InterestBankrollService interestBankrollService;
    
    @Autowired
    private TradeIndexService tradeIndexService;
    @Autowired
    private TradeIndexSalemanService tradeIndexSalemanService;
    
    //获取当前操作员权限
    private Iterable<String> getPermisssionEmployee() {
        QueryBean queryBean = new QueryBean();
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_JSXG, queryBean);
        Map<String, Object> searchFileds = queryBean.getSearchFileds();
        @SuppressWarnings("unchecked")
        List<String> employeeCodeList = (List<String>)searchFileds.getOrDefault("employeeCode_in", null);
        //List<String> employeeCodeList = Arrays.asList("----");
        return employeeCodeList;
    }
    
    /**
     * 
     * @Title: getSaleGoodsWeightToday   
     * @Description: 今日销售重量   
     * @param: @return      
     * @return: Double      
     * @throws
     */
    public Double getSaleGoodsWeightToday() {
        Double d = mobileIndexSalesmanRepository.getSaleGoodsWeightByDate(DateUtil.getDateString(new Date(), DateUtil.ORA_DATE_FORMAT),this.getPermisssionEmployee());
        return DataValUtil.nullConvertZero(d);
    }
    
    /**
     * 
     * @Title: getSaleGoodsWeightToday   
     * @Description: 昨日销售重量   
     * @param: @return      
     * @return: Double      
     * @throws
     */
    public Double getSaleGoodsWeightYesterday() {
        Double d = mobileIndexSalesmanRepository.getSaleGoodsWeightByDate(DateUtil.getDateString(DateUtil.getBeforeDate(new Date(), 1), DateUtil.ORA_DATE_FORMAT),this.getPermisssionEmployee());
        return DataValUtil.nullConvertZero(d);
    }
    
    /**
     * 
     * @Title: getSaleSumGoodsWeightTop5   
     * @Description: 销售重量排行Top5   
     * @param: @return      
     * @return: List<Map<String,Object>>      
     * @throws
     */
    public List<Map<String,Object>> getSaleSumGoodsWeightTop5(){
        return mobileIndexSalesmanRepository.getSaleSumGoodsWeightTop5(this.getPermisssionEmployee());
    }
    
    /**
     * 
     * @Title: getSumGoodsMoneyTop5   
     * @Description: 销售金额排名Top5   
     * @param: @return      
     * @return: List<Map<String,Object>>      
     * @throws
     */
    public List<Map<String,Object>> getSaleSumGoodsMoneyTop5(){
        return mobileIndexSalesmanRepository.getSaleSumGoodsMoneyTop5(this.getPermisssionEmployee());
    }
    
    /**
     * 
     * @Title: getPurchaseGoodsWeightToday   
     * @Description: 今日采购重量   
     * @param: @return      
     * @return: Double      
     * @throws
     */
    public Double getPurchaseGoodsWeightToday() {
        Double d = mobileIndexSalesmanRepository.getPurchaseGoodsWeightByDate(DateUtil.getDateString(new Date(), DateUtil.ORA_DATE_FORMAT),this.getPermisssionEmployee());
        return DataValUtil.nullConvertZero(d);
    }
    
    /**
     * 
     * @Title: getPurchaseGoodsWeightYesterday   
     * @Description: 昨日采购重量   
     * @param: @return      
     * @return: Double      
     * @throws
     */
    public Double getPurchaseGoodsWeightYesterday() {
        Double d = mobileIndexSalesmanRepository.getPurchaseGoodsWeightByDate(DateUtil.getDateString(DateUtil.getBeforeDate(new Date(), 1), DateUtil.ORA_DATE_FORMAT),this.getPermisssionEmployee());
        return DataValUtil.nullConvertZero(d);
    }
    
    /**
     * 
     * @Title: getPurchaseSumGoodsWeightTop5   
     * @Description: 采购重量top5   
     * @param: @return      
     * @return: List<Map<String,Object>>      
     * @throws
     */
    public List<Map<String,Object>> getPurchaseSumGoodsWeightTop5(){
        return mobileIndexSalesmanRepository.getPurchaseSumGoodsWeightTop5(this.getPermisssionEmployee());
    }
    
    /**
     * 
     * @Title: getPurchaseSumGoodsMoneyTop5   
     * @Description: 采购金额top5   
     * @param: @return      
     * @return: List<Map<String,Object>>      
     * @throws
     */
    public List<Map<String,Object>> getPurchaseSumGoodsMoneyTop5(){
        return mobileIndexSalesmanRepository.getPurchaseSumGoodsMoneyTop5(this.getPermisssionEmployee());
    }
    
    //客户销售重量top5
    public List<Map<String,Object>> getCustomerGoodsWeightTop5(){
        return mobileIndexSalesmanRepository.getCustomerGoodsWeightTop5(this.getPermisssionEmployee());
    }
    
    //客户销售金额top5
    public List<Map<String,Object>> getCustomerGoodsMoneyTop5(){
        return mobileIndexSalesmanRepository.getCustomerGoodsMoneyTop5(this.getPermisssionEmployee());
    }
    
    //供应商采购金额top5
    public List<Map<String,Object>> getSupplierGoodsMoneyTop5(){
        return mobileIndexSalesmanRepository.getSupplierGoodsMoneyTop5(this.getPermisssionEmployee());
    }
    
    //供应商采购重量top5
    public List<Map<String,Object>> getSupplierGoodsWeightTop5(){
        return mobileIndexSalesmanRepository.getSupplierGoodsWeightTop5(this.getPermisssionEmployee());
    }
    
    //应收账款排名top6
    public List<Map<String,Object>> getRecMoneyTop6(){
        return tradeIndexService.getRecMoneyTop6(this.getPermisssionEmployee());
    }
    
    //应收账款排名top6
    public List<Map<String,Object>> getPayMoneyTop6(){
        return tradeIndexService.getPayMoneyTop6(this.getPermisssionEmployee());
    }
    
    //获取资金占用信息，根据收付款
    public InterestBankroll  getInterestBankroll(QueryBean queryBean) {
        InterestBankroll bankroll = interestBankrollService.findSumOne(queryBean);
        return bankroll;
    }
    //业务员  当天毛利
    public VIndexProfitReport findIndexProfitMoneyOfEmp() {
        return tradeIndexSalemanService.findIndexProfitMoneyOfEmp();
    }

}
