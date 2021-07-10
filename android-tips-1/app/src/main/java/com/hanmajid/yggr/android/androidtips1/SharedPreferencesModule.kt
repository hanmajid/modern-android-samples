package com.hanmajid.yggr.android.androidtips1

class SharedPreferencesModule {

    companion object {
        // This is the version of SharedPreferences.
        // Increment this value every time a value type is changed.
        private const val SP_VERSION = 1

        // This is the key to getting "user_id" value
        const val SP_USER_ID = "user_id_$SP_VERSION"
    }
}