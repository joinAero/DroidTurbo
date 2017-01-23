package cc.eevee.turbo.ui.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.eevee.turbo.BuildConfig;
import cc.eevee.turbo.R;
import cc.eevee.turbo.core.os.Terminal;
import cc.eevee.turbo.core.util.Log;
import cc.eevee.turbo.ui.base.BaseActivity;

/**
 * <pre>
 * COMMAND      TEST
 * su           √
 * cd           x       Not record the work directory
 * top          ×       Not support any unterminated commands
 * unknown      √       Catch the exception "Cannot run program "unknown": ..."
 * </pre>
 *
 * <pre>
 * $ ls /sbin
 * ls: /sbin: Permission denied
 * $ su
 * # ls /sbin
 * ...
 * # exit
 * $
 * </pre>
 */
public class TerminalActivity extends BaseActivity {

    private static final String TAG = TerminalActivity.class.getSimpleName();
    private static final boolean DBG = BuildConfig.DEBUG;

    @BindView(R.id.edit) EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        ButterKnife.bind(this);
        initToolbar();
        new CommandProcessor(mEditText);
    }

    private static class TextSpan<T> {

        T mSpan;
        int mStart;
        int mEnd;
        int mFlags;

        public TextSpan(T span, Editable s) {
            mSpan = span;
            mStart = s.getSpanStart(span);
            mEnd = s.getSpanEnd(span);
            mFlags = s.getSpanFlags(span);
        }

        public void apply(Editable s) {
            s.setSpan(mSpan, mStart, mEnd, mFlags);
        }
    }

    private static class TextSpansRestorer {

        private Editable mEditable;
        private ArrayList<TextSpan<ForegroundColorSpan>> mTextSpans;

        public TextSpansRestorer() {
        }

        public TextSpansRestorer(Editable s, int start, int end) {
            reset(s, start, end);
        }

        public void reset(Editable s, int start, int end) {
            mEditable = s;
            ForegroundColorSpan[] spans = s.getSpans(start, end, ForegroundColorSpan.class);
            if (mTextSpans == null) {
                mTextSpans = new ArrayList<>();
            } else {
                mTextSpans.clear();
            }
            for (ForegroundColorSpan span : spans) {
                mTextSpans.add(new TextSpan<>(span, s));
            }
        }

        public void apply() {
            apply(mEditable);
        }

        public void apply(Editable s) {
            for (TextSpan<ForegroundColorSpan> span : mTextSpans) {
                span.apply(s);
            }
        }
    }

    private static class CommandProcessor implements TextWatcher,
            TextView.OnEditorActionListener {

        private final String LINE_DELIMITERS = "\r\n";

        private EditText mEditText;

        private int mFixedLength = 0;

        private int mTextStart;
        private int mTextSelection;
        private CharSequence mTextFixed = null;
        private CharSequence mTextInput = null;
        private CharSequence mTextRest = null;

        private TextSpansRestorer mTextFixedSpans = new TextSpansRestorer();

        private Terminal mTerminal;

        public CommandProcessor(EditText editText) {
            mEditText = editText;

            // TextWatcher events are being fired multiple times
            // http://stackoverflow.com/questions/17535415/textwatcher-events-are-being-fired-multiple-times
            mEditText.addTextChangedListener(this);
            mEditText.setOnEditorActionListener(this);

            mTerminal = new Terminal();

            mEditText.removeTextChangedListener(this);
            doPromptAppend(mEditText);
            mFixedLength = mEditText.length();
            mEditText.addTextChangedListener(this);
        }

        private void doPromptAppend(EditText edit) {
            doPromptAppend(edit, mTerminal.prompt());
        }

        private void doPromptAppend(EditText edit, CharSequence prompt) {
            doPromptAppend(edit.getEditableText(), prompt);
        }

        private void doPromptAppend(Editable s) {
            doPromptAppend(s, mTerminal.prompt());
        }

        private void doPromptAppend(Editable s, CharSequence prompt) {
            doTextAppend(s, prompt, Color.GREEN);
        }

        private void doCommandAppend(EditText edit, CharSequence command) {
            doCommandAppend(edit.getEditableText(), command);
        }

        private void doCommandAppend(Editable s, CharSequence command) {
            doTextAppend(s, command, Color.RED);
        }

        private void doResultAppend(EditText edit, CharSequence result) {
            doResultAppend(edit.getEditableText(), result);
        }

        private void doResultAppend(Editable s, CharSequence result) {
            doTextAppend(s, result, Color.BLUE);
        }

        private void doTextAppend(Editable s, CharSequence text, int color) {
            final int start = s.length();
            ForegroundColorSpan span = new ForegroundColorSpan(color);
            s.append(text);
            s.setSpan(span, start, s.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        /*private void clearTextSpans(Editable s) {
            ForegroundColorSpan[] spans = s.getSpans(0, s.length(), ForegroundColorSpan.class);
            for (ForegroundColorSpan span : spans) s.removeSpan(span);
        }*/

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (DBG) Log.i(TAG, "beforeTextChanged: " + s + ", " + start + ", " + count + ", " + after);
            if (start < mFixedLength) {
                // fixed: [start, mFixedLength)
                mTextStart = start;
                mTextFixed = s.subSequence(start, mFixedLength); // fixed but deleted
                mTextFixedSpans.reset(mEditText.getEditableText(), start, mFixedLength);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (DBG) Log.i(TAG, "onTextChanged: " + s + ", " + start + ", " + before + ", " + count);
            if (start < mFixedLength) {
                // input: [start, start + count)
                // rest: [start + count, s.length()), may fixed text in this
                int endBefore = start + before;
                int endAfter = start + count;
                if (endBefore > mFixedLength) {
                    // not accept input if only replace the fixed text
                    mTextInput = s.subSequence(start, endAfter);
                    mTextSelection = mFixedLength + count;
                } else {
                    endAfter = endAfter + (mFixedLength - endBefore);
                    mTextSelection = endBefore;
                }
                if (endAfter < s.length()) {
                    mTextRest = s.subSequence(endAfter, s.length());
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (DBG) Log.i(TAG, "afterTextChanged: " + s);
            mEditText.removeTextChangedListener(this);
            if (mTextFixed != null) {
                //if (DBG) Log.i(TAG, "mTextFixed: " + mTextFixed);
                //if (DBG) Log.i(TAG, "mTextInput: " + ((mTextInput == null) ? "null" : mTextInput));
                //if (DBG) Log.i(TAG, "mTextRest: " + ((mTextRest == null) ? "null" : mTextRest));
                s.delete(mTextStart, s.length());
                s.append(mTextFixed);
                // restore the fixed text spans
                mTextFixedSpans.apply(s);
                if (mTextInput != null) {
                    CharSequence text = mTextInput;
                    if (mTextRest != null) {
                        text = TextUtils.concat(mTextInput, mTextRest);
                    }
                    int restSelection = s.length() + text.length() - mTextSelection;
                    processInput(s, text);
                    mTextSelection = s.length() - restSelection;
                } else if (mTextRest != null) {
                    // the rest command
                    doCommandAppend(s, mTextRest);
                }

                mEditText.setSelection(mTextSelection);

                mTextFixed = null;
                mTextInput = null;
                mTextRest = null;
            } else {
                int restSelection = s.length() - mEditText.getSelectionEnd();
                CharSequence text = s.subSequence(mFixedLength, s.length());
                s.delete(mFixedLength, s.length());
                processInput(s, text);
                mTextSelection = s.length() - restSelection;

                mEditText.setSelection(mTextSelection);
            }
            mEditText.addTextChangedListener(this);
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (DBG) Log.i(TAG, "onEditorAction: " + actionId + ", " + event);
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // newline to process
                mEditText.getEditableText().insert(mEditText.getSelectionEnd(), "\n");
                return true;
            }
            return false;
        }

        private boolean processInput(Editable s, CharSequence text) {
            if (text.length() == 0) return false;
            StringTokenizer st = new StringTokenizer(text.toString(), LINE_DELIMITERS);
            int count = st.countTokens();
            if (count > 0) {
                int end;
                if (isLastCharNewline(text)) {
                    end = count;
                } else {
                    end = count - 1;
                }
                String cmd;
                for (int i = 0; st.hasMoreTokens(); i++) {
                    cmd = st.nextToken();
                    if (i < end) {
                        onCommandExec(cmd, i, count);
                    } else { // not process the last one
                        onCommandRest(cmd, i, count);
                    }
                }
                return true;
            } else {
                // only a line delimiter
                s.append(text);
                doPromptAppend(s);
                mFixedLength = s.length();
            }
            return false;
        }

        private void onCommandExec(String cmd, int index, int count) {
            if (DBG) Log.i(TAG, "onCommandExec: " + index + ", " + cmd);
            if (index > 0) {
                doPromptAppend(mEditText);
            }
            doCommandAppend(mEditText, cmd);
            mEditText.append("\n");
            mFixedLength = mEditText.length();

            ArrayList<String> result = new ArrayList<>();
            mTerminal.exec(cmd)
                    .onStdout(result::addAll)
                    .onStderr(result::addAll)
                    .onError(e -> {
                        String message = e.getLocalizedMessage();
                        if (message != null) result.add(message);
                    })
                    .close();
            if (!result.isEmpty()) {
                doResultAppend(mEditText, TextUtils.join("\n", result));
                mEditText.append("\n");
            }
            if (index == (count-1)) {
                doPromptAppend(mEditText);
            }
            mFixedLength = mEditText.length();
        }

        private void onCommandRest(String cmd, int index, int count) {
            if (DBG) Log.i(TAG, "onCommandRest: " + index + ", " + cmd);
            if (index > 0) {
                doPromptAppend(mEditText);
                mFixedLength = mEditText.length();
            }
            doCommandAppend(mEditText, cmd);
        }

        private boolean isLastCharNewline(CharSequence text) {
            final int n = text.length();
            if (n == 0) return false;
            return charInString(text.charAt(n-1), LINE_DELIMITERS);
        }

        private boolean charInString(char c, CharSequence s) {
            final int n = s.length();
            if (n == 0) return false;
            for (int i = 0; i < n; i++) {
                if (c == s.charAt(i)) return true;
            }
            return false;
        }
    }

}
