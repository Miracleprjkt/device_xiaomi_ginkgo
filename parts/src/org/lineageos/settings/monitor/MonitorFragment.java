/*
 * Copyright (C) 2025 kenway214
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.monitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.android.settingslib.widget.MainSwitchPreference;

import org.lineageos.settings.R;

public class MonitorFragment extends PreferenceFragmentCompat {

    private Monitor mMonitor;
    private MainSwitchPreference mMasterSwitch;
    private SwitchPreferenceCompat mAutoEnableSwitch;
    private SwitchPreferenceCompat mFpsSwitch;
    private SwitchPreferenceCompat mBatteryTempSwitch;
    private SwitchPreferenceCompat mCpuUsageSwitch;
    private SwitchPreferenceCompat mCpuClockSwitch;
    private SwitchPreferenceCompat mCpuTempSwitch;
    private SwitchPreferenceCompat mRamSwitch;
    private SwitchPreferenceCompat mGpuUsageSwitch;
    private SwitchPreferenceCompat mGpuClockSwitch;
    private SwitchPreferenceCompat mGpuTempSwitch;
    private Preference mCaptureStartPref;
    private Preference mCaptureStopPref;
    private Preference mCaptureExportPref;
    private SwitchPreferenceCompat mDoubleTapCapturePref;
    private SwitchPreferenceCompat mSingleTapTogglePref;
    private SwitchPreferenceCompat mLongPressEnablePref;
    private ListPreference  mLongPressTimeoutPref;
    private SeekBarPreference mTextSizePref;
    private SeekBarPreference mBgAlphaPref;
    private SeekBarPreference mCornerRadiusPref;
    private SeekBarPreference mPaddingPref;
    private SeekBarPreference mItemSpacingPref;
    private ListPreference mUpdateIntervalPref;
    private ListPreference mTextColorPref;
    private ListPreference mTitleColorPref;
    private ListPreference mValueColorPref;
    private ListPreference mPositionPref;
    private ListPreference mSplitModePref;
    private ListPreference mOverlayFormatPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.monitor_preferences, rootKey);

        mMonitor = Monitor.getInstance(getContext());

        // Initialize all preferences.
        mMasterSwitch       = findPreference("monitor_enable");
        mAutoEnableSwitch   = findPreference("monitor_auto_enable");
        mFpsSwitch          = findPreference("monitor_fps_enable");
        mBatteryTempSwitch  = findPreference("monitor_temp_enable");
        mCpuUsageSwitch     = findPreference("monitor_cpu_usage_enable");
        mCpuClockSwitch     = findPreference("monitor_cpu_clock_enable");
        mCpuTempSwitch      = findPreference("monitor_cpu_temp_enable");
        mRamSwitch          = findPreference("monitor_ram_enable");
        mGpuUsageSwitch     = findPreference("monitor_gpu_usage_enable");
        mGpuClockSwitch     = findPreference("monitor_gpu_clock_enable");
        mGpuTempSwitch      = findPreference("monitor_gpu_temp_enable");

        mCaptureStartPref   = findPreference("monitor_capture_start");
        mCaptureStopPref    = findPreference("monitor_capture_stop");
        mCaptureExportPref  = findPreference("monitor_capture_export");

        mDoubleTapCapturePref = findPreference("monitor_doubletap_capture");
        mSingleTapTogglePref  = findPreference("monitor_single_tap_toggle");
        mLongPressEnablePref  = findPreference("monitor_longpress_enable");
        mLongPressTimeoutPref = findPreference("monitor_longpress_timeout");

        mTextSizePref       = findPreference("monitor_text_size");
        mBgAlphaPref        = findPreference("monitor_background_alpha");
        mCornerRadiusPref   = findPreference("monitor_corner_radius");
        mPaddingPref        = findPreference("monitor_padding");
        mItemSpacingPref    = findPreference("monitor_item_spacing");

        mUpdateIntervalPref = findPreference("monitor_update_interval");
        mTextColorPref      = findPreference("monitor_text_color");
        mTitleColorPref     = findPreference("monitor_title_color");
        mValueColorPref     = findPreference("monitor_value_color");
        mPositionPref       = findPreference("monitor_position");
        mSplitModePref      = findPreference("monitor_split_mode");
        mOverlayFormatPref  = findPreference("monitor_format");

        Preference appSelectorPref = findPreference("monitor_app_selector");
        if (appSelectorPref != null) {
            appSelectorPref.setOnPreferenceClickListener(pref -> {
                Intent intent = new Intent(getContext(), MonitorAppSelectorActivity.class);
                startActivity(intent);
                return true;
            });
        }
        Preference appRemoverPref = findPreference("monitor_app_remover");
        if (appRemoverPref != null) {
            appRemoverPref.setOnPreferenceClickListener(pref -> {
                Intent intent = new Intent(getContext(), MonitorAppRemoverActivity.class);
                startActivity(intent);
                return true;
            });
        }

        if (mMasterSwitch != null) {
            mMasterSwitch.setOnPreferenceChangeListener((pref, newValue) -> {
                boolean enabled = (boolean) newValue;
                if (enabled) {
                    if (Settings.canDrawOverlays(getContext())) {
                        mMonitor.applyPreferences();
                        mMonitor.show();
                        getContext().startService(new Intent(getContext(), MonitorService.class));
                    } else {
                        Toast.makeText(getContext(), R.string.overlay_permission_required, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    mMonitor.hide();
                    if (mAutoEnableSwitch == null || !mAutoEnableSwitch.isChecked()) {
                        getContext().stopService(new Intent(getContext(), MonitorService.class));
                    }
                }
                return true;
            });
        }

        if (mAutoEnableSwitch != null) {
            mAutoEnableSwitch.setOnPreferenceChangeListener((pref, newValue) -> {
                boolean autoEnabled = (boolean) newValue;
                if (autoEnabled) {
                    getContext().startService(new Intent(getContext(), MonitorService.class));
                } else {
                    if (mMasterSwitch == null || !mMasterSwitch.isChecked()) {
                        getContext().stopService(new Intent(getContext(), MonitorService.class));
                    }
                }
                return true;
            });
        }

        if (mFpsSwitch != null) {
            mFpsSwitch.setOnPreferenceChangeListener((pref, newValue) -> {
                mMonitor.setShowFps((boolean) newValue);
                return true;
            });
        }
        if (mBatteryTempSwitch != null) {
            mBatteryTempSwitch.setOnPreferenceChangeListener((pref, newValue) -> {
                mMonitor.setShowBatteryTemp((boolean) newValue);
                return true;
            });
        }
        if (mCpuUsageSwitch != null) {
            mCpuUsageSwitch.setOnPreferenceChangeListener((pref, newValue) -> {
                mMonitor.setShowCpuUsage((boolean) newValue);
                return true;
            });
        }
        if (mCpuClockSwitch != null) {
            mCpuClockSwitch.setOnPreferenceChangeListener((pref, newValue) -> {
                mMonitor.setShowCpuClock((boolean) newValue);
                return true;
            });
        }
        if (mCpuTempSwitch != null) {
            mCpuTempSwitch.setOnPreferenceChangeListener((pref, newValue) -> {
                mMonitor.setShowCpuTemp((boolean) newValue);
                return true;
            });
        }
        if (mRamSwitch != null) {
            mRamSwitch.setOnPreferenceChangeListener((pref, newValue) -> {
                mMonitor.setShowRam((boolean) newValue);
                return true;
            });
        }
        if (mGpuUsageSwitch != null) {
            mGpuUsageSwitch.setOnPreferenceChangeListener((pref, newValue) -> {
                mMonitor.setShowGpuUsage((boolean) newValue);
                return true;
            });
        }
        if (mGpuClockSwitch != null) {
            mGpuClockSwitch.setOnPreferenceChangeListener((pref, newValue) -> {
                mMonitor.setShowGpuClock((boolean) newValue);
                return true;
            });
        }
        if (mGpuTempSwitch != null) {
            mGpuTempSwitch.setOnPreferenceChangeListener((pref, newValue) -> {
                mMonitor.setShowGpuTemp((boolean) newValue);
                return true;
            });
        }
        if (mCaptureStartPref != null) {
            mCaptureStartPref.setOnPreferenceClickListener(pref -> {
                MonitorDataExport.getInstance().startCapture();
                Toast.makeText(getContext(), "Started logging Data", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
        if (mCaptureStopPref != null) {
            mCaptureStopPref.setOnPreferenceClickListener(pref -> {
                MonitorDataExport.getInstance().stopCapture();
                Toast.makeText(getContext(), "Stopped logging Data", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
        if (mCaptureExportPref != null) {
            mCaptureExportPref.setOnPreferenceClickListener(pref -> {
                MonitorDataExport.getInstance().exportDataToCsv();
                Toast.makeText(getContext(), "Exported log data to file", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
        if (mDoubleTapCapturePref != null) {
            mDoubleTapCapturePref.setOnPreferenceChangeListener((pref, newValue) -> {
                mMonitor.setDoubleTapCaptureEnabled((boolean) newValue);
                return true;
            });
        }
        if (mSingleTapTogglePref != null) {
            mSingleTapTogglePref.setOnPreferenceChangeListener((pref, newValue) -> {
                mMonitor.setSingleTapToggleEnabled((boolean) newValue);
                return true;
            });
        }
        if (mLongPressEnablePref != null) {
            mLongPressEnablePref.setOnPreferenceChangeListener((pref, newValue) -> {
                mMonitor.setLongPressEnabled((boolean) newValue);
                return true;
            });
        }
        if (mLongPressTimeoutPref != null) {
            mLongPressTimeoutPref.setOnPreferenceChangeListener((pref, newValue) -> {
                if (newValue instanceof String) {
                    long ms = Long.parseLong((String) newValue);
                    mMonitor.setLongPressThresholdMs(ms);
                }
                return true;
            });
        }
        if (mTextSizePref != null) {
            mTextSizePref.setOnPreferenceChangeListener((pref, newValue) -> {
                if (newValue instanceof Integer) {
                    mMonitor.updateTextSize((Integer) newValue);
                }
                return true;
            });
        }
        if (mBgAlphaPref != null) {
            mBgAlphaPref.setOnPreferenceChangeListener((pref, newValue) -> {
                if (newValue instanceof Integer) {
                    mMonitor.updateBackgroundAlpha((Integer) newValue);
                }
                return true;
            });
        }
        if (mCornerRadiusPref != null) {
            mCornerRadiusPref.setOnPreferenceChangeListener((pref, newValue) -> {
                if (newValue instanceof Integer) {
                    mMonitor.updateCornerRadius((Integer) newValue);
                }
                return true;
            });
        }
        if (mPaddingPref != null) {
            mPaddingPref.setOnPreferenceChangeListener((pref, newValue) -> {
                if (newValue instanceof Integer) {
                    mMonitor.updatePadding((Integer) newValue);
                }
                return true;
            });
        }
        if (mItemSpacingPref != null) {
            mItemSpacingPref.setOnPreferenceChangeListener((pref, newValue) -> {
                if (newValue instanceof Integer) {
                    mMonitor.updateItemSpacing((Integer) newValue);
                }
                return true;
            });
        }
        if (mUpdateIntervalPref != null) {
            mUpdateIntervalPref.setOnPreferenceChangeListener((pref, newValue) -> {
                if (newValue instanceof String) {
                    mMonitor.updateUpdateInterval((String) newValue);
                }
                return true;
            });
        }
        if (mTextColorPref != null) {
            mTextColorPref.setOnPreferenceChangeListener((pref, newValue) -> true);
        }
        if (mTitleColorPref != null) {
            mTitleColorPref.setOnPreferenceChangeListener((pref, newValue) -> {
                if (newValue instanceof String) {
                    mMonitor.updateTitleColor((String) newValue);
                }
                return true;
            });
        }
        if (mValueColorPref != null) {
            mValueColorPref.setOnPreferenceChangeListener((pref, newValue) -> {
                if (newValue instanceof String) {
                    mMonitor.updateValueColor((String) newValue);
                }
                return true;
            });
        }
        if (mPositionPref != null) {
            mPositionPref.setOnPreferenceChangeListener((pref, newValue) -> {
                if (newValue instanceof String) {
                    mMonitor.updatePosition((String) newValue);
                }
                return true;
            });
        }
        if (mSplitModePref != null) {
            mSplitModePref.setOnPreferenceChangeListener((pref, newValue) -> {
                if (newValue instanceof String) {
                    mMonitor.updateSplitMode((String) newValue);
                }
                return true;
            });
        }
        if (mOverlayFormatPref != null) {
            mOverlayFormatPref.setOnPreferenceChangeListener((pref, newValue) -> {
                if (newValue instanceof String) {
                    mMonitor.updateOverlayFormat((String) newValue);
                }
                return true;
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hasUsageStatsPermission(requireContext())) {
            requestUsageStatsPermission();
        }
        Context context = getContext();
        if (context != null) {
            if ((mMasterSwitch != null && mMasterSwitch.isChecked()) ||
                (mAutoEnableSwitch != null && mAutoEnableSwitch.isChecked())) {
                context.startService(new Intent(context, MonitorService.class));
            } else {
                context.stopService(new Intent(context, MonitorService.class));
            }
        }
    }

    private boolean hasUsageStatsPermission(Context context) {
        android.app.AppOpsManager appOps = (android.app.AppOpsManager)
                context.getSystemService(Context.APP_OPS_SERVICE);
        if (appOps == null) return false;
        int mode = appOps.checkOpNoThrow(
                android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.getPackageName()
        );
        return (mode == android.app.AppOpsManager.MODE_ALLOWED);
    }

    private void requestUsageStatsPermission() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }
}

