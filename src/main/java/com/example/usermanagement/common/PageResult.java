package com.example.usermanagement.common;

import java.util.List;

/**
 * 分页查询结果封装
 */
public class PageResult<T> {
    
    private Integer pagenum;
    private Integer pagesize;
    private Long total;
    private List<T> list;
    
    public PageResult() {
    }
    
    public PageResult(Integer pagenum, Integer pagesize, Long total, List<T> list) {
        this.pagenum = pagenum;
        this.pagesize = pagesize;
        this.total = total;
        this.list = list;
    }
    
    public Integer getPagenum() {
        return pagenum;
    }
    
    public void setPagenum(Integer pagenum) {
        this.pagenum = pagenum;
    }
    
    public Integer getPagesize() {
        return pagesize;
    }
    
    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }
    
    public Long getTotal() {
        return total;
    }
    
    public void setTotal(Long total) {
        this.total = total;
    }
    
    public List<T> getList() {
        return list;
    }
    
    public void setList(List<T> list) {
        this.list = list;
    }
}
