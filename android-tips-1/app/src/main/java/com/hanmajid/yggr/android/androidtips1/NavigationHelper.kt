package com.hanmajid.yggr.android.androidtips1

import androidx.navigation.NavController
import androidx.navigation.NavDirections

class NavigationHelper {

    companion object {

        fun safeNavigate(navController: NavController, navDirections: NavDirections) {
            try {
                navController.navigate(navDirections)
            } catch (e: IllegalArgumentException) {
                // Ignore this exception
            }
        }
    }
}