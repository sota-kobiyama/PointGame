package com.ksinfo.pointGame.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class GameDTO {
	
	//ポイント情報テーブル
	private int point = 0;
	private String hide_num = "";
	private List<ResultList> resultList = new ArrayList<>();
	private int game_count = 0;
	private int game_act_flg = 0;
	private String popup_msg = "";
	private boolean success = false;
	private String err_msg = "";
	
	@Data
	public static class ResultList {
		private String input_num = "";
		private String result = "";
	}

}
