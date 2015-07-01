package com.github.svnpack;

import java.util.Date;

/**
 *
 *
 * @author wyzfzu (wyzfzu@qq.com)
 */
public class SvnBean implements Comparable<SvnBean> {
	private String name;
	private String path;
	private Date changeDate;
	private long revision;
	private String commitMessage;
	private String lastAuthor;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}

	public String getLastAuthor() {
		return lastAuthor;
	}

	public void setLastAuthor(String lastAuthor) {
		this.lastAuthor = lastAuthor;
	}

	@Override
	public int compareTo(SvnBean o) {
		if (o == null || o.getClass() != SvnBean.class) {
            return -1;
        }
        return path.compareTo(o.getPath());
	}

}
