package com.ksinfo.pointGame.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ksinfo.pointGame.dao.MemberDAO;
import com.ksinfo.pointGame.dto.MemberDTO;

@Service
public class LoginService {

	@Autowired
	private MemberDAO mdao;

	public MemberDTO login(String member_id, String password) {
		MemberDTO mdto = new MemberDTO();
		try {
			if (mdao.chkMember(member_id, password) == 1) {
				mdto.setSuccess(true);
			} else {
				mdto.setSuccess(false);
				mdto.setErr_msg("会員IDとパスワードを確認してください");
			}
		} catch (DataAccessException e) {
			mdto.setSuccess(false);
			mdto.setErr_msg("システムエラーが発生しました");
			System.out.println("データベースの接続に失敗しました");
		} catch (Exception e) {
			mdto.setSuccess(false);
			mdto.setErr_msg("システムエラーが発生しました");
			System.out.println("システムエラーが発生しました");
		}
		return mdto;
	}
}
