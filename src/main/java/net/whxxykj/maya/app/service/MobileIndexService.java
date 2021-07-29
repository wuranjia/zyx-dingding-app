package net.whxxykj.maya.app.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.whxxykj.maya.app.repository.MobileIndexRepository;
import net.whxxykj.maya.base.BaseConstant;
import net.whxxykj.maya.base.common.uitls.DataPermissionNewUtil;
import net.whxxykj.maya.base.service.ActivitiService;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.service.BaseService;
import net.whxxykj.maya.common.util.DataValUtil;
import net.whxxykj.maya.common.util.DateUtil;
import net.whxxykj.maya.trade.entity.InterestBankroll;
import net.whxxykj.maya.trade.entity.SaleScontract;
import net.whxxykj.maya.trade.entity.VFundBankrollDetailReport;
import net.whxxykj.maya.trade.entity.VFundGatheringLead;
import net.whxxykj.maya.trade.entity.VFundPayLead;
import net.whxxykj.maya.trade.entity.VFundPayapplyDetailReport;
import net.whxxykj.maya.trade.entity.VInvoiceMakeinvLead;
import net.whxxykj.maya.trade.entity.VInvoiceSaleinvLead;
import net.whxxykj.maya.trade.entity.VWarehouseGoodsReport;
import net.whxxykj.maya.trade.entity.WarehouseSbill;
import net.whxxykj.maya.trade.repository.VWarehouseGoodsReportRepository;
import net.whxxykj.maya.trade.service.InterestBankrollService;
import net.whxxykj.maya.trade.service.SaleScontractService;
import net.whxxykj.maya.trade.service.TradeIndexService;
import net.whxxykj.maya.trade.service.VFundBankrollDetailReportService;
import net.whxxykj.maya.trade.service.VFundGatheringLeadService;
import net.whxxykj.maya.trade.service.VFundPayLeadService;
import net.whxxykj.maya.trade.service.VFundPayapplyDetailReportService;
import net.whxxykj.maya.trade.service.VIndexContractReportService;
import net.whxxykj.maya.trade.service.VInvoiceMakeinvLeadService;
import net.whxxykj.maya.trade.service.VInvoiceSaleinvLeadService;
import net.whxxykj.maya.trade.service.VWarehouseGoodsReportService;

@Service
public class MobileIndexService extends BaseService<MobileIndexRepository, WarehouseSbill> {
    
    @Autowired
    private MobileIndexRepository mobileIndexRepository;
    @Autowired
    private TradeIndexService tradeIndexService;
    @Autowired
    private VIndexContractReportService vIndexContractReportService;
    @Autowired
    private InterestBankrollService interestBankrollService;
    @Autowired
    private VWarehouseGoodsReportService vWarehouseGoodsReportService;
    @Autowired 
    private VWarehouseGoodsReportRepository vWarehouseGoodsReportRepository;
    @Autowired
    private SaleScontractService saleScontractService;
    @Autowired
    private VInvoiceMakeinvLeadService vInvoiceMakeinvLeadService;
    @Autowired
    private VInvoiceSaleinvLeadService vInvoiceSaleinvLeadService;
    @Autowired
    private VFundPayLeadService vFundPayLeadService;
    @Autowired
    private VFundBankrollDetailReportService vFundBankrollDetailReportService;
    @Autowired
    private VFundGatheringLeadService vFundGatheringLeadService;
    @Autowired
    private ActivitiService activitiService;
    
    
    
    /**
     * 
     * @Title: getSaleGoodsWeightToday   
     * @Description: 今日销售重量   
     * @param: @return      
     * @return: Double      
     * @throws
     */
    public Double getSaleGoodsWeightToday() {
        Double d = mobileIndexRepository.getSaleGoodsWeightByDate(DateUtil.getDateStr(DateUtil.G_DATE_FORMAT));
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
        Double d = mobileIndexRepository.getSaleGoodsWeightByDate(DateUtil.getBeforeDate(getSysDate(), 1));
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
        return mobileIndexRepository.getSaleSumGoodsWeightTop5();
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
        return mobileIndexRepository.getSaleSumGoodsMoneyTop5();
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
        Double d = mobileIndexRepository.getPurchaseGoodsWeightByDate(DateUtil.getDateStr(DateUtil.G_DATE_FORMAT));
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
        Double d = mobileIndexRepository.getPurchaseGoodsWeightByDate(DateUtil.getBeforeDate(getSysDate(), 1));
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
        return mobileIndexRepository.getPurchaseSumGoodsWeightTop5();
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
        return mobileIndexRepository.getPurchaseSumGoodsMoneyTop5();
    }
    
    //客户销售重量top5
    public List<Map<String,Object>> getCustomerGoodsWeightTop5(){
        return mobileIndexRepository.getCustomerGoodsWeightTop5();
    }
    
    //客户销售金额top5
    public List<Map<String,Object>> getCustomerGoodsMoneyTop5(){
        return mobileIndexRepository.getCustomerGoodsMoneyTop5();
    }
    
    //供应商采购金额top5
    public List<Map<String,Object>> getSupplierGoodsMoneyTop5(){
        return mobileIndexRepository.getSupplierGoodsMoneyTop5();
    }
    
    //供应商采购重量top5
    public List<Map<String,Object>> getSupplierGoodsWeightTop5(){
        return mobileIndexRepository.getSupplierGoodsWeightTop5();
    }
    
    //应收账款排名top6
    public List<Map<String,Object>> getRecMoneyTop6(){
        return tradeIndexService.getRecMoneyTop6(null);
    }
    
    //应收账款排名top6
    public List<Map<String,Object>> getPayMoneyTop6(){
        return tradeIndexService.getPayMoneyTop6(null);
    }
    
    //库存占比(当日库存状态)
    public List<Map<String,Object>>  getWarehouseGoodsPercent(String warehouseCode){
        QueryBean queryBean = new QueryBean();
        queryBean.defalutParam("warehouseCode_eq", warehouseCode);
        return tradeIndexService.findIndexWarehouse(queryBean);
    }
    
    //查询当前库存，包括当前库存
    public Map<String,Object>  getWarehouseGoodsWeight(){
        return tradeIndexService.findIndexGoods();
    }
    
    //销售分析
    public Object  getSaleAnalysis(QueryBean queryBean){
        return vIndexContractReportService.queryMap(queryBean);
    }
    
    //采购分析
    public Object  getPurchaseAnalysis(QueryBean queryBean){
        return vIndexContractReportService.queryMap(queryBean);
    }
    
    //应收总金额
    public Map<String,Object>  getRecMoneyTotal() {
        Map<String,Object> map =   tradeIndexService.findIndexMoney();
        return map;
    }
    
    //获取资金占用信息，根据收付款
    public InterestBankroll  getInterestBankroll(QueryBean queryBean) {
        InterestBankroll  bankroll = interestBankrollService.findSumOne(queryBean);
        return bankroll;
    }
    
    //根据日期查询销售金额
    public Double getSaleGoodsMoneyToday() {
        Double d = mobileIndexRepository.getSaleGoodsMoneyByDate(DateUtil.getDateString(new Date(), DateUtil.ORA_DATE_FORMAT));
        return DataValUtil.nullConvertZero(d);
    }
    
    //根据日期查询采购金额
    public Double getPurchaseGoodsMoneyToday() {
        Double d = mobileIndexRepository.getPurchaseGoodsMoneyByDate(DateUtil.getDateString(new Date(), DateUtil.ORA_DATE_FORMAT));
        return DataValUtil.nullConvertZero(d);
    }
    
    //库存概况
    public Page<VWarehouseGoodsReport> queryWarehousegoods(QueryBean queryBean){
        Set<String> sumField = queryBean.getAllSumField();
        sumField.add("goodsSupplyweight");
        sumField.add("goodsWeight");
        sumField.add("goodsSlockweight");
        sumField.add("goodsOrdweight");
        sumField.add("goodsBillweight");
        sumField.add("goodsMoweight");
        return vWarehouseGoodsReportRepository.querySumPageList(queryBean);        
    }
    
    //今日开单
    public Long getSaleScontractToday() {
        QueryBean queryBean = new QueryBean();
        queryBean.defalutParam("scontractDate_eq", DateUtil.getDateString(new Date(), DateUtil.ORA_DATE_FORMAT));
        Page<SaleScontract> page = saleScontractService.queryPageList(queryBean);
        return page.getTotalElements();
    }
    
    //采购未到票
    public Long  getNeedInvoiceMakeinv() {
        QueryBean queryBean = new QueryBean();
        queryBean.defalutParam("dataNoInvmoney_gt", 0);
        Page<VInvoiceMakeinvLead>  page = vInvoiceMakeinvLeadService.queryPageList(queryBean);
        return page.getTotalElements();
    }
    
    //销售未开票
    public Long  getNeedInvoiceSaleinv() {
        QueryBean queryBean = new QueryBean();
        queryBean.defalutParam("dataNoInvmoney_gt", 0);
        Page<VInvoiceSaleinvLead>  page = vInvoiceSaleinvLeadService.queryPageList(queryBean);
        return page.getTotalElements();
    }
    
    //待付款
    public Long  getNeedPaymoney() {
        QueryBean queryBean = new QueryBean();
        queryBean.defalutParam("dataNbfundmoney_gt", 0);
        queryBean.defalutParam("payLeadFunflag_eq", "F004");
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_JSXG, queryBean);
//        DataPermissionUtil.getInstance().getReadPermisssion(queryBean, this.getManagerUser());
        Page<VFundPayLead> page = vFundPayLeadService.queryPageList(queryBean);
        return page.getTotalElements();
    }
    
    //待收款
    public Long  getNeedRecmoney() {
        QueryBean queryBean = new QueryBean();
        queryBean.defalutParam("dataNfundmoney_gt", 0);
        queryBean.defalutParam("overdueDays_gt", 0);
        //查询权限
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_JSXG, queryBean);
//        DataPermissionUtil.getInstance().getReadPermisssion(queryBean, this.getManagerUser());
        Page<VFundGatheringLead>  page = vFundGatheringLeadService.queryPageList(queryBean);
        return page.getTotalElements();
    }
    
    //待退款(销售退款)
    public Long  getNeedRefund() {
        QueryBean queryBean = new QueryBean();
        queryBean.defalutParam("dataNfundmoney_gt", 0);
        queryBean.defalutParam("payLeadFunflag_eq", "F005");
        DataPermissionNewUtil.getInstance().getReadPermission(this.getManagerUser(), BaseConstant.PermType.PERMTYPE_JSXG, queryBean);
//        DataPermissionUtil.getInstance().getReadPermisssion(queryBean, this.getManagerUser());
        Page<VFundPayLead>  page = vFundPayLeadService.queryPageList(queryBean);
        return page.getTotalElements();
    }
    
    //待审批
    public Long getNeedApply() {
        return activitiService.queryCountByTaskCandidateUser(this.getManagerUser().getId(), null, null, null, null, null);
    }
    
}
