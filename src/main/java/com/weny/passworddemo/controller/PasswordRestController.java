package com.weny.passworddemo.controller;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.weny.passworddemo.utils.SerializeUtils;

import javacryption.aes.AesCtr;
import javacryption.jcryption.JCryption;

@RestController
public class PasswordRestController {

	@Autowired
	private RedisTemplate redisTemplate;

	@RequestMapping("/number")
	public String getSecNum() {
		return "123";
	}

	@RequestMapping("/encrypt/generateKeyPair")
	public Map<String, String> generateKeyPair() {
		JCryption jc = new JCryption();
		KeyPair keys = jc.getKeyPair();
		byte[] b = SerializeUtils.serialize(keys);
		redisTemplate.opsForValue().set("key", b, 600, TimeUnit.SECONDS);
		Map<String, String> map = new HashMap<>();
		map.put("e", jc.getPublicExponent());
		map.put("n", jc.getKeyModulus());
		map.put("maxdigits", String.valueOf(jc.getMaxDigits()));
		System.out.println(map);
		return map;
	}

	@RequestMapping("/encrypt/handshake")
	public Map<String, String> handshake(HttpServletRequest request) {
		JCryption jc = new JCryption(
				(KeyPair) SerializeUtils.deSerialize((byte[]) redisTemplate.opsForValue().get("key")));
		String key = jc.decrypt(request.getParameter("key"));
		redisTemplate.opsForValue().set("jCryptionKey", key, 600, TimeUnit.SECONDS);

		/** Encrypts password using AES **/
		String ct = AesCtr.encrypt(key, key, 256);
				
		Map<String, String> map = new HashMap<>();
		map.put("challenge", ct);
		return map;
	}

	@RequestMapping("/encrypt/decryptData")
	public Map<String, String> decryptData(HttpServletRequest request) {
		String key = (String) redisTemplate.opsForValue().get("jCryptionKey");
		String pt = AesCtr.decrypt(request.getParameter("jCryption"), key, 256);
		Map<String, String> map = new HashMap<>();
		map.put("data", "admin".equals(request.getParameter("username")) && "123".equals(pt) ? "登录成功":"登录失败");
		return map;
	}

	@RequestMapping("/encrypt/encryptData")
	public Map<String, String> encryptData(HttpServletRequest request) {
		/** Encrypts the request using password **/
		String key = (String) redisTemplate.opsForValue().get("jCryptionKey");
		String ct = AesCtr.encrypt(request.getParameter("jCryption"), key, 256);
		Map<String, String> map = new HashMap<>();
		map.put("data", ct);
		return map;
	}
}
