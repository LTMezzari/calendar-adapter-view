package mezzari.torres.lucas.calendar_adapter_view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import mezzari.torres.lucas.calendar_adapter_view.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).let {
            binding = it
            binding.root
        })

        binding.cvCalendar.headerView = TextView(this).apply {
            textAlignment = LinearLayoutCompat.TEXT_ALIGNMENT_CENTER
        }

        binding.cvCalendar.onCalendarPageChanged = { _, header, _, date ->
            (header as? TextView)?.run {
                text = date.toString("MMMM yyyy")
            }
        }

        binding.btnNext.setOnClickListener {
            binding.cvCalendar.nextPage()
        }

        binding.btnPrevious.setOnClickListener {
            binding.cvCalendar.previousPage()
        }
    }
}