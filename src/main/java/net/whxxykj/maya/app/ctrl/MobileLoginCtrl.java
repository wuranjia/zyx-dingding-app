package net.whxxykj.maya.app.ctrl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.service.WxOAuth2Service;
import me.chanjar.weixin.mp.api.WxMpService;
import net.whxxykj.maya.MayaConstant;
import net.whxxykj.maya.base.BaseConstant;
import net.whxxykj.maya.base.entity.LogOperateData;
import net.whxxykj.maya.base.entity.SysCompany;
import net.whxxykj.maya.base.entity.SysEmployee;
import net.whxxykj.maya.base.entity.SysOperator;
import net.whxxykj.maya.base.service.LogOperateDataService;
import net.whxxykj.maya.base.service.SysCompanyService;
import net.whxxykj.maya.base.service.SysEmployeeService;
import net.whxxykj.maya.base.service.SysLoginService;
import net.whxxykj.maya.base.service.SysOperatorService;
import net.whxxykj.maya.base.service.VPermissionAllotDataService;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.entity.ManagerUser;
import net.whxxykj.maya.common.exception.BaseException;
import net.whxxykj.maya.common.kaptcha.MyKaptcha;
import net.whxxykj.maya.common.log.oper.enums.LogLevel;
import net.whxxykj.maya.common.log.oper.enums.LogType;
import net.whxxykj.maya.common.runtime.Runtime;
import net.whxxykj.maya.common.util.DateUtil;
import net.whxxykj.maya.common.util.HttpUtil;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.common.util.JwtUtils;
import net.whxxykj.maya.common.util.StringUtil;
import net.whxxykj.maya.plugin.cache.RedisCacheService;

/**
 * 
 * @ClassName:  MobileLoginCtrl   
 * @Description: app登录端接口   
 * @author: YEJUN
 * @date:   2021年6月21日 下午7:16:37      
 * @Copyright:
 */
@RestController
@RequestMapping("/mobile/sysm/login")
public class MobileLoginCtrl extends BaseCtrl<SysOperatorService, SysOperator> {

    @Autowired
    private SysLoginService sysLoginService;
    @Autowired
    private SysEmployeeService sysEmployeeService;
    @Autowired
    private SysCompanyService sysCompanyService;
    @Autowired
    private MyKaptcha producer;
    @Autowired
    private RedisCacheService redisCacheService;
    @Autowired
    private LogOperateDataService logOperateDataService;
    @Autowired
    private SysOperatorService sysOperatorService;
    @Autowired
    private VPermissionAllotDataService vPermissionAllotDataService;
    @Autowired
    private WxMpService wxMpService;
    
    /**
     * 此接口为初始化登录信息接口
     * 
     * @return 处理结果
     */
    @PostMapping("/initLogin")
    public JsonModel initLogin(HttpSession session) {
        ManagerUser user = (ManagerUser) session.getAttribute(MayaConstant.LOGIN.MANAGER_LOGIN_USER_KEY);
        if (user != null) {
            return JsonModel.mkSuccess(user.getOptUserid()); // 已登录的返回主页
        }
        return JsonModel.mkFaile(MayaConstant.RETURN_STATUS_LOGIN, "用户未登录"); // 未登录的返回登录页
    }
    
    
    /**
     * 此接口为验证登录
     * 
     * @return 处理结果
     */
    @PostMapping("/check")
    public JsonModel check(HttpSession session, @RequestBody ManagerUser user) {
        String userId = user.getUserName();
        String userPwd = user.getUserPwd();
        String coName = user.getCoName();
        SysOperator bean = null;
        try {
        	if(!"上海蔚塑电子商务有限公司".equals(coName)) {
        		throw new BaseException("公司名称不存在");
        	}
            if (StringUtils.isEmpty(userId)) {
                throw new BaseException("请输入用户名！");
            }
            if (StringUtils.isEmpty(userPwd)) {
                throw new BaseException("请输入密码!");
            }
            bean = this.sysLoginService.findManagerCheck(userId, userPwd, null);
            if (null == bean) {
                throw new BaseException("登录名或密码错误!");
            }
            if(bean.getCoName().equals(coName)) {
            	throw new BaseException("公司【"+coName+"】下不存在该操作员");
            }
            if(StringUtil.isNotBlank(user.getJpushId())) {
              //同一设备只能匹配一个账号
                sysOperatorService.editJpushIdByJpushId(user.getJpushId());
              //设备ID不为空 说明是手机端登录  更新操作员的设备ID
                sysOperatorService.updateJpushId(bean.getId(),user.getJpushId());
            }
            //
            // 获取所属公司
            SysCompany company = sysCompanyService.findById(bean.getCompanyId());
            if (null == company) {
                throw new BaseException("系统数据异常：用户所属单位不存在!");
            }
            if (company.getCoState() == null || company.getCoState() == 0) {
                throw new BaseException("用户所属单位已停用，请联系管理员");
            }
            //
            // 获取所属职员
            SysEmployee employee = sysEmployeeService.findById(bean.getEmpId());
            if (null == employee) {
                throw new BaseException("系统数据异常：用户所属员工不存在!");
            }
            if (employee.getEmpState() == null || employee.getEmpState() == 0) {
                throw new BaseException("用户所属员工已停用，请联系管理员");
            }
            bean.setLegalEntityId(employee.getLegalEntityId());
            bean.setLegalEntityName(employee.getLegalEntityName());
            bean.setDeptName(employee.getDeptName());
            bean.setDeptId(employee.getDeptId());
            bean.setCoSupLegalCode(company.getCoSupLegalCode());
            bean.setCoLevel(company.getCoLevel());
            if (bean.getOptState() == null || bean.getOptState() == 0) {
                throw new BaseException("该账号已停用，请联系管理员");
            }

            Date sysDate = sysLoginService.getSysDate();
            bean.setCoName(company.getCoName());
            bean.setCoAbbreviate(company.getCoAbbreviate());
            bean.setEmpName(employee.getEmpName());
            bean.setSysDate(DateUtil.getDateString(sysDate, DateUtil.G_DATE_FORMAT) + " " + DateUtil.getWeekOfDate(sysDate));

            // 缓存用户信息到redis
            ManagerUser managerUser = new ManagerUser();
            BeanUtils.copyProperties(bean, managerUser);
            this.login(managerUser);//处理token
            
            this.loadDataPermission(managerUser.getId());//加载数据权限
            
            // 清除过期验证码
            session.removeAttribute(MayaConstant.LOGIN.MANAGER_LOGIN_RANDOM);
            this.saveLoginLog(userId, "登录成功");
            return JsonModel.dataResult(managerUser, "登录成功");
        } catch (BaseException ex) {
            logger.error("登录异常", ex);
            this.saveLoginLog(userId, ex.getMessage());
            return JsonModel.mkFaile(ex.getMessage());
        } catch (Exception ex) {
            this.saveLoginLog(userId, ex.getMessage());
            logger.error("登录异常", ex);
        }
        return JsonModel.mkFaile("系统异常");
    }
    
    
    @GetMapping("/loginAuto")
    public JsonModel  loginAuto (@RequestParam("code") String code) {
        logger.info("微信网页授权开始:code={}",code);
        WxOAuth2Service oAuth2Service =   wxMpService.getOAuth2Service();
        WxOAuth2AccessToken oAuth2AccessToken = null;
        String userId = null;
        try {
            oAuth2AccessToken =  oAuth2Service.getAccessToken(code);
        } catch (WxErrorException e) {
            logger.error("微信网页授权异常{}",e);
            e.printStackTrace();
            return JsonModel.mkFaile("登录失败:{"+e.getMessage()+"}");
        }
        try {
            String openId = oAuth2AccessToken.getOpenId();
            //根据openId查询用户信息
            SysOperator operator =  sysOperatorService.findByOpenId(openId);
            if(operator == null) {
                logger.error("获取用户信息为空：{}",openId);
                return JsonModel.mkFaile("未获取到操作员信息");
            }
            
            if(StringUtil.isNotBlank(operator.getJpushId())) {
                //同一设备只能匹配一个账号
                  sysOperatorService.editJpushIdByJpushId(operator.getJpushId());
                //设备ID不为空 说明是手机端登录  更新操作员的设备ID
                  sysOperatorService.updateJpushId(operator.getId(),operator.getJpushId());
            }
            // 获取所属公司
            SysCompany company = sysCompanyService.findById(operator.getCompanyId());
            if (null == company) {
                throw new BaseException("系统数据异常：用户所属单位不存在!");
            }
            if (company.getCoState() == null || company.getCoState() == 0) {
                throw new BaseException("用户所属单位已停用，请联系管理员");
            }

            // 获取所属职员
            SysEmployee employee = sysEmployeeService.findById(operator.getEmpId());
            if (null == employee) {
                throw new BaseException("系统数据异常：用户所属员工不存在!");
            }
            if (employee.getEmpState() == null || employee.getEmpState() == 0) {
                throw new BaseException("用户所属员工已停用，请联系管理员");
            }
            operator.setLegalEntityId(employee.getLegalEntityId());
            operator.setLegalEntityName(employee.getLegalEntityName());
            operator.setDeptName(employee.getDeptName());
            operator.setDeptId(employee.getDeptId());
            operator.setCoSupLegalCode(company.getCoSupLegalCode());
            operator.setCoLevel(company.getCoLevel());
            if (operator.getOptState() == null || operator.getOptState() == 0) {
                throw new BaseException("该账号已停用，请联系管理员");
            }

            Date sysDate = sysLoginService.getSysDate();
            operator.setCoName(company.getCoName());
            operator.setCoAbbreviate(company.getCoAbbreviate());
            operator.setEmpName(employee.getEmpName());
            operator.setSysDate(DateUtil.getDateString(sysDate, DateUtil.G_DATE_FORMAT) + " " + DateUtil.getWeekOfDate(sysDate));

            // 缓存用户信息到redis
            ManagerUser managerUser = new ManagerUser();
            BeanUtils.copyProperties(operator, managerUser);
            this.login(managerUser);//处理token
            
            this.loadDataPermission(managerUser.getId());//加载数据权限
            logger.info("微信自动登录成功");
            return JsonModel.dataResult(managerUser); 
        } catch (BaseException ex) {
            logger.error("登录异常", ex);
            this.saveLoginLog(userId, ex.getMessage());
            return JsonModel.mkFaile(ex.getMessage());
        } catch (Exception ex) {
            this.saveLoginLog(userId, ex.getMessage());
            logger.error("登录异常", ex);
        }
        return JsonModel.mkFaile("系统异常");

    }

    /**
     * 此接口为退出登录接口
     * 
     * @return 登录ctrl
     */
    @PostMapping("/logout")
    public JsonModel logout(HttpServletRequest request) {
        //清除session
        ManagerUser user =   super.getManagerUser();
        if (user != null) {
            if (sysLoginService.getSingleFlag()) {
                sysLoginService.downlineOpt(user.getOptUserid(), MayaConstant.SysType.MANAGER, null);
            }
            redisCacheService.deleteInMap(MayaConstant.CACHE.MANAGE_LOGIN_SESSION_ID, user.getOptUserid());
            //redisCacheService.deleteInMap(MayaConstant.CACHE.MANAGER_LOGIN_USER_KEY, user.getId());
        }
        //退出登陆 清空jpushId
        sysOperatorService.updateJpushId(user.getId(),"");
        //调用接口删除认证中心登录信息
        return JsonModel.mkFaile("退出成功");
    }
    
    private void loadDataPermission(String optId) {
        /**用户数据权限**/
        //机构权限
        Map<String,Map<String,Set<String>>> map =vPermissionAllotDataService.findOptPermission(optId,BaseConstant.DataperCode.COMPANY_CODE);
        if(null!=map.get("readMap") &&map.get("readMap").size() > 0) {
            redisCacheService.addMapValue(MayaConstant.CACHE.PERMISSION_COMPANY_READ, optId, map.get("readMap"));
        }
        if(null!=map.get("writeMap") &&map.get("writeMap").size() > 0) {
            redisCacheService.addMapValue(MayaConstant.CACHE.PERMISSION_COMPANY_WRITE, optId, map.get("writeMap"));
        }
        //部门权限
        map =vPermissionAllotDataService.findOptPermission(optId,BaseConstant.DataperCode.DEPT_CODE);
        if(null!=map.get("readMap") &&map.get("readMap").size() > 0) {
            redisCacheService.addMapValue(MayaConstant.CACHE.PERMISSION_DEPT_READ, optId, map.get("readMap"));
        }
        if(null!=map.get("writeMap") &&map.get("writeMap").size() > 0) {
            redisCacheService.addMapValue(MayaConstant.CACHE.PERMISSION_DEPT_WRITE, optId, map.get("writeMap"));
        }
        //仓库权限
        map =vPermissionAllotDataService.findOptPermission(optId,BaseConstant.DataperCode.WARE_CODE);
        if(null!=map.get("readMap") &&map.get("readMap").size() > 0) {
            redisCacheService.addMapValue(MayaConstant.CACHE.PERMISSION_WARE_READ, optId, map.get("readMap"));
        }
        if(null!=map.get("writeMap") &&map.get("writeMap").size() > 0) {
            redisCacheService.addMapValue(MayaConstant.CACHE.PERMISSION_WARE_WRITE, optId, map.get("writeMap"));
        }
        //业务员
        map =vPermissionAllotDataService.findOptPermission(optId,BaseConstant.DataperCode.EMP_CODE);
        if(null!=map.get("readMap") &&map.get("readMap").size() > 0) {
            redisCacheService.addMapValue(MayaConstant.CACHE.PERMISSION_EMP_READ, optId, map.get("readMap"));
        }
        if(null!=map.get("writeMap") &&map.get("writeMap").size() > 0) {
            redisCacheService.addMapValue(MayaConstant.CACHE.PERMISSION_EMP_WRITE, optId, map.get("writeMap"));
        }
        //往来单位
        map =vPermissionAllotDataService.findOptPermission(optId,BaseConstant.DataperCode.ORG_CODE);
        if(null!=map.get("readMap") &&map.get("readMap").size() > 0) {
            redisCacheService.addMapValue(MayaConstant.CACHE.PERMISSION_ORG_READ, optId, map.get("readMap"));
        }
        if(null!=map.get("writeMap") &&map.get("writeMap").size() > 0) {
            redisCacheService.addMapValue(MayaConstant.CACHE.PERMISSION_ORG_WRITE, optId, map.get("writeMap"));
        }
        /**用户数据权限**/
    }


    /**
     * 此接口为请求验证码
     * 
     * @return 验证码图片
     */

    @PostMapping("/captcha")
    public JsonModel captcha(HttpSession session) throws IOException {
        // 生成文字验证码
        String text = producer.createText();
        System.out.println("后台登录验证码：" + text);
        session.setAttribute(MayaConstant.LOGIN.MANAGER_LOGIN_RANDOM, text);

        // 生成图片验证码
        ByteArrayOutputStream outputStream = null;
        BufferedImage image = producer.createImage(text);
        outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        byte[] captchaChallengeAsJpeg = outputStream.toByteArray();

        return JsonModel.dataResult(captchaChallengeAsJpeg);
    }

    private void saveLoginLog(String userId, String resMsg) {
        SysOperator opt = sysOperatorService.findByOptUserid(userId);
        LogOperateData logData = new LogOperateData();
        logData.setId(UUID.randomUUID().toString());
        if (null != opt) {
            logData.setUserid(opt.getOptUserid());
            logData.setUserName(opt.getOptUsername());
        }
        logData.setLogTitle("后台登录管理");
        logData.setRq(new Date());
        logData.setSiteType(Runtime.get(MayaConstant.SysType.RUN_TYPE));
        logData.setType(Runtime.get(MayaConstant.SysType.SITE_TYPE));
        logData.setRzType(LogType.TYPE2.getKey());
        logData.setResMsg(resMsg);
        logData.setLogLevel(LogLevel.LEVEL1.name());
        logData.setUip(HttpUtil.getIpAddr());

        this.logOperateDataService.add(logData);
    }
    
        //只要是登录了，所有的东西重新生成
        private TokenInfo login(ManagerUser user) {
            if (user == null) {
                throw new BaseException("操作员信息不能为空");
            }           
            String optId = user.getId();
            String accessToken = JwtUtils.createAccessToken(optId); 
            String refreshToken = JwtUtils.createRefreshToken(optId);
            user.setAccessToken(accessToken);
            user.setRefreshToken(refreshToken);
            redisCacheService.addMapValue(MayaConstant.CACHE.MANAGER_LOGIN_USER_KEY, optId, user);
            return new TokenInfo(accessToken,refreshToken);
    }
}

class TokenInfo{
    
    private String accessToken;
    private String refreshToken;
    
    public TokenInfo(String accessToken, String refreshToken) {
        super();
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
    public String getAccessToken() {
        return accessToken;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    
}
