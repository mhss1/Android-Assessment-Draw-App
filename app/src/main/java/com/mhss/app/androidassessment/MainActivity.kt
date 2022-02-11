package com.mhss.app.androidassessment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mhss.app.androidassessment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainButtonGroup.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.drawButton -> binding.drawView.setDrawMode(DRAW_MODE_PEN)
                R.id.arrowButton -> binding.drawView.setDrawMode(DRAW_MODE_ARROW)
                R.id.rectangleButton -> binding.drawView.setDrawMode(DRAW_MODE_REC)
                R.id.circleButton -> binding.drawView.setDrawMode(DRAW_MODE_ELL)
                R.id.colorsButton -> binding.drawView.setDrawMode(DRAW_MODE_NON)
            }
            if (checkedId == R.id.colorsButton) showColorsGroup(true) else showColorsGroup(false)
        }
        binding.colorButtonGroup.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.redButton -> setPaintColor(R.color.red)
                R.id.greenButton -> setPaintColor(R.color.green)
                R.id.blueButton -> setPaintColor(R.color.blue)
                R.id.blackButton -> setPaintColor(R.color.black)
            }
        }

    }

    private fun showColorsGroup(show: Boolean) {
        binding.colorGroupContainer.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setPaintColor(color: Int) {
        binding.drawView.setPaintColor(color)
    }
}