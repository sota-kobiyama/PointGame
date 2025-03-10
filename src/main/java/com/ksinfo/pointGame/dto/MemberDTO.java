package com.ksinfo.pointGame.dto;

import lombok.Data;

@Data
public class MemberDTO {
	
	private String member_id;
	private String password;
	
	private String err_msg;
	private boolean success;
	
}
