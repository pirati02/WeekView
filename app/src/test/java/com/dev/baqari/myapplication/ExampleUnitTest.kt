package com.dev.baqari.myapplication

import com.dev.baqari.myapplication.view.Padding
import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun testPadding(){
        val padding = Padding.BOTTOM
        Assert.assertEquals(padding.value, 5)
    }
}