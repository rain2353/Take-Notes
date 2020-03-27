package com.example.takenotes.Main

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.takenotes.Memo.MemoFragment
import com.example.takenotes.Picture.PictureFragment
import com.example.takenotes.R
import com.example.takenotes.Recording.RecordingFragment
import com.example.takenotes.Video.VideoFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

/*    2020년 3월 25일부터 개발 공부용 프로젝트 시작.
      Take Notes 는 필기를 하다 라는 뜻으로 수업, 강의 혹은 자신의 생각을 적는 메모장 어플입니다.
      사진, 동영상, 음성 녹음, 메모를 사용자가 사용할수 있게 만들 것입니다.*/

    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener {

        var selectedFragment: Fragment? = null

        when(it.itemId) {
            R.id.navigation_memo -> selectedFragment = MemoFragment()          // 사용자가 작성한 메모를 볼 수 있는 프래그먼트
            R.id.navigation_picture -> selectedFragment = PictureFragment()   // 사용자가 업로드한 이미지들을 볼 수 있는 프래그먼트
            R.id.navigation_video -> selectedFragment = VideoFragment()         // 사용자가 업로드한 동영상을 볼 수 있는 프래그먼트
            R.id.navigation_recording -> selectedFragment = RecordingFragment() // 사용자가 업로드한 음성녹음을 들을 수 있는 프래그먼트
        }

        selectedFragment?.let { it1 ->
            supportFragmentManager.beginTransaction().replace(
                R.id.container,
                it1
            ).commit()
        }

        true

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val num = intent.getIntExtra("Fragment",0)

        // 툴바 타이틀 변경하기.
        toolbar.title = "Take Notes"
        toolbar.setTitleTextColor(Color.WHITE)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnNavigationItemSelectedListener(navListener)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.container,
                MemoFragment()
            ).commit()
        }

        if(num == 2) {
            supportFragmentManager.beginTransaction().replace(
                R.id.container,
                PictureFragment()
            ).commit()
        }
    }
}
