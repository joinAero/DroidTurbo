package cc.eevee.turbo.core.os;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cc.eevee.turbo.core.util.Log;

public class Terminal {

    public static final String PREFIX = "$ ";
    public static final String PREFIX_SU = "# ";

    public interface OutputCallback {
        void onOutput(ArrayList<String> lines);
    }

    public interface ErrorCallback {
        void onError(Exception e);
    }

    private Process mProcess;

    private DataOutputStream mStdin;
    private BufferedReader mStdout;
    private BufferedReader mStderr;

    private String mCommand;
    private Exception mException;

    private boolean mSuperUser = false;

    public Terminal() {
    }

    public boolean su() {
        return mSuperUser;
    }

    public String prompt() {
        return mSuperUser ? PREFIX_SU : PREFIX;
    }

    public Terminal exec(String command) {
        if (command == null || command.isEmpty())
            throw new IllegalArgumentException("Empty command");

        mException = null;

        try {
            if (mProcess == null) {
                mProcess = Runtime.getRuntime().exec(command);

                mStdin = new DataOutputStream(mProcess.getOutputStream());
                mStdout = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
                mStderr = new BufferedReader(new InputStreamReader(mProcess.getErrorStream()));
            } else {
                DataOutputStream stdin = new DataOutputStream(mProcess.getOutputStream());

                String cmd = command;
                if (!cmd.endsWith("\n")) cmd += "\n";
                stdin.writeBytes(cmd);

                stdin.flush();
            }
            if (!mSuperUser) {
                // easy way to know superuser now
                mSuperUser = checkSuperUser(command);
            }
            mCommand = command;
        } catch (IOException e) {
            e.printStackTrace();
            mException = e;
        }

        return this;
    }

    public Terminal onStdout(OutputCallback callback) {
        if (checkNotProcess()) return this;
        if (callback != null) {
            try {
                String line;
                ArrayList<String> lines = new ArrayList<>();
                while ((line = mStdout.readLine()) != null) {
                    lines.add(line);
                }
                callback.onOutput(lines);
            } catch (IOException e) {
                e.printStackTrace();
                mException = e;
            }
        }
        return this;
    }

    public Terminal onStderr(OutputCallback callback) {
        if (checkNotProcess()) return this;
        if (callback != null) {
            try {
                String line;
                ArrayList<String> lines = new ArrayList<>();
                while ((line = mStderr.readLine()) != null) {
                    lines.add(line);
                }
                callback.onOutput(lines);
            } catch (IOException e) {
                e.printStackTrace();
                mException = e;
            }
        }
        return this;
    }

    public Terminal onError(ErrorCallback callback) {
        if (callback != null && mException != null) {
            callback.onError(mException);
        }
        return this;
    }

    public void close() {
        if (mProcess != null) {
            try {
                mStdin.close();
                mStdout.close();
                mStderr.close();
            } catch (IOException e) {
                e.printStackTrace();
                mException = e;
            }
            mProcess.destroy();
            mProcess = null;
        }
    }

    private boolean checkSuperUser(String command) {
        // how about "su -h"
        return command.trim().equals("su");
    }

    private boolean checkNotProcess() {
        if (mProcess == null) {
            if (mException == null) {
                throw new IllegalStateException("Not command in processing");
            } else {
                Log.w("Terminal", mException);
            }
            return true;
        }
        if (checkSuperUser(mCommand)) {
            // not wait read output if do su
            return true;
        }
        return false;
    }

}
