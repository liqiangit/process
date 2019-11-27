package com.liqiangit.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ChildProcessClass {

    private static Logger logger = LoggerFactory.getLogger(ChildProcessClass.class);

    public static void main(String[] args) throws Exception {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            // 通过System.out.println来返回主进程数据,所以注意代码中的通过System.out.println
            System.out.println(handle(line));
        }
        bufferedReader.close();
    }

    /**
     * 处理请求
     *
     * @param msg
     * @return
     */
    private static String handle(String msg) {
        // TODO 处理请求,根据业务来控制
        // ---start---
        String result = "";
        try {
            result = new String("请求报文:这是返回报文|".getBytes("UTF-8")) + msg;
        } catch (UnsupportedEncodingException e) {
            logger.error("返回报文字符编码异常", e);
        }
        // ---end--

        return result;
    }
}
