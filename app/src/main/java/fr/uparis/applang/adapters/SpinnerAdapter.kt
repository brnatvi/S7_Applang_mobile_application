package fr.uparis.applang.adapters

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import fr.uparis.applang.model.Language

class SpinnerAdapter (spinner: Spinner,
                      context: Context,
                      langList: List<Language>) {

    init{
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, langList)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                spinner.setSelection(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
}