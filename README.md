# Система заказов на покупку билетов
# I. Коллекция Postman
Коллекция доступна по ссылке:
https://www.postman.com/payload-geologist-75900251/workspace/ordersystem

Для того, чтобы запустить каждый проект, нужно в корне написать: 
		
			docker-compose up -d

Одновременно оба запустить не получится, так как они используют разные базы данных, каждая из которых занимает порт 5432
# II. Микросервис авторизации пользователей
## Как пользоваться?
### Регистрация: http://localhost:8081/auth-api/register
Нужно предоставить три непустые строки: никнейм, почту и пароль.
В Postman нужно перейти в Body и выбрать raw.

Пример корректного ввода:

		{
			"nickname": "user",
			"email": "user@example.com",
			"password": "Password123!"
		}
Почта должна содержать знаки "." и "@".
Пароль должен состоять из не менее восьми символов, включая буквы обоих регистров, цифры и специальные символы.

При успешной регистрации вывод выглядит так:

		Registration successful
		
		access_token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzE3OTYxNzU2LCJleHAiOjE3MTgwNDgxNTZ9.Zz1pMCAfrBb_k0Au2NSd78i_O2MSuzOVrl2M3tNtgO4
		refresh_token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzE3OTYxNzU2LCJleHAiOjE3MTg1NjY1NTZ9.iZcN5FbZoK4T929O3QmWUdldyslj5RMT4GA3vJzkPNk

Ошибка при пустом никнейме:

		Nickname must not be empty

Ошибка при некорректной почте:

		Invalid email. Email must not be empty and contain @ and .

Ошибка при неверном пароле:

		The password must consist of at least 8 characters, including both case letters, numbers and special characters

### Аутентификация: http://localhost:8081/auth-api/authenticate
Нужно предоставить две непустые строки: почту и пароль.
В Postman нужно перейти в Body и выбрать raw.

Пример корректного ввода:

		{
			"email": "user@example.com",
			"password": "Password123!"
		}

Вывод при успешной авторизации выглядит так:

		Authentication successful

		access_token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzE3OTYyMjQ2LCJleHAiOjE3MTgwNDg2NDZ9.XPfG-MNXnDv6vAQOlrotQi57idRhbZLHO-ekVk9OFEQ
		refresh_token: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzE3OTYyMjQ2LCJleHAiOjE3MTg1NjcwNDZ9.ewijUNV_jMZjt-kwnp3D3qG2-WM8iAME1RYn1eP8lnY
	
Вывод при некорректных данных:

		Incorrect email or password

### Выдача информации о пользователе по токену: http://localhost:8081/auth-api/user-info

Нужно предоставить корректный токен, который можно получить, например, при аутентификации. 
В Postman перейти в Authorization, выбрать Bearer Token и вставить токен в поле Token.

Вывод при корректном токене выглядит так:

		Nickname: user
		Email: user@example.com
		Created: 2024-06-09 19:35:56.763

Вывод при некорректном токене:

		Invalid token or user not found

# Архитектура микросервиса
## auditing:
- ApplicationAuditAware - используется для отслеживания, кто совершил определенные действия, такие как создание или изменение записей
## config:
- ApplicationConfiguration - объединяет различные компоненты Spring Security и аутентификации в приложении, обеспечивая настройку аутентификационного провайдера, менеджера аутентификации, сервиса для работы с пользователями, кодировщика паролей и аудитора
- JwtAuthenticationFilter - фильтр Spring, который обрабатывает аутентификацию пользователей с использованием JWT токенов
- JwtService - предоставляет методы для работы с JWT токенами
- SecurityConfiguration - конфигурация Spring Security для настройки безопасности приложения
## controller:
### auth:
- AuthenticationController - контроллер, предоставляющий конечные точки для регистрации и аутентификации пользователей
- AuthenticationRequest - запрос на аутентификацию пользователя
- AuthenticationResponse - ответ на запрос аутентификации пользователя
- AuthenticationService - сервис обработки запросов на аутентификацию и регистрацию пользователей
- RegisterRequest - запрос при регистрации нового пользователя
### info:
- UserInfoController - эндпоинт для получения информации о пользователе по его токену аутентификации
## model:
### session:
- Session - класс, представляющий сущность Сессии в системе, используется для хранения данных о сессиях пользователей в базе данных
- SessionRepository - репозиторий для работы с сущностью Session в базе данных
- SessionService - класс, предоставляющий методы для работы с сессиями пользователей
### token:
- Token - класс, представляющий сущность токена в системе, используется для хранения данных о токенах пользователей в базе данных
- TokenRepository - репозиторий для работы с сущностью Token в базе данных
- TokenType - определяет типы токенов аутентификации
### user:
- User - класс, представляющий сущность пользователя в системе, используется для хранения данных о пользователях в базе данных
- UserRepository - репозиторий для работы с сущностью User в базе данных
- Permission - перечисление разрешений
- Role - роль пользователя
# III. Микросервис заказов на покупку билетов
## Как пользоваться?
### Обработка заказов на покупку билетов: http://localhost:8080/ticket-api/order 

При запуске программы с помощью класса DataInitializer автоматически заполняются таблицы пользователей и станций. Для успешного создания заказа нужно вводить данные пользователей и станций, содержащихся в таблицах.

Нужно предоставить id авторизированного пользователя, id станции отправления, назначения и токен. (Для проверки того, что пользователь есть в системе и его сессия еще не завершена)
В Postman нужно перейти в Body и выбрать raw.

Пример корректного ввода:

		{
			"userId": "1",
			"departureId": "4",
			"destinationId": "1",
			"token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzE3OTI1OTQ4LCJleHAiOjE3MjA1MTc5NDh9.BhDnJImtv2R3QgshxDj2U5bI_b3UwK2CZdW3_SGGYmY"
		}
Здесь дан токен пользователя, который есть в таблице (заполняющейся при запуске программы), истекающий 9 июля, поэтому при проверке можно будет все еще использовать его. 
При корректном вводе вывод выглядит так:

		Success, order is being checked

		Details
		user: user(id = 1)
		from: West Station(id = 4)
		to: Central Station(id = 1)
		created: 2024-06-09 20:04:25.642

Вывод при неверном id пользователя:

		User with id = 0 not found in repository

Вывод при неверном id станции:

		Departure station with id = 98 not found in repository

		Destination station with id = 90 not found in repository

Вывод при некорректном токене:

		Sorry, your token is not valid


### Предоставление информации о заказе: http://localhost:8080/ticket-api/orders/{i}
i - номер заказа

Нужно предоставить только номер существующего заказа.

Пример вывода при корректном вводе:

		Order id = 1 details
		
		user: user(id = 1)
		from: West Station(id = 4)
		to: Central Station(id = 1)
		status: success
		created: 2024-06-09 20:02:22.802

Пример вывода при некорректном вводе:
		
		Order with id 09 not found

# Архитектура микросервиса
## config:

 - DataInitializer - заполняет таблицы авторизированных пользователей и станций при запуске
 - JwtUtil - для работы с JWT токенами
 - SecurityConfig - конфигурирует безопасность приложения
## controller:
- OrderController - обрабатывает запросы на создание и получение информации о заказах
- OrderRequest - используется для передачи данных о заказе от клиента на сервер
- OrderResponse - используется для передачи данных о заказе от сервера клиенту
## model:
### order:
- Order - класс, представляющий сущность заказа в системе, используется для хранения данных о заказах в базе данных
- OrderRepository - репозиторий для работы с сущностью Order в базе данных.
### station:
- Station - класс, представляющий сущность станции в системе, используется для хранения данных о станциях в базе данных
- StationRepository - репозиторий для работы с сущностью Station в базе данных.
### user:
- User - класс, представляющий сущность пользователя в системе, используется для хранения данных о пользователях в базе данных
- UserRepository - репозиторий для работы с сущностью User в базе данных.
## service:
- OrderProcessing - класс, имитирующий обработку заказов и изменяющий их статусы в системе бронирования билетов.
- OrderService - класс, который обрабатывает запросы на создание заказов. Он выполняет проверку валидности введенных данных, создание нового заказа и сохранение его в базе данных.

# IV. Список использованных технологий (библиотек, фреймворков)
    -  `spring-boot-starter-data-jpa`: Для работы с базами данных через JPA.
    
    -   `spring-boot-starter-security`: Для обеспечения безопасности.
    
    -   `spring-boot-starter-web`: Для разработки веб-приложений.
    
    -   `spring-boot-starter-test`: Для тестирования приложений.
    
    -   `spring-boot-maven-plugin`: Для сборки и запуска Spring Boot приложений.
    
    -   `org.postgresql:postgresql`: Драйвер для работы с PostgreSQL.
    
    -   `com.mysql:mysql-connector-j`: Драйвер для работы с MySQL.
    
    -   `org.projectlombok:lombok`: Для сокращения шаблонного кода с помощью аннотаций.
    
    -   `io.jsonwebtoken:jjwt-api`: Основная библиотека для работы с JWT.
    
    -   `io.jsonwebtoken:jjwt-impl`: Реализация библиотеки JWT.
    
    -   `io.jsonwebtoken:jjwt-jackson`: Для интеграции с Jackson.
    
