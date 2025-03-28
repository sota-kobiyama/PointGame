package com.ksinfo.pointGame.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
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
		String member_id = (String) session.getAttribute("member_id");
		if (member_id == null) {
			return "redirect:/";
		} else {
			GameDTO gdto = gs.gameInit(member_id);
			try {
				ObjectMapper objectMapper  = new ObjectMapper();
				String gdto_json = objectMapper.writeValueAsString(gdto);
				System.out.println("gameInit: " + member_id + ", " + gdto_json);
				model.addAttribute("gdto", gdto_json);
			} catch (Exception e) {
				System.out.println("システムエラーが発生しました: " + e);
			}
			return "game";
		}
	}

	@PostMapping("")
	@ResponseBody
	public GameDTO playGame(@RequestParam("input_num") String input_num,
											 @RequestParam("hide_num") String hide_num,
											 @RequestParam("game_count") int game_count,
											 @RequestParam("point") int point) {
		String member_id = (String) session.getAttribute("member_id");
		GameDTO gdto = gs.playGame(member_id,input_num,hide_num,game_count,point);
		System.out.println("playGame: " + member_id + ", " + gdto);
		return gdto;
	}
}
