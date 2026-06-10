package com.example.usermanagement.mapper;

import com.example.usermanagement.entity.User;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据访问层
 */
public interface UserMapper {
    
    /**
     * 根据用户名查询用户
     */
    User selectByLoginName(@Param("floginname") String floginname);
    
    /**
     * 根据ID查询用户
     */
    User selectById(@Param("fid") Long fid);
    
    /**
     * 新增用户
     */
    int insert(User user);
    
    /**
     * 更新用户
     */
    int update(User user);
    
    /**
     * 删除用户
     */
    int deleteById(@Param("fid") Long fid);
    
    /**
     * 查询用户列表（不分页）
     */
    List<User> selectList(@Param("fid") Long fid,
                          @Param("unfid") Long unfid,
                          @Param("floginname") String floginname,
                          @Param("fnickname") String fnickname,
                          @Param("fstate") Integer fstate,
                          @Param("fbirthdaybegin") LocalDate fbirthdaybegin,
                          @Param("fbirthdayend") LocalDate fbirthdayend,
                          @Param("fphonenumber") String fphonenumber,
                          @Param("femail") String femail,
                          @Param("ftype") Integer ftype,
                          @Param("fcreatedatetimebegin") LocalDateTime fcreatedatetimebegin,
                          @Param("fcreatedatetimeend") LocalDateTime fcreatedatetimeend,
                          @Param("fcreateuserid") Long fcreateuserid,
                          @Param("fcondition") String fcondition);
    
    /**
     * 查询用户列表（分页）
     */
    List<User> selectListByPage(@Param("fid") Long fid,
                                @Param("unfid") Long unfid,
                                @Param("floginname") String floginname,
                                @Param("fnickname") String fnickname,
                                @Param("fstate") Integer fstate,
                                @Param("fbirthdaybegin") LocalDate fbirthdaybegin,
                                @Param("fbirthdayend") LocalDate fbirthdayend,
                                @Param("fphonenumber") String fphonenumber,
                                @Param("femail") String femail,
                                @Param("ftype") Integer ftype,
                                @Param("fcreatedatetimebegin") LocalDateTime fcreatedatetimebegin,
                                @Param("fcreatedatetimeend") LocalDateTime fcreatedatetimeend,
                                @Param("fcreateuserid") Long fcreateuserid,
                                @Param("fcondition") String fcondition,
                                @Param("offset") Integer offset,
                                @Param("pagesize") Integer pagesize);
    
    /**
     * 查询用户总数（分页用）
     */
    int selectCount(@Param("fid") Long fid,
                    @Param("unfid") Long unfid,
                    @Param("floginname") String floginname,
                    @Param("fnickname") String fnickname,
                    @Param("fstate") Integer fstate,
                    @Param("fbirthdaybegin") LocalDate fbirthdaybegin,
                    @Param("fbirthdayend") LocalDate fbirthdayend,
                    @Param("fphonenumber") String fphonenumber,
                    @Param("femail") String femail,
                    @Param("ftype") Integer ftype,
                    @Param("fcreatedatetimebegin") LocalDateTime fcreatedatetimebegin,
                    @Param("fcreatedatetimeend") LocalDateTime fcreatedatetimeend,
                    @Param("fcreateuserid") Long fcreateuserid,
                    @Param("fcondition") String fcondition);
    
    /**
     * 查询所有创建人（用于下拉筛选）
     */
    List<User> selectAllCreators();
}
