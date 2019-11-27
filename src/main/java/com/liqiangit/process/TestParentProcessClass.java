package com.liqiangit.process;

import java.io.IOException;

public class TestParentProcessClass {

    /**
     * 测试方法
     *
     * @param args
     */
    public static void main(String[] args) {

        try {
            // 创建子进程
            String pid = ChildProcessManager.childCreate();

            String result;
            for (int i = 0; i < 10; i++) {
                // 请求报文
                result = ChildProcessManager.execute(pid, "请求报文:" + "测试方法" + i);
                // 打印返回报文
                System.out.println(result);
            }

            // 关闭子进程
            ChildProcessManager.childClose(pid);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
