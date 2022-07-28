package ru.jacobo.example_wallpapers.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import ru.jacobo.example_wallpapers.R
import ru.jacobo.example_wallpapers.ui.TopicListFragment.Companion.FRAGMENT_TOPIC_LIST_TAG
import ru.jacobo.example_wallpapers.util.Navigator
import ru.jacobo.example_wallpapers.util.network.NetworkMonitoringUtil
import ru.jacobo.example_wallpapers.util.network.NetworkStateManager


class MainActivity : AppCompatActivity(), Navigator {
    private lateinit var mNetworkMonitoringUtil: NetworkMonitoringUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mNetworkMonitoringUtil = NetworkMonitoringUtil(applicationContext)
        mNetworkMonitoringUtil.checkNetworkState()
        mNetworkMonitoringUtil.registerNetworkCallbackEvents()

        setMainFragment()
    }

    private fun setMainFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_container,
                TopicListFragment.newInstance(),
                FRAGMENT_TOPIC_LIST_TAG
            )
            .commit()
    }

    override fun navigate(fragment: Fragment, TAG: String?) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment, TAG ?: "${fragment::class.java}")
//            .addToBackStack(null)
            .addToBackStack(TAG ?: "${fragment::class.java}")
            .commit()
    }

    override fun goBack() {
        supportFragmentManager.popBackStack()
    }
}