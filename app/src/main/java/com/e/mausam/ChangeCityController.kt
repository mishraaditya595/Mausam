package com.e.mausam

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class ChangeCityController : AppCompatActivity() {
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_city_layout)
        val editTextField: EditText = findViewById(R.id.queryET)
        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener { // Go back and destroy the ChangeCityController
            finish()
        }

        // Buttons can have a listener for clicks.
        // EditTexts can have listeners for keyboard presses like hitting the enter key.
        editTextField.setOnEditorActionListener { v, actionId, event ->
            val newCity = editTextField.text.toString()
            val newCityIntent =
                Intent(this@ChangeCityController, WeatherController::class.java)

            // Adds what was entered in the EditText as an extra to the intent.
            newCityIntent.putExtra("City", newCity)

            // We started this activity for a result, so now we are setting the result.
            setResult(Activity.RESULT_OK, newCityIntent)

            // This destroys the ChangeCityController.
            finish()
            true
        }
    }
}