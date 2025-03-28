package com.ksinfo.pointGame.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ksinfo.pointGame.dto.MemberDTO;
import com.ksinfo.pointGame.service.LoginService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class LoginController {

	@Autowired
	HttpSession session;

	@Autowired
	LoginService ls;

	@GetMapping("")
	public String login() {
		return "login";
	}

	@PostMapping("login")
	@ResponseBody
	public MemberDTO login(
			@RequestParam("member_id") String member_id,
			@RequestParam("password") String password) {

		MemberDTO mdto = ls.login(member_id, password);
		if (mdto.isSuccess()) {
			session.setAttribute("member_id", member_id);
		} 
		return mdto;
	}

	@GetMapping("logout")
	public String logout() {
		session.removeAttribute("member_id");
		return "redirect:/";
	}
}
