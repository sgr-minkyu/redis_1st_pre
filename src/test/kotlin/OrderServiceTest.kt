import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class OrderServiceTest {

    private val service = OrderService()

    @Test
    fun `동시 주문 시 재고 불일치 발생 테스트`() {
        val productName = "apple"
        val initialStock = service.getStock(productName)

        val orderAmount = 8
        val threadCount = 100

        val executor = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount)

        // 각 스레드에서 주문을 수행하는 작업 생성
        repeat(threadCount) {
            executor.execute {
                try {
                    service.order(productName, orderAmount)
                } finally {
                    latch.countDown() // 작업 완료 후 카운트 감소
                }
            }
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await()
        executor.shutdown()

        // 최종 재고 값 확인
        val expectedStock = initialStock % orderAmount
        val actualStock = service.getStock(productName)

        println("Expected Stock: $expectedStock, Actual Stock: $actualStock")

        // 동시성 이슈로 인해 재고가 맞지 않는 경우를 확인
        assertNotEquals(expectedStock, actualStock, "재고 불일치 발생!")
    }
}
