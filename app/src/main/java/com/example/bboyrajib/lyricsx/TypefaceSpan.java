package com.example.bboyrajib.lyricsx;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

/**
 * Created by bboyrajib on 19/12/17.
 */

public  class TypefaceSpan extends MetricAffectingSpan {

    private final Typeface typeface;

    public TypefaceSpan(Typeface typeface) {
        this.typeface = typeface;
    }

    @Override public void updateDrawState(TextPaint tp) {
        tp.setTypeface(typeface);
        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override public void updateMeasureState(TextPaint p) {
        p.setTypeface(typeface);
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

}