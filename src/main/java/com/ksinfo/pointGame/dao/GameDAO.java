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

	@Select("SELECT point, hide_num, game_count, game_act_flg,DATE(rec_update_date) AS rec_update_date "
			+ "FROM POINTINFO "
			+ "WHERE member_id=#{member_id}")
	GameDTO getPointInfo(String member_id);

	@Select("UPDATE POINTINFO "
			+ "SET hide_num = #{hide_num},"
			+ "game_count = #{game_count},"
			+ "game_act_flg = #{game_act_flg} "
			+ "WHERE member_id=#{member_id} "
			+ "RETURNING *")
	GameDTO setHideNum(String member_id, String hide_num, int game_count, int game_act_flg);

	@Select("SELECT input_num, result "
			+ "FROM RESULTINFO "
			+ "WHERE member_id=#{member_id} "
			+ "AND game_date=#{game_date} "
			+ "ORDER BY game_count ASC")
	List<GameDTO.ResultList> getResultInfo(String member_id, LocalDate game_date);

	@Insert("INSERT INTO RESULTINFO "
			+ "(member_id,game_count,input_num,result,game_date) "
			+ "VALUES(#{member_id},#{game_count},#{input_num},#{result},#{game_date})")
	int setResultInfo(String member_id, int game_count, String input_num, String result, LocalDate game_date);

	@Update("UPDATE POINTINFO "
			+ "SET point=#{point},"
			+ "game_count=#{game_count},"
			+ "game_act_flg=#{game_act_flg} "
			+ "WHERE member_id=#{member_id} "
			+ "RETURNING point,game_count,game_act_flg")
	int setPointInfo(String member_id, int point, int game_count, int game_act_flg);

}
