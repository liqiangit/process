package com.liqiangit.process;

import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ProcessUtil {

    private static Logger logger = LoggerFactory.getLogger(ProcessUtil.class);

    /**
     * 启动子进程
     *
     * @param clazz   子进程Main类
     * @param jvmArgs 启动参数
     * @param args    参数
     * @return
     */
    public static ProcessBuilder exec(Class clazz, List<String> jvmArgs, List<String> args) {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = clazz.getName();

        List<String> command = new ArrayList<>();
        command.add(javaBin);
        command.addAll(jvmArgs);
        command.add("-cp");
        command.add(classpath);
        command.add(className);
        command.addAll(args);
        return new ProcessBuilder(command);
    }


    /**
     * 获取子进程Pid
     *
     * @param process
     * @return
     */
    public static String getProcessId(Process process) {
        long pid = -1;
        Field field = null;
        if (Platform.isWindows()) {

            try {
                field = process.getClass().getDeclaredField("handle");
                field.setAccessible(true);
                pid = Kernel32.INSTANCE.GetProcessId((Long) field.get(process));
            } catch (Exception e) {
                logger.error("获取windows pid异常", e);
            }

        } else if (Platform.isLinux()) {//|| Platform.isAIX()

            try {
                Class<?> clazz = Class.forName("java.lang.UNIXProcess");
                field = clazz.getDeclaredField("pid");
                field.setAccessible(true);
                pid = (Integer) field.get(process);
            } catch (Throwable e) {
                logger.error("获取linux pid异常", e);
            }

        }
        return String.valueOf(pid);
    }

    /**
     * 关闭进程
     *
     * @param pid 进程的PID
     */
    public static boolean killProcessByPid(String pid) {
        if ((pid == null && "".equals(pid)) || "-1".equals(pid)) {
            return true;
        }

        Process process = null;
        BufferedReader reader = null;
        String command = "";
        boolean result = false;

        if (Platform.isWindows()) {
            command = "cmd.exe /c taskkill /PID " + pid + " /F /T ";
        } else if (Platform.isLinux()) { // || Platform.isAIX()
            command = "kill -9 " + pid;
        }

        try {

            //杀掉进程
            process = Runtime.getRuntime().exec(command);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                logger.info("kill PID return info -----> " + line);
            }
            result = true;

        } catch (Exception e) {
            logger.info("杀进程出错：", e);
            result = false;
        } finally {

            if (process != null) {
                process.destroy();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.info("杀进程出错：", e);
                }
            }

        }

        return result;
    }
}