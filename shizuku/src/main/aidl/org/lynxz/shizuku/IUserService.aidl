package org.lynxz.shizuku;

interface IUserService {

    void destroy() = 16777114; // Destroy method defined by Shizuku server

    void exit() = 1; // Exit method defined by user

    /**
     * 执行命令
     * @param cmd 命令
     */
    String exec(String cmd) = 2;
}