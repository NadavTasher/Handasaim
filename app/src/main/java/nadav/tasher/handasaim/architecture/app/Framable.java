package nadav.tasher.handasaim.architecture.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;

import nadav.tasher.handasaim.activities.Launcher;
import nadav.tasher.handasaim.tools.architecture.KeyManager;

public class Framable {
    public KeyManager keyManager;
    public SharedPreferences sp;
    public Activity a;

    public Framable(Activity a, SharedPreferences sp, KeyManager keyManager) {
        this.sp = sp;
        this.keyManager = keyManager;
        this.a = a;
    }

    public Context getApplicationContext() {
        return a.getApplicationContext();
    }

    public Window getWindow() {
        return a.getWindow();
    }

    public void setContentView(View v) {
        a.setContentView(v);
    }

    public void start() {
        Launcher.currentFramable = this;
        go();
    }

    public void go() {
    }

    public void onBackPressed() {
    }
}