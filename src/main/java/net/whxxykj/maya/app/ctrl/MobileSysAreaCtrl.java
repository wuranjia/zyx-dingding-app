package net.whxxykj.maya.app.ctrl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.whxxykj.maya.base.BaseConstant;
import net.whxxykj.maya.base.entity.SysArea;
import net.whxxykj.maya.base.service.SysAreaService;
import net.whxxykj.maya.common.cache.CacheGroup;
import net.whxxykj.maya.common.cache.CacheLevel;
import net.whxxykj.maya.common.ctrl.BaseCtrl;
import net.whxxykj.maya.common.repository.QueryBean;
import net.whxxykj.maya.common.util.JsonModel;
import net.whxxykj.maya.plugin.cache.RedisCacheService;

@RestController
@RequestMapping("/mobile/sysm/area")
public class MobileSysAreaCtrl extends BaseCtrl<SysAreaService, SysArea>{

    @Autowired
    private SysAreaService areaService;

    @Autowired
    private RedisCacheService cacheService;

    /**
     * 此接口为所有地区分页查询
     * @param queryBean
     * @return
     */
    @PostMapping(value = "/findPageList")
    public JsonModel findPageList(@RequestBody QueryBean queryBean) {
        Page<SysArea> list = areaService.findPageList(queryBean);
        return JsonModel.dataResult(list.getTotalElements(), list.getContent());
    }

    /**
     * 此接口为根据地区Id查询地区详情
     * @param id 地区ID
     * @return
     */
    
    @GetMapping("/getById")
    public JsonModel getById(@RequestParam String id){
        SysArea area = areaService.getOne(id);
        return JsonModel.dataResult(area);
    }
    
    @Override
    public JsonModel findInfo(String ids) {
        String[] id = ids.split(",");
        List<SysArea> list = new ArrayList<SysArea>();
        for (int i = 0; i < id.length; i++) {
            SysArea mod = areaService.getOne(id[i]);
            list.add(mod);
        }
        return JsonModel.dataResult(list.size(), list);
    }

    /**
     * 此接口为新增地区接口
     * @param area 地区对象
     * @return
     */
    
    @PostMapping(value = "/addArea")
    public JsonModel addArea(@RequestBody  SysArea area) {
        areaService.addArea(area);
        return JsonModel.mkSuccess("新增成功，请更新缓存");
    }

    /**
     * 此接口为编辑地区接口
     * @param area 地区对象
     * @return
     */
    
    @PostMapping(value = "/editArea")
    public JsonModel editArea(@RequestBody  SysArea area) {
        areaService.editArea(area);
        return JsonModel.mkSuccess("编辑成功，请更新缓存");
    }

    /*
    @GetMapping(value = "/delArea")
    public JsonModel delArea(@ApiParam("地区ID") @RequestParam String id){
        areaService.delById(id);
        return JsonModel.mkSuccess("删除成功");
    }*/

    /**
     * 此接口为根据父级代码得到父级地区下所有的子级地区
     * @param areaPcode 地区CODE
     * @return
     */
    
    @GetMapping(value = "/findByAreaPcode")
    public JsonModel findByAreaPcode(@RequestParam String areaPcode) {
        List<SysArea> list = areaService.findByAreaPcode(areaPcode);
        if (CollectionUtils.isEmpty(list)) {
            return JsonModel.mkFaile("未查询出该地区子级地区，请核查地区信息表是否健全");
        }
        return JsonModel.dataResult(list.size(), list);
    }

    /**
     * 此接口为根据地区代码查询所有同父级地区
     * @param areaCode 地区CODE
     * @return
     */
    
    @GetMapping(value = "/findVavByCode")
    public JsonModel findVavByCode(@RequestParam String areaCode){
        List<SysArea> list = areaService.findVavByCode(areaCode);
        return JsonModel.dataResult(list.size(), list);
    }

    /**
     * 此接口为删除地区记录
     * @param ids 地区ID集合
     * @return
     */
    
    
    @GetMapping("/delBatch")
    public JsonModel delBatch(@RequestParam String ids) {
        areaService.deleteSysAreas(ids);
        return JsonModel.mkSuccess("删除成功，请更新缓存");
    }

    /**
     * 此接口为查询所有省份信息
     * @return
     */
    
    @GetMapping(value = "/findByProvince")
    public JsonModel findByProvince(){
        List<SysArea> list = areaService.findByAreaType(SysArea.Type.TYPE1);
        Collections.sort(list, new Comparator<SysArea>() {
            @Override
            public int compare(SysArea o1, SysArea o2) {
                //升序
                return o1.getAreaCode().compareTo(o2.getAreaCode());
            }
        });
        return JsonModel.dataResult(list.size(), list);
    }

    @GetMapping(value = "/findAllArea")
    public JsonModel findAllArea(@RequestParam(required=false) Integer level){
        String areaLevel = null;
        if(level == null || level == 3) {
            areaLevel = BaseConstant.PROVICE_3_LEVEL;
        }else if(level == 2) {
            areaLevel = BaseConstant.PROVICE_2_LEVEL;
        }
        CacheGroup cacheGroup = new CacheGroup(areaLevel,CacheLevel.LEVEL2);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("areaTree", cacheService.getList(cacheGroup));
        return JsonModel.dataResult(map);
    }
    
}

