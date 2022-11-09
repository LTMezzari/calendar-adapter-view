package mezzari.torres.lucas.calendar.adapter

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
 * @since 08/11/2022
 */
open class WeekAdapter(private val context: Context) :
    CalendarAdapterView.CalendarAdapter<WeekAdapter.WeekViewHolder>() {

    override fun getColumnsCount(date: DateTime): Int {
        return 7 // Monday, Sunday, Tuesday, Wednesday, Thursday, Friday, Saturday
    }

    override fun getRowsCount(date: DateTime): Int {
        return 1
    }

    override fun onCreateViewHolder(viewType: Int, container: ViewGroup?): WeekViewHolder {
        val view = TextView(context)
        view.setPadding(32)
        return WeekViewHolder(view)
    }

    override fun onBindViewHolder(
        row: Int,
        column: Int,
        date: DateTime,
        holder: WeekViewHolder
    ) {
        var startDate = date.withDayOfWeek(1)
        startDate = startDate.plusDays(1 * column)
        onBindDayViewHolder(startDate, date, holder)
    }

    open fun onBindDayViewHolder(
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

    override fun getNextPage(current: DateTime): DateTime? {
        return current.withDayOfWeek(1).plusDays(7)
    }

    override fun getPreviousPage(current: DateTime): DateTime? {
        return current.withDayOfWeek(1).minusDays(7)
    }

    class WeekViewHolder(view: View) : CalendarAdapterView.ViewHolder(view)
}