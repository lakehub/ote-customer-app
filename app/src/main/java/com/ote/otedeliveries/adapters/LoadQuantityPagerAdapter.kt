package com.ote.otedeliveries.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ote.otedeliveries.fragments.LargeLoadFragment
import com.ote.otedeliveries.fragments.MediumLoadFragment
import com.ote.otedeliveries.fragments.SmallLoadFragment
import java.util.*

class LoadQuantityPagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val count = 3
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when(position) {
            0 -> fragment = SmallLoadFragment()
            1 -> fragment = MediumLoadFragment()
            2 -> fragment = LargeLoadFragment()
        }
        return fragment!!
    }

    override fun getCount(): Int {
        return count
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var pageTitle: String? = null
        when(position) {
            0 -> pageTitle = "Small"
            1 -> pageTitle = "Medium"
            2 -> pageTitle = "Large"
        }
        return pageTitle?.toUpperCase(Locale.getDefault())
    }
}