package com.github.HumanLearning2021.HumanLearningApp.view.dataset_list_fragment

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.github.HumanLearning2021.HumanLearningApp.R
import com.github.HumanLearning2021.HumanLearningApp.hilt.GlobalDatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.DatabaseManagement
import com.github.HumanLearning2021.HumanLearningApp.model.Dataset
import com.github.HumanLearning2021.HumanLearningApp.model.UniqueDatabaseManagement
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * A fragment displaying a list of datasets.
 */
@AndroidEntryPoint
class DatasetListWidget : Fragment() {

    @Inject
    @GlobalDatabaseManagement
    lateinit var globalDatabaseManagement: UniqueDatabaseManagement

    lateinit var dbMgt: DatabaseManagement

    private val mutableSelectedDataset = SingleLiveData<Dataset>()
    private lateinit var adapter: DatasetListRecyclerViewAdapter

    /**
     * LiveData representing the Dataset that has been clicked last
     * Add yourself as observer to get notified when the value changes
     * (use `observe` method of LiveData)
     */
    val selectedDataset: SingleLiveData<Dataset> get() = mutableSelectedDataset

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            dbMgt = globalDatabaseManagement.accessDatabase(
                getString(
                    R.string.production_database_name
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        runBlocking {
            dbMgt = globalDatabaseManagement.accessDatabase(
                getString(
                    R.string.production_database_name
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =
            inflater.inflate(R.layout.fragment_dataset_list, container, false) as RecyclerView
        adapter = DatasetListRecyclerViewAdapter(
            lifecycleScope = lifecycleScope,
            hostActivity = requireActivity(),
            dbMgt = dbMgt,
        ) {
            mutableSelectedDataset.value = it
        }
        view.adapter = adapter
        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                adapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
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
