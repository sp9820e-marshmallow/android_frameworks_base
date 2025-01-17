
package com.sprd.systemui;

import android.app.AddonManager;
import android.content.Context;
import com.android.systemui.R;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import com.android.systemui.statusbar.phone.ActivityStarter;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import android.view.WindowManager;
import com.android.systemui.statusbar.KeyguardIndicationController;

public class SystemuiFeatureUtil {
    static SystemuiFeatureUtil sInstance;

    public SystemuiFeatureUtil() {
    }

    public static SystemuiFeatureUtil getInstance() {
        if (sInstance != null)
            return sInstance;
        sInstance = (SystemuiFeatureUtil) AddonManager.getDefault().getAddon(
                R.string.feature_systemui, SystemuiFeatureUtil.class);
        return sInstance;
    }

    public boolean launchAudioProfile(ActivityStarter activityStarter, Context context) {
        return false;
    }

    public void changeCameraToProfile(KeyguardAffordanceView keyguardAffordanceView) {
    }

    public void changeHeadsUpbelowStatusBar(WindowManager.LayoutParams lp, PhoneStatusBar bar) {
    }

    public boolean changeProfileHint(KeyguardIndicationController keyguardIndicationController) {
        return false;
    }

    //SPRD: 541291 modify the SystemProperties to plugins
    public boolean isCMCC() {
        return false;
    }
}
