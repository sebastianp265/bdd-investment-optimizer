import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

fun BigDecimal.shouldBeCloseTo(
    expected: BigDecimal,
    tolerance: BigDecimal = "0.01".toBigDecimal(),
) {
    val difference = this.subtract(expected).abs()
    withClue(
        "Expected value within tolerance, expected=$expected, actual=$this, tolerance=$tolerance, difference=$difference"
    ) {
        (difference <= tolerance) shouldBe true
    }
}
