package ru.jacobo.example_wallpapers.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.jacobo.example_wallpapers.R
import ru.jacobo.example_wallpapers.adapters.PhotoListAdapter
import ru.jacobo.example_wallpapers.api.RetrofitInstance
import ru.jacobo.example_wallpapers.model.json.Photo
import ru.jacobo.example_wallpapers.util.Navigator

private const val ARG_ID_TOPIC = "idTopic"

class PhotoListFragment : Fragment() {
    private lateinit var navigator: Navigator
    private lateinit var idTopic: String
    private lateinit var tvPhotoListInfoMessage: TextView
    private lateinit var pbWaiting: ProgressBar

    private var photoListAdapter = PhotoListAdapter() { photo ->
        navigator.navigate(PhotoFullscreenFragment.newInstance(photo.id))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            bundle.getString(ARG_ID_TOPIC)?.let {
                idTopic = it
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "PhotoListFragment onViewCreated: idTopic: $idTopic")
        findViews()

        bindViews()
    }

    private fun findViews() {
        requireView().run {
            tvPhotoListInfoMessage = findViewById(R.id.tvPhotoListInfoMessage)
            pbWaiting = findViewById(R.id.pbWaiting)
        }
    }

    private fun bindViews() {
        bindPhotoList()
    }

    private fun bindPhotoList() {
        requireView().findViewById<RecyclerView>(R.id.rvPhotoList).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = photoListAdapter
//            recycledViewPool.setMaxRecycledViews(0, 20)
        }

        pbWaiting.visibility = View.VISIBLE

        RetrofitInstance.api.getPhotoList(idTopic, 20).enqueue(
            (object : Callback<ArrayList<Photo>?> {
                override fun onResponse(
                    call: Call<ArrayList<Photo>?>,
                    response: Response<ArrayList<Photo>?>
                ) {
                    pbWaiting.visibility = View.GONE
                    if (response.isSuccessful) {
                        photoListAdapter.updateList(response.body()!!)
                        Log.d(TAG, "PhotoListFragment onResponse:")
                    }
                }

                override fun onFailure(call: Call<ArrayList<Photo>?>, t: Throwable) {
                    Log.d(TAG, "PhotoListFragment onFailure: $t")
                    tvPhotoListInfoMessage.text = getString(R.string.loading_error)
                    pbWaiting.visibility = View.GONE
                }
            })
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo_list, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Navigator) {
            navigator = context
        } else {
            error("Host should implement Navigator interface")
        }
    }

    companion object {
        const val FRAGMENT_IMAGE_LIST_TAG = "FRAGMENT_IMAGE_LIST_TAG"

        @JvmStatic
        fun newInstance(idTopic: String) =
            PhotoListFragment().apply {
                arguments = Bundle().apply { putString(ARG_ID_TOPIC, idTopic) }
            }
    }
}