package com.example.takenotes.PictureViewPager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.takenotes.PictureViewPager.Adapter.ViewPagerAdapter
import com.example.takenotes.R
import kotlinx.android.synthetic.main.activity_picture_view_pager.*

class PictureViewPagerActivity : AppCompatActivity() {
    var num : Int? = null
    private val adapter by lazy { ViewPagerAdapter(supportFragmentManager, num) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_view_pager)
        num = intent.getIntExtra("PagerNum",0)  // 뷰페이저에서 생성 할 Fragment 의 개수
        PictureViewPager.adapter = adapter
    }
}



