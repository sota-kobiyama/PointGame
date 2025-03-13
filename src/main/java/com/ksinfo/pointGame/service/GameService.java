package com.ksinfo.pointGame.service;

import java.time.LocalDate;
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
			//ポイント情報テーブル取得
			try {
				gdto = gdao.getPointInfo(member_id);
			} catch (DataAccessException e) {
				gdto = new GameDTO();
				gdto.setMember_id(member_id);
				gdto.setSuccess(false);
				gdto.setErr_msg("システムエラーが発生しました");
				System.out.println("データベースの接続に失敗しました");
				return gdto;
			}

			gdto.setMember_id(member_id);
			LocalDate rec_update_date = gdto.getRec_update_date();

			//当日ゲーム初期化処理
			if (!(rec_update_date.equals(LocalDate.now()))) {
				Random random = new Random();
				String hide_num;
				while (true) {
					hide_num = String.format("%03d", random.nextInt(1000));
					if (hide_num.charAt(0) != hide_num.charAt(1)
							&& hide_num.charAt(0) != hide_num.charAt(2)
							&& hide_num.charAt(1) != hide_num.charAt(2)) {
						break;
					}
				}
				int game_count = 0;
				int game_act_flg = 0;
				
				try {
					gdao.setHideNum(member_id, hide_num, game_count, game_act_flg);
					gdto.setHide_num(hide_num);
					gdto.setGame_count(game_count);
					gdto.setGame_act_flg(game_act_flg);
				} catch (DataAccessException e) {
					gdto.setSuccess(false);
					gdto.setErr_msg("システムエラーが発生しました");
					System.out.println("データベースの接続に失敗しました");
					return gdto;
				}
			}

			//ゲーム履歴情報取得
			try {
				gdto.setResultList(gdao.getResultInfo(member_id, LocalDate.now()));
			} catch (DataAccessException e) {
				gdto.setSuccess(false);
				gdto.setErr_msg("システムエラーが発生しました");
				System.out.println("データベースの接続に失敗しました");
				return gdto;
			}
			//当日ゲーム終了有無判定
			if (gdto.getGame_act_flg() == 1) {
				gdto.setPopup_msg("本日のゲームは実施済みです");
			}
			gdto.setSuccess(true);

		} catch (Exception e) {
			gdto.setSuccess(false);
			gdto.setErr_msg("システムエラーが発生しました");
			System.out.println("システムエラーが発生しました");
		}
		return gdto;
	}

	public GameDTO playGame(GameDTO gdto, String input_num) {
		try {
			int game_count = gdto.getGame_count();
			String hide_num = gdto.getHide_num();

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

			if (s == 3) {
				result = "あたり";
			} else if (s > 0 && b > 0) {
				result = s + "S" + b + "B";
			} else if (s > 0) {
				result = s + "S";
			} else if (b > 0) {
				result = b + "B";
			} else {
				result = "はずれ";
			}

			int point = gdto.getPoint();
			int game_act_flg = gdto.getGame_act_flg();
			String popup_msg = gdto.getPopup_msg();

			if (result.equals("あたり")) {
				switch (game_count) {
				case 1, 2, 3, 4, 5:
					point = point + 1000;
					popup_msg = "挑戦に成功しました！ 獲得ポイント：1000";
					break;
				case 6, 7:
					point = point + 500;
					popup_msg = "挑戦に成功しました！ 獲得ポイント：500";
					break;
				case 8, 9, 10:
					point = point + 200;
					popup_msg = "挑戦に成功しました！ 獲得ポイント：200";
					break;
				}
				game_act_flg = 1;
			}

			if (!(result.equals("あたり")) && game_count == 10) {
				popup_msg = "挑戦に失敗しました...";
				game_act_flg = 1;
			}

			String member_id = gdto.getMember_id();
			LocalDate game_date = LocalDate.now();

			try {
				//ゲーム履歴情報テーブル追加
				gdao.setResultInfo(member_id, game_count, input_num, result, game_date);
				//ポイント情報テーブル更新
				gdao.setPointInfo(member_id, point, game_count, game_act_flg);

			} catch (DataAccessException e) {
				gdto.setSuccess(false);
				gdto.setErr_msg("システムエラーが発生しました");
				System.out.println("データベースの接続に失敗しました");
				return gdto;
			}

			//結果をDTOに格納
			gdto.setMember_id(member_id);
			gdto.setResultList(gdao.getResultInfo(member_id, game_date));
			gdto.setPoint(point);
			gdto.setGame_count(game_count);
			gdto.setGame_act_flg(game_act_flg);
			gdto.setPopup_msg(popup_msg);
			gdto.setSuccess(true);

		} catch (Exception e) {
			gdto.setSuccess(false);
			gdto.setErr_msg("システムエラーが発生しました");
			System.out.println("システムエラーが発生しました");
			return gdto;
		}

		return gdto;
	}

}
