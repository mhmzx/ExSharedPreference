package io.github.sgpublic.exsp.demo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import io.github.sgpublic.exsp.ExPreference
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {
    private lateinit var mstring: EditText
    private lateinit var minteger: EditText
    private lateinit var mlong: EditText
    private lateinit var mdate: EditText
    private lateinit var mfloat: EditText
    private lateinit var mbool: Switch
    private lateinit var mtypea: RadioButton
    private lateinit var mtypeb: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding()
        onViewSetup()

        findViewById<Button>(R.id.msave).setOnClickListener {
            TestPreference.testString = mstring.text.toString()
            TestPreference.testInt = minteger.text.toString().toInt()
            TestPreference.testFloat = mfloat.text.toString().toFloat()
            TestPreference.testLong = mlong.text.toString().toLong()
            TestPreference.isTestBool = mbool.isChecked
            TestPreference.testDate = SimpleDateFormat("yyyy.MM.dd").parse(mdate.text.toString())
            when {
                mtypea.isChecked -> TestPreference.testEnum = TestPreference.Type.TYPE_A
                mtypeb.isChecked -> TestPreference.testEnum = TestPreference.Type.TYPE_B
            }
        }
    }

    private fun binding() {
        mstring = findViewById(R.id.mstring)
        minteger = findViewById(R.id.minteger)
        mlong = findViewById(R.id.mlong)
        mfloat = findViewById(R.id.mfloat)
        mbool = findViewById(R.id.mbool)
        mdate = findViewById(R.id.mdate)
        mtypea = findViewById(R.id.mtypea)
        mtypeb = findViewById(R.id.mtypeb)
    }

    private fun onViewSetup() {
        mstring.setText(test.testString)
        minteger.setText(test.testInt.toString())
        mlong.setText(test.testLong.toString())
        mfloat.setText(test.testFloat.toString())
        mbool.isChecked = test.isTestBool
        mdate.setText(SimpleDateFormat("yyyy.MM.dd").format(test.testDate))
        when (test.testEnum) {
            TestPreference.Type.TYPE_A -> mtypea.isChecked = true
            TestPreference.Type.TYPE_B -> mtypeb.isChecked = true
        }
    }
}