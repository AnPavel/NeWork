package ru.netology.nework.utils

import android.os.Bundle
import com.yandex.mapkit.geometry.Point
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object PointArg : ReadWriteProperty<Bundle?, Point?> {
    override fun getValue(thisRef: Bundle?, property: KProperty<*>): Point {
        val arrayDouble = thisRef?.getDoubleArray(property.name)
        return if (arrayDouble != null) Point(arrayDouble[0], arrayDouble[1]) else {
            Point(55.748008, 55.748008)
        }

    }

    override fun setValue(thisRef: Bundle?, property: KProperty<*>, value: Point?) {
        val arrayDouble = doubleArrayOf(value!!.latitude, value.longitude)
        thisRef?.putDoubleArray(property.name, arrayDouble)
    }
}
