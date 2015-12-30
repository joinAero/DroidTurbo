package cc.cubone.turbo.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Menu;
import android.view.MenuItem;

public class TintUtils {

    /**
     * Tint color to drawable.
     *
     * @param drawable The desired drawable
     * @param color The tint color
     */
    public static void tint(@NonNull Drawable drawable, @ColorInt int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable.mutate(), color);
    }

    /**
     * Tint color to drawable.
     *
     * @param context The context
     * @param drawable The desired drawable
     * @param resId The resource identifier
     */
    public static void tintList(@NonNull Context context, @NonNull Drawable drawable, @ColorRes int resId) {
        drawable = DrawableCompat.wrap(drawable);
        ColorStateList tint = ContextCompat.getColorStateList(context, resId);
        DrawableCompat.setTintList(drawable.mutate(), tint);
    }

    /**
     * Tint color to menu item.
     *
     * @param item The menu item
     * @param color The tint color
     */
    public static void tint(MenuItem item, @ColorInt int color) {
        Drawable drawable = item.getIcon();
        if (drawable == null) return;
        tint(drawable, color);
    }

    /**
     * Tint color state list to menu item.
     *
     * @param item The menu item
     * @param resId The resource identifier
     */
    public static void tintList(Context context, MenuItem item, @ColorRes int resId) {
        Drawable drawable = item.getIcon();
        if (drawable == null) return;
        tintList(context, drawable, resId);
    }

    /**
     * Tint color to menu all items.
     *
     * @param menu The menu
     * @param color The tint color
     */
    public static void tint(Menu menu, @ColorInt int color) {
        MenuItem item;
        final int size = menu.size();
        for (int i = 0; i < size; ++i) {
            item = menu.getItem(i);
            tint(item, color);
        }
    }

    /**
     * Tint color state list to menu all items.
     *
     * @param menu The menu
     * @param resId The resource identifier
     */
    public static void tintList(Context context, Menu menu, @ColorRes int resId) {
        MenuItem item;
        final int size = menu.size();
        for (int i = 0; i < size; ++i) {
            item = menu.getItem(i);
            tintList(context, item, resId);
        }
    }

}
