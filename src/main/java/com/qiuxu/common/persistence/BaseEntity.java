package com.qiuxu.common.persistence;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Long createId; // 创建者
	protected Long updateId; // 更新者
	protected Date createDate; // 创建日期
	protected Date updateDate; // 更新日期
	protected String delFlag; // 删除标记（0：正常；1：删除；2：审核）
	protected String remarks; // 备注
	public Long getCreateId() {
		return createId;
	}
	public void setCreateId(Long createId) {
		this.createId = createId;
	}
	public Long getUpdateId() {
		return updateId;
	}
	public void setUpdateId(Long updateId) {
		this.updateId = updateId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	
	

}
