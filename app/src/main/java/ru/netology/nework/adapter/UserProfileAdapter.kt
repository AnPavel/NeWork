package ru.netology.nework.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.ui.EventsFragment
import ru.netology.nework.ui.JobsFragment
import ru.netology.nework.ui.WallFragment

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val itemCount = 3

    override fun getItemCount(): Int {
        return this.itemCount
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("MyAppLog", "UserProfileAdapter * createFragment : $position")
        when (position) {
            0 -> return WallFragment()
            1 -> return JobsFragment()
            2 -> return EventsFragment()
        }
        return Fragment()
    }
}
