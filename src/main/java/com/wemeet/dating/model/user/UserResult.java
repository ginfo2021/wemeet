package com.wemeet.dating.model.user;




import com.wemeet.dating.model.TokenInfo;
import com.wemeet.dating.model.entity.User;

import java.io.Serializable;


public class UserResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private TokenInfo tokenInfo;
	private User user;


	public UserResult() {

	}
	public UserResult(User user, TokenInfo tokenInfo) {
		this.user = user;
		this.tokenInfo = tokenInfo;

	}

	public TokenInfo getTokenInfo() {
		return tokenInfo;
	}

	public void setTokenInfo(TokenInfo tokenInfo) {
		this.tokenInfo = tokenInfo;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}



}
