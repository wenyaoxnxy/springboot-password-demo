package com.weny.passworddemo.test;

import java.io.IOException;
import java.io.Serializable;
import java.security.KeyPair;

import com.weny.passworddemo.utils.SerializeUtils;

import javacryption.jcryption.JCryption;

public class SerializeTest {
	public static void main(String[] args) throws IOException {
		User user = new User();
		user.setName("张三");

		/*
		 * String s = new String(SerializeUtils.serialize(user), "UTF-8");
		 * System.out.println(s);
		 * 
		 * User u = (User) SerializeUtils.deSerialize(s.getBytes("UTF-8"));
		 * System.out.println(u.getName());
		 */
		/*
		 * String s = JacksonSerializeUtils.serialize(user); System.out.println(s);
		 * 
		 * User u = JacksonSerializeUtils.deSerialize(s, User.class);
		 * System.out.println(u.getName());
		 * 
		 * JCryption jc = new JCryption(); KeyPair keys = jc.getKeyPair();
		 * System.out.println(keys); String keyss =
		 * JacksonSerializeUtils.serialize(keys); System.out.println(keyss);
		 */
		JCryption jc = new JCryption();
		KeyPair keys = jc.getKeyPair();

		byte[] b = SerializeUtils.serialize(keys);
		String s = new String(b, "UTF-8");

		KeyPair k = (KeyPair) SerializeUtils.deSerialize(s.getBytes("UTF-8"));
		System.out.println(k);
		// System.out.println(s);
		// System.out.println(s.getBytes("UTF-8").length == b.length);

		// String ss = "hellos~~~~~~~~~~~~~~~";
		//
		// System.out.println(new String(ss.getBytes("UTF-8")).equals(ss));

	}
}

class User implements Serializable {
	private static final long serialVersionUID = 5829139073259708252L;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
