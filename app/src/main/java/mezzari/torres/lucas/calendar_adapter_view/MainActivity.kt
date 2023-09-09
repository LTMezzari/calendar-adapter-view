package mezzari.torres.lucas.calendar_adapter_view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import mezzari.torres.lucas.calendar_adapter_view.adapter.CustomMonthAdapter
import mezzari.torres.lucas.calendar_adapter_view.databinding.ActivityMainBinding
import org.joda.time.DateTime

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).let {
            binding = it
            binding.root
        })

        val date = DateTime.now().withDayOfYear(1)
        binding.cvCalendar.adapter = CustomMonthAdapter(
            this,
            date.minusMonths(2),
            date.plusMonths(2)
        )

        binding.cvCalendar.onCalendarPageChanged = { _, header, footer, date ->
            (header as? TextView)?.run {
                text = date.toString("MMMM")
            }
            (footer as? TextView)?.run {
                text = date.toString("yyyy")
            }
        }

        binding.btnNext.setOnClickListener {
            binding.cvCalendar.nextPage()
        }

        binding.btnPrevious.setOnClickListener {
            binding.cvCalendar.previousPage()
        }

        binding.cvCalendar.currentPageDate = date
    }
}