package net.whxxykj.maya.app.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.whxxykj.maya.app.repository.MobileIndexRepository;
import net.whxxykj.maya.base.BaseConstant;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.service.BaseService;
import net.whxxykj.maya.common.util.DateUtil;
import net.whxxykj.maya.trade.entity.InterestBankroll;
import net.whxxykj.maya.trade.entity.SaleScontract;
import net.whxxykj.maya.trade.entity.VFundBankrollDetailReport;
import net.whxxykj.maya.trade.entity.VFundGatheringLead;
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
import net.whxxykj.maya.trade.service.VFundPayapplyDetailReportService;
import net.whxxykj.maya.trade.service.VIndexContractReportService;
import net.whxxykj.maya.trade.service.VInvoiceMakeinvLeadService;
import net.whxxykj.maya.trade.service.VInvoiceSaleinvLeadService;
import net.whxxykj.maya.trade.service.VWarehouseGoodsReportService;

@Service
public class MobileHomeService extends BaseService<MobileIndexRepository, WarehouseSbill> {
    
    @Autowired
    private MobileIndexRepository mobileIndexRepository;
    @Autowired 
    private VWarehouseGoodsReportRepository vWarehouseGoodsReportRepository;
    @Autowired
    private SaleScontractService saleScontractService;
    @Autowired
    private VInvoiceMakeinvLeadService vInvoiceMakeinvLeadService;
    @Autowired
    private VInvoiceSaleinvLeadService vInvoiceSaleinvLeadService;
    @Autowired
    private VFundPayapplyDetailReportService vFundPayapplyDetailReportService;
    @Autowired
    private VFundGatheringLeadService vFundGatheringLeadService;
    
    
    //根据日期查询销售金额
    public Double getSaleGoodsMoneyToday() {
        Double d = mobileIndexRepository.getSaleGoodsMoneyByDate(DateUtil.getDateString(new Date(), DateUtil.ORA_DATE_FORMAT));
        return d;
    }
    
    //根据日期查询采购金额
    public Double getPurchaseGoodsMoneyToday() {
        Double d = mobileIndexRepository.getPurchaseGoodsMoneyByDate(DateUtil.getDateString(new Date(), DateUtil.ORA_DATE_FORMAT));
        return d;
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
        queryBean.defalutParam("payapplyDetailNotmoney_gt", 0);
        queryBean.defalutParam("billTypeCode", BaseConstant.BillTypeCode.FK);
        Page<VFundPayapplyDetailReport>  page = vFundPayapplyDetailReportService.queryPageList(queryBean);
        return page.getTotalElements();
    }
    
    //待收款
    public Long  getNeedRecmoney() {
        QueryBean queryBean = new QueryBean();
        queryBean.defalutParam("dataNfundmoney_gt", 0);
        Page<VFundGatheringLead>  page = vFundGatheringLeadService.queryPageList(queryBean);
        return page.getTotalElements();
    }
    
    //待退款
    public Long  getNeedRefund() {
        QueryBean queryBean = new QueryBean();
        queryBean.defalutParam("dataNfundmoney_gt", 0);
        Page<VFundGatheringLead>  page = vFundGatheringLeadService.queryPageList(queryBean);
        return page.getTotalElements();
    }
    
}
