package com.owncloud.android.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.owncloud.android.MainApp
import com.owncloud.android.R
import com.owncloud.android.lib.resources.files.SearchRemoteOperation
import com.owncloud.android.ui.activity.FileDisplayActivity
import com.owncloud.android.ui.events.SearchEvent
import com.owncloud.android.utils.ThemeUtils
import kotlinx.android.synthetic.main.fragment_more.*
import kotlinx.android.synthetic.main.fragment_shared.*
import org.parceler.Parcels

class SharedFragment : Fragment() {

    private var pageTitle = "/"

    private var isShowAddButton = false

    private var fileFragment: Fragment? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_shared, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as FileDisplayActivity

        nav_shared_to_me.setOnClickListener {
            MainApp.showOnlyFilesOnDevice(false)
            pageTitle = view.context.getString(R.string.etm_shared_to_mine)
            showFiles(SearchEvent("", SearchRemoteOperation.SearchType.SHARED_FILTER_TO_MINE))
        }

        nav_mine_shared.setOnClickListener {
            MainApp.showOnlyFilesOnDevice(false)
            pageTitle = view.context.getString(R.string.etm_shared_mine)
            showFiles(SearchEvent("", SearchRemoteOperation.SearchType.SHARED_FILTER_MINE))
        }

        nav_shared_link.setOnClickListener {
            MainApp.showOnlyFilesOnDevice(false)
            pageTitle = view.context.getString(R.string.etm_shared_link)
            showFiles(SearchEvent("", SearchRemoteOperation.SearchType.SHARED_FILTER_LINK))
        }


        ThemeUtils.setColoredTitle(
            (activity as FileDisplayActivity?)?.supportActionBar,
            getString(R.string.share), context
        )
        ThemeUtils.colorProgressBar(drawerQuotaProgressBar, ThemeUtils.primaryColor(activity))
    }

    fun getListOfFilesFragment(): OCFileListFragment? {
        if (!isAdded) {
            return null
        }
        val listOfFiles: Fragment? = childFragmentManager.findFragmentByTag(FileDisplayActivity.TAG_LIST_OF_FILES)
        if (listOfFiles != null) {
            return listOfFiles as OCFileListFragment
        }
        return null
    }

    private fun showFiles(searchEvent: SearchEvent?) {
        val bundle = Bundle()
        searchEvent?.apply {
            bundle.putParcelable(OCFileListFragment.SEARCH_EVENT, Parcels.wrap(searchEvent))
        }
        bundle.putBoolean(OCFileListFragment.ARG_HIDE_FAB, true)
        val fragment = OCFileListFragment()
        fragment.arguments = bundle
        childFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, FileDisplayActivity.TAG_LIST_OF_FILES)
            .commit()
        contentView.visibility = View.GONE
        fileFragment = fragment
    }

    fun removeFiles() {
        fileFragment?.apply {
            this@SharedFragment.childFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }
        fileFragment = null
        contentView.visibility = View.VISIBLE
    }

    /**
     *
     * Desc:显示第二标题
     * <p>
     * Author: pengyushan
     * Date: 2020-12-27
     */
    fun needShowSecondTitle(): Boolean {
        return !isRoot() && getListOfFilesFragment() != null && getListOfFilesFragment()!!.currentFile == null
    }

    fun getPageTitle(): String {
        return pageTitle
    }

    fun isRoot(): Boolean {
        return fileFragment == null
    }

    fun setShowAddBtn(flag: Boolean) {
        isShowAddButton = flag
    }

    fun showAddBtn(): Boolean {
        return isShowAddButton
    }
}
