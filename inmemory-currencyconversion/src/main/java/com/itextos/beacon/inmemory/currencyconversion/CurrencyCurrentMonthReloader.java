package com.itextos.beacon.inmemory.currencyconversion;

import com.itextos.beacon.inmemory.loader.process.InmemoryInput;

public class CurrencyCurrentMonthReloader
        extends
        CurrencyReloader
{

    // configuration.currency_rates_current_month

    public CurrencyCurrentMonthReloader(
            InmemoryInput aInmemoryInputDetail)
    {
        super(aInmemoryInputDetail);
    }

}