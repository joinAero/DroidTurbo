package cc.eevee.turbo.core.os;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cc.eevee.turbo.core.util.Log;

/**
 * @see <a href="https://github.com/Stericson/RootShell">RootShell</a>
 * @see <a href="https://github.com/Stericson/RootTools">RootTools</a>
 * @see <a href="https://github.com/jackpal/Android-Terminal-Emulator"></a>
 */
public class Terminal {

    public static final String TAG = Terminal.class.getSimpleName();

    public static final String PROMPT = "$ ";
    public static final String PROMPT_ROOT = "# ";

    private boolean mUseRoot = false;

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

    private Exception mException;

    public Terminal() {
    }

    public boolean useRoot() {
        return mUseRoot;
    }

    public String prompt() {
        return mUseRoot ? PROMPT_ROOT : PROMPT;
    }

    public Terminal exec(String command) {
        if (command == null || command.isEmpty())
            throw new IllegalArgumentException("Empty command");

        if (processRoot(command)) return this;
        if (mProcess != null) {
            Log.w(TAG, "Close for each command to prevent \"Stream closed\"");
            close();
        }

        mException = null;
        try {
            if (mProcess == null) {
                if (mUseRoot) {
                    mProcess = Runtime.getRuntime().exec("su");
                } else {
                    mProcess = Runtime.getRuntime().exec("sh");
                }
                mStdin = new DataOutputStream(mProcess.getOutputStream());
                mStdout = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
                mStderr = new BufferedReader(new InputStreamReader(mProcess.getErrorStream()));
            }
            String cmd = command;
            if (!cmd.endsWith("\n")) cmd += "\n";
            mStdin.writeBytes(cmd);
            mStdin.flush();
            // not block for su & sh
            mStdin.writeBytes("exit\n");
            mStdin.flush();
        } catch (IOException e) {
            e.printStackTrace();
            mException = e;
        }

        return this;
    }

    private boolean processRoot(String command) {
        boolean changed = false;
        // easy way to change root access
        if (mUseRoot) {
            if (command.trim().equals("exit")) {
                mUseRoot = false;
                changed = true;
            }
        }
        // not exec su whatever root access or not
        if (command.trim().equals("su")) {
            if (!mUseRoot) {
                // TODO check "su" granted or not
            }
            mUseRoot = true;
            changed = true;
        }
        if (changed) {
            // close sub process when change root access
            close();
            Log.i(TAG, "Change root access");
        }
        return changed;
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
                mProcess.waitFor();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            mProcess.destroy();
            mProcess = null;
        }
    }

    private boolean checkNotProcess() {
        if (mProcess == null) {
            if (mException != null) {
                Log.w(TAG, mException);
            }
            return true;
        }
        return false;
    }

}
