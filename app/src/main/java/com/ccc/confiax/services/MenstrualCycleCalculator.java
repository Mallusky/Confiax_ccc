package com.ccc.confiax.services;

import android.util.Log;

import com.ccc.confiax.beans.MenstrualCycleBean;
import com.ccc.confiax.managers.MenstrualCycleManager;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class MenstrualCycleCalculator {
    private static final String TAG = "MenstrualCycleCalculator";

    private final MenstrualCycleManager manager;

    public MenstrualCycleCalculator(MenstrualCycleManager manager) {
        this.manager = manager;
    }

    public void calculate() {
        List<MenstrualCycleBean> allBeans = manager.getAllMenstrualCycleBeans();

        if (allBeans.size() == 1) {
            throw new IllegalStateException("Al menos debe haber dos periodos");
        }

        final LocalDate lastPeriodDate = actualLastPeriodDate();
        allBeans = Lists.newArrayList(Iterables.filter(allBeans, new Predicate<MenstrualCycleBean>() {
            @Override
            public boolean apply(MenstrualCycleBean mcBean) {
                return !mcBean.getEarliestDate().isAfter(lastPeriodDate) &&
                        !mcBean.getLatestDate().isAfter(lastPeriodDate);
            }
        }));


        allBeans.addAll(computeAllPeriodDaysSinceLastPeriod(lastPeriodDate));
        MenstrualCycleBean nextPeriod = allBeans.get(allBeans.size() - 1);
        LocalDate now = new LocalDate();

        if (!now.isBefore(nextPeriod.getEarliestDate())) { // new period start
            LocalDate currentPeriodStart = nextPeriod.getPeriodDays().get(0);
            manager.updateLastPeriodDate(currentPeriodStart);
            allBeans.addAll(computeAllPeriodDaysSinceLastPeriod(currentPeriodStart));
        } else {
            MenstrualCycleBean actualPeriod = allBeans.get(allBeans.size() - 2);
            manager.updateLastPeriodDate(actualPeriod.getPeriodDays().get(0));
        }

        manager.updateAllPeriodBeans(allBeans);
    }

    private LocalDate actualLastPeriodDate() {
        Optional<LocalDate> lastPeriodDateOpt = manager.getLastPeriodDate();
        if (!lastPeriodDateOpt.isPresent()) {
            Log.e(TAG, "No hay constancia de la fecha del último periodo para hacer el recálculo");
            throw new IllegalStateException("Falta la fecha del último periodo");
        }
        return lastPeriodDateOpt.get();
    }

    private List<MenstrualCycleBean> computeAllPeriodDaysSinceLastPeriod(LocalDate lastPeriodDate) {
        List<MenstrualCycleBean> list = new ArrayList<>();
        LocalDate now = new LocalDate();
        LocalDate lastPeriod = new LocalDate(lastPeriodDate);
        int periodLength = manager.getPeriodLength();
        MenstrualCycleBean bean;

        do  {
            bean = computePeriodBean(lastPeriod);
            list.add(bean);
            lastPeriod = lastPeriod.plusDays(periodLength);
        } while (!bean.getEarliestDate().isAfter(now));

        return list;
    }

    private MenstrualCycleBean computePeriodBean(LocalDate periodStart) {
        List<LocalDate> periodDays = getPeriod(periodStart);
        List<LocalDate> fertileDays = getFertile(periodStart);
        LocalDate ovulationDay = getOvulation(fertileDays);

        return new MenstrualCycleBean(periodDays, fertileDays, ovulationDay);
    }

    private List<LocalDate> getPeriod(LocalDate periodStart) {
        List<LocalDate> period = new ArrayList<>();
        LocalDate periodDay = new LocalDate(periodStart);
        period.add(periodDay);

        for (int i = 0; i < manager.getMenstruationLength() - 1; ++i) {
            periodDay = periodDay.plusDays(1);
            period.add(periodDay);
        }

        return period;
    }

    private List<LocalDate> getFertile(LocalDate periodStart) {
        int diff = manager.getPeriodLength() - 20;
        LocalDate fertileDay = periodStart.plusDays(diff);
        List<LocalDate> fertile = new ArrayList<>();
        fertile.add(fertileDay);

        for (int i = 0; i < 6; ++i) {
            fertileDay = fertileDay.plusDays(1);
            fertile.add(fertileDay);
        }

        return fertile;
    }

    private LocalDate getOvulation(List<LocalDate> fertileDays) {
        int size = fertileDays.size();
        if (size < 2) {
            return fertileDays.get(0);
        } else {
            return fertileDays.get(size - 2);
        }
    }
}

