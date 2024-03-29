Руководство пользователя для ресторанного приложения
Данное ресторанное приложение позволяет пользователям, как посетителям, так и администраторам, управлять меню ресторана, создавать заказы и обрабатывать их в реальном времени. 
Ниже приведены инструкции по использованию программы.

Запуск программы: 
После запуска программы вам будет предложено войти, зарегистрироваться или выйти. Выберите соответствующее действие, введя номер команды.

Логин и Регистрация:
При выборе опции "Войти", введите свое имя пользователя и пароль. В случае успешного входа, вас перенаправят в соответствующий раздел для посетителя или администратора.
При выборе опции "Зарегистрироваться", введите желаемое имя пользователя, пароль и выберите тип пользователя (посетитель или администратор).

Меню посетителя:
Посетитель может просмотреть меню, создать заказ, добавить блюдо в заказ, удалить блюдо из заказа и отменить заказ.

Меню администратора:
Администратор может просмотреть меню, добавить новое блюдо в меню и удалить существующее блюдо из меню.

Обработка заказов:
Заказы обрабатываются автоматически в отдельных потоках (пул потоков) с симуляцией времени на подготовку блюд. После завершения заказа вы получите уведомление.
Использование шаблонов проектирования

Шаблон Singleton: Шаблон Singleton был применен для класса AuthManager, чтобы гарантировать, что система работы с пользователями будет иметь единственный экземпляр.

Шаблон MVC (Model-View-Controller): В программа использован шаблон MVC для отделения логики бизнес-логики от представления пользовательского интерфейса. 
RestaurantApp является контроллером, управляющим взаимодействием с пользователем, AuthManager, MenuManager и OrderManager являются моделями, содержащими бизнес-логику, а части кода, отвечающие за вывод информации и ввод от пользователя, являются представлением.
