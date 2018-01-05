package treehou.se.habit.ui.control

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup

import javax.inject.Inject

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import io.realm.Realm
import treehou.se.habit.HabitApplication
import treehou.se.habit.R
import treehou.se.habit.core.db.model.controller.ControllerDB
import treehou.se.habit.ui.BaseFragment
import treehou.se.habit.ui.adapter.ControllerAdapter

/**
 * Fragment listing all app controllers.
 */
class ControllsFragment : BaseFragment() {

    private var mAdapter: ControllerAdapter? = null

    @BindView(R.id.list) lateinit var mListView: RecyclerView
    @BindView(R.id.empty) lateinit var viwEmpty: View
    @BindView(R.id.fab_add) lateinit var fabAdd: FloatingActionButton

    private var unbinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (context!!.applicationContext as HabitApplication).component().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_control_universal, container, false)
        unbinder = ButterKnife.bind(this, view)

        viwEmpty.setOnClickListener { createNewController() }
        fabAdd.setOnClickListener { createNewController() }
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setTitle(R.string.controllers)

        mAdapter = ControllerAdapter(context!!)
        mAdapter!!.setItemListener(object : ControllerAdapter.ItemListener {
            override fun itemCountUpdated(itemCount: Int) {
                updateEmptyView(itemCount)
            }

            override fun itemClickListener(controllerHolder: ControllerAdapter.ControllerHolder) {
                val controller = mAdapter!!.getItem(controllerHolder.adapterPosition)
                activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.page_container, ControlFragment.newInstance(controller.id))
                        .addToBackStack(null)
                        .commit()
            }

            override fun itemLongClickListener(controllerHolder: ControllerAdapter.ControllerHolder): Boolean {
                val controller = mAdapter!!.getItem(controllerHolder.adapterPosition)
                AlertDialog.Builder(activity)
                        .setItems(R.array.controll_manager) { _, which ->
                            when (which) {
                                0 -> loadController(controller.id)
                                1 -> {
                                    mAdapter!!.removeItem(controllerHolder.adapterPosition)

                                    val id = controller.id
                                    realm.executeTransaction { realm -> realm.where(ControllerDB::class.java).equalTo("id", id).findAll().deleteAllFromRealm() }
                                }
                            }
                        }.create().show()
                return true
            }
        })
        val controllers = realm.where(ControllerDB::class.java).findAll()
        mAdapter!!.addAll(controllers)

        // Set the adapter
        val gridLayoutManager = GridLayoutManager(activity, 1)
        mListView.layoutManager = gridLayoutManager
        mListView.itemAnimator = DefaultItemAnimator()
        mListView.adapter = mAdapter

        updateEmptyView(controllers.size)

        setHasOptionsMenu(true)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    /**
     * Show empty view if no controllers exist
     */
    private fun updateEmptyView(itemCount: Int) {
        viwEmpty.visibility = if (itemCount <= 0) View.VISIBLE else View.GONE
    }

    fun loadController(id: Long) {
        activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.page_container, EditControlFragment.newInstance(id))
                .addToBackStack(null)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.controllers, menu)
    }

    /**
     * Creates a new empty controller.
     */
    private fun createNewController() {
        val controller = ControllerDB()
        val name = "Controller"
        controller.name = name
        ControllerDB.save(realm, controller)
        controller.addRow(realm)

        loadController(controller.id)
    }

    companion object {

        private val TAG = "ControllsFragment"

        fun newInstance(): ControllsFragment {
            val fragment = ControllsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
