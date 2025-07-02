package com.hootsuite.nachos.sample

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hootsuite.nachos.ChipConfiguration
import com.hootsuite.nachos.NachoTextView
import com.hootsuite.nachos.chip.Chip
import com.hootsuite.nachos.chip.ChipSpan
import com.hootsuite.nachos.chip.ChipSpanChipCreator
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.hootsuite.nachos.tokenizer.SpanChipTokenizer
import com.hootsuite.nachos.validator.ChipifyingNachoValidator
import com.hootsuite.nachos.validator.IllegalCharacterIdentifier

class MainActivity : AppCompatActivity() {

    private lateinit var infoBodyView: TextView
    private lateinit var nachoTextView: NachoTextView
    private lateinit var nachoTextViewWithIcons: NachoTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        infoBodyView = findViewById(R.id.info_body)
        nachoTextView = findViewById(R.id.nacho_text_view)
        nachoTextViewWithIcons = findViewById(R.id.nacho_text_view_with_icons)

        // Set up click listeners
        findViewById<View>(R.id.list_chip_values).setOnClickListener { listChipValues(it) }
        findViewById<View>(R.id.list_chip_and_token_values).setOnClickListener { listChipAndTokenValues(it) }
        findViewById<View>(R.id.to_string).setOnClickListener { toastToString(it) }

        val infoText: Spanned = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(getString(R.string.info_text_body), Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(getString(R.string.info_text_body))
        }
        infoBodyView.text = infoText

        setupChipTextView(nachoTextView)
        setupChipTextView(nachoTextViewWithIcons)

        val testList = mutableListOf("testing", "setText")
        nachoTextView.setText(testList)

        nachoTextViewWithIcons.chipTokenizer =
            SpanChipTokenizer(
                this,
                object : ChipSpanChipCreator() {
                    override fun createChip(context: Context, text: CharSequence, data: Any?): ChipSpan {
                        return ChipSpan(
                            context,
                            text,
                            ContextCompat.getDrawable(this@MainActivity, R.mipmap.ic_launcher),
                            data
                        )
                    }

                    override fun configureChip(chip: ChipSpan, chipConfiguration: ChipConfiguration) {
                        super.configureChip(chip, chipConfiguration)
                    }
                },
                ChipSpan::class.java
            )
    }

    private fun setupChipTextView(nachoTextView: NachoTextView) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, SUGGESTIONS)
        nachoTextView.setAdapter(adapter)
        nachoTextView.setIllegalCharacterIdentifier(object : IllegalCharacterIdentifier {
            override fun isCharacterIllegal(c: Char): Boolean {
                return !c.lowercaseChar().toString().matches("[a-z0-9 ]".toRegex())
            }
        })
        nachoTextView.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
        nachoTextView.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR)
        nachoTextView.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
        nachoTextView.setNachoValidator(ChipifyingNachoValidator())
        nachoTextView.enableEditChipOnTouch(true, true)
        nachoTextView.setOnChipClickListener(object : NachoTextView.OnChipClickListener {
            override fun onChipClick(chip: Chip?, event: MotionEvent?) {
                Log.d(TAG, "onChipClick: ${'$'}{chip?.text}")
            }
        })
        nachoTextView.setOnChipRemoveListener(object : NachoTextView.OnChipRemoveListener {
            override fun onChipRemove(chip: Chip?) {
                Log.d(TAG, "onChipRemoved: ${'$'}{chip?.text}")
                nachoTextView.setSelection(nachoTextView.text?.length ?: 0)
            }
        })
    }

    fun listChipValues(view: View) {
        val chipValues = nachoTextView.chipValues
        alertStringList("Chip Values", chipValues)
    }

    fun listChipAndTokenValues(view: View) {
        val chipAndTokenValues = nachoTextView.chipAndTokenValues
        alertStringList("Chip and Token Values", chipAndTokenValues)
    }

    fun toastToString(view: View) {
        alertStringList("toString()", listOf(nachoTextView.toString()))
    }

    private fun alertStringList(title: String, list: List<String>) {
        val alertBody = if (list.isNotEmpty()) list.joinToString("\n") else "No strings"

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(alertBody)
            .setCancelable(true)
            .setNegativeButton("Close", null)
            .create()
            .show()
    }

    companion object {
        private const val TAG = "Nachos"
        private val SUGGESTIONS = arrayOf(
            "Nachos",
            "Chip",
            "Tortilla Chips",
            "Melted Cheese",
            "Salsa",
            "Guacamole",
            "Cheddar",
            "Mozzarella",
            "Mexico",
            "Jalapeno"
        )
    }
} 