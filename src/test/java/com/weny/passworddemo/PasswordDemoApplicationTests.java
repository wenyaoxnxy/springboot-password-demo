package com.weny.passworddemo;

import java.security.KeyPair;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.weny.passworddemo.utils.SerializeUtils;

import javacryption.jcryption.JCryption;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PasswordDemoApplicationTests {

	@Autowired
	private RedisTemplate redisTemplate;

	@SuppressWarnings("unchecked")
	@Test
	public void contextLoads() {
		JCryption jc = new JCryption();
		KeyPair keys = jc.getKeyPair();
		byte[] b = SerializeUtils.serialize(keys);
		redisTemplate.opsForValue().set(generateKey("key", "password"), b, 60, TimeUnit.SECONDS);
		System.out.println("==================");
		JCryption jc1 = new JCryption((KeyPair) SerializeUtils
				.deSerialize((byte[]) redisTemplate.opsForValue().get(generateKey("key", "password"))));

		System.out.println("=========" + jc1.toString());
		System.out.println("====" + jc1.decrypt("true") + "====");
	}

	private String generateKey(String key, String namespace) {
		if (StringUtils.isNotBlank(namespace)) {
			StringBuilder sb = new StringBuilder(namespace.length() + key.length() + 1);
			sb.append(namespace);
			sb.append(':');
			sb.append(key);
			return sb.toString();
		} else {
			return key;
		}

	}
}
