package com.qiuxu.ssm.controller.login;

import java.util.List;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.qiuxu.common.cache.RedisCache;
import com.qiuxu.ssm.domain.User;
import com.qiuxu.ssm.service.UserService;

@Controller
public class LoginController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RedisCache<String, Object> redisCache;
	
	@RequestMapping("/login.html")
	public String login(ModelMap model){
//		User addUser = new User();
//		addUser.setUserId(IdGenerator.randomLong());
//		addUser.setUserName("qx123");
//		addUser.setMobile("13552424019");
//		String encodePassword = PasswordEncoder.encode("MD5", "qx1234", "qx", 1024);
//		addUser.setPassword(encodePassword);
//		addUser.setEmail("775469137@qq.com");
//		userService.save(addUser);
		User user = (User) redisCache.get("test1");
		if(user == null){
			user = (User) userService.selectById(1);
			redisCache.set("test1",user,6000);
		}
				
		User querUser = new User();
		querUser.setMobile("13552424019");
		List<User>	list = (List<User>) userService.selectList(querUser);
		model.put("user", user);
		
		return "login/login";
	}

}
