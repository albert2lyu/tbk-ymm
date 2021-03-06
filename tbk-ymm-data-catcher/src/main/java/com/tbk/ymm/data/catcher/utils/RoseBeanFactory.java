package com.tbk.ymm.data.catcher.utils;

import net.paoding.rose.scanning.context.RoseAppContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class RoseBeanFactory implements ApplicationContextAware {

	private static volatile ApplicationContext ctx;
	private static final Logger logger = LoggerFactory.getLogger(RoseBeanFactory.class);

	private static void init() {
		if (ctx == null) {
			logger.info("ApplicationContext is null, need be injected!");
			synchronized (RoseBeanFactory.class) {
				if (ctx == null) {
					logger.info("RoseBeanFactory init using roseAppContext.");
					// 只所以使用RoseAppContext而不是我们自己精简的RoseBeanFactory
					// 是因为现在大量工程已经spring托管了，所以只能这样搞了

					// web环境下，是不会这样初始化的，因为applicationContext会被射进来。
					// 只有在非web环境下，才会这样搞飞机
					ctx = new RoseAppContext();
					logger.info("RoseBeanFactory init ok.");
				}
			}
		}
	}

	/*private static void more() {
		Set<String> beanNames = new HashSet<String>();
		ApplicationContext tmp = ctx;
		while (tmp != null) {
			String[] names = tmp.getBeanDefinitionNames();
			for (String name : names) {
				beanNames.add(name);
			}
			tmp = tmp.getParent();
		}
		System.out.println(beanNames);
		for (String name : beanNames) {
			Object bean = ctx.getBean(name);
			Class<?> clz = bean.getClass();
			String cname = clz.getName();
			if (cname.startsWith("com.xiaonei.vip.") || cname.startsWith("com.xiaonei.gift.") || cname.startsWith("com.xiaonei.member.")) {
				Set<Class<?>> cls = ReflectUtil.getAllInterfacesAndSuperClass(clz);
				cls.add(clz);
				for (Class<?> clazz : cls) {
					Field[] fields = clazz.getDeclaredFields();
					for (Field f : fields) {
						boolean needResetAccessible = false;
						try {
							if (!f.isAccessible()) {
								f.setAccessible(true);
								needResetAccessible = true;
							}
							Object value = f.get(bean);
							if (value == null) {
								System.out.println(cname + " -- " + f.getName());
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (needResetAccessible) {
								f.setAccessible(false);
							}
						}
					}
				}
			}
		}
	}*/

	@SuppressWarnings("unchecked")
	public static <T> T getBean(final String name) {
		init();
		return (T) ctx.getBean(name);
	}

	public static <T> T getBean(final Class<T> clazz) {
		init();
		return clazz.cast(BeanFactoryUtils.beanOfTypeIncludingAncestors(ctx, clazz));
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
		logger.info("RoseBeanFactory: applicationContext was set in.");
	}

}
