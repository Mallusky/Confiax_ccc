package com.ccc.confiax.utils;

import android.widget.TextView;

import com.google.common.base.Optional;

/**
 * Created by Przemek on 14.01.2017.
 */
public class IntegerWithinRange extends AbstractTextValidator {
    private final int minValue;
    private final int maxValue;
    private final int radix;

    public IntegerWithinRange(TextView textView, int minValue, int maxValue, int radix) {
        super(textView);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.radix = radix;

        if (maxValue < minValue) {
            throw new IllegalArgumentException("maxValue must be greater than or equal to minValue");
        }
    }

    public IntegerWithinRange(TextView textView, int minValue, int maxValue) {
        this(textView, minValue, maxValue, 10);
    }

    @Override
    public void validate(TextView textView, String text) {
        Optional<Integer> inputValue = OptionalUtil.parseInteger(text, radix);

        if (!inputValue.isPresent()) {
            textView.setText("");
        } else {
            Integer value = inputValue.get();
            if (value < minValue) {
                textView.setText(String.valueOf(minValue));
            } else if (value > maxValue) {
                textView.setText(String.valueOf(maxValue));
            }
        }
    }
}
