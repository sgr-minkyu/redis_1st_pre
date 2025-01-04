// 주문 정보를 저장하는 데이터 클래스
data class OrderInfo(
    val productName: String,
    val amount: Int,
    val timestamp: Long = System.currentTimeMillis()
)

// 주문 처리를 담당하는 도메인 서비스
class OrderService {

    // 상품 DB
    private val productDatabase = mutableMapOf(
        "apple" to 100,
        "banana" to 50,
        "orange" to 75
    )

    // 주문 정보를 저장하는 DB
    private val orderDatabase = mutableMapOf<String, MutableList<OrderInfo>>()

    // 주문 처리 메서드
    fun order(productName: String, amount: Int) {
        val currentStock = productDatabase[productName] ?: 0

        Thread.sleep(5) // 동시성 이슈 유발을 위한 인위적 지연 (해당 값을 조정하면서 테스트할 수 있습니다.)

        if (currentStock >= amount) {
            // 동시성 이슈를 확인하기 위한 로그
            println("Current Thread : ${Thread.currentThread().name} - CurrentStock : $currentStock - Order : $amount")

            productDatabase[productName] = currentStock - amount
            orderDatabase[productName] = mutableListOf(OrderInfo(productName, amount))
        }
    }

    // 재고 조회
    fun getStock(productName: String): Int {
        return productDatabase[productName] ?: 0
    }
}