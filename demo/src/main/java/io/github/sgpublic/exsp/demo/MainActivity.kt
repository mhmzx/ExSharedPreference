package io.github.sgpublic.exsp.demo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import io.github.sgpublic.exsp.ExPreference

class MainActivity : AppCompatActivity() {
    private val test: TestPreference = ExPreference.get()

    private lateinit var mstring: EditText
    private lateinit var minteger: EditText
    private lateinit var mlong: EditText
    private lateinit var mfloat: EditText
    private lateinit var mbool: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding()
        onViewSetup()

        findViewById<Button>(R.id.msave).setOnClickListener {
            test.testString = mstring.text.toString()
            test.testInt = minteger.text.toString().toInt()
            test.testFloat = mfloat.text.toString().toFloat()
            test.testLong = mlong.text.toString().toLong()
            test.isTestBool = mbool.isChecked
        }
    }

    private fun binding() {
        mstring = findViewById(R.id.mstring)
        minteger = findViewById(R.id.minteger)
        mlong = findViewById(R.id.mlong)
        mfloat = findViewById(R.id.mfloat)
        mbool = findViewById(R.id.mbool)
    }

    private fun onViewSetup() {
        mstring.setText(test.testString)
        minteger.setText(test.testInt.toString())
        mlong.setText(test.testLong.toString())
        mfloat.setText(test.testFloat.toString())
        mbool.isChecked = test.isTestBool
    }
}