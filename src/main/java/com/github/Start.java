package com.github;

import com.github.svnpack.SvnPack;

/**
 * 启动类。
 *
 * @author wyzfzu (wyzfzu@qq.com)
 */
public class Start {
	public static void main(String[] args) {
		SvnPack sp = new SvnPack();
		if (!sp.parseArgs(args)) {
			return ;
		}
		sp.pack();
	}
}
