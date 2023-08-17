package eu.auct.twitter2nitter

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }



    private fun getTweet(input: String): String? {
        val pattern: Pattern = Pattern.compile(".*?(twitter|[/.]x)\\.com(/.*?)(\\s|\$|&)")
        val matcher: Matcher = pattern.matcher(input)
        matcher.find()
        try {
            return matcher.group(2)
        } catch (t: Throwable) {
            return null
        }
    }
    @Test

    fun patternMatch_isCorrect() {


        assertEquals( "/AucT", getTweet("https://twitter.com/AucT"))
        assertEquals( "/AucT", getTweet("https://www.twitter.com/AucT"))
        assertEquals( "/AucT", getTweet("http://twitter.com/AucT"))
        assertEquals( "/AucT", getTweet("http://www.twitter.com/AucT"))


        assertEquals( "/AucT", getTweet("https://x.com/AucT"))
        assertEquals( "/AucT", getTweet("https://www.x.com/AucT"))
        assertEquals( "/AucT", getTweet("http://x.com/AucT"))
        assertEquals( "/AucT", getTweet("http://www.x.com/AucT"))

        assertEquals( null, getTweet("http://mywebx.com/AucT"))
        assertEquals( null, getTweet("https://mywebx.com/AucT"))
        assertEquals( null, getTweet("http://www.mywebx.com/AucT"))
        assertEquals( null, getTweet("https://www.mywebx.com/AucT"))
    }

}