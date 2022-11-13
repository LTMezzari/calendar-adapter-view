package mezzari.torres.lucas.calendar.adapter.week

import mezzari.torres.lucas.calendar.CalendarAdapterView
import org.joda.time.DateTime

/**
 * @author Lucas T. Mezzari
 * @since 08/11/2022
 */
abstract class WeekAdapter<T : CalendarAdapterView.ViewHolder> :
    CalendarAdapterView.CalendarAdapter<T>() {

    override fun getColumnsCount(date: DateTime): Int {
        return 7 // Monday, Sunday, Tuesday, Wednesday, Thursday, Friday, Saturday
    }

    override fun getRowsCount(date: DateTime): Int {
        return 1
    }

    override fun onBindViewHolder(
        row: Int,
        column: Int,
        date: DateTime,
        holder: T
    ) {
        var startDate = date.withDayOfWeek(1)
        startDate = startDate.plusDays(1 * column)
        onBindDayViewHolder(startDate, date, holder)
    }

    abstract fun onBindDayViewHolder(
        day: DateTime, date: DateTime, holder: T
    )

    override fun getNextPage(current: DateTime): DateTime? {
        return current.withDayOfWeek(1).plusDays(7)
    }

    override fun getPreviousPage(current: DateTime): DateTime? {
        return current.withDayOfWeek(1).minusDays(7)
    }
}