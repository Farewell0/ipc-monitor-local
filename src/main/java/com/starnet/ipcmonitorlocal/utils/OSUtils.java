package com.starnet.ipcmonitorlocal.utils;

/**
 * OSUtils
 * 判断服务器操作系统信息的工具类
 * @author wzz
 * @date 2020/8/19 14:22
 **/
public class OSUtils {

    private OSUtils(){

    }

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isLinux() {
        return OS.contains("linux");
    }

    public static boolean isWindows() {
        return OS.contains("windows");
    }
}
