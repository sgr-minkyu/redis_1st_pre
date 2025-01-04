import java.util.concurrent.ConcurrentHashMap

class ConcurrentOrderService {
    private val productDatabase = ConcurrentHashMap<String, Int>()

    // 주문 정보를 저장하는 DB
    private val threadLocalOrderDatabase = ThreadLocal.withInitial { mutableMapOf<String, MutableList<OrderInfo>>() }

    init {
        productDatabase.putAll(mapOf("apple" to 100, "banana" to 50, "orange" to 75))
    }

    // 주문 처리 메서드
    fun order(productName: String, amount: Int) {
        Thread.sleep(5) // 동시성 이슈 유발을 위한 인위적 지연 (해당 값을 조정하면서 테스트할 수 있습니다.)

        productDatabase.computeIfPresent(productName) {_, currentStock ->
            if(currentStock >= amount) {
                val orderDatabase = threadLocalOrderDatabase.get()
                val orders = orderDatabase.getOrPut(productName) { mutableListOf() }
                orders.add(OrderInfo(productName, amount))
                threadLocalOrderDatabase.set(orderDatabase)

                println("Thread ${Thread.currentThread().threadId()}의 주문 정보:")
                println("\t${productName}: ${threadLocalOrderDatabase.get()[productName]!!.size}건 ([${threadLocalOrderDatabase.get()[productName]!!.last().amount}])")

                threadLocalOrderDatabase.remove()
                currentStock - amount
            } else {
                currentStock
            }
        }
    }

    // 재고 조회
    fun getStock(productName: String): Int {
        return productDatabase.getOrDefault(productName, 0)
    }
}