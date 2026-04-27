package com.quicknote.app

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class QuickCaptureActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var db: NoteDatabase
    private lateinit var btnSave: ImageButton
    private lateinit var btnClose: ImageButton
    private lateinit var tvHint: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_capture)

        db = NoteDatabase(this)

        editText = findViewById(R.id.etNote)
        btnSave = findViewById(R.id.btnSave)
        btnClose = findViewById(R.id.btnClose)
        tvHint = findViewById(R.id.tvHint)

        // Save on IME action (Enter / Done)
        editText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                saveAndClose()
                true
            } else {
                false
            }
        }

        btnSave.setOnClickListener { saveAndClose() }
        btnClose.setOnClickListener { finish() }

        // Force keyboard open
        editText.post {
            editText.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun saveAndClose() {
        val text = editText.text.toString().trim()
        if (text.isEmpty()) {
            tvHint.visibility = View.VISIBLE
            return
        }

        db.insertNote(text)

        // Haptic feedback
        editText.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)

        Toast.makeText(this, "✓ Nota salva", Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onBackPressed() {
        val text = editText.text.toString().trim()
        if (text.isNotEmpty()) {
            saveAndClose()
        } else {
            super.onBackPressed()
        }
    }
}
