package com.example.takenotes.PictureViewPager.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.takenotes.Common.Common
import com.example.takenotes.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.view_pager_fragment.*

class ViewPagerFragment8: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_pager_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //뷰 설정
        Picasso.get().load("http://10.0.2.2:3000/"+ Common.SelectPicture?.file8).into(PictureView)

    }
}