package io.github.sgpublic.xxpref.demo.kt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.sgpublic.xxpref.demo.databinding.ActivityMainBinding

class KtActivity : AppCompatActivity() {
    private val ViewBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ViewBinding.root)
        onViewSetup()

        ViewBinding.msave.setOnClickListener {
//            KtPreference.testString = ViewBinding.mstring.text.toString()
//            KtPreference.testInt = ViewBinding.minteger.text.toString().toInt()
//            KtPreference.testFloat = ViewBinding.mfloat.text.toString().toFloat()
//            KtPreference.testLong = ViewBinding.mlong.text.toString().toLong()
//            KtPreference.testBool = ViewBinding.mbool.isChecked
//            KtPreference.testDate = SimpleDateFormat("yyyy.MM.dd", Locale.CHINA).parse(ViewBinding.mdate.text.toString())
//            when {
//                ViewBinding.mtypea.isChecked -> KtPreference.testEnum = KtPreference.Type.TYPE_A
//                ViewBinding.mtypeb.isChecked -> KtPreference.testEnum = KtPreference.Type.TYPE_B
//            }
        }
    }

    private fun onViewSetup() {
//        ViewBinding.mstring.setText(KtPreference.testString)
//        ViewBinding.minteger.setText(KtPreference.testInt.toString())
//        ViewBinding.mlong.setText(KtPreference.testLong.toString())
//        ViewBinding.mfloat.setText(KtPreference.testFloat.toString())
//        ViewBinding.mbool.isChecked = teKtPreferencest.isTestBool
//        ViewBinding.mdate.setText(SimpleDateFormat("yyyy.MM.dd", Locale.CHINA).format(KtPreference.testDate))
//        when (KtPreference.testEnum) {
//            KtPreference.Type.TYPE_A -> ViewBinding.mtypea.isChecked = true
//            KtPreference.Type.TYPE_B -> ViewBinding.mtypeb.isChecked = true
//        }
    }
}