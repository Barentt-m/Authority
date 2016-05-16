package com.authority.entity;

import com.authority.annotation.TableSeg;
import com.authority.util.FormMap;



/**
 * 角色关联权限资源实体表
 */
@TableSeg(tableName = "ly_role_res", id="roleId")
public class RoleResFormMap extends FormMap<String,Object>{
	private static final long serialVersionUID = 1L;

}
