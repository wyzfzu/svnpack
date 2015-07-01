package com.github.svnpack;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.io.*;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * 压缩工具类。
 *
 * @author wyzfzu (wyzfzu@qq.com)
 */
public class CompressUtils {
	private AppBean appBean;
	
	private static final String packFileFmt = "%s_%s_%s_%s.zip";
	private static final String changeLog = "更新文件列表.txt";
	private static final String notice = "注意事项.txt";
	
	public CompressUtils(AppBean appBean) {
		this.appBean = appBean;
	}

	/**
	 * 压缩打包。
	 *
	 * @param beans
	 * @return
	 * @throws Exception
	 */
	public int compress(Set<SvnBean> beans) throws Exception {
		File pkPath = new File(appBean.getTargetDir());
		if (!pkPath.exists()) {
			pkPath.mkdirs();
		}
		
		String pkFilePath = getPackName();
		File pkFile = new File(pkFilePath);
		if (pkFile.exists()) {
			pkFile.delete();
		}
		pkFile.createNewFile();
		
		File changeLogFile = new File(FilenameUtils.concat(appBean.getTargetDir(), changeLog));
		if (!changeLogFile.exists()) {
			changeLogFile.createNewFile();
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(changeLogFile));
		appendChangeLogHeader(bw);
		FileOutputStream fos = new FileOutputStream(pkFile);
		ArchiveStreamFactory asf = new ArchiveStreamFactory();
		ZipArchiveOutputStream zout = (ZipArchiveOutputStream) asf.createArchiveOutputStream("zip", fos);
		zout.setEncoding(System.getProperty("file.encoding"));
		String basePath = appBean.getWorkspaceDir();
		
		int totalFile = 0;
		
		for (SvnBean bean : beans) {
			String fileName = FilenameUtils.concat(basePath, bean.getPath());
			File src = new File(fileName);
			if (!src.exists()) {
				continue;
			}
			String unixPath = bean.getPath().replace('\\', '/');
			bw.append(unixPath);
			bw.newLine();
			
			addToArchive(src, unixPath.replace(appBean.getWebDir(), "更新内容"), zout);
			++totalFile;
			String name = bean.getName();
			if (name.endsWith(".class")) {
				String path = FilenameUtils.getFullPath(fileName);
				String className = name.substring(0, name.indexOf('.'));
				Collection<File> files = FileUtils.listFiles(new File(path),
						FileFilterUtils.prefixFileFilter(className + "$"),
						FileFilterUtils.falseFileFilter());
				if (files != null && !files.isEmpty()) {
					String subPath = "";
					for (File f : files) {
						subPath = FilenameUtils.getPath(unixPath) + f.getName();
						bw.append(subPath);
						bw.newLine();
						addToArchive(f, subPath.replace(appBean.getWebDir(), "更新内容"), zout);
						++totalFile;
					}
				}
			}
			
		}
		bw.flush();
		bw.close();
		// 把更新列表打包进去
		addToArchive(changeLogFile, changeLogFile.getName(), zout);
		// 把注意事项文件打包进去
		File noticeFile = createNotice();
		addToArchive(noticeFile, noticeFile.getName(), zout);
		zout.close();
		fos.close();
		// 删除更新列表文件
		changeLogFile.delete();
		// 删除注意事项文件
		noticeFile.delete();
		
		return totalFile;
	}

	/**
	 * 创建更新列表文件头。
	 * @param bw 写入流
	 * @throws IOException
	 */
	public void appendChangeLogHeader(BufferedWriter bw) throws IOException {
		bw.append(StringUtils.repeat("/", 62));
		bw.newLine();
		bw.append("//");
		bw.newLine();
		bw.append("//" + StringUtils.center("文件更新列表", 56, " "));
		bw.newLine();
		bw.append("//");
		bw.newLine();
		bw.append("// 截止时间：" + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:dd"));
		bw.newLine();
		bw.append("//");
		bw.newLine();
		bw.append("//  SVN版本：" + appBean.getLeastRevision());
		bw.newLine();
		bw.append("//");
		bw.newLine();
		bw.append(StringUtils.repeat("/", 62));
		bw.newLine();
		bw.newLine();
	}

	/**
	 * 添加文件到压缩包中。
	 *
	 * @param file 写入的文件
	 * @param name 压缩包中的文件名
	 * @param zout 写入流
	 * @throws Exception
	 */
	public void addToArchive(File file, String name, ZipArchiveOutputStream zout) throws Exception {
		ZipArchiveEntry entry = new ZipArchiveEntry(file, name);
		zout.putArchiveEntry(entry);
		FileInputStream fis = new FileInputStream(file);
		IOUtils.copy(fis, zout, 1024);
		zout.closeArchiveEntry();
		fis.close();
	}

	/**
	 * 获取打包的文件名。
	 *
	 * @return 文件名
	 */
	public String getPackName() {
		String today = DateFormatUtils.format(new Date(), "yyyyMMdd");
		String type = "更新包";
		if (appBean.getLastRevision() < 0) {
			type = "全量包";
		}
		String name = String.format(packFileFmt, 
				appBean.getName(),
				type,
				today,
				appBean.getLeastRevision()
				);
		return FilenameUtils.concat(appBean.getTargetDir(), name);
	}

	/**
	 * 追加内容并换行.
	 *
	 * @param bw 写入流
	 * @param str 追加的内容
	 * @throws Exception
	 */
	public void appendLine(BufferedWriter bw, String str) throws Exception {
		bw.append(str);
		bw.newLine();
		bw.newLine();
	}
	
	/**
	 * 创建注意事项文件。
	 */
	public File createNotice() throws Exception {
		File file = new File(notice);
		if (file.exists()) {
			file.delete();
		}
		
		file.createNewFile();
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		appendLine(bw, "// -------- 注意事项 ------- //");
		appendLine(bw, "1. 更新前请先做好备份");
		appendLine(bw, "2. 更新包与工程目录结构相同，可直接覆盖");
		appendLine(bw, "3. 请确保文件正确覆盖");
		appendLine(bw, "4. 更新后，请重启服务");
		appendLine(bw, "5. 本次更新SVN版本：" + appBean.getLeastRevision());
		appendLine(bw, "6. 本次更新的内容(" + DateFormatUtils.format(new Date(), "yyyy-MM-dd") + "):");
		appendLine(bw, "       <<在这里编辑更新的内容>>");
		
		bw.flush();
		bw.close();
		
		return file;
	}
}
