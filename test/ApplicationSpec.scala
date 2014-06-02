import etch.EtchController
import org.scalatest.junit.JUnitRunner
import org.scalatest.WordSpec
import org.junit.runner._


@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends WordSpec {

  "truncate" should {
    "remove " in {
      val x: Double = 1.23456789
      val result = EtchController truncate x
      val expected = 1.23456
      assertResult(expected)(result)

    }
  }
}
