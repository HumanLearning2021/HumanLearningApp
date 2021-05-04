package com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.Demo2Database
import com.github.HumanLearning2021.HumanLearningApp.hilt.ScratchDatabase
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * A fragment displaying a list of datasets.
 */
@AndroidEntryPoint
class DatasetListWidget : Fragment() {

    @Inject
    @Demo2Database
    lateinit var dbMgt: DatabaseManagement

    private val mutableSelectedDataset = SingleLiveData<Dataset>()

    /**
     * LiveData representing the Dataset that has been clicked last
     * Add yourself as observer to get notified when the value changes
     * (use `observe` method of LiveData)
     */
    val selectedDataset: SingleLiveData<Dataset> get() = mutableSelectedDataset

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dataset_list, container, false)

        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = activity?.let { fragActivity ->
                    DatasetListRecyclerViewAdapter(
                        lifecycleScope = lifecycleScope,
                        hostActivity = fragActivity,
                        dbMgt = dbMgt,
                    ) {
                        mutableSelectedDataset.value = it
                    }
                }
            }
        }
        return view
    }
}


/**
 * Class necessary for the backstack to work
 * Code taken from https://stackoverflow.com/questions/59834398/android-navigation-component-back-button-not-working
 */
class SingleLiveData<T> : MutableLiveData<T>() {

    private val pending = AtomicBoolean()

    /**
     * Adds the given observer to the observers list within the lifespan of the given
     * owner. The events are dispatched on the main thread. If LiveData already has data
     * set, it will be delivered to the observer.
     *
     * @param owner The LifecycleOwner which controls the observer
     * @param observer The observer that will receive the events
     * @see MutableLiveData.observe
     */
    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    /**
     * Sets the value. If there are active observers, the value will be dispatched to them.
     *
     * @param value The new value
     * @see MutableLiveData.setValue
     */
    @MainThread
    override fun setValue(value: T?) {
        pending.set(true)
        super.setValue(value)
    }
}
