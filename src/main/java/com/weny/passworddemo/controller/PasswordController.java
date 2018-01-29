package com.weny.passworddemo.controller;

import java.util.HashMap;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PasswordController {

	@RequestMapping("/password")
	public String passwordHtml(HashMap<String, Object> map) {
		map.put("input", "please input password");
		return "/passwordFormSubmit";
	}

	@RequestMapping("/index")
	public String indexHtml(HashMap<String, Object> map) {
		map.put("input", "please input password");
		return "/index";
	}

	@RequestMapping("/passwordEncrypt")
	public String passwordEncrypt(HashMap<String, Object> map) {
		map.put("input", "please input password");
		map.put("factor", System.currentTimeMillis() + "userid");// 加密因子，建议使用时间戳加上用户名等信息，保证唯一。
		System.out.println(map);
		return "/passwordEncrypt";
	}

}
