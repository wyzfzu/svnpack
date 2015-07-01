package com.github.svnpack;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置读取工具类。
 *
 * @author wyzfzu (wyzfzu@qq.com)
 */
public class ConfigUtils {
	private static Logger logger = LoggerFactory.getLogger(ConfigUtils.class);
	
	private static Map<String, AppBean> apps;
	private static Map<String, Pair> svns;
	private static XMLConfiguration config;
	
	static {
		init();
	}
	
	private static void init() {
		try {
			apps = new ConcurrentHashMap<String, AppBean>();
			svns = new ConcurrentHashMap<String, Pair>();
			config = new XMLConfiguration("config.xml");
			config.setAutoSave(true);
			config.setEncoding("UTF-8");
			
			List<HierarchicalConfiguration> svnHc = config.configurationsAt("svns.svn");
			if (svnHc != null && !svnHc.isEmpty()) {
				for (HierarchicalConfiguration hc : svnHc) {
					Pair svn = new Pair();
					String id = hc.getString("[@id]");
					svn.first = hc.getString("userName");
					svn.second = hc.getString("password");
					svns.put(id, svn);
				}
			}
			
			List<HierarchicalConfiguration> hcs = config.configurationsAt("apps.app");
			if (hcs != null && !hcs.isEmpty()) {
				for (HierarchicalConfiguration hc : hcs) {
					AppBean bean = new AppBean();
					
					String id = hc.getString("[@id]");
					String appName = hc.getString("[@name]");
					bean.setId(id);
					bean.setName(appName);
					// 获取属性
					List<HierarchicalConfiguration> chcs = hc.configurationsAt("property");
					if (chcs != null && !chcs.isEmpty()) {
						for (HierarchicalConfiguration chc : chcs) {
							String name = chc.getString("[@name]");
							String value = chc.getString("");
							MethodUtils.invokeMethod(bean, "set" + StringUtils.capitalize(name), value);
						}
					} else {
						continue;
					}
					// 忽略的路径
					Set<String> paths = ignores(hc, "ignore.paths.path");
					// 忽略的文件夹
					Set<String> dirs = ignores(hc, "ignore.dirs.dir");
					// 忽略的文件
					Set<String> files = ignores(hc, "ignore.files.file");
					
					bean.setPathIgnorePatterns(paths);
					bean.setDirIgnorePatterns(dirs);
					bean.setFileIgnorePatterns(files);
					
					apps.put(id, bean);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public static Set<String> ignores(HierarchicalConfiguration hc, String key) {
		List<HierarchicalConfiguration> ignore = hc.configurationsAt(key);
		Set<String> files = new HashSet<String>();
		if (ignore != null && !ignore.isEmpty()) {
			for (HierarchicalConfiguration phc : ignore) {
				String file = phc.getString("");
				files.add(file);
			}
		}
		return files;
	}
	
	public static AppBean getConfig(String id) {
		if (apps.containsKey(id)) {
			return apps.get(id);
		}
		return null;
	}
	
	public static void setConfig(String id, String property, Object val) {
		List<HierarchicalConfiguration> hcs = config.configurationsAt("apps.app");
		if (hcs == null || hcs.isEmpty()) {
			return ;
		}
		for (HierarchicalConfiguration hc : hcs) {
			String appId = hc.getString("[@id]");
			if (appId.equals(id)) {
				List<HierarchicalConfiguration> chcs = hc.configurationsAt("property");
				if (chcs == null || chcs.isEmpty()) {
					return ;
				}
				for (HierarchicalConfiguration chc : chcs) {
					String name = chc.getString("[@name]");
					if (name.equals(property)) {
						chc.setProperty("", val);
						return ;
					}
				}
			}
		}
	}
	
	public static Pair getSvn(String id) {
		if (svns.containsKey(id)) {
			return svns.get(id);
		}
		return null;
	}
	
	public static Map<String, AppBean> getApps() {
		return Collections.unmodifiableMap(ConfigUtils.apps);
	}
	
	public static Collection<AppBean> getAppBeans() {
		return Collections.unmodifiableCollection(ConfigUtils.apps.values());
	}
}
