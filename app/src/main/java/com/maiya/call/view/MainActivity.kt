package com.maiya.call.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maiya.call.R
import com.maiya.call.hit.BugClass
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