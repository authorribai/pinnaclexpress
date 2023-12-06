package com.itextos.beacon.httpclienthandover.validator;

import java.util.HashMap;
import java.util.Map;

import com.itextos.beacon.httpclienthandover.data.ParamDataType;

public class DataValidatorImpl
{

    private static final DataValidatorImpl instance = new DataValidatorImpl();

    public static DataValidatorImpl getInstance()
    {
        return instance;
    }

    private final Map<String, IDataValidator> dataValidatorMap = new HashMap<>();
    public static final String                EMPTY_STRING     = "[EMPTY]";
    public static final String                NULL             = "[NULL]";
    public static final String                OPEN_PARAN       = "{";
    public static final String                END_PARAN        = "}";

    private DataValidatorImpl()
    {}

    public boolean validate(
            ParamDataType aDataType,
            String aValidationPattern,
            String aActualData,
            String dateFormat)
    {
        final IDataValidator dataValidatorBean = dataValidatorMap.computeIfAbsent(aValidationPattern, k -> getDataType(aDataType, aValidationPattern, dateFormat));

        return dataValidatorBean.validate(aActualData);
    }

    private static IDataValidator getDataType(
            ParamDataType aDataType,
            String aValidationPattern,
            String dateFormat)
    {

        switch (aDataType)
        {
            case DATE_TIME:
                return getDateValidatorBean(aValidationPattern, dateFormat);

            case NUMBER:
                return getNumberDataValidatorBean(aValidationPattern);

            case STRING:
                return getStringDataValidatorBean(aValidationPattern);

            default:
                break;
        }
        return null;
    }

    private static StringDataValidator getStringDataValidatorBean(
            String aValidationPattern)
    {
        if ((aValidationPattern == null) || aValidationPattern.trim().equals(""))
            return null;

        return new StringDataValidator(aValidationPattern.split(","));
    }

    private static NumberDataValidator getNumberDataValidatorBean(
            String aValidationPattern)
    {
        if ((aValidationPattern == null) || aValidationPattern.trim().equals(""))
            return null;

        return new NumberDataValidator(aValidationPattern.split(","));
    }

    private static DateValidator getDateValidatorBean(
            String aValidationPattern,
            String format)
    {
        if ((aValidationPattern == null) || aValidationPattern.trim().equals(""))
            return null;

        return new DateValidator(aValidationPattern.split(","), format);
    }

}