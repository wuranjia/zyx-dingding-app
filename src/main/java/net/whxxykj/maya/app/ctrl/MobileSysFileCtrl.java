package net.whxxykj.maya.app.ctrl;

import net.whxxykj.maya.base.entity.SysFile;
import net.whxxykj.maya.base.service.SysFileService;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.JsonModel;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


import java.util.List;


/************************************************
 * Copyright (c)  by whxxykj
 * All right reserved.
 * Create Author: jm
 * Create Date  : 2018-11-07
 * Last version : 1.0
 * Description  : 系统文件Ctrl
 * Last Update Date:
 * Change Log:
 **************************************************/

/**
 * 此类为系统文件接口类
 */
@RestController
@RequestMapping("/mobile/sysm/file")
public class MobileSysFileCtrl extends BaseCtrl<SysFileService, SysFile> {

    @Autowired
    private SysFileService sysFileService;

    /**
     * 此接口为分页查询文件列表
     * @param queryBean 通用查询对象
     * @return
     */
    @PostMapping("/findPageList")
    public JsonModel findPageList(@RequestBody QueryBean queryBean){
        Page<SysFile> page = sysFileService.queryPageList(queryBean);
        return JsonModel.dataResult(page.getTotalElements(), page.getContent());
    }

    /**
     * 此接口为根据文件Id删除文件记录
     * @param id 文件ID
     * @return
     */
    @GetMapping("/delSysFile")
    public JsonModel delSysFile(String id){
        try {
            sysFileService.deleteById(id);
        } catch (Exception e) {
        	logger.error("删除异常", e);
            return JsonModel.mkFaile("删除失败");
        }
        return JsonModel.mkSuccess("删除成功");
    }

    /**
     * 此接口为根据文件Id查询文件信息
     * @param id 文件ID
     * @return
     */
    @GetMapping("/findById")
    public JsonModel findById(String id){
        SysFile sysFile = sysFileService.getOne(id);
        return JsonModel.dataResult(sysFile);
    }

    /**
     * 此接口为根据大类和对象ID查询文件集合
     * @param category 大类
     * @param objId 对象ID
     * @return
     */
    @GetMapping("/findByCategoryAndObjId")
    public JsonModel findByCategoryAndObjId(String category, String objId){
        List<SysFile> list = sysFileService.findByCategoryAndObjId(category, objId);
        return JsonModel.dataResult(list.size(), list);
    }

    /**
     * 此接口为根据大类查询文件集合
     * @param category 大类
     * @return
     */
    @GetMapping("/findByCategory")
    public JsonModel findByCategory(String category){
        List<SysFile> list = sysFileService.findByCategory(category);
        return JsonModel.dataResult(list.size(), list);
    }
    
    @PostMapping(value = "/addBatch")
    @ResponseBody
    public JsonModel add(@RequestBody List<SysFile> files) {
        if(CollectionUtils.isNotEmpty(files)) {
            for(SysFile file : files) {
                sysFileService.add(file);    
            }
        }
        return JsonModel.dataResult("新增成功");
    }
    
    /**
     * 此接口为根据对象ID查询文件集合
     * @param category 大类
     * @param objId 对象ID
     * @return
     */
    @GetMapping("/findByObjId")
    public JsonModel findByObjId(String objId){
        List<SysFile> list = sysFileService.findByCategoryAndObjId("30000", objId);
        if(null==list||CollectionUtils.isEmpty(list)) {
            list = sysFileService.findByCategoryAndObjId("10000", objId);
        }
        return JsonModel.dataResult(list.size(), list);
    }
}
