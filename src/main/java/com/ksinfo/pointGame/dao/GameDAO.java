package com.ksinfo.pointGame.dao;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ksinfo.pointGame.dto.GameDTO;

@Mapper
public interface GameDAO {

	@Select("SELECT point "
			+ "FROM POINTINFO "
			+ "WHERE member_id=#{member_id}")
	int getPointInfo(String member_id);

	@Select("SELECT hide_num "
			+ "FROM RESULTINFO "
			+ "WHERE member_id=#{member_id} "
			+ "AND game_date=#{game_date} "
			+ "AND game_count=1")
	String getHideNum(String member_id, LocalDate game_date);

	@Select("SELECT input_num,result "
			+ "FROM RESULTINFO "
			+ "WHERE member_id=#{member_id} "
			+ "AND game_date=#{game_date} "
			+ "ORDER BY game_count ASC")
	List<GameDTO.ResultList> getResultInfo(String member_id, LocalDate game_date);

	@Insert("INSERT INTO RESULTINFO "
			+ "(member_id,game_count,hide_num,input_num,result,game_date) "
			+ "VALUES(#{member_id},#{game_count},#{hide_num},#{input_num},#{result},#{game_date})")
	int setResultInfo(String member_id, int game_count, String hide_num, String input_num, String result, LocalDate game_date);

	@Update("UPDATE POINTINFO "
			+ "SET point=#{point} "
			+ "WHERE member_id=#{member_id}")
	int setPointInfo(String member_id, int point);

}
