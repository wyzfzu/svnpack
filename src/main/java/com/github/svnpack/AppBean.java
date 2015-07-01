package com.github.svnpack;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 配置文件对于的javabean。
 *
 * @author wyzfzu (wyzfzu@qq.com)
 */
public class AppBean implements Serializable {
	private static final long serialVersionUID = 1947710432958420469L;

	// ID，用于标识APP
	private String id;
	// 名称，用于打包的名称
	private String name;
	// SVN路径
	private String svnDir;
	// SVN用户名
	private String svnUserName;
	// SVN密码
	private String svnPassword;
	// 项目工程路径
	private String workspaceDir;
	// 源代码相对项目工程路径
	private String srcDir;
	// class文件路径
	private String classDir;
	// 资源配置文件相对项目工程路径
	private String resourceDir;
	// 部署文件相对项目工程路径
	private String webDir;
	// 打包导出路径
	private String targetDir;
	// 上次打包后的最新版本
	private String lastPackRevision;

	// 最新版本
	private long lastRevision;
	//
	private long leastRevision;

	private Set<String> dirIgnorePatterns = new HashSet<String>();
	private Set<String> fileIgnorePatterns = new HashSet<String>();
	private Set<String> pathIgnorePatterns = new HashSet<String>();

	/**   
	 * id
	 * @return the id 
	 */
	public String getId() {
		return id;
	}

	/**   
	 * @param id the id to set   
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**   
	 * name
	 * @return the name 
	 */
	public String getName() {
		return name;
	}

	/**   
	 * @param name the name to set   
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * workspaceDir
	 * 
	 * @return the workspaceDir
	 */
	public String getWorkspaceDir() {
		return workspaceDir;
	}

	/**
	 * @param workspaceDir
	 *            the workspaceDir to set
	 */
	public void setWorkspaceDir(String workspaceDir) {
		this.workspaceDir = workspaceDir;
	}

	/**
	 * srcDir
	 * 
	 * @return the srcDir
	 */
	public String getSrcDir() {
		return srcDir;
	}

	/**
	 * @param srcDir
	 *            the srcDir to set
	 */
	public void setSrcDir(String srcDir) {
		this.srcDir = srcDir;
	}

	/**
	 * resourceDir
	 * 
	 * @return the resourceDir
	 */
	public String getResourceDir() {
		return resourceDir;
	}

	/**
	 * @param resourceDir
	 *            the resourceDir to set
	 */
	public void setResourceDir(String resourceDir) {
		this.resourceDir = resourceDir;
	}

	/**
	 * webDir
	 * 
	 * @return the webDir
	 */
	public String getWebDir() {
		return webDir;
	}

	/**
	 * @param webDir
	 *            the webDir to set
	 */
	public void setWebDir(String webDir) {
		this.webDir = webDir;
	}

	/**   
	 * targetDir
	 * @return the targetDir 
	 */
	public String getTargetDir() {
		return targetDir;
	}

	/**   
	 * @param targetDir the targetDir to set   
	 */
	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}

	/**
	 * lastRevision
	 * 
	 * @return the lastRevision
	 */
	public long getLastRevision() {
		return lastRevision;
	}

	/**
	 * @param lastRevision
	 *            the lastRevision to set
	 */
	public void setLastRevision(long lastRevision) {
		this.lastRevision = lastRevision;
	}

	/**
	 * dirIgnorePatterns
	 * 
	 * @return the dirIgnorePatterns
	 */
	public Set<String> getDirIgnorePatterns() {
		return dirIgnorePatterns;
	}

	/**
	 * @param dirIgnorePatterns
	 *            the dirIgnorePatterns to set
	 */
	public void setDirIgnorePatterns(Set<String> dirIgnorePatterns) {
		this.dirIgnorePatterns = dirIgnorePatterns;
	}

	/**
	 * fileIgnorePatterns
	 * 
	 * @return the fileIgnorePatterns
	 */
	public Set<String> getFileIgnorePatterns() {
		return fileIgnorePatterns;
	}

	/**
	 * @param fileIgnorePatterns
	 *            the fileIgnorePatterns to set
	 */
	public void setFileIgnorePatterns(Set<String> fileIgnorePatterns) {
		this.fileIgnorePatterns = fileIgnorePatterns;
	}

	/**
	 * pathIgnorePatterns
	 * 
	 * @return the pathIgnorePatterns
	 */
	public Set<String> getPathIgnorePatterns() {
		return pathIgnorePatterns;
	}

	/**
	 * @param pathIgnorePatterns
	 *            the pathIgnorePatterns to set
	 */
	public void setPathIgnorePatterns(Set<String> pathIgnorePatterns) {
		this.pathIgnorePatterns = pathIgnorePatterns;
	}

	/**   
	 * svnDir
	 * @return the svnDir 
	 */
	public String getSvnDir() {
		return svnDir;
	}

	/**   
	 * @param svnDir the svnDir to set   
	 */
	public void setSvnDir(String svnDir) {
		this.svnDir = svnDir;
	}

	/**   
	 * svnUserName
	 * @return the svnUserName 
	 */
	public String getSvnUserName() {
		return svnUserName;
	}

	/**   
	 * @param svnUserName the svnUserName to set   
	 */
	public void setSvnUserName(String svnUserName) {
		this.svnUserName = svnUserName;
	}

	/**   
	 * svnPassword
	 * @return the svnPassword 
	 */
	public String getSvnPassword() {
		return svnPassword;
	}

	/**   
	 * @param svnPassword the svnPassword to set   
	 */
	public void setSvnPassword(String svnPassword) {
		this.svnPassword = svnPassword;
	}

	/**   
	 * classDir
	 * @return the classDir 
	 */
	public String getClassDir() {
		return classDir;
	}

	/**   
	 * @param classDir the classDir to set   
	 */
	public void setClassDir(String classDir) {
		this.classDir = classDir;
	}

	/**   
	 * leastRevision
	 * @return the leastRevision 
	 */
	public long getLeastRevision() {
		return leastRevision;
	}

	/**   
	 * @param leastRevision the leastRevision to set   
	 */
	public void setLeastRevision(long leastRevision) {
		this.leastRevision = leastRevision;
	}

	public String getLastPackRevision() {
		return lastPackRevision;
	}

	public void setLastPackRevision(String lastPackRevision) {
		this.lastPackRevision = lastPackRevision;
	}

}
