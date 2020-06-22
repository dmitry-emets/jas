package com.demets.jas.repository.api.model

/**
 * Created by DEmets on 18.01.2018.
 */
abstract class Wrappable<T> {
    abstract val values: List<T>
}
