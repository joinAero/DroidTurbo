package cc.eevee.turbo.persistence;

import android.content.Context;
import android.content.SharedPreferences;

/*package*/ abstract class Pref {

    protected SharedPreferences mSp;

    public Pref(Context context, String name) {
        mSp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

}
