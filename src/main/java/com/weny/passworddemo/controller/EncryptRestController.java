package com.weny.passworddemo.controller;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weny.passworddemo.utils.SerializeUtils;

import javacryption.aes.AesCtr;
import javacryption.jcryption.JCryption;

public class EncryptRestController {
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@RequestMapping("/encrypt")
	public String passwordEncrypt(Model model, HttpServletRequest request)
			throws UnsupportedEncodingException, JsonProcessingException {
		String returnStr = "";
		if (request.getParameter("generateKeyPair") != null && request.getParameter("generateKeyPair").equals("true")) {
			JCryption jc = new JCryption();
			KeyPair keys = jc.getKeyPair();
			byte[] b = SerializeUtils.serialize(keys);
			redisTemplate.opsForValue().set("key", b, 600, TimeUnit.SECONDS);
			String e = jc.getPublicExponent();
			String n = jc.getKeyModulus();
			String md = String.valueOf(jc.getMaxDigits());
			returnStr = "{\"e\":\"" + e + "\",\"n\":\"" + n + "\",\"maxdigits\":\"" + md + "\"}";
		} else if (request.getParameter("handshake") != null && request.getParameter("handshake").equals("true")) {
			JCryption jc = new JCryption(
					(KeyPair) SerializeUtils.deSerialize((byte[]) redisTemplate.opsForValue().get("key")));
			String a = request.getParameter("key");
			System.out.println(a);
			String key = jc.decrypt(request.getParameter("key"));
			redisTemplate.opsForValue().set("jCryptionKey", key, 600, TimeUnit.SECONDS);

			/** Encrypts password using AES **/
			String ct = AesCtr.encrypt(key, key, 256);
			returnStr = "{\"challenge\":\"" + ct + "\"}";
		} else if (request.getParameter("decryptData") != null && request.getParameter("decryptData").equals("true")
				&& request.getParameter("jCryption") != null) {
			String key = (String) redisTemplate.opsForValue().get("jCryptionKey");
			String pt = AesCtr.decrypt(request.getParameter("jCryption"), key, 256);
			returnStr = "{\"data\":\"" + pt + "\"}";
		}
		/** jCryption request to encrypt a String **/
		else if (request.getParameter("encryptData") != null && request.getParameter("encryptData").equals("true")
				&& request.getParameter("jCryption") != null) {
			/** Encrypts the request using password **/
			String key = (String) redisTemplate.opsForValue().get("jCryptionKey");

			String ct = AesCtr.encrypt(request.getParameter("jCryption"), key, 256);

			/** Sends response **/
			returnStr = "{\"data\":\"" + ct + "\"}";
		}
		/** A test request from jCryption **/
		else if (request.getParameter("decryptTest") != null && request.getParameter("decryptTest").equals("true")) {

			/** Encrypts a timestamp **/
			String key = (String) redisTemplate.opsForValue().get("jCryptionKey");

			String date = DateFormat.getInstance().format(new Date());

			String ct = AesCtr.encrypt(date, key, 256);

			/** Sends response **/
			returnStr = "{\"encrypted\":\"" + ct + "\", \"unencrypted\":\"" + date + "\"}";
		}
		System.out.println("=================" + returnStr);
		return returnStr;
	}

}
