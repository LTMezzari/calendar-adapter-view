package mezzari.torres.lucas.calendar.adapter.week

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import mezzari.torres.lucas.calendar.CalendarAdapterView
import org.joda.time.DateTime

/**
 * @author Lucas T. Mezzari
 * @since 09/11/22
 */
internal class DefaultWeekAdapter(private val context: Context) :
    WeekAdapter<DefaultWeekAdapter.WeekViewHolder>() {

    override fun onCreateViewHolder(viewType: Int, container: ViewGroup?): WeekViewHolder {
        val view = TextView(context)
        view.setPadding(32)
        return WeekViewHolder(view)
    }

    override fun onBindDayViewHolder(
        day: DateTime, date: DateTime, holder: WeekViewHolder
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

    class WeekViewHolder(view: View) : CalendarAdapterView.ViewHolder(view)
}