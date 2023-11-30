package com.ccc.confiax.beans;

import org.joda.time.LocalDate;
import java.util.List;

public class MenstrualCycleBean {

    private final List<LocalDate> periodDays;
    private final List<LocalDate> fertileDays;
    private final LocalDate ovulationDay;

    private LocalDate earliestDate;
    private LocalDate latestDate;

    public MenstrualCycleBean(List<LocalDate> periodDays, List<LocalDate> fertileDays, LocalDate ovulationDay) {
        this.periodDays = periodDays;
        this.fertileDays = fertileDays;
        this.ovulationDay = ovulationDay;
        setEarliestDate();
        setLatestDate();
    }

    private void setEarliestDate() {
        earliestDate = ovulationDay;
        for (LocalDate periodDay : periodDays) {
            earliestDate = periodDay.isBefore(earliestDate) ? periodDay : earliestDate;
        }

        for (LocalDate fertileDay : fertileDays) {
            earliestDate = fertileDay.isBefore(earliestDate) ? fertileDay : earliestDate;
        }
    }

    private void setLatestDate() {
        latestDate = ovulationDay;
        for (LocalDate periodDay : periodDays) {
            latestDate = periodDay.isAfter(latestDate) ? periodDay : latestDate;
        }

        for (LocalDate fertileDay : fertileDays) {
            latestDate = fertileDay.isAfter(fertileDay) ? fertileDay : latestDate;
        }
    }

    public LocalDate getEarliestDate() {
        return earliestDate;
    }

    public LocalDate getLatestDate() {
        return latestDate;
    }

    public List<LocalDate> getPeriodDays() {
        return periodDays;
    }

    public List<LocalDate> getFertileDays() {
        return fertileDays;
    }

    public LocalDate getOvulationDay() {
        return ovulationDay;
    }
}
