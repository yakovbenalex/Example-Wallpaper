package ru.jacobo.example_wallpapers.util

import androidx.fragment.app.Fragment

interface Navigator {
    fun navigate(fragment: Fragment, TAG: String? = null)
    fun goBack()
}