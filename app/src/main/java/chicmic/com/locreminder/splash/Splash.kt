package chicmic.com.locreminder.splash

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import chicmic.com.locreminder.R
import chicmic.com.locreminder.alarm.Create
import chicmic.com.locreminder.alarm.Dashboard
import chicmic.com.locreminder.utils.CONST

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)

        Handler().postDelayed(

                {

                    var intent=Intent(this,Dashboard::class.java)
                    startActivity(intent)
                    finish()
                }, CONST.SPLASH_TIME.toLong())

    }
}
