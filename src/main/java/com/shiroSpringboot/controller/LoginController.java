package com.shiroSpringboot.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shiroSpringboot.common.Contant;
import com.shiroSpringboot.config.RedisCache;
import com.shiroSpringboot.entity.User;
import com.shiroSpringboot.vo.AjaxResponse;

@Controller
public class LoginController {

	@Autowired
	private RedisCache redisCache;
	
	/**
	 * 登录页面
	 * @return
	 */
	@RequestMapping(value="/login")
	public String doLogin() {
		return "/login";
	}
	/**
	 * 用户开始登录接口
	 * @param user
	 * @return
	 */
	@RequestMapping(value="/userLogin",method=RequestMethod.POST)
	@ResponseBody
	public AjaxResponse<User> login(@RequestBody User user){
		AjaxResponse<User> ajaxResponse = new AjaxResponse<>(Contant.RESULT_ERROR,"登陆失败！");
		UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(),user.getPassword(),user.getRememberMe());
		//token.setRememberMe(true);
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(token);
			ajaxResponse.setCode(Contant.RESULT_SUCCESS);
			ajaxResponse.setMessge("登陆成功");
			
		} catch (UnknownAccountException ex) {
			ajaxResponse.setMessge("用户名不存在！");
			return ajaxResponse;
		} catch (IncorrectCredentialsException ex) {
			ajaxResponse.setMessge("用户不存在或者密码错误！");
			return ajaxResponse;
		} catch (Exception ex) {
			ex.printStackTrace();
			ajaxResponse.setMessge("内部错误，请重试！");
			return ajaxResponse;
		}
		return ajaxResponse;
		
	}
	
	
	@RequestMapping(value="/logout",method=RequestMethod.GET)
	@ResponseBody
	public AjaxResponse<Object> loginOut(){
		AjaxResponse<Object> ajaxResponse = new AjaxResponse<Object>(Contant.RESULT_SUCCESS,"退出登录！");
		Subject subject = SecurityUtils.getSubject();
		String id = (String) subject.getSession().getId();
		subject.logout();
		redisCache.deleteKey(Contant.SESSION_KEY_PREFIX+id);//删除redis缓存session
		return ajaxResponse;
		
	}
	
	
}
