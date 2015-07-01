package com.github.svnpack;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * SVN工具类。
 *
 * @author wyzfzu (wyzfzu@qq.com)
 */
public class SvnUtils {
	private static final Logger logger = LoggerFactory.getLogger(SvnUtils.class);
	
	private SVNRepository repository;
	private Set<String> dirIgnorePatterns;
	private Set<String> fileIgnorePatterns;
	private Set<String> pathIgnorePatterns;
	private AppBean app;
	
	/**
	 * 创建一个新的实例 SvnUtils.   
	 *   
	 * @param app
	 */
	public SvnUtils(AppBean app) {
		this.app = app;
		init();
	}

	public void init() {
		dirIgnorePatterns = app.getDirIgnorePatterns();
		fileIgnorePatterns = app.getFileIgnorePatterns();
		pathIgnorePatterns = app.getPathIgnorePatterns();
	}
	
	/**
	 * 作者: wuyzh  <br />
	 * 版本: May 17, 2013 10:13:37 AM v1.0  <br />
	 * 日期: May 17, 2013   <br />
	 * 参数: @return   <br />
	 * 描述: SVN登入
	 */
	public boolean login() {
		try {
			repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(app.getSvnDir()));
			// 身份验证
			ISVNAuthenticationManager authManager = null;
			authManager = SVNWCUtil.createDefaultAuthenticationManager(
					app.getSvnUserName(), app.getSvnPassword());
			// 设置验证管理器
			repository.setAuthenticationManager(authManager);
			repository.testConnection();
			return true;
		} catch (SVNException svne) {
			logger.error(svne.getMessage(), svne);
		}
		return false;
	}
	
	/**
	 * 作者: wuyzh  <br />
	 * 版本: May 17, 2013 11:31:17 AM v1.0  <br />
	 * 日期: May 17, 2013   <br />
	 * 参数:    <br />
	 * 描述: 关闭连接
	 */
	public void logout() {
		repository.closeSession();
	}

	/**
	 * 作者: wuyzh  <br />
	 * 版本: May 17, 2013 4:15:13 PM v1.0  <br />
	 * 日期: May 17, 2013   <br />
	 * 参数: @param entry
	 * 参数: @return   <br />
	 * 描述: 过滤
	 */
	public boolean checkEntry(SVNDirEntry entry, int flag) {
		Set<String> patterns = (flag == 0 ? dirIgnorePatterns : fileIgnorePatterns);
		for (String s : patterns) {
			if (Pattern.matches(s, entry.getName())) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * 作者: wuyzh  <br />
	 * 版本: May 22, 2013 10:51:04 AM v1.0  <br />
	 * 日期: May 22, 2013   <br />
	 * 参数: @return   <br />
	 * 描述: 获取最新版本
	 */
	public long getLeastRevision() {
		try {
			return repository.getLatestRevision();
		} catch (SVNException e) {
			logger.error(e.getMessage(), e);
		}
		return -1;
	}
	
	/**
	 * 作者: wuyzh  <br />
	 * 版本: May 20, 2013 5:36:31 PM v1.0  <br />
	 * 日期: May 20, 2013   <br />
	 * 参数: @param path
	 * 参数: @return   <br />
	 * 描述: 过滤路径
	 */
	public boolean checkPath(String path) {
		// 每次都过滤路径
		for (String s : pathIgnorePatterns) {
			if (Pattern.matches(s, path)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 作者: wuyzh  <br />
	 * 版本: May 17, 2013 4:14:18 PM v1.0  <br />
	 * 日期: May 17, 2013   <br />
	 * 参数: @param path
	 * 参数: @param lastRevision
	 * 参数: @return   <br />
	 * 描述: 返回所有指定版本后的更改文件
	 */
	public Set<SvnBean> listEntries(String path) {
		Set<SvnBean> files = new TreeSet<SvnBean>();
		try {
			listEntries(files, path, app.getLastRevision());
			return files;
		} catch (SVNException e) {
			logger.error(e.getMessage(), e);
		}
		return new TreeSet<SvnBean>();
	}
	
	/**
	 * 作者: wuyzh  <br />
	 * 版本: May 22, 2013 9:26:43 AM v1.0  <br />
	 * 日期: May 22, 2013   <br />
	 * 参数: @param beans
	 * 参数: @param path
	 * 参数: @param revision
	 * 参数: @throws SVNException   <br />
	 * 描述: 递归导出文件路径
	 */
	public void listEntries(Set<SvnBean> beans, String path, long revision) throws SVNException {
		if (path == null) {
			path = "";
		}
		
		Collection<SVNDirEntry> dirEntries = repository.getDir(
				path, -1, null, SVNDirEntry.DIRENT_ALL, (Collection<?>)null);
		
		if (dirEntries == null || dirEntries.isEmpty()) {
			return ;
		}
		
		for (SVNDirEntry entry : dirEntries) {
			if (!checkPath(path  + entry.getName())) {
				continue;
			}
			if (entry.getKind().compareTo(SVNNodeKind.DIR) == 0) {
				if (checkEntry(entry, 0)) {
					listEntries(beans, path + entry.getName() + "/", revision);
				}
			} else if (entry.getKind().compareTo(SVNNodeKind.FILE) == 0) {
				if (checkEntry(entry, 1) && entry.getRevision() > revision) {
					SvnBean svn = new SvnBean();
					svn.setChangeDate(entry.getDate());
					svn.setCommitMessage(entry.getCommitMessage());
					svn.setLastAuthor(entry.getAuthor());
					if (entry.getName().endsWith(".java")) {
						svn.setName(entry.getName().replace(".java", ".class"));
					} else {
						svn.setName(entry.getName());
					}
					// 如果是源代码目录或资源下的文件
					if (path.startsWith(app.getSrcDir())) {
						String savePath = app.getClassDir();
						String s = path.substring(app.getSrcDir().length() + 1);
						String p = FilenameUtils.concat(s, svn.getName());
						savePath = FilenameUtils.concat(savePath, p);
						svn.setPath(savePath);
					} else if (path.startsWith(app.getResourceDir())) {
						String savePath = app.getClassDir();
						String s = path.substring(app.getResourceDir().length() + 1);
						String p = FilenameUtils.concat(s, svn.getName());
						savePath = FilenameUtils.concat(savePath, p);
						svn.setPath(savePath);
					} else {
						svn.setPath(path + svn.getName());
					}
					svn.setRevision(entry.getRevision());
					beans.add(svn);
				}
			}
		}
		
		return ;
	}
	
}
