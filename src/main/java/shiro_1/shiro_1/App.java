package shiro_1.shiro_1;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App {
	static Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
		session.setAttribute("someKey", "aValue");
		String value = (String) session.getAttribute("someKey");
		if (value.equals("aValue")) {// 获取session
			logger.info("================== Retrieved the correct value! " + value);
		}
		// 测试当前的用户是否已经被认证，即是否已经登录
		// 调动Subject的isAuthenticated()
		if (!currentUser.isAuthenticated()) {
			// 将用户名和密码封装到UsernamePasswordToken对象当中
			UsernamePasswordToken token = new UsernamePasswordToken("lonestarr", "vespa");
			token.setRememberMe(true);
			// 执行登录
			try {
				currentUser.login(token);
			} // 若没有指定的账户, 则 shiro 将会抛出 UnknownAccountException 异常.
			catch (UnknownAccountException uae) {
				logger.info("================== There is no user with username of " + token.getPrincipal());
				return;
			}
			// 若账户存在, 但密码不匹配, 则 shiro 会抛出 IncorrectCredentialsException 异常。
			catch (IncorrectCredentialsException ice) {
				logger.info("================== Password for account " + token.getPrincipal() + " was incorrect!");
				return;
			}
			// 用户被锁定的异常 LockedAccountException
			catch (LockedAccountException lae) {
				logger.info("================== The account for username " + token.getPrincipal() + " is locked.  "
						+ "Please contact your administrator to unlock it.");
			}
			// 所有认证时异常的父类.
			catch (AuthenticationException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
		}
		logger.info("================== User is " + currentUser.getPrincipal() + " logged is Successfully.");
		// 测试是否某一个角色。调用Subject的hasRole方法
		if (currentUser.hasRole("schwartz")) {
			logger.info("================== May the Schwartz be with you!");
		} else {
			logger.info("================== Hello,mere mortal.");
			return;
		}
		// 测试用户是否具备某一个行为，调用Subject的isPermitted()方法
		if (currentUser.isPermitted("lightsaber:weild")) {
			logger.info("================== You may use a lightsaber ring.Use it wisely");
		} else {
			logger.info("================== Sorry,lightsaber rings are for schwartz master only.");
		}
		// 测试用户是否具备某一个行为。
		if (currentUser.isPermitted("user:delete:zhangsan")) {
			logger.info(
					"================== You are permitted to 'delete' the zhangsan with license plate (id) 'eagle5'."
							+ "Here are the keys - have fun!");
		} else {
			logger.info("================== Sorry, you aren't allowed to delete the 'eagle5' zhangsan!");
		}

		// 执行登出，调用Subject的Logout方法
		System.out.println("================== " + currentUser.isAuthenticated());
		currentUser.logout();
		System.out.println("================== " + currentUser.isAuthenticated());
		System.exit(0);
	}
}
