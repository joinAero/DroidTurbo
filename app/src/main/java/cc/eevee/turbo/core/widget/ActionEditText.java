package cc.eevee.turbo.core.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/**
 * An EditText that lets you use actions ("Done", "Go", etc.) on multi-line edits.
 * @see <a href="http://stackoverflow.com/questions/5014219/multiline-edittext-with-done-softinput-action-label-on-2-3">
 *     Multiline EditText with Done SoftInput Action</a>
 */
public class ActionEditText extends EditText {

    public ActionEditText(Context context) {
        super(context);
    }

    public ActionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ActionEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }
}
