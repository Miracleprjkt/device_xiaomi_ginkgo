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

import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.preference.PreferenceManager;

import org.lineageos.settings.R;

public class MonitorTileService extends TileService {
    private Monitor mMonitor;

    @Override
    public void onCreate() {
        super.onCreate();
        mMonitor = Monitor.getInstance(this);
    }

    @Override
    public void onStartListening() {
        boolean enabled = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("monitor_enable", false);
        updateTileState(enabled);
    }

    @Override
    public void onClick() {
        boolean currentlyEnabled = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("monitor_enable", false);
        boolean newState = !currentlyEnabled;

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean("monitor_enable", newState)
                .commit();

        updateTileState(newState);

        if (newState) {
            mMonitor.applyPreferences();
            mMonitor.show();
        } else {
            mMonitor.hide();
        }
    }

    private void updateTileState(boolean enabled) {
        Tile tile = getQsTile();
        if (tile == null) return;
        
        tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setLabel(getString(R.string.monitor_tile_label));
        tile.setContentDescription(getString(R.string.monitor_tile_description));
        tile.updateTile();
    }
}
