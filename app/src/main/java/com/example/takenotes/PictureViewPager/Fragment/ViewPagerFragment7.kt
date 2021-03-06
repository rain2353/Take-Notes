package com.example.takenotes.PictureViewPager.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.takenotes.Api.ipaddress
import com.example.takenotes.Common.Common
import com.example.takenotes.R
import kotlinx.android.synthetic.main.view_pager_fragment.*

class ViewPagerFragment7: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_pager_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
 
        //뷰 설정
        Glide.with(view!!.context).load(ipaddress.ip+Common.SelectPicture?.file7).into(PictureView)
    }
}
