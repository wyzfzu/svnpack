package com.github.svnpack;

import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;

public class SvnPack {
	private static final Logger logger = LoggerFactory.getLogger(SvnPack.class);

	private static final String VERSION = "1.0";

	private SvnUtils svnUtils;
	private AppBean appBean;

	private Options options;
	
	public SvnPack() {
		
	}

	/**
	 * 解析参数。
	 *
	 * @param args 命令行传入的参数
	 * @return 是否解析成功
	 */
	public boolean parseArgs(String[] args) {
		try {
			CommandLineParser parser = new BasicParser();
			CommandLine cl = null;
			try {
				cl = parser.parse(getOptions(), args);
			} catch (ParseException pe) {
				logger.error("参数解析错误, 请检查输入的参数是否正确.");
				printHelp();
				return false;
			}

			if (cl.hasOption('h') || cl.hasOption("help")) {
				printHelp();
				return false;
			}
			
			if (cl.hasOption('v') || cl.hasOption("version")) {
				logger.info("工具当前版本：svnpack " + VERSION);
				return false;
			}
			
			if (cl.hasOption('l') || cl.hasOption("list")) {
				StringBuilder list = new StringBuilder();
				int maxLen = 0;
				Collection<AppBean> beans = ConfigUtils.getAppBeans();
				for (AppBean bean : beans) {
					if (maxLen < bean.getId().length()) {
						maxLen = bean.getId().length();
					}
				}
				list.append(StringUtils.center("项目ID", maxLen + 2))
					.append(StringUtils.center("上次最新版本", 6))
					.append(StringUtils.center("项目描述", 10))
					.append("\n")
					.append(StringUtils.repeat("-", 60))
					.append("\n");
				
				for (AppBean bean : beans) {
					list.append(StringUtils.center(bean.getId(), maxLen + 2))
						.append("   ")
						.append(StringUtils.leftPad(bean.getLastPackRevision(), 6))
						.append(StringUtils.repeat(" ", 8))
						.append(bean.getName())
						.append("\n");
				}
				
				if (list.length() > 0) {
					list.deleteCharAt(list.length() - 1);
				}
				logger.info(list.toString());
				return false;
			}

			String appId = "";
			if (cl.hasOption('n') || cl.hasOption("name")) {
				String val = cl.getOptionValue('n', cl.getOptionValue("name"));
				if (StringUtils.isBlank(val)) {
					logger.info("必须指定项目名称");
					return false;
				} else {
					appId = val;
				}
			} else {
				logger.error("必须指定项目名称");
				return false;
			}
			
			appBean = ConfigUtils.getConfig(appId);

			if (appBean == null) {
				logger.error("没有找到指定的配置！");
				return false;
			}

			long revision = -1L;
			
			if (StringUtils.isNotEmpty(appBean.getLastPackRevision())) {
				revision = Long.parseLong(appBean.getLastPackRevision());
			}
			
			if (cl.hasOption('r') || cl.hasOption("revision")) {
				String val = cl.getOptionValue('r', cl.getOptionValue("revision"));
				if (StringUtils.isNotBlank(val)) {
					try {
						revision = Long.parseLong(val);
					} catch (NumberFormatException ne) {
						logger.error("版本号必须为数字");
						return false;
					}
				}
			}

			svnUtils = new SvnUtils(appBean);
			if (!svnUtils.login()) {
				logger.error("SVN验证失败！");
				return false;
			}
			
			appBean.setLastRevision(revision);
			appBean.setLeastRevision(svnUtils.getLeastRevision());
			
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			printHelp();
		}
		return false;
	}

	public void pack() {
		try {
			if (appBean.getLastRevision() > 0) {
				logger.info("开始打更新包...");
			} else {
				logger.info("开始打全量包...");
			}
			Set<SvnBean> filePaths = svnUtils.listEntries("");
			if (filePaths == null || filePaths.isEmpty()) {
				logger.info("没有需要打包的更新文件。");
				return ;
			}
			
			logger.info("正在创建压缩文件...");
			
			CompressUtils cu = new CompressUtils(appBean);
			int totalFile = cu.compress(filePaths);
			
			logger.info("共有 " + totalFile + " 个文件。");
			logger.info("生成压缩文件：" + cu.getPackName());
			logger.info("打包成功。");
			
			// 记录本次打包的最新版本
			ConfigUtils.setConfig(appBean.getId(), "lastPackRevision", appBean.getLeastRevision());
			Runtime r = Runtime.getRuntime();
			if (r != null) {
				r.exec("explorer " + appBean.getTargetDir().replace('/', '\\'));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		svnUtils.logout();
	}

	/**
	 * 打印帮助。
	 */
	public void printHelp() {
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("svnpack", "    工程打包工具, 用于在工程文件更新时, 提取更新文件, 并打包。",
				getOptions(), "", true);
	}

	/**
	 * 获取命令选项。
	 *
	 * @return 命令行选项对象
	 */
	public Options getOptions() {
		if (options == null) {
			options = new Options();
			
			OptionBuilder.withArgName("project name");
			OptionBuilder.hasOptionalArg();
			OptionBuilder.withLongOpt("name");
			OptionBuilder.withDescription("工程的名称(配置文件中的app id), 必须项");
			Option nameOption = OptionBuilder.create("n");
			
			OptionBuilder.withArgName("last revision");
			OptionBuilder.hasOptionalArg();
			OptionBuilder.withType(Long.class);
			OptionBuilder.withLongOpt("revision");
			OptionBuilder.withDescription("上次打包的版本, 若为-1, 则表示打全量包, 默认为上次打包后的版本，如果没有配置，则默认为-1");
			Option revisionOption = OptionBuilder.create("r");
			
			OptionBuilder.withArgName("config app list");
			OptionBuilder.hasOptionalArg();
			OptionBuilder.withLongOpt("list");
			OptionBuilder.withDescription("已经配置的app列表");
			Option listOption = OptionBuilder.create("l");
			
			options.addOption("h", "help", false, "查看命令帮助");
			options.addOption("v", "version", false, "查看工具当前版本");
			options.addOption(nameOption);
			options.addOption(revisionOption);
			options.addOption(listOption);
		}
		return options;
	}
}
