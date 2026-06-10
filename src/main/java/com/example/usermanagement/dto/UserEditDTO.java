package com.example.usermanagement.dto;

import com.example.usermanagement.entity.User;

/**
 * 用户编辑DTO - 包含密码，用于编辑时返回
 */
public class UserEditDTO {

    private Long fid;
    private String floginname;
    private String fpassword;
    private String fnickname;
    private Integer fstate;
    private String fstatename;
    private Integer ftype;
    private String ftypename;
    private String fbirthday;
    private String fphonenumber;
    private String femail;
    private Integer fsex;
    private String faddress;
    private String fidcard;
    private String fentrydate;
    private Long fdeptid;
    private String fposition;
    private Long fsuperiorid;

    public UserEditDTO() {}

    public UserEditDTO(User user) {
        this.fid = user.getFid();
        this.floginname = user.getFloginname();
        this.fpassword = user.getFpassword();
        this.fnickname = user.getFnickname();
        this.fstate = user.getFstate();
        this.fstatename = user.getFstatename();
        this.ftype = user.getFtype();
        this.ftypename = user.getFtypename();
        this.fbirthday = user.getFbirthday() != null ? user.getFbirthday().toString() : null;
        this.fphonenumber = user.getFphonenumber();
        this.femail = user.getFemail();
        this.fsex = user.getFsex();
        this.faddress = user.getFaddress();
        this.fidcard = user.getFidcard();
        this.fentrydate = user.getFentrydate();
        this.fdeptid = user.getFdeptid();
        this.fposition = user.getFposition();
        this.fsuperiorid = user.getFsuperiorid();
    }

    // Getters and Setters
    public Long getFid() { return fid; }
    public void setFid(Long fid) { this.fid = fid; }

    public String getFloginname() { return floginname; }
    public void setFloginname(String floginname) { this.floginname = floginname; }

    public String getFpassword() { return fpassword; }
    public void setFpassword(String fpassword) { this.fpassword = fpassword; }

    public String getFnickname() { return fnickname; }
    public void setFnickname(String fnickname) { this.fnickname = fnickname; }

    public Integer getFstate() { return fstate; }
    public void setFstate(Integer fstate) { this.fstate = fstate; }

    public String getFstatename() { return fstatename; }
    public void setFstatename(String fstatename) { this.fstatename = fstatename; }

    public Integer getFtype() { return ftype; }
    public void setFtype(Integer ftype) { this.ftype = ftype; }

    public String getFtypename() { return ftypename; }
    public void setFtypename(String ftypename) { this.ftypename = ftypename; }

    public String getFbirthday() { return fbirthday; }
    public void setFbirthday(String fbirthday) { this.fbirthday = fbirthday; }

    public String getFphonenumber() { return fphonenumber; }
    public void setFphonenumber(String fphonenumber) { this.fphonenumber = fphonenumber; }

    public String getFemail() { return femail; }
    public void setFemail(String femail) { this.femail = femail; }

    public Integer getFsex() { return fsex; }
    public void setFsex(Integer fsex) { this.fsex = fsex; }

    public String getFaddress() { return faddress; }
    public void setFaddress(String faddress) { this.faddress = faddress; }

    public String getFidcard() { return fidcard; }
    public void setFidcard(String fidcard) { this.fidcard = fidcard; }

    public String getFentrydate() { return fentrydate; }
    public void setFentrydate(String fentrydate) { this.fentrydate = fentrydate; }

    public Long getFdeptid() { return fdeptid; }
    public void setFdeptid(Long fdeptid) { this.fdeptid = fdeptid; }

    public String getFposition() { return fposition; }
    public void setFposition(String fposition) { this.fposition = fposition; }

    public Long getFsuperiorid() { return fsuperiorid; }
    public void setFsuperiorid(Long fsuperiorid) { this.fsuperiorid = fsuperiorid; }
}
