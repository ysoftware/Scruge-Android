package com.scruge.scruge.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.scruge.scruge.R
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.api.Api

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Service.api.login("yaroslav.erohin@scruge.world", "1234567") { response, error ->
//            response?.let {
                Service.tokenManager.save("123")


                Service.api.getUserId { response, error ->
                    print(response)
                }
//            }
//        }

    }
}
