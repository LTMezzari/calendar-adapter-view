package mezzari.torres.lucas.calendar.adapter.month

import mezzari.torres.lucas.calendar.CalendarAdapterView
import org.joda.time.DateTime
import kotlin.math.ceil

/**
 * @author Lucas T. Mezzari
 * @since 08/11/2022
 */
abstract class MonthAdapter<T : CalendarAdapterView.ViewHolder> :
    CalendarAdapterView.CalendarAdapter<T>() {
    override fun getColumnsCount(date: DateTime): Int {
        return 7 // Monday, Sunday, Tuesday, Wednesday, Thursday, Friday, Saturday
    }

    override fun getRowsCount(date: DateTime): Int {
        val monthSize = date.dayOfMonth().maximumValue
        val monthStart = DateTime(date.year, date.monthOfYear, 1, 0, 0).dayOfWeek().get()
        return ceil((monthSize + (monthStart - 1)) / 7.0).toInt()
    }

    override fun onBindViewHolder(
        row: Int,
        column: Int,
        date: DateTime,
        holder: T
    ) {
        val startDate = DateTime(date.year, date.monthOfYear, 1, 0, 0)
        val monthStart = startDate.dayOfWeek().get()
        var currentDate = startDate.minusDays(monthStart - 1)
        currentDate = currentDate.plusDays(row * 7 + column)
        onBindDayViewHolder(currentDate, date, holder)
    }

    abstract fun onBindDayViewHolder(
        day: DateTime, date: DateTime, holder: T
    )

    override fun getNextPage(current: DateTime): DateTime? {
        return current.withDayOfMonth(1).plusMonths(1)
    }

    override fun getPreviousPage(current: DateTime): DateTime? {
        return current.withDayOfMonth(1).minusMonths(1)
    }
}