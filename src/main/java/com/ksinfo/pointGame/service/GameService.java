package com.ksinfo.pointGame.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ksinfo.pointGame.dao.GameDAO;
import com.ksinfo.pointGame.dto.GameDTO;

@Service
public class GameService {

	@Autowired
	GameDAO gdao;

	public GameDTO gameInit(String member_id) {

		GameDTO gdto = new GameDTO();
		try {
			//ポイント、ゲーム結果履歴取得
			gdto.setPoint(gdao.getPointInfo(member_id));
			gdto.setResultList(gdao.getResultInfo(member_id, LocalDate.now()));
			
			List<GameDTO.ResultList> resultList = gdto.getResultList();
			int game_count = resultList.size();
			String hide_num;
			//当日ゲーム初期化処理
			if (game_count < 1) {
				Random random = new Random();
				while (true) {
					hide_num = String.format("%03d", random.nextInt(1000));
					if (hide_num.charAt(0) != hide_num.charAt(1)
							&& hide_num.charAt(0) != hide_num.charAt(2)
							&& hide_num.charAt(1) != hide_num.charAt(2)) {
						break;
					}
				}
			} else {
				hide_num = gdao.getHideNum(member_id, LocalDate.now());
				//ゲーム履歴が10回以上 or 最後の履歴が「あたり」の場合、当日ゲーム終了と判定する
				if (game_count >= 10 || resultList.get(game_count - 1).getResult().equals("あたり")) {
				//if (game_count >= 10 || hide_num.equals(resultList.get(game_count - 1).getInput_num())) {
					gdto.setPopup_msg("本日のゲームは実施済みです");
					gdto.setGame_act_flg(1);
				}
			}
			gdto.setHide_num(hide_num);
			gdto.setGame_count(game_count);
			gdto.setSuccess(true);
		} catch (DataAccessException e) {
			gdto.setSuccess(false);
			gdto.setErr_msg("システムエラーが発生しました");
			System.out.println("データベースの接続に失敗しました: " + e);
			return gdto;
		} catch (Exception e) {
			gdto.setSuccess(false);
			gdto.setErr_msg("システムエラーが発生しました");
			System.out.println("システムエラーが発生しました: " + e);
		}
		return gdto;
	}

	public GameDTO playGame(String member_id, String input_num, String hide_num, int game_count, int point) {
		GameDTO gdto = new GameDTO();
		try {
			//判定結果作成
			game_count++;
			int I = 0;
			int J = 0;
			int s = 0;
			int b = 0;

			while (I < 3) {
				while (J < 3) {
					if (hide_num.charAt(I) == input_num.charAt(J)) {
						if (I == J) {
							s++;
						} else {
							b++;
						}
						break;
					} else {
						J++;
					}
				}
				I++;
				J = 0;
			}

			String result;
			if (s > 2) {
				result = "あたり";
			} else if (s > 0 || b > 0) {
				result = (s > 0 ? s + "S" : "") + (b > 0 ? b + "B" : "");
			} else {
				result = "はずれ";
			}

			int game_act_flg = 0;
			String popup_msg = "";

			if (s > 2) {
				switch (game_count) {
				case 1, 2, 3, 4, 5:
					point += 1000;
					popup_msg = "挑戦に成功しました！ 獲得ポイント：1000";
					break;
				case 6, 7:
					point += 500;
					popup_msg = "挑戦に成功しました！ 獲得ポイント：500";
					break;
				case 8, 9, 10:
					point += 200;
					popup_msg = "挑戦に成功しました！ 獲得ポイント：200";
					break;
				}
				//ポイント情報テーブル更新
				gdao.setPointInfo(member_id, point);
				game_act_flg = 1;
			} else if (game_count > 9) {
				popup_msg = "挑戦に失敗しました...";
				game_act_flg = 1;
			}
			//ゲーム履歴情報テーブル追加
			gdao.setResultInfo(member_id, game_count, hide_num, input_num, result, LocalDate.now());
			//結果をDTOに格納
			gdto.setHide_num(hide_num);
			gdto.setResultList(gdao.getResultInfo(member_id, LocalDate.now()));
			gdto.setGame_count(game_count);
			gdto.setGame_act_flg(game_act_flg);
			gdto.setPoint(point);
			gdto.setPopup_msg(popup_msg);
			gdto.setSuccess(true);
			
		} catch (DataAccessException e) {
			gdto.setSuccess(false);
			gdto.setErr_msg("システムエラーが発生しました");
			System.out.println("データベースの接続に失敗しました: " + e);
			return gdto;

		} catch (Exception e) {
			gdto.setSuccess(false);
			gdto.setErr_msg("システムエラーが発生しました");
			System.out.println("システムエラーが発生しました: " + e);
			return gdto;
		}
		return gdto;
	}
}

