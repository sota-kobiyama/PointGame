package com.ksinfo.pointGame.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GameDTO {
	
	//ポイント情報テーブル
	private String member_id = "";
	private int point = 0;
	private String hide_num = "";
	private int game_count = 0;
	private int game_act_flg = 0;
	private LocalDate rec_update_date;
	
	//ゲーム結果履歴情報テーブル
	private List<ResultList> resultList = new ArrayList<>();
	private LocalDate game_date;
	
	private boolean success = false;
	private String popup_msg = "";
	private String err_msg = "";
	
	@Data
	public static class ResultList {
		private String input_num = "";
		private String result = "";
	}

}
