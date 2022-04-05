package com.hootsuite.nachos.tokenizer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hootsuite.nachos.chip.ChipCreator;

import java.util.Set;

public class UniqueSpanChipTokenizer extends SpanChipTokenizer{
    @NonNull
    private Set<String> mTokens;

    public UniqueSpanChipTokenizer(Context context, @NonNull ChipCreator chipCreator, @NonNull Class chipClass, @NonNull Set<String> mTokens) {
        super(context, chipCreator, chipClass);
        this.mTokens = mTokens;
    }

    @Override
    public CharSequence terminateToken(CharSequence text, @Nullable Object data) {
        if(mTokens.contains(text.toString())) {
            return "";
        }
        mTokens.add(text.toString());
        return super.terminateToken(text, data);
    }

}
