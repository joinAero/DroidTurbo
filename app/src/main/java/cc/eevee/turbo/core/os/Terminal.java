package cc.eevee.turbo.core.os;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
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

    public static final String PROMPT = "$";
    public static final String PROMPT_ROOT = "#";

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

    private File mWorkDir = null;
    private File mWorkDirRoot = null;

    private ArrayList<String> mChangeDirResult = null;
    private boolean mChangeDirResultError = false;

    public Terminal() {
    }

    public boolean useRoot() {
        return mUseRoot;
    }

    public String prompt() {
        String prompt = mUseRoot ? PROMPT_ROOT : PROMPT;
        return workDir() + " " + prompt + " ";
    }

    public File workDir() {
        if (mUseRoot) {
            if (mWorkDirRoot == null) mWorkDirRoot = new File("/");
            return mWorkDirRoot;
        } else {
            if (mWorkDir == null) mWorkDir = new File("/");
            return mWorkDir;
        }
    }

    public void setWorkDir(File dir) {
        if (mUseRoot) {
            mWorkDirRoot = dir;
        } else {
            mWorkDir = dir;
        }
    }

    public Terminal exec(String cmd) {
        return exec(cmd, workDir());
    }

    public Terminal exec(String cmd, File dir) {
        if (cmd == null || cmd.isEmpty())
            throw new IllegalArgumentException("Empty command");

        String trimCmd = cmd.trim();
        if (processRoot(trimCmd)) return this;

        if (mProcess != null) {
            Log.w(TAG, "Close for each command to prevent \"Stream closed\"");
            close();
        }

        mException = null;
        try {
            if (mProcess == null) {
                if (mUseRoot) {
                    mProcess = Runtime.getRuntime().exec("su", null);
                } else {
                    mProcess = Runtime.getRuntime().exec("sh", null, dir);
                }
                mStdin = new DataOutputStream(mProcess.getOutputStream());
                mStdout = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
                mStderr = new BufferedReader(new InputStreamReader(mProcess.getErrorStream()));
            }
            if (mUseRoot && dir != null && !dir.toString().equals("/")) {
                mStdin.writeBytes("cd " + dir + "\n");
                mStdin.flush();
            }
            if (!cmd.endsWith("\n")) cmd += "\n";
            mStdin.writeBytes(cmd);
            mStdin.flush();
            // not block for su & sh
            mStdin.writeBytes("exit\n");
            mStdin.flush();

            processChangeDir(trimCmd);
        } catch (IOException e) {
            e.printStackTrace();
            mException = e;
        }

        return this;
    }

    private boolean processRoot(String cmd) {
        boolean changed = false;
        // easy way to change root access
        if (mUseRoot) {
            if (cmd.equals("exit")) {
                mUseRoot = false;
                changed = true;
            }
        }
        // not exec su whatever root access or not
        if (cmd.equals("su")) {
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

    private void processChangeDir(String cmd) {
        mChangeDirResult = null;
        mChangeDirResultError = false;
        if (cmd.startsWith("cd")) {
            onStdout(lines -> {
                if (!lines.isEmpty()) {
                    mChangeDirResult = lines;
                }
            });
            if (mChangeDirResult != null) return;
            onStderr(lines -> {
                if (!lines.isEmpty()) {
                    mChangeDirResult = lines;
                    mChangeDirResultError = true;
                }
            });
            if (mChangeDirResult != null) return;
            // result is null, regard as ok
            String dir = cmd.substring(cmd.indexOf("cd")+2).trim();
            File workDir = workDir();
            if (dir.startsWith("/")) {
                workDir = new File(dir);
            } else {
                workDir = new File(workDir, dir);
            }
            setWorkDir(workDir);
            Log.i(TAG, "ChangeDir: " + dir + ", " + workDir);
        }
    }

    private boolean processChangeDirResult(boolean stderr, OutputCallback callback) {
        //if (callback == null) return false;
        if (mChangeDirResult == null) return false;
        if (stderr) {
            if (mChangeDirResultError) {
                callback.onOutput(mChangeDirResult);
                return true;
            }
        } else { // stdout
            if (!mChangeDirResultError) {
                callback.onOutput(mChangeDirResult);
                return true;
            }
        }
        return false;
    }

    public Terminal onStdout(OutputCallback callback) {
        if (checkNotProcess()) return this;
        if (callback != null) {
            if (processChangeDirResult(false, callback))
                return this;
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
            if (processChangeDirResult(true, callback))
                return this;
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
