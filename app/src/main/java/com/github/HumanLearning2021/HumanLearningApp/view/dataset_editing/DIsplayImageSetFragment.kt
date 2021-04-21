package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.model.CategorizedPicture
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DisplayImageSetFragment: Fragment() {
    private lateinit var parentActivity: FragmentActivity

    @Inject
    @Demo2Database
    lateinit var dBManagement: DatabaseManagement

    private var categorizedPicturesList = setOf<CategorizedPicture>()
    private lateinit var datasetId: String
    private lateinit var category: Category

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()
        return inflater.inflate(R.layout.fragment_display_image_set, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        category =
            arguments?.getParcelable(ARG_CAT)!!
        datasetId = arguments?.getString(DisplayImageFragment.ARG_DSET_ID)!!

        lifecycleScope.launch {
            categorizedPicturesList = dBManagement.getAllPictures(category)
            if (categorizedPicturesList.isNotEmpty()) {
                parentActivity.findViewById<TextView>(R.id.display_image_set_name).text =
                    (categorizedPicturesList.elementAt(0)).category.name

                val displayImageSetAdapter =
                    DisplayImageSetAdapter(
                        categorizedPicturesList,
                        parentActivity
                    )

                parentActivity.findViewById<GridView>(R.id.display_image_set_imagesGridView).adapter =
                    displayImageSetAdapter
                setPictureItemListener()
            }
        }
    }

    companion object {
        const val ARG_CAT = "category_of_pictures"
        const val ARG_DSET_ID = "dataset_id"

        fun newInstance(dSetId: String, picture: Parcelable): DisplayImageSetFragment {
            val args = Bundle()
            args.putString(ARG_DSET_ID, dSetId)
            args.putParcelable(ARG_CAT, picture)
            val fragment = DisplayImageSetFragment()
            fragment.arguments = args
            return fragment
        }
    }



    class DisplayImageSetAdapter(
        private val images: Set<CategorizedPicture>,
        private val context: Activity
    ) : BaseAdapter() {

        private var layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
            val view =
                view ?: layoutInflater.inflate(R.layout.image_item, viewGroup, false)

            val imageView = view?.findViewById<ImageView>(R.id.image_item_imageView)

            images.elementAt(position).displayOn(context, imageView as ImageView)

            return view
        }

        override fun getItem(position: Int): Any {
            return images.elementAt(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return images.size
        }

    }

    private fun setPictureItemListener(){
        parentActivity.findViewById<GridView>(R.id.display_image_set_imagesGridView).setOnItemClickListener { adapterView, view, i, l ->
            val intent =
                Intent(parentActivity, DisplayImageActivity::class.java)
            intent.putExtra(
                "single_picture",
                (categorizedPicturesList.elementAt(i)) as Parcelable
            )
            intent.putExtra("dataset_id", datasetId)
            startActivity(intent)
        }
    }
}