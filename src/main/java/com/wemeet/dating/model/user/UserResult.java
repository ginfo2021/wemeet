package com.wemeet.dating.model.user;




import com.wemeet.dating.model.TokenInfo;
import com.wemeet.dating.model.entity.User;
import com.wemeet.dating.model.enums.UserType;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserResult implements Serializable{
	private static final long serialVersionUID = 1L;
	private TokenInfo tokenInfo;
	private User user;
	private UserType userType;
}
