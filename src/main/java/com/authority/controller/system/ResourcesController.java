package com.authority.controller.system;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.authority.annotation.SystemLog;
import com.authority.controller.index.BaseController;
import com.authority.entity.ButtomFormMap;
import com.authority.entity.Params;
import com.authority.entity.ResFormMap;
import com.authority.entity.ResUserFormMap;
import com.authority.entity.RoleResFormMap;
import com.authority.mapper.ResourcesMapper;
import com.authority.mapper.RoleResMapper;
import com.authority.util.Common;
import com.authority.util.TreeObject;
import com.authority.util.TreeUtil;

@Controller
@RequestMapping("/resources/")
public class ResourcesController extends BaseController {
	@Inject
	private ResourcesMapper resourcesMapper;//资源
	@Inject
	private RoleResMapper roleResMapper;//角色资源

	/**
	 * @param model
	 * 存放返回界面的model
	 * @return
	 */
	@ResponseBody
	@RequestMapping("treelists")
	public ResFormMap findByPage(Model model) {
		ResFormMap resFormMap = getFormMap(ResFormMap.class);
		String order = " order by level asc";
		resFormMap.put("$orderby", order);
		List<ResFormMap> mps = resourcesMapper.findByNames(resFormMap);
		List<TreeObject> list = new ArrayList<TreeObject>();
		for (ResFormMap map : mps) {
			TreeObject ts = new TreeObject();
			Common.flushObject(ts, map);
			list.add(ts);
		}
		TreeUtil treeUtil = new TreeUtil();
		List<TreeObject> ns = treeUtil.getChildTreeObjects(list, 0);
		resFormMap = new ResFormMap();
		resFormMap.put("treelists", ns);
		return resFormMap;
	}

	/**
	 * 新增菜单是获取上级菜单列表
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("reslists")
	public List<TreeObject> reslists(Model model) throws Exception {
		ResFormMap resFormMap = getFormMap(ResFormMap.class);
		resFormMap.put("where", " where ishide != 1");
		List<ResFormMap> mps = resourcesMapper.findByWhere(resFormMap);
		List<TreeObject> list = new ArrayList<TreeObject>();
		for (ResFormMap map : mps) {
			TreeObject ts = new TreeObject();
			Common.flushObject(ts, map);
			list.add(ts);
		}
		TreeUtil treeUtil = new TreeUtil();
		List<TreeObject> ns = treeUtil.getChildTreeObjects(list, 0, "　");
		return ns;
	}

	/**
	 * @param model
	 *            存放返回界面的model
	 * @return
	 */
	@RequestMapping("list")
	public String list(Model model) {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/system/resources/list";
	}

	/**
	 * 跳转到修改界面
	 * 
	 * @param model
	 * @param resourcesId
	 *            修改菜单信息ID
	 * @return
	 */
	@RequestMapping("editUI")
	public String editUI(Model model) {
		String id = getPara("id");
		if(Common.isNotEmpty(id)){
			model.addAttribute("resources", resourcesMapper.findbyFrist("id", id, ResFormMap.class));
		}
		return Common.BACKGROUND_PATH + "/system/resources/edit";
	}

	/**
	 * 跳转到新增界面
	 * 
	 * @return
	 */
	@RequestMapping("addUI")
	public String addUI(Model model) {
		return Common.BACKGROUND_PATH + "/system/resources/add";
	}

	/**
	 * 角色组/个人 权限分配页面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("permissions")
	public String permissions(Model model) {
		//判断操作类型type  1.组权限   2.个人权限
		String type = getPara("type");
		ResFormMap resFormMap = getFormMap(ResFormMap.class);
		List<ResFormMap> mps = resourcesMapper.findByWhere(resFormMap);
		List<TreeObject> list = new ArrayList<TreeObject>();
		for (ResFormMap map : mps) {
			TreeObject ts = new TreeObject();
			Common.flushObject(ts, map);
			list.add(ts);
		}
		TreeUtil treeUtil = new TreeUtil();
		List<TreeObject> ns = treeUtil.getChildTreeObjects(list, 0);
		model.addAttribute("permissions", ns);
		model.addAttribute("type", type);
		return Common.BACKGROUND_PATH + "/system/resources/permissions";
	}
	

	/**
	 * 添加菜单
	 * 
	 * @param resources
	 * @return Map
	 * @throws Exception
	 */
	@RequestMapping("addEntity")
	@ResponseBody
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="资源管理-新增资源")//凡需要处理业务逻辑的.都需要记录操作日志
	public String addEntity() throws Exception {
		ResFormMap resFormMap = getFormMap(ResFormMap.class);
		if("2".equals(resFormMap.get("type"))){
			resFormMap.put("description", Common.htmltoString(resFormMap.get("description")+""));
		}
		Object o = resFormMap.get("ishide");
		if(null==o){
			resFormMap.set("ishide", "0");
		}
		
		resourcesMapper.addEntity(resFormMap);
		return "success";
	}

	/**
	 * 更新菜单
	 * 
	 * @param model
	 * @param Map
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("editEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="资源管理-修改资源")//凡需要处理业务逻辑的.都需要记录操作日志
	public String editEntity(Model model) throws Exception {
		ResFormMap resFormMap = getFormMap(ResFormMap.class);
		if("2".equals(resFormMap.get("type"))){
			resFormMap.put("description", Common.htmltoString(resFormMap.get("description")+""));
		}
		Object o = resFormMap.get("ishide");
		if(null==o){
			resFormMap.set("ishide", "0");
		}
		resourcesMapper.editEntity(resFormMap);
		return "success";
	}

	/**
	 * 根据ID删除菜单
	 * 
	 * @param model
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("deleteEntity")
	@SystemLog(module="系统管理",methods="资源管理-删除资源")//凡需要处理业务逻辑的.都需要记录操作日志
	public String deleteEntity(Model model) throws Exception {
		String[] ids = getParaValues("ids");
		for (String id : ids) {
			resourcesMapper.deleteByAttribute("id", id, ResFormMap.class);
		};
		return "success";
	}

	@RequestMapping("sortUpdate")
	@ResponseBody
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	public String sortUpdate(Params params) throws Exception {
		List<String> ids = params.getId();
		List<String> es = params.getRowId();
		List<ResFormMap> maps = new ArrayList<ResFormMap>();
		for (int i = 0; i < ids.size(); i++) {
			ResFormMap map = new ResFormMap();
			map.put("id", ids.get(i));
			map.put("level", es.get(i));
			maps.add(map);
		}
		resourcesMapper.updateSortOrder(maps);
		return "success";
	}

	/**
	 * 权限资源checkbox选中
	 * @return
	 */
	@ResponseBody
	@RequestMapping("findRes")
	public List<ResFormMap> findUserRes() {
		//判断操作类型type  1.组权限   2.个人权限
		String type = getPara("type");
		List<ResFormMap> rs = new ArrayList<ResFormMap>();
		ResFormMap resFormMap = getFormMap(ResFormMap.class);
		if("1".equals(type)){
			rs = resourcesMapper.findRoleResorucess(resFormMap);
		}
		if("2".equals(type)){
			rs = resourcesMapper.findRes(resFormMap);
		}
		return rs;
	}
	
	
	@ResponseBody
	@RequestMapping("addUserRes")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="用户管理-修改权限")//凡需要处理业务逻辑的.都需要记录操作日志
	public String addUserRes() throws Exception {
		String u = getPara("userId");
		resourcesMapper.deleteByAttribute("userId", u, ResUserFormMap.class);
		String[] s = getParaValues("resId[]");
		List<ResUserFormMap> resUserFormMaps = new ArrayList<ResUserFormMap>();
		for (String rid : s) {
			ResUserFormMap resUserFormMap = new ResUserFormMap();
			resUserFormMap.put("resId", rid);
			resUserFormMap.put("userId", u);
			resUserFormMaps.add(resUserFormMap);
		}
		resourcesMapper.batchSave(resUserFormMaps);
		return "success";
	}
	
	@ResponseBody
	@RequestMapping("addRoleRes")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="角色管理-修改权限")//凡需要处理业务逻辑的.都需要记录操作日志
	public String addRoleRes() throws Exception {
		String roleId = getPara("roleId");
		String[] s = getParaValues("resId[]");
		List<RoleResFormMap> resRoleFormMaps = new ArrayList<RoleResFormMap>();
		roleResMapper.deleteByAttribute("roleId", roleId, RoleResFormMap.class);
		for (String rid : s) {
			RoleResFormMap roleResFormMap = new RoleResFormMap();
			roleResFormMap.put("resId", rid);
			roleResFormMap.put("roleId", roleId);
			resRoleFormMaps.add(roleResFormMap);
		}
		roleResMapper.batchSave(resRoleFormMaps);
		return "success";
	}

	@ResponseBody
	@RequestMapping("findByButtom")
	public List<ButtomFormMap> findByButtom(){
		return resourcesMapper.findByWhere(new ButtomFormMap());
	}
	
	/**
	 * 验证菜单是否存在
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping("isExist")
	@ResponseBody
	public boolean isExist(String name,String resKey) {
		ResFormMap resFormMap = getFormMap(ResFormMap.class);
		List<ResFormMap> r = resourcesMapper.findByNames(resFormMap);
		if (r.size()==0) {
			return true;
		} else {
			return false;
		}
	}
}