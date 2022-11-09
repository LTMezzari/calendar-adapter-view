package mezzari.torres.lucas.calendar_adapter_view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mezzari.torres.lucas.calendar_adapter_view.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).let {
            binding = it
            binding.root
        })

        binding.btnNext.setOnClickListener {
            binding.cvCalendar.nextPage()
        }

        binding.btnPrevious.setOnClickListener {
            binding.cvCalendar.previousPage()
        }
    }
}