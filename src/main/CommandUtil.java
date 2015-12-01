/*
 * zhangqunshi@126.com
 * 2014-01-16
 */
package main;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 通过SSH执行命令
 *
 * @author zhangqunshi
 */
public class CommandUtil {

    private final String usr;
    private final String pwd;
    private final String host;

    public CommandUtil(String host, String user, String password) {
        this.usr = user;
        this.pwd = password;
        this.host = host;
    }

    /**
     * 在远程运行一个命令
     *
     * @param cmd
     * @return
     * @throws Exception
     */
    public String runCmd(String cmd) throws Exception {
        if (null == cmd || "".equals(cmd.trim())) {
            throw new Exception("Invalid command: " + cmd);
        }

        Connection con = new Connection(host);

        try {
            con.connect();
            boolean isAuthed = con.authenticateWithPassword(usr, pwd);
            System.out.println("authed: " + isAuthed);

            Session session = con.openSession();
            session.execCommand(cmd);

            InputStream stdout = new StreamGobbler(session.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            StringBuilder output = new StringBuilder();

            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                output.append(line).append("\n");
            }
            session.close();
            return output.toString();

        } catch (Exception e) {
            System.out.println("Fail to run command: " + cmd + ", reason: " + e.getMessage());
            throw e;
        } finally {
            con.close();
        }
    }

}
