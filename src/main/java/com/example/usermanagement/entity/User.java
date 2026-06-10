package com.example.usermanagement.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
public class User {
    
    /** 主键 */
    private Long fid;
    
    /** 用户名 */
    private String floginname;
    
    /** 密码 */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String fpassword;
    
    /** 昵称 */
    private String fnickname;
    
    /** 状态（0=禁用、1=正常、2=冻结） */
    private Integer fstate;
    
    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fbirthday;
    
    /** 电话号码 */
    private String fphonenumber;
    
    /** 电子邮箱 */
    private String femail;
    
    /** 类型（0=注册、1=添加） */
    private Integer ftype;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fcreatedatetime;
    
    /** 创建人主键 */
    private Long fcreateuserid;
    
    /** 性别（0=保密、1=男、2=女） */
    private Integer fsex;
    
    /** 家庭住址 */
    private String faddress;
    
    /** 身份证号 */
    private String fidcard;
    
    /** 入职日期 */
    private String fentrydate;
    
    /** 所属部门ID */
    private Long fdeptid;
    
    /** 职位 */
    private String fposition;
    
    /** 直属上级ID */
    private Long fsuperiorid;
    
    // 扩展字段（用于返回给前端展示）
    /** 状态名称 */
    private String fstatename;
    
    /** 类型名称 */
    private String ftypename;
    
    /** 创建人名称 */
    private String fcreateusername;
    
    /** 部门名称 */
    private String fdeptname;
    
    /** 上级姓名 */
    private String fsuperiorname;
    
    public Long getFid() {
        return fid;
    }
    
    public void setFid(Long fid) {
        this.fid = fid;
    }
    
    public String getFloginname() {
        return floginname;
    }
    
    public void setFloginname(String floginname) {
        this.floginname = floginname;
    }
    
    public String getFpassword() {
        return fpassword;
    }
    
    public void setFpassword(String fpassword) {
        this.fpassword = fpassword;
    }
    
    public String getFnickname() {
        return fnickname;
    }
    
    public void setFnickname(String fnickname) {
        this.fnickname = fnickname;
    }
    
    public Integer getFstate() {
        return fstate;
    }
    
    public void setFstate(Integer fstate) {
        this.fstate = fstate;
    }
    
    public LocalDate getFbirthday() {
        return fbirthday;
    }
    
    public void setFbirthday(LocalDate fbirthday) {
        this.fbirthday = fbirthday;
    }
    
    public String getFphonenumber() {
        return fphonenumber;
    }
    
    public void setFphonenumber(String fphonenumber) {
        this.fphonenumber = fphonenumber;
    }
    
    public String getFemail() {
        return femail;
    }
    
    public void setFemail(String femail) {
        this.femail = femail;
    }
    
    public Integer getFtype() {
        return ftype;
    }
    
    public void setFtype(Integer ftype) {
        this.ftype = ftype;
    }
    
    public LocalDateTime getFcreatedatetime() {
        return fcreatedatetime;
    }
    
    public void setFcreatedatetime(LocalDateTime fcreatedatetime) {
        this.fcreatedatetime = fcreatedatetime;
    }
    
    public Long getFcreateuserid() {
        return fcreateuserid;
    }
    
    public void setFcreateuserid(Long fcreateuserid) {
        this.fcreateuserid = fcreateuserid;
    }
    
    public String getFstatename() {
        return fstatename;
    }
    
    public void setFstatename(String fstatename) {
        this.fstatename = fstatename;
    }
    
    public String getFtypename() {
        return ftypename;
    }
    
    public void setFtypename(String ftypename) {
        this.ftypename = ftypename;
    }
    
    public Integer getFsex() {
        return fsex;
    }
    
    public void setFsex(Integer fsex) {
        this.fsex = fsex;
    }
    
    public String getFaddress() {
        return faddress;
    }
    
    public void setFaddress(String faddress) {
        this.faddress = faddress;
    }
    
    public String getFidcard() {
        return fidcard;
    }
    
    public void setFidcard(String fidcard) {
        this.fidcard = fidcard;
    }
    
    public String getFentrydate() {
        return fentrydate;
    }
    
    public void setFentrydate(String fentrydate) {
        this.fentrydate = fentrydate;
    }
    
    public Long getFdeptid() {
        return fdeptid;
    }
    
    public void setFdeptid(Long fdeptid) {
        this.fdeptid = fdeptid;
    }
    
    public String getFposition() {
        return fposition;
    }
    
    public void setFposition(String fposition) {
        this.fposition = fposition;
    }
    
    public Long getFsuperiorid() {
        return fsuperiorid;
    }
    
    public void setFsuperiorid(Long fsuperiorid) {
        this.fsuperiorid = fsuperiorid;
    }
    
    public String getFcreateusername() {
        return fcreateusername;
    }
    
    public void setFcreateusername(String fcreateusername) {
        this.fcreateusername = fcreateusername;
    }
    
    public String getFdeptname() {
        return fdeptname;
    }
    
    public void setFdeptname(String fdeptname) {
        this.fdeptname = fdeptname;
    }
    
    public String getFsuperiorname() {
        return fsuperiorname;
    }
    
    public void setFsuperiorname(String fsuperiorname) {
        this.fsuperiorname = fsuperiorname;
    }
    
    /**
     * 获取状态名称
     */
    public void generateStateName() {
        if (this.fstate == null) {
            this.fstatename = "";
        } else {
            switch (this.fstate) {
                case 0:
                    this.fstatename = "禁用";
                    break;
                case 1:
                    this.fstatename = "正常";
                    break;
                case 2:
                    this.fstatename = "冻结";
                    break;
                default:
                    this.fstatename = "";
            }
        }
    }
    
    /**
     * 获取类型名称
     */
    public void generateTypeName() {
        if (this.ftype == null) {
            this.ftypename = "";
        } else {
            switch (this.ftype) {
                case 0:
                    this.ftypename = "注册";
                    break;
                case 1:
                    this.ftypename = "添加";
                    break;
                default:
                    this.ftypename = "";
            }
        }
    }
}
