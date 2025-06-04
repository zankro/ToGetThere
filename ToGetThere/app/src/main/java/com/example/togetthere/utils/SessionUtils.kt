package com.example.togetthere.utils

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.edit

object SessionPrefs {
    private const val NAME = "session_prefs"
    private const val KEY_LOGGED_ONCE = "logged_onboarding_done"

    fun hasLoggedOnce(ctx: Context): Boolean {
        return ctx
            .getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_LOGGED_ONCE, false)
    }

    fun markLoggedOnce(ctx: Context) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit() {
                putBoolean(KEY_LOGGED_ONCE, true)
            }
    }

    fun clear(ctx: Context) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit() {
                remove(KEY_LOGGED_ONCE)
            }
    }
}
