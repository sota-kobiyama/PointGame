package com.ksinfo.pointGame.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ksinfo.pointGame.dto.GameDTO;
import com.ksinfo.pointGame.service.GameService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/game")
public class GameController {

	@Autowired
	HttpSession session;

	@Autowired
	GameService gs;

	@GetMapping("")
	public String gameInit(Model model) {
		GameDTO gdto = new GameDTO();
		String member_id = (String) session.getAttribute("member_id");
		
		if (member_id != null) {
			gdto.setMember_id(member_id);
		} else {
			return "redirect:/";
		}

		gdto = gs.gameInit(member_id);
		session.setAttribute("gdto", gdto);
		model.addAttribute("gdto", gdto);
		
		return "game";
	}

	@PostMapping("")
	@ResponseBody
	public GameDTO playGame(@RequestParam("input_num") String input_num) {

		GameDTO gdto = (GameDTO) session.getAttribute("gdto");
		gdto = gs.playGame(gdto, input_num);
		session.setAttribute("gdto", gdto);
		return gdto;
	}
}
