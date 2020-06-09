package com.maiya.leetcode.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maiya.leetcode.R
import com.maiya.leetcode.hit.BugClass
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bt.setOnClickListener{
            val bug = BugClass(this)
        }
    }

}