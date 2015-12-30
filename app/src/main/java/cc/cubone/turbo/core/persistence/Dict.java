package cc.cubone.turbo.core.persistence;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class Dict {

    protected SharedPreferences mSp;

    public Dict(Context context, String name) {
        mSp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

}
