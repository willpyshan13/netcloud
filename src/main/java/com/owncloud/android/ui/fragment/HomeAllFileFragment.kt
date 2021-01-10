package com.owncloud.android.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nextcloud.client.device.DeviceInfo
import com.owncloud.android.R
import com.owncloud.android.datamodel.OCFile
import com.owncloud.android.ui.activity.FileDisplayActivity
import com.owncloud.android.ui.events.SearchEvent
import kotlinx.android.synthetic.main.fragment_all_file_home.*
import org.parceler.Parcels

class HomeAllFileFragment : Fragment() {

    private var pageTitle = "/"

    private var isShowAddButton = false

    private var fileFragment: OCFileListFragment? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_file_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as FileDisplayActivity

        nav_mine_zone.setOnClickListener {
            pageTitle = view.context.getString(R.string.etm_mine_zone)
            showFiles(null, nav_mine_zone_text.text.toString(), OCFileListFragment.FOLDER_TYPE_MINE_ZONE)
        }

        nav_group_zone.setOnClickListener {
            pageTitle = view.context.getString(R.string.etm_group_zone)
            showFiles(null, nav_group_zone_text.text.toString(), OCFileListFragment.FOLDER_TYPE_GROUP)
        }

        nav_shared_zone.setOnClickListener {
            pageTitle = view.context.getString(R.string.etm_shared_zone)
            showFiles(null, nav_shared_zone_text.text.toString(), OCFileListFragment.FOLDER_TYPE_PUBLIC)
        }
        (activity as? FileDisplayActivity)?.setupToolbar()
        (activity as? FileDisplayActivity)?.updateActionBarTitleAndHomeButtonByString(activity.getString(R.string.file_icon))
    }

    private fun showFiles(searchEvent: SearchEvent?, title: String, folerType: Int) {
        val bundle = Bundle()
        searchEvent?.apply {
            bundle.putParcelable(OCFileListFragment.SEARCH_EVENT, Parcels.wrap(searchEvent))
        }
        bundle.putString(OCFileListFragment.SEARCH_EVENT_TITLE, title)
        bundle.putInt(OCFileListFragment.FOLDER_TYPE, folerType)
        bundle.putBoolean(OCFileListFragment.REGISTER_SYNC_EVENT, true)

        val fragment = OCFileListFragment()
        fragment.arguments = bundle
        childFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, FileDisplayActivity.TAG_LIST_OF_FILES)
            .commit()
        contentView.visibility = View.GONE
        fileFragment = fragment
        (activity as? FileDisplayActivity)?.setupToolbar()
    }

    fun getCurrentFile(): OCFile? {
        return if (fileFragment == null) {
            null
        } else {
            fileFragment!!.currentFile
        }
    }

    fun getDevicesInfo(): DeviceInfo? {
        return if (fileFragment == null) {
            null
        } else {
            fileFragment!!.deviceInfo
        }
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

    fun removeFiles() {
        fileFragment?.apply {
            this@HomeAllFileFragment.childFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }
        fileFragment = null
        contentView.visibility = View.VISIBLE
        pageTitle = "/"
        (activity as? FileDisplayActivity)?.setupToolbar()
    }

    /**
     *
     * Desc:显示第二标题
     * <p>
     * Author: pengyushan
     * Date: 2020-12-27
     */
    fun needShowSecondTitle():Boolean{
        return !isRoot() && getCurrentFile()!=null && getCurrentFile()!!.remotePath == "/"
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
