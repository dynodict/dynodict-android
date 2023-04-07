package org.dynodict.storage

import org.junit.Assert.assertEquals
import org.junit.Test

class BucketNameGeneration {

    @Test
    fun `WHEN generate file name THEN file should be correct `() {
        assertEquals("login_1_en.json", generateBucketName("login", "en", 1))
    }
}