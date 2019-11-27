package com.liqiangit.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChildProcessManager {

    private static Logger logger = LoggerFactory.getLogger(ChildProcessManager.class);

    /**
     * 子进程
     */
    private static Map<String, Process> childProcessMap = new HashMap<>();
    public static Map<String, BufferedReader> readerMap = new HashMap<>();
    private static Map<String, BufferedWriter> writerMap = new HashMap<>();
    private static Map<String, BufferedReader> readerErrorMap = new HashMap<>();

    /**
     * 创建子进程
     *
     * @return pid
     * @throws IOException
     */
    public static String childCreate() throws IOException {
        // ChildProcessClass 为子进程
        ProcessBuilder builder = ProcessUtil.exec(ChildProcessClass.class, new ArrayList<>(), new ArrayList<>());
        Process process = builder.start();

        String pid = ProcessUtil.getProcessId(process);
        childProcessMap.put(pid, process);
        readerMap.put(pid, new BufferedReader(new InputStreamReader(process.getInputStream())));
        writerMap.put(pid, new BufferedWriter(new OutputStreamWriter(process.getOutputStream())));
        readerErrorMap.put(pid, new BufferedReader(new InputStreamReader(process.getErrorStream())));

        // 防止异常造成阻塞
        new Thread() {
            @Override
            public void run() {
                try {
                    String line = null;
                    while ((line = readerErrorMap.get(pid).readLine()) != null) {
                        if (line != null) {
                            logger.error("子进程" + pid + "异常:" + line);
                        }
                    }
                } catch (IOException e) {
                    logger.error("记录子进程错误异常", e);
                }
            }
        }.start();

        return pid;
    }

    /**
     * 请求子进程处理报文
     *
     * @param pid 子进程PID
     * @param msg 请求消息
     * @return 返回结果
     * @throws IOException
     */
    public static String execute(String pid, String msg) throws IOException {
        writerMap.get(pid).write(msg + "\n");
        writerMap.get(pid).flush();
        return readerMap.get(pid).readLine();
    }

    /**
     * 结束进线程
     *
     * @param pid
     */
    public static void childClose(String pid) {
        try {
            readerMap.get(pid).close();
            writerMap.get(pid).close();
            readerErrorMap.get(pid).close();
        } catch (IOException e) {
            logger.error("结束子进程异常", e);
        } finally {
            readerMap.remove(pid);
            readerMap.remove(pid);
            readerErrorMap.remove(pid);
            childProcessMap.remove(pid);
            ProcessUtil.killProcessByPid(pid);
        }
    }
}
