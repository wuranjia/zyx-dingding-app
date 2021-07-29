package net.whxxykj.maya.app.ctrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.fasterxml.jackson.annotation.JsonFormat;

import net.whxxykj.maya.base.service.ActivitiService;
import net.whxxykj.maya.base.entity.BaseTrademDetail;
import net.whxxykj.maya.base.entity.SysProcessInstance;
import net.whxxykj.maya.base.entity.SysProcessKey;
import net.whxxykj.maya.base.entity.SysProcessValue;
import net.whxxykj.maya.base.entity.VerifyInfo;
import net.whxxykj.maya.base.service.SysProcessInstanceService;
import net.whxxykj.maya.base.service.SysProcessKeyService;
import net.whxxykj.maya.base.service.SysProcessValueService;
import net.whxxykj.maya.common.entity.ManagerUser;
import net.whxxykj.maya.common.entity.UserCache;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.DataValUtil;
import net.whxxykj.maya.common.util.DateUtil;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.common.util.SpringBeanUtils;
import net.whxxykj.maya.common.util.StringUtil;
import net.whxxykj.maya.trade.service.BaseTradeService;

@RestController
@RequestMapping("/mobile/tradem/activiti")
public class MobileActivitiCtrl {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ActivitiService activitiUtils;

    @Autowired
    private SysProcessInstanceService sysProcessInstanceService;
    
    @Autowired
    private HttpServletRequest  request;
    
    @Autowired
    private SysProcessKeyService sysProcessKeyService;
    
    @Autowired
    private SysProcessValueService sysProcessValueService;

    @Autowired
    private ProcessDiagramGenerator processDiagramGenerator;

    @Autowired
    private HistoryService historyService;
    
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private ProcessEngine processEngine;
    
    /**
     * 
     * <p>Title: findPageList</p>   
     * <p>Description: 查询我的待办</p>   
     * @param queryBean
     * @return   
     * @see net.whxxykj.maya.tradem.ctrl.ActivitiCtrl#findPageList(net.whxxykj.maya.common.repository.QueryBean)
     */
    @PostMapping(value = "/queryTaskPage")
    public JsonModel findPageList(@RequestBody QueryBean queryBean) {
        String optId = this.getManagerUser().getId();
        Map<String,Object> searchFileds = queryBean.getSearchFileds();
        String variablesValueLike = MapUtils.getString(searchFileds, "variablesValue_like", null);
        String beginDateGte = MapUtils.getString(searchFileds, "beginDate_gte", null);
        String endDateLte = MapUtils.getString(searchFileds, "endDate_lte", null);
        Date beginDate = DateUtil.getDate(beginDateGte+" 00:00:00", DateUtil.ORA_DATE_TIME_FORMAT1);
        Date endDate = DateUtil.getDate(endDateLte+" 23:59:59", DateUtil.ORA_DATE_TIME_FORMAT1);
        List<String> billtypeCodeIn = (List<String>)MapUtils.getObject(searchFileds, "billtypeCode_in", null);
        List<String> applyStateIn = (List<String>)MapUtils.getObject(searchFileds, "applyState_in", null);
        
        long count = activitiUtils.queryCountByTaskCandidateUser(optId,variablesValueLike,billtypeCodeIn,applyStateIn,beginDate,endDate);
        if(count == 0) {
            return JsonModel.dataResult(count, CollectionUtils.emptyCollection());    
        }
        int firstResult = (queryBean.getPage() - 1) * queryBean.getPageSize();
        int maxResults =  queryBean.getPageSize();
        List<SysProcessInstance> sysProcessInstanceList = sysProcessInstanceService.queryPageByTaskCandidateUser(optId,variablesValueLike,billtypeCodeIn,applyStateIn,beginDate,endDate, firstResult, maxResults);
        if(CollectionUtils.isNotEmpty(sysProcessInstanceList)) {
            for(SysProcessInstance processInstance : sysProcessInstanceList) {
                BaseTradeService service = null;
                try {
                    service = SpringBeanUtils.getBean(Class.forName(processInstance.getServiceName()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                List<BaseTrademDetail>  detailList = (List<BaseTrademDetail>)service.findDetailByBillcode(processInstance.getBillCode());
                processInstance.setTrademDetailList(detailList);
                processInstance.setTotalMoney(0D);
                if(CollectionUtils.isNotEmpty(detailList)) {
                    processInstance.setTotalMoney(detailList.stream().mapToDouble( x -> DataValUtil.nullConvertZero(x.getGoodsMoney())).sum());    
                }
            }
        }
        return JsonModel.dataResult(count, sysProcessInstanceList);
    }
    
    
    /**
     * 查询已办任务(分页)
     * @param firstResult
     * @param maxResults
     * @return
     */
    @PostMapping(value = "/queryHistoricTaskPage")
    public JsonModel findPageHistoricTaskList(@RequestBody QueryBean queryBean) {
        String optId = this.getManagerUser().getId();
        Map<String,Object> searchFileds = queryBean.getSearchFileds();
        String variablesValueLike = MapUtils.getString(searchFileds, "variablesValue_like", null);
        String beginDateGte = MapUtils.getString(searchFileds, "beginDate_gte", null);
        String endDateLte = MapUtils.getString(searchFileds, "endDate_lte", null);
        Date beginDate = DateUtil.getDate(beginDateGte+" 00:00:00", DateUtil.ORA_DATE_TIME_FORMAT1);
        Date endDate = DateUtil.getDate(endDateLte+" 23:59:59", DateUtil.ORA_DATE_TIME_FORMAT1);
        List<String> billtypeCodeIn = (List<String>)MapUtils.getObject(searchFileds, "billtypeCode_in", null);
        List<String> applyStateIn = (List<String>)MapUtils.getObject(searchFileds, "applyState_in", null);
        
        long count = activitiUtils.queryCountHistoricTaskByTaskAssigned(optId,variablesValueLike,billtypeCodeIn,applyStateIn,beginDate,endDate);
        if(count == 0) {
            return JsonModel.dataResult(count, CollectionUtils.emptyCollection());    
        }
        int firstResult = (queryBean.getPage() - 1) * queryBean.getPageSize();
        int maxResults =  queryBean.getPageSize();
        List<SysProcessInstance> sysProcessInstanceList = sysProcessInstanceService.queryPageHistoricTaskByTaskAssigned(optId,variablesValueLike,billtypeCodeIn,applyStateIn,beginDate,endDate,firstResult, maxResults);
        if(CollectionUtils.isNotEmpty(sysProcessInstanceList)) {
            for(SysProcessInstance processInstance : sysProcessInstanceList) {
                BaseTradeService service = null;
                try {
                    service = SpringBeanUtils.getBean(Class.forName(processInstance.getServiceName()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                List<BaseTrademDetail>  detailList = (List<BaseTrademDetail>)service.findDetailByBillcode(processInstance.getBillCode());
                processInstance.setTrademDetailList(detailList);
                processInstance.setTotalMoney(0D);
                if(CollectionUtils.isNotEmpty(detailList)) {
                    processInstance.setTotalMoney(detailList.stream().mapToDouble( x -> DataValUtil.nullConvertZero(x.getGoodsMoney())).sum());    
                }
            }
        }
        return JsonModel.dataResult(count, sysProcessInstanceList);
    }
    
    /**
     * 查询发起任务(分页)
     * @param firstResult
     * @param maxResults
     * @return
     */
    @PostMapping(value = "/findPageCreateHistoricTaskPage")
    public JsonModel findPageCreateHistoricTaskPage(@RequestBody QueryBean queryBean) {
        String optId = this.getManagerUser().getId();
        Map<String,Object> searchFileds = queryBean.getSearchFileds();
        String variablesValueLike = MapUtils.getString(searchFileds, "variablesValue_like", null);
        String beginDateGte = MapUtils.getString(searchFileds, "beginDate_gte", null);
        String endDateLte = MapUtils.getString(searchFileds, "endDate_lte", null);
        Date beginDate = DateUtil.getDate(beginDateGte+" 00:00:00", DateUtil.ORA_DATE_TIME_FORMAT1);
        Date endDate = DateUtil.getDate(endDateLte+" 23:59:59", DateUtil.ORA_DATE_TIME_FORMAT1);
        List<String> billtypeCodeIn = (List<String>)MapUtils.getObject(searchFileds, "billtypeCode_in", null);
        List<String> applyStateIn = (List<String>)MapUtils.getObject(searchFileds, "applyState_in", null);
        
        long count = activitiUtils.queryCountHistoricProcessInstancByTaskAssigned(optId,variablesValueLike,billtypeCodeIn,applyStateIn,beginDate,endDate);
        if(count == 0) {
            return JsonModel.dataResult(count, CollectionUtils.emptyCollection());    
        }
        int firstResult = (queryBean.getPage() - 1) * queryBean.getPageSize();
        int maxResults =  queryBean.getPageSize();
        List<SysProcessInstance> sysProcessInstanceList = sysProcessInstanceService.queryPageHistoricProcessInstancByTaskAssigned(optId,variablesValueLike,billtypeCodeIn,applyStateIn,beginDate,endDate, firstResult, maxResults);
        if(CollectionUtils.isNotEmpty(sysProcessInstanceList)) {
            for(SysProcessInstance processInstance : sysProcessInstanceList) {
                BaseTradeService service = null;
                try {
                    service = SpringBeanUtils.getBean(Class.forName(processInstance.getServiceName()));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                List<BaseTrademDetail>  detailList = (List<BaseTrademDetail>)service.findDetailByBillcode(processInstance.getBillCode());
                processInstance.setTrademDetailList(detailList);
                processInstance.setTotalMoney(0D);
                if(CollectionUtils.isNotEmpty(detailList)) {
                    processInstance.setTotalMoney(detailList.stream().mapToDouble( x -> DataValUtil.nullConvertZero(x.getGoodsMoney())).sum());    
                }
            }
        }
        return JsonModel.dataResult(count, sysProcessInstanceList);
    }
    
    
    /**
     * 查询待办任务总数
     * @param queryBean 
     * @return
     */
    @GetMapping(value = "/queryTaskCount")
    public JsonModel findPageList() {
        return JsonModel.dataResult(activitiUtils.queryCountByTaskCandidateUser(this.getManagerUser().getId(),null,null,null,null,null));
    }

    @GetMapping("/isHaveAuth")
    public JsonModel isHaveAuth(@RequestParam String billId) {
        String taskId = sysProcessInstanceService.getHaveAuth(billId);
        Map<String, String> map = new HashMap<String, String>();
        map.put("taskId", taskId);
        return JsonModel.dataResult(map);
    }

    /**
     * 
     * @return
     */
    @PostMapping(value = "/completeMyTask")
    public JsonModel completeMyTask(@RequestBody Map<String, Object> map) {
        ManagerUser user = this.getManagerUser();
        String taskId = (String) map.get("taskId");
        boolean verifyFlag = (boolean) map.get("verifyFlag");
        String verifyRemark = (String) map.get("verifyRemark");
        VerifyInfo verifyInfo = new VerifyInfo();
        verifyInfo.setVerifyRemark(verifyRemark);
        verifyInfo.setVerifyFlag(verifyFlag);
        verifyInfo.setVerifyOptId(user.getId());
        verifyInfo.setVerifyOptUsername(user.getOptUsername());
        verifyInfo.setVerifyDate(new Date());
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("verifyInfo-"+taskId+"-"+StringUtil.getRand(6), verifyInfo);
        variables.put("applyState", verifyFlag?"1":"-1");
        activitiUtils.updateCompleteTask(taskId, user.getId(), variables,verifyInfo);
        return JsonModel.mkSuccess();
    }

    /**
     * 
     * @return
     */
    @GetMapping(value = "/findVerifyInfoList")
    public JsonModel findVerifyInfoList(@RequestParam String billId) {
        List<SubmitInfo> submitInfoList  = new LinkedList<>(); 
        List<SysProcessInstance>  instanceList = sysProcessInstanceService.findByBillIdOrderByCreateTime(billId);
        if(CollectionUtils.isNotEmpty(instanceList)){
            int times = 0;
            for(SysProcessInstance processInstance : instanceList) {
                ++times;
                 //查询需要谁来审核
                String processInstanceId = processInstance.getProcessInstanceId();
                List<SysProcessKey> processKeyList = sysProcessKeyService.findByProcessInstanceIdAndIsUsedOrderBySortNo(processInstanceId, "1");
                for(SysProcessKey processKey : processKeyList){
                    processKey.setProcessValueList(sysProcessValueService.findByProcessInstanceIdAndTaskDefinitionKey(processInstanceId, processKey.getTaskDefinitionKey()));
                }
                SysProcessKey processKeySubmit = new  SysProcessKey("", "", "", processInstanceId,0,"提交");
                SysProcessValue processValueSubmit = new SysProcessValue(processInstance.getCreateOptId(), processInstance.getCreateDeptName(), processInstance.getCreateTime());
                processValueSubmit.setApplyStateStr("提交");
                List<SysProcessValue> processValueList = new LinkedList<>();
                processValueList.add(processValueSubmit);
                processKeySubmit.setProcessValueList(processValueList);
                processKeyList.add(0, processKeySubmit);
                String abandonOptId = processInstance.getAbandonOptId();
                if(StringUtil.isNotEmpty(abandonOptId)){
                    SysProcessKey processKeyAbandon = new  SysProcessKey("", "", "", processInstanceId,0,"弃审");
                    SysProcessValue processValueAbandon = new SysProcessValue(processInstance.getAbandonOptId(), processInstance.getAbandonDeptName(), processInstance.getAbandonTime());
                    processValueAbandon.setApplyStateStr("弃审");
                    List<SysProcessValue> processValueAbandonList = new LinkedList<>();
                    processValueAbandonList.add(processValueAbandon);
                    processKeyAbandon.setProcessValueList(processValueAbandonList);
                    processKeyList.add(processKeyAbandon);
                }
                SubmitInfo submitInfo = new SubmitInfo();
                submitInfo.setIndex(times);
                submitInfo.setSysProcessKeyList(processKeyList);
                submitInfo.setSubmitDate(processInstance.getCreateTime());
                submitInfoList.add(submitInfo);
            }
            Collections.sort(submitInfoList);
        }
        return JsonModel.dataResult(submitInfoList);
    }

    /**
     * <p>
     * 查看当前流程图
     * </p>
     * 
     * @param instanceId 流程实例
     * @param response   void 响应
     * @version 1.0
     */
    @GetMapping(value = "/showProcessImg")
    public void showImg(@RequestParam(required = false) String billId, HttpServletResponse response) {
        SysProcessInstance sysProcessInstance = sysProcessInstanceService.findByBillIdAndState(billId, "1");
        String instanceId = "";
        String processDefinitionId = null;
        if (sysProcessInstance != null) {
            instanceId = sysProcessInstance.getProcessInstanceId();
        }
        
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(instanceId).singleResult();
        if (processInstance == null) {
            logger.error("流程实例ID:{}没查询到流程实例！", instanceId);
            return;
        }
        processDefinitionId = processInstance.getProcessDefinitionId();

        // 根据流程对象获取流程对象模型
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        /*
         * 查看已执行的节点集合 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
         */
        // 构造历史流程查询
        HistoricActivityInstanceQuery historyInstanceQuery = historyService.createHistoricActivityInstanceQuery().processInstanceId(instanceId);
        // 查询历史节点
        List<HistoricActivityInstance> historicActivityInstanceList = historyInstanceQuery.orderByHistoricActivityInstanceStartTime().asc().list();
        if (historicActivityInstanceList == null || historicActivityInstanceList.size() == 0) {
            logger.info("流程实例ID:{}没有历史节点信息！", instanceId);
            outputImg(response, bpmnModel, null, null);
            return;
        }
        // 已执行的节点ID集合(将historicActivityInstanceList中元素的activityId字段取出封装到executedActivityIdList)
        List<String> executedActivityIdList = historicActivityInstanceList.stream().map(item -> item.getActivityId()).collect(Collectors.toList());

        /*
         * 获取流程走过的线
         */
        // 获取流程定义
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(processDefinitionId);
        List<String> flowIds = activitiUtils.getHighLightedFlows(bpmnModel, processDefinition,historicActivityInstanceList);

        /*
         * 输出图像，并设置高亮
         */
        outputImg(response, bpmnModel, flowIds, executedActivityIdList);
    }

    /**
     * <p>
     * 输出图像
     * </p>
     * 
     * @param response               响应实体
     * @param bpmnModel              图像对象
     * @param flowIds                已执行的线集合
     * @param executedActivityIdList void 已执行的节点ID集合
     * @version 1.0
     */
    private void outputImg(HttpServletResponse response, BpmnModel bpmnModel, List<String> flowIds,
            List<String> executedActivityIdList) {
        InputStream imageStream = null;
        try {
//          imageStream = repositoryService.getProcessModel("pc_1620978936484:1:70003");
//          repositoryService.getProcessModel(processDefinitionId);
            imageStream = processDiagramGenerator.generateDiagram(bpmnModel, executedActivityIdList, flowIds, "宋体","微软雅黑", "黑体", false);
            //输出资源内容到相应对象
            byte[] b = new byte[1024];
            int len;
            while ((len = imageStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
            response.getOutputStream().flush();
        } catch (Exception e) {
            logger.error("流程图输出异常！", e);
        } finally { // 流关闭
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    
//  public InputStream getDiagram(String processInstanceId) {
//        //获得流程实例
//        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
//                .processInstanceId(processInstanceId).singleResult();
//        String processDefinitionId = StringUtils.EMPTY;
//        if (processInstance == null) {
//            //查询已经结束的流程实例
//            HistoricProcessInstance processInstanceHistory =
//                    historyService.createHistoricProcessInstanceQuery()
//                            .processInstanceId(processInstanceId).singleResult();
//            if (processInstanceHistory == null)
//                return null;
//            else
//                processDefinitionId = processInstanceHistory.getProcessDefinitionId();
//        } else {
//            processDefinitionId = processInstance.getProcessDefinitionId();
//        }
//
//        //使用宋体
//        String fontName = "宋体";
//        //获取BPMN模型对象
//        BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
//        //获取流程实例当前的节点，需要高亮显示
//        List<String> currentActs = Collections.EMPTY_LIST;
//        if (processInstance != null)
//            currentActs = runtimeService.getActiveActivityIds(processInstance.getId());
//        
//        processDiagramGenerator.generateDiagram
//
//        return processEngine.getProcessEngineConfiguration()
//                .getProcessDiagramGenerator()
//                .generateDiagram(model, "png", currentActs, new ArrayList<String>(),
//                        fontName, fontName, fontName, null, 1.0);
//    }

    
    /**
     * 修改历史流程变量
     * @param executionId
     * @param variableName
     * @param value
     * @return
     */
    @PostMapping(value = "/updateHistoricVariable")
    public JsonModel  updateHistoricVariable(@RequestBody Map<String,Object> map) { 
        String executionId = (String) map.get("executionId");
        String variableName = (String) map.get("variableName");
        String variableValue = (String) map.get("variableValue"); 
        VerifyInfo  verifyInfo = JSON.parseObject(variableValue, VerifyInfo.class);
        //@RequestParam String executionId,@RequestParam String variableName,@RequestParam Object value
        //activitiUtils.updateHistoricVariable(executionId, variableName, verifyInfo);
        return JsonModel.mkSuccess();
    }
    
    /**
     * 修改历史流程变量
     * @param executionId
     * @param variableName
     * @param value
     * @return
     */
    @GetMapping(value = "/getHistoricVariable")
    public JsonModel  getHistoricVariable(@RequestParam  String executionId) { 
        return JsonModel.dataResult(activitiUtils.getHistoricVariable(executionId));
    }

    public ManagerUser getManagerUser() {
        String accessToken = request.getHeader("Authorization");
        String optId = JWT.decode(accessToken).getAudience().get(0);
        return UserCache.getInstance().getUser(optId);
    }
    class SubmitInfo implements Serializable ,Comparable<SubmitInfo>{
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private int index;
        @JsonFormat(pattern=DateUtil.DATE_TIME2, timezone=DateUtil.TIMEZONE)
        private Date submitDate;
        private List<SysProcessKey> sysProcessKeyList;
        public int getIndex() {
            return index;
        }
        public Date getSubmitDate() {
            return submitDate;
        }
        public void setIndex(int index) {
            this.index = index;
        }
        public void setSubmitDate(Date submitDate) {
            this.submitDate = submitDate;
        }
        @Override
        public int compareTo(SubmitInfo o) {
            return (int) (this.getSubmitDate().getTime()-o.getSubmitDate().getTime());
        }
        public List<SysProcessKey> getSysProcessKeyList() {
            return sysProcessKeyList;
        }
        public void setSysProcessKeyList(List<SysProcessKey> sysProcessKeyList) {
            this.sysProcessKeyList = sysProcessKeyList;
        }
    }


}
