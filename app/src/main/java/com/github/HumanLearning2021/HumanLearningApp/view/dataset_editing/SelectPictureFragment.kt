package com.github.HumanLearning2021.HumanLearningApp.view.dataset_editing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.databinding.FragmentSelectPictureBinding
import com.github.HumanLearning2021.HumanLearningApp.model.Category
import com.github.HumanLearning2021.HumanLearningApp.model.Id


class SelectPictureFragment : Fragment() {
    private lateinit var parentActivity: FragmentActivity
    private var selectedPicture: Uri? = null
    private var selectedCategory: Category? = null
    private var categories = setOf<Category>()
    private lateinit var datasetId: Id // ugly hack, but necessary to navigate back to display dataset fragment. Popping backstack doesnt seem to work

    private var _binding: FragmentSelectPictureBinding? = null
    private val binding get() = _binding!!

    private val args: SelectPictureFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentActivity = requireActivity()



        _binding = FragmentSelectPictureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        datasetId = args.datasetId
        categories = categories.plus(args.categories.toList())

        binding.choosePictureButton.setOnClickListener {
            launchOpenPicture()
        }

        binding.selectCategoryButton2.setOnClickListener {
            onSelectCategoryButton()
        }

        binding.saveButton3.setOnClickListener {
            setFragmentResult(
                AddPictureFragment.REQUEST_KEY,
                bundleOf("chosenCategory" to selectedCategory!!, "pictureUri" to selectedPicture!!)
            )
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

    private fun launchOpenPicture() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        /** This method is deprecated. However, the [official documentation][1] recommends using it
         * for this purpose as of 2021-05-12. We do not currently know how to avoid this.
         *
         * [1]:
         * https://developer.android.com/training/data-storage/shared/documents-files#open-file
         */
        @Suppress("DEPRECATION")  // FIXME: use something non-deprecated
        startActivityForResult(intent, RC_OPEN_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_OPEN_PICTURE -> {
                data?.data?.also {
                    selectedPicture = it
                    displayPicture(it)
                    notifySaveButton()
                }
            }
        }
    }

    val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback.isEnabled = false
        callback.remove()
        _binding = null

    }

    private fun displayPicture(pic: Uri) {
        Glide.with(this).load(pic).into(binding.selectedPicturePreview)
    }

    private fun onSelectCategoryButton() {
        val builder = AlertDialog.Builder(parentActivity)
        builder.apply {
            setTitle(getString(R.string.AddPicture_categorySelectionDialogTitle))
            setItems(categories.map { cat -> cat.name }.toTypedArray()) { _, category_index ->
                val button = binding.selectCategoryButton2
                categories.elementAt(category_index).let {
                    button.text = it.name
                    selectedCategory = it
                    button.apply {
                        setBackgroundColor(getColor(parentActivity, R.color.button_set))
                        button.setTextColor(getColor(parentActivity, R.color.black))
                    }
                    notifySaveButton()
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
    }


    private fun notifySaveButton() {
        binding.saveButton3.isEnabled =
            selectedCategory != null && selectedPicture != null
    }

    companion object {
        val RC_OPEN_PICTURE = "open picture".hashCode()
    }
}
