package mezzari.torres.lucas.calendar.adapter.month

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import mezzari.torres.lucas.calendar.CalendarAdapterView
import org.joda.time.DateTime
import kotlin.math.ceil

/**
 * @author Lucas T. Mezzari
 * @since 09/11/22
 */
internal class DefaultMonthAdapter(private val context: Context) :
    MonthAdapter<DefaultMonthAdapter.MonthViewHolder>() {

    override fun onCreateViewHolder(viewType: Int, container: ViewGroup?): MonthViewHolder {
        val view = TextView(context)
        view.setPadding(32)
        return MonthViewHolder(view)
    }

    override fun onBindDayViewHolder(
        day: DateTime, date: DateTime, holder: MonthViewHolder
    ) {
        (holder.view as? TextView)?.apply {
            text = day.toString("dd")
            setTextColor(
                if (date.monthOfYear() == day.monthOfYear()) {
                    ContextCompat.getColorStateList(context, android.R.color.black)
                } else {
                    ContextCompat.getColorStateList(context, android.R.color.darker_gray)
                }
            )
        }
    }

    class MonthViewHolder(view: View) : CalendarAdapterView.ViewHolder(view)
}