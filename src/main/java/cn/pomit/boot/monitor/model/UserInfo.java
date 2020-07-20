package cn.pomit.boot.monitor.model;

import java.io.Serializable;

/**
 * 用户信息
 *
 * @author 陈付菲
 */
@lombok.Data
public class UserInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5729690130594095773L;
	private String salt;
	private String userName;
	private String password;
	private String userNameToken;

	public UserInfo() {

	}

	public UserInfo(String salt, String userName, String password) {
		this.salt = salt;
		this.userName = userName;
		this.password = password;
	}
}
