package mezzari.torres.lucas.calendar.adapter

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
 * @since 08/11/2022
 */

open class MonthAdapter(private val context: Context) :
    CalendarAdapterView.CalendarAdapter<MonthAdapter.MonthViewHolder>() {
    override fun getColumnsCount(date: DateTime): Int {
        return 7 // Monday, Sunday, Tuesday, Wednesday, Thursday, Friday, Saturday
    }

    override fun getRowsCount(date: DateTime): Int {
        val monthSize = date.dayOfMonth().maximumValue
        val monthStart = DateTime(date.year, date.monthOfYear, 1, 0, 0).dayOfWeek().get()
        return ceil((monthSize + (monthStart - 1)) / 7.0).toInt()
    }

    override fun onCreateViewHolder(viewType: Int, container: ViewGroup?): MonthViewHolder {
        val view = TextView(context)
        view.setPadding(32)
        return MonthViewHolder(view)
    }

    override fun onBindViewHolder(
        row: Int,
        column: Int,
        date: DateTime,
        holder: MonthViewHolder
    ) {
        val startDate = DateTime(date.year, date.monthOfYear, 1, 0, 0)
        val monthStart = startDate.dayOfWeek().get()
        var currentDate = startDate.minusDays(monthStart - 1)
        currentDate = currentDate.plusDays(row * 7 + column)
        onBindDayViewHolder(currentDate, date, holder)
    }

    open fun onBindDayViewHolder(
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

    override fun getNextPage(current: DateTime): DateTime? {
        return current.withDayOfMonth(1).plusMonths(1)
    }

    override fun getPreviousPage(current: DateTime): DateTime? {
        return current.withDayOfMonth(1).minusMonths(1)
    }

    class MonthViewHolder(view: View) : CalendarAdapterView.ViewHolder(view)
}