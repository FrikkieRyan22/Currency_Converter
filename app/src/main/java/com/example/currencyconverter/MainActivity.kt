package com.example.currencyconverter

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    lateinit var convertFromDropdownTextView: TextView
    lateinit var convertToDropdownTextView: TextView
    lateinit var conversionRateText: TextView
    lateinit var amountToConvert: EditText
    lateinit var convertButton: Button
    lateinit var fromDialog: Dialog
    lateinit var toDialog: Dialog
    lateinit var convertFromValue: String
    lateinit var convertToValue: String
    lateinit var conversionValue: String
    var country: Array<String> = arrayOf("ZAR","EUR","USD")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        convertFromDropdownTextView = findViewById(R.id.convert_from_dropdown_menu)
        convertToDropdownTextView = findViewById(R.id.convert_to_dropdown_menu)
        convertButton = findViewById(R.id.conversionButton)
        conversionRateText = findViewById(R.id.conversionRateText)
        amountToConvert = findViewById(R.id.amountToConvertValueEditText)

        val arrayList = ArrayList<String>()
        for (i in country) {
            arrayList.add(i)
        }

        convertFromDropdownTextView.setOnClickListener {
            fromDialog = Dialog(this@MainActivity)
            fromDialog.setContentView(R.layout.from_spinner)
            fromDialog.window?.setLayout(650, 800)
            fromDialog.show()

            val editText = fromDialog.findViewById<EditText>(R.id.edit_text)
            val listView = fromDialog.findViewById<ListView>(R.id.list_view)

            val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, arrayList)
            listView.adapter = adapter

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    adapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable) {}
            })

            listView.setOnItemClickListener { parent, view, position, id ->
                convertFromDropdownTextView.text = adapter.getItem(position)
                fromDialog.dismiss()
                convertFromValue = adapter.getItem(position).toString()
            }
        }

        convertToDropdownTextView.setOnClickListener {
            toDialog = Dialog(this@MainActivity)
            toDialog.setContentView(R.layout.to_spinner)
            toDialog.window?.setLayout(650, 800)
            toDialog.show()

            val editText = toDialog.findViewById<EditText>(R.id.edit_text)
            val listView = toDialog.findViewById<ListView>(R.id.list_view)

            val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, arrayList)
            listView.adapter = adapter

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    adapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable) {}
            })

            listView.setOnItemClickListener { parent, view, position, id ->
                convertToDropdownTextView.text = adapter.getItem(position)
                toDialog.dismiss()
                convertToValue = adapter.getItem(position).toString()
            }
        }

        convertButton.setOnClickListener {
            try {
                val amount = amountToConvert.text.toString().toDouble()
                getConversionRate(convertFromValue, convertToValue, amount)
            } catch (e: Exception) {
            }
        }
    }

    fun getConversionRate(convertFrom: String, convertTo: String, amountToConvert: Double) {
        val queue: RequestQueue = Volley.newRequestQueue(this)
        val url = "https://api.fastforex.io/convert?from=$convertFrom&to=$convertTo&amount=$amountToConvert&apiKey=ee70501a9f-54bdd57ab2-rxell8"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                var jsonObject: JSONObject? = null
                try {
                    jsonObject = JSONObject(response)
                    val conversionRateValue = round(jsonObject.getDouble("$convertFrom"+"_"+"$convertTo"), 2)
                    conversionValue = "" + round((conversionRateValue * amountToConvert), 2)
                    conversionRateText.text = conversionValue
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error -> })
        queue.add(stringRequest)
    }

    companion object {
        fun round(value: Double, places: Int): Double {
            require(places >= 0)
            var bd = BigDecimal.valueOf(value)
            bd = bd.setScale(places, RoundingMode.HALF_UP)
            return bd.toDouble()
        }
    }
}
