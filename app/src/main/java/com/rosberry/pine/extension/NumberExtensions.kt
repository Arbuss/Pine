package com.rosberry.pine.extension

import android.content.res.Resources
import android.util.TypedValue

fun Number.toPx() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics)