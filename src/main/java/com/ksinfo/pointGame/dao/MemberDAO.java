package com.ksinfo.pointGame.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MemberDAO {

    @Select("SELECT COUNT(1) "
    		+ "FROM MEMBERINFO "
    		+ "WHERE member_id = #{member_id} "
    		+ "AND password = #{password}")
    int chkMember(String member_id, String password);
}
