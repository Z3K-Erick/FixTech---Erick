package com.fixtech

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.fixtech.core.ui.theme.FixTechTheme
import com.fixtech.navigation.AppNavGraph

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FixTechTheme {
                AppNavGraph()
            }
        }
    }
}
