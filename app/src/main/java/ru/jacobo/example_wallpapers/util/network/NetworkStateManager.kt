package ru.jacobo.example_wallpapers.util.network

import android.content.Context
import android.net.ConnectivityManager
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.jacobo.example_wallpapers.ui.TAG


class NetworkStateManager {
    /**
     * Updates the active network status live-data
     */
    fun setNetworkConnectivityStatus(connectivityStatus: Boolean) {
        Log.d(TAG, "setNetworkConnectivityStatus() called with: connectivityStatus = [$connectivityStatus]")
        if (Looper.myLooper() == Looper.getMainLooper()) {
            activeNetworkStatusMLD.setValue(connectivityStatus)
        } else {
            activeNetworkStatusMLD.postValue(connectivityStatus)
        }
    }

    /**
     * Returns the current network status
     */
    val networkConnectivityStatus: LiveData<Boolean>
        get() {
            Log.d(TAG, "getNetworkConnectivityStatus() called")
            return activeNetworkStatusMLD
        }

    companion object {
        private var INSTANCE: NetworkStateManager? = null
        private val activeNetworkStatusMLD = MutableLiveData<Boolean>()

        @get:Synchronized
        val instance: NetworkStateManager?
            get() {
                if (INSTANCE == null) {
                    Log.d(TAG, "getInstance() called: Creating new instance")
                    INSTANCE = NetworkStateManager()
                }
                return INSTANCE
            }
    }
}