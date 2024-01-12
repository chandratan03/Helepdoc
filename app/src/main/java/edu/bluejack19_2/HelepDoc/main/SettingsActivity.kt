package edu.bluejack19_2.HelepDoc.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import edu.bluejack19_2.HelepDoc.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener{

    var themeStr = ""
    var fontSizeStr = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        var theme =  findViewById<Spinner>(R.id.theme_spinner)
        var font =  findViewById<Spinner>(R.id.font_size_spinner)

        ArrayAdapter.createFromResource(this, R.array.theme, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            theme.adapter = adapter
        }

        ArrayAdapter.createFromResource(this, R.array.font_size, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            font.adapter = adapter
        }

        save_changes.setOnClickListener {

        }

        theme.onItemSelectedListener = this
        font_size_spinner.onItemSelectedListener = this
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var temp = p0!!.getItemAtPosition(p2).toString()
        if(temp.equals("Green") || temp.equals("Red")) {
            themeStr = temp
        }
        else {
            fontSizeStr = temp
        }
    }
}
