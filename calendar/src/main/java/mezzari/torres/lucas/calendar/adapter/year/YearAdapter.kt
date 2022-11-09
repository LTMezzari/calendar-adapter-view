package mezzari.torres.lucas.calendar.adapter.year

import mezzari.torres.lucas.calendar.CalendarAdapterView
import org.joda.time.DateTime

/**
 * @author Lucas T. Mezzari
 * @since 08/11/2022
 */
abstract class YearAdapter<T : CalendarAdapterView.ViewHolder> :
    CalendarAdapterView.CalendarAdapter<T>() {
    override fun getColumnsCount(date: DateTime): Int {
        /*
         * Jan, Feb, Mar
         * Apr, May, Jun
         * Jul, Aug, Sep
         * Oct, Nov, Dec
         */
        return 3
    }

    override fun getRowsCount(date: DateTime): Int {
        /*
         * Jan, Feb, Mar
         * Apr, May, Jun
         * Jul, Aug, Sep
         * Oct, Nov, Dec
         */
        return 4
    }

    override fun onBindViewHolder(
        row: Int,
        column: Int,
        date: DateTime,
        holder: T
    ) {
        var startDate = DateTime(date.year, 1, 1, 0, 0)
        startDate = startDate.plusMonths(row * 3 + column)
        onBindMonthViewHolder(startDate, date, holder)
    }

    abstract fun onBindMonthViewHolder(
        month: DateTime, date: DateTime, holder: T
    )

    override fun getNextPage(current: DateTime): DateTime? {
        return current.withDayOfMonth(1).withMonthOfYear(1).plusYears(1)
    }

    override fun getPreviousPage(current: DateTime): DateTime? {
        return current.withDayOfMonth(1).withMonthOfYear(1).minusYears(1)
    }
}