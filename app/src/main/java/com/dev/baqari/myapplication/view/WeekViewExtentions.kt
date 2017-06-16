package com.dev.baqari.myapplication.view

import android.graphics.Path
import android.graphics.Rect


fun Int.toHour(): String {
    return (if (this >= 10) this.toString() else "0" + this.toString()) + ":00"
}

fun Rect.toPath(radius: Float): Path {
    val path = Path()

    path.moveTo(this.left + radius / 2, this.top.toFloat())
    path.lineTo(this.right - radius / 2, this.top.toFloat())
    path.quadTo(this.right.toFloat(), this.top.toFloat(), this.right.toFloat(), this.top + radius / 2)
    path.lineTo(this.right.toFloat(), this.bottom - radius / 2)
    path.quadTo(this.right.toFloat(), this.bottom.toFloat(), this.right - radius / 2, this.bottom.toFloat())
    path.lineTo(this.left + radius / 2, this.bottom.toFloat())
    path.quadTo(this.left.toFloat(), this.bottom.toFloat(), this.left.toFloat(), this.bottom - radius / 2)
    path.lineTo(this.left.toFloat(), this.top + radius / 2)
    path.quadTo(this.left.toFloat(), this.top.toFloat(), this.left + radius / 2, this.top.toFloat())
    path.close()

    return path
}

