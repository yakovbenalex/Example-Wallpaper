package ru.jacobo.example_wallpapers.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.jacobo.example_wallpapers.R
import ru.jacobo.example_wallpapers.adapters.TopicListAdapter
import ru.jacobo.example_wallpapers.api.RetrofitInstance
import ru.jacobo.example_wallpapers.model.json.Topic
import ru.jacobo.example_wallpapers.util.Navigator
import ru.jacobo.example_wallpapers.util.network.NetworkStateManager


const val TAG = "myLogTag"

class TopicListFragment : Fragment() {
    private lateinit var navigator: Navigator
    private lateinit var tvWaitingInternet: TextView
    private lateinit var pbWaitingInternet: ProgressBar
    private lateinit var activeNetworkStateObserver: Observer<Boolean?>

    private var topicListAdapter = TopicListAdapter() { idTopic ->
        navigator.navigate(PhotoListFragment.newInstance(idTopic))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Navigator) {
            navigator = context
        } else {
            error("Host should implement Navigator interface")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_topic_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViews()

        bindViews()

        /**
         * Observer for internet connectivity status live-data
         */
        activeNetworkStateObserver = Observer<Boolean?> { isConnected ->
            if (isConnected) {
                tvWaitingInternet.visibility = View.GONE
                pbWaitingInternet.visibility = View.GONE
                Log.d(TAG, "TopicListFragment: isConnected:")
                bindViews()
            } else {
                tvWaitingInternet.visibility = View.VISIBLE
                pbWaitingInternet.visibility = View.VISIBLE
                Log.d(TAG, "TopicListFragment: isNotConnected:")

                Toast.makeText(
                    requireContext(),
                    "There is no Internet connection",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        NetworkStateManager.instance?.networkConnectivityStatus?.observe(
            viewLifecycleOwner,
            activeNetworkStateObserver
        )
    }

    //    private fun findViews(view: View) {
    private fun findViews() {
        requireView().run {
            tvWaitingInternet = findViewById(R.id.tvWaitingInternet)
            pbWaitingInternet = findViewById(R.id.pbWaitingInternet)
        }
    }


    private fun bindViews() {
        bindTopicList()
    }

    private fun bindTopicList() {
        requireView().findViewById<RecyclerView>(R.id.rvTopicList).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = topicListAdapter
        }
        pbWaitingInternet.visibility = View.VISIBLE
        RetrofitInstance.api.getTopicList(20).enqueue(object : Callback<ArrayList<Topic>?> {
            override fun onResponse(
                call: Call<ArrayList<Topic>?>,
                response: Response<ArrayList<Topic>?>
            ) {
                pbWaitingInternet.visibility = View.GONE
                if (response.isSuccessful) {
                    topicListAdapter.updateList(response.body()!!)
//                    Log.d(TAG, "TopicListFragment onResponse: ")
                }
            }

            override fun onFailure(call: Call<ArrayList<Topic>?>, t: Throwable) {
//                Log.d(TAG, "TopicListFragment onFailure: $t")
            }
        })
    }

    companion object {
        const val FRAGMENT_TOPIC_LIST_TAG = "FRAGMENT_CATEGORY_LIST_TAG"

        fun newInstance() = TopicListFragment()
    }
}