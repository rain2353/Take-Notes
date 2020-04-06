package com.example.takenotes.PictureViewPager.Adapter

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.takenotes.PictureViewPager.Fragment.*

class ViewPagerAdapter(fm: FragmentManager, var num: Int?) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {

        return when (position) {

            0 -> VIewPagerFragment()

            1 -> ViewPagerFragment1()

            2 -> ViewPagerFragment2()

            3 -> ViewPagerFragment3()

            4 -> ViewPagerFragment4()

            5 -> ViewPagerFragment5()

            6 -> ViewPagerFragment6()

            7 -> ViewPagerFragment7()

            8 -> ViewPagerFragment8()

            9 -> ViewPagerFragment9()

            else -> null!!
        }

    }

    // 생성 할 Fragment 의 개수
    override fun getCount(): Int =num!!

}