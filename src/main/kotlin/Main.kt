import java.util.concurrent.Executors

// Интерфейс аутентификации
interface Authenticator {
    fun login(username: String, password: String): Boolean
    fun register(username: String, password: String, userType: UserType): Boolean
    fun getUserType(username: String): UserType?
}

// Типы пользователей
enum class UserType {
    VISITOR,
    ADMIN
}

// Менеджер аутентификации
class AuthManager : Authenticator {
    private val users: MutableMap<String, User> = mutableMapOf()

    override fun login(username: String, password: String): Boolean {
        val user = users[username]
        return user?.password == password
    }

    override fun register(username: String, password: String, userType: UserType): Boolean {
        if (users.containsKey(username)) {
            return false
        }

        users[username] = User(username, password, userType)
        return true
    }

    override fun getUserType(username: String): UserType? {
        val user = users[username]
        return user?.userType
    }
}

// Модель пользователя
data class User(val username: String, val password: String, val userType: UserType)

// Модель блюда
data class Dish(val name: String, val price: Double, val preparationTime: Int, val count: Int)

// Менеджер меню
class MenuManager {
    private val menu: MutableList<Dish> = mutableListOf()

    fun addDish(dish: Dish) {
        menu.add(dish)
    }

    fun removeDish(dish: Dish) {
        menu.remove(dish)
    }

    fun getMenu(): List<Dish> {
        return menu.toList()
    }
}

// Модель заказа
data class Order(val visitor: User, val dishes: MutableList<Dish> = mutableListOf())

// Менеджер заказов
class OrderManager(private val menuManager: MenuManager) {
    private val orders: MutableMap<User, Order> = mutableMapOf()

    fun createOrder(user: User) {
        val order = Order(user)
        orders[user] = order
    }
    fun getOrder(user: User) : Order {
        return Order(user)
    }

    fun addToOrder(user: User, dish: Dish) {
        val order = orders[user]
        order?.dishes?.add(dish)
    }

    fun removeFromOrder(user: User, dish: Dish) {
        val order = orders[user]
        order?.dishes?.remove(dish)
    }

    fun cancelOrder(user: User) {
        orders.remove(user)
    }

    fun processOrders() {
        val executor = Executors.newFixedThreadPool(5) // Пул потоков для обработки заказов

        for ((user) in orders) {
            executor.submit {
                // Симуляция обработки заказа
                println("Готовится")
                Thread.sleep(10000)
                println("Готов")
                // Удаление заказа после выполнения
                orders.remove(user)
            }
        }

        executor.shutdown()
    }
}

// Класс приложения
class RestaurantApp(private val authManager: Authenticator, private val menuManager: MenuManager, private val orderManager: OrderManager) {
    fun run() {
        while (true) {
            println("1. Войти")
            println("2. Зарегистрироваться")
            println("3. Выйти")
            print("Выберите действие: ")
            val choice = readLine()

            when (choice) {
                "1" -> login()
                "2" -> register()
                "3" -> return
                else -> println("Неверный выбор. Попробуйте еще раз.")
            }
        }
    }

    private fun login() {
        print("Введите имя пользователя: ")
        val username : String = readLine() as String
        print("Введите пароль: ")
        val password : String = readLine() as String

        if (authManager.login(username, password)) {
            val userType = authManager.getUserType(username)
            when (userType) {
                UserType.VISITOR -> runVisitorMenu(username)
                UserType.ADMIN -> runAdminMenu()
                else -> println("Ошибка входа. Пользователь не найден.")
            }
        }
    }

        private fun register() {
            print("Введите имя пользователя: ")
            val username : String = readLine() as String
            print("Введите пароль: ")
            val password : String = readLine() as String
            print("Выберите тип пользователя (1 - Посетитель, 2 - Администратор): ")
            val userTypeChoice = readLine()

            val userType = when (userTypeChoice) {
                "1" -> UserType.VISITOR
                "2" -> UserType.ADMIN
                else -> null
            }

            if (userType != null && authManager.register(username, password, userType)) {
                println("Регистрация успешна.")
            } else {
                println("Ошибка регистрации. Пользователь уже существует.")
            }
        }

        private fun runVisitorMenu(username: String) {
            println("Добро пожаловать, $username!")
            while (true) {
                println("1. Просмотреть меню")
                println("2. Создать заказ")
                println("3. Добавить блюдо в заказ")
                println("4. Удалить блюдо из заказа")
                println("5. Отменить заказ")
                println("6. Выйти")
                println("Выберите действие: ")
                val choice = readLine()

                when (choice) {
                    "1" -> showMenu()
                    "2" -> createOrder(username)
                    "3" -> addToOrder(username)
                    "4" -> removeFromOrder(username)
                    "5" -> cancelOrder(username)
                    "6" -> return
                    else -> println("Неверный выбор. Попробуйте еще раз.")
                }
            }
        }

        private fun showMenu() {
            val menu = menuManager.getMenu()
            println("Меню:")
            for (dish in menu) {
                println("${dish.name} - ${dish.price}$, Время приготовления: ${dish.preparationTime} мин., Количество: ${dish.count}")
            }
        }

        private fun createOrder(visitorName: String) {
            val user = User(visitorName, "", UserType.VISITOR)
            orderManager.createOrder(user)
            println("Заказ создан.")
        }

        private fun addToOrder(visitorName: String) {
            val menu = menuManager.getMenu()
            println("Выберите блюдо для добавления в заказ:")
            for ((index, dish) in menu.withIndex()) {
                println("${index + 1}. ${dish.name}")
            }

            print("Введите номер блюда: ")
            val dishNumber = readLine()?.toIntOrNull()

            if (dishNumber != null && dishNumber in 1..menu.size) {
                val dish = menu[dishNumber - 1]
                val user = User(visitorName, "", UserType.VISITOR)
                orderManager.addToOrder(user, dish)
                println("Блюдо добавлено в заказ.")
                orderManager.processOrders()
            } else {
                println("Неверный выбор блюда.")
            }
        }

        private fun removeFromOrder(visitorName: String) {
            val user = User(visitorName, "", UserType.VISITOR)
            val order = orderManager.getOrder(user)

            if (order != null) {
                println("Выберите блюдо для удаления из заказа:")
                for ((index, dish) in order.dishes.withIndex()) {
                    println("${index + 1}. ${dish.name}")
                }

                print("Введите номер блюда: ")
                val dishNumber = readLine()?.toIntOrNull()

                if (dishNumber != null && dishNumber in 1..order.dishes.size) {
                    val dish = order.dishes[dishNumber - 1]
                    orderManager.removeFromOrder(user, dish)
                    println("Блюдо удалено из заказа.")
                } else {
                    println("Неверный выбор блюда.")
                }
            } else {
                println("Заказ не найден.")
            }
        }

        private fun cancelOrder(visitorName: String) {
            val user = User(visitorName, "", UserType.VISITOR)
            orderManager.cancelOrder(user)
            println("Заказ отменен.")
        }

        private fun runAdminMenu() {
            println("Вы вошли как администратор.")
            while (true) {
                println("1. Просмотреть меню")
                println("2. Добавить блюдо в меню")
                println("3. Удалить блюдо из меню")
                println("4. Выйти")
                print("Выберите действие: ")
                val choice = readLine()

                when (choice) {
                    "1" -> showMenu()
                    "2" -> addDishToMenu()
                    "3" -> removeDishFromMenu()
                    "4" -> return
                    else -> println("Неверный выбор. Попробуйте еще раз.")
                }
            }
        }

    private fun addDishToMenu() {
        print("Введите название блюда: ")
        val name = readLine()
        print("Введите цену блюда: ")
        val price = readLine()?.toDoubleOrNull()
        print("Введите время приготовления блюда (в минутах): ")
        val preparationTime = readLine()?.toIntOrNull()
        print("Введите Количество приготовления блюда (от 1 до 5): ")
        val count = readLine()?.toIntOrNull()

        if (name != null && price != null && preparationTime != null && count != null) {
            val dish = Dish(name, price, preparationTime, count)
            menuManager.addDish(dish)
            println("Блюдо добавлено в меню.")
        } else {
            println("Неверно введены данные блюда.")
        }
    }

    private fun removeDishFromMenu() {
        val menu = menuManager.getMenu()
        println("Выберите блюдо для удаления из меню:")
        for ((index, dish) in menu.withIndex()) {
            println("${index + 1}. ${dish.name}")
        }

        print("Введите номер блюда: ")
        val dishNumber = readLine()?.toIntOrNull()

        if (dishNumber != null && dishNumber in 1..menu.size) {
            val dish = menu[dishNumber - 1]
            menuManager.removeDish(dish)
            println("Блюдо удалено из меню.")
        } else {
            println("Неверный выбор блюда.")
        }
    }
}

fun main() {
    val authManager = AuthManager()
    val menuManager = MenuManager()
    val orderManager = OrderManager(menuManager)
    val app = RestaurantApp(authManager, menuManager, orderManager)
    val dish1 = Dish("Карбонара", 499.00, 60, 3)
    val dish2 = Dish("Котлетка с пюрешкой", 299.00, 50, 3)
    val dish3 = Dish("Цезарь с курицей", 399.00, 40, 3)
    val dish4 = Dish("Чизбургер", 199.00, 20, 3)
    val dish5 = Dish("Борщ", 200.00, 40, 3)
    menuManager.addDish(dish1)
    menuManager.addDish(dish2)
    menuManager.addDish(dish3)
    menuManager.addDish(dish4)
    menuManager.addDish(dish5)
    app.run()
}