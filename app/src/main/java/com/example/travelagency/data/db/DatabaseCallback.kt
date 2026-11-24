package com.example.travelagency.data.db

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.travelagency.core.sha256
import java.util.concurrent.TimeUnit

/**
 * Начальное заполнение БД и средства поддержания целостности:
 *  - триггеры квоты мест (free quota = tours.quota, брони списывают места);
 *  - триггер аудита смены статуса брони;
 *  - предзаполнение справочников и контрольных данных (>= 10 записей в таблицах).
 */
class DatabaseCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        createTriggers(db)
        seedEmployees(db)
        seedDestinations(db)
        seedMealTypes(db)
        seedOperators(db)
        seedHotels(db)
        seedTours(db)
        seedClients(db)
        seedBookingsWithDocuments(db)
        seedAuditExamples(db)
    }

    private fun createTriggers(db: SupportSQLiteDatabase) {
        // Нельзя занять больше мест, чем свободно в туре.
        db.execSQL(
            """
            CREATE TRIGGER IF NOT EXISTS trg_bookings_guard_insert
            BEFORE INSERT ON bookings
            BEGIN
                SELECT CASE WHEN NEW.status != 'CANCELLED'
                    AND (SELECT quota FROM tours WHERE id = NEW.tourId) < NEW.touristsCount
                THEN RAISE(ABORT, 'QUOTA_EXCEEDED') END;
            END
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TRIGGER IF NOT EXISTS trg_bookings_guard_update
            BEFORE UPDATE ON bookings
            BEGIN
                SELECT CASE WHEN NEW.status != 'CANCELLED'
                    AND (SELECT quota FROM tours WHERE id = NEW.tourId)
                        + (CASE WHEN OLD.status != 'CANCELLED' AND OLD.tourId = NEW.tourId
                           THEN OLD.touristsCount ELSE 0 END) < NEW.touristsCount
                THEN RAISE(ABORT, 'QUOTA_EXCEEDED') END;
            END
            """.trimIndent(),
        )
        // Бронь списывает места из квоты тура.
        db.execSQL(
            """
            CREATE TRIGGER IF NOT EXISTS trg_bookings_after_insert
            AFTER INSERT ON bookings
            BEGIN
                UPDATE tours SET quota = quota - NEW.touristsCount
                WHERE id = NEW.tourId AND NEW.status != 'CANCELLED';
            END
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TRIGGER IF NOT EXISTS trg_bookings_after_update
            AFTER UPDATE ON bookings
            BEGIN
                UPDATE tours SET quota = quota
                    + (CASE WHEN OLD.status != 'CANCELLED' THEN OLD.touristsCount ELSE 0 END)
                    - (CASE WHEN NEW.status != 'CANCELLED' THEN NEW.touristsCount ELSE 0 END)
                WHERE id = OLD.tourId;
            END
            """.trimIndent(),
        )
        db.execSQL(
            """
            CREATE TRIGGER IF NOT EXISTS trg_bookings_after_delete
            AFTER DELETE ON bookings
            BEGIN
                UPDATE tours SET quota = quota + OLD.touristsCount
                WHERE id = OLD.tourId AND OLD.status != 'CANCELLED';
            END
            """.trimIndent(),
        )
        // Аудит: смена статуса брони попадает в журнал.
        db.execSQL(
            """
            CREATE TRIGGER IF NOT EXISTS trg_bookings_audit
            AFTER UPDATE OF status ON bookings
            BEGIN
                INSERT INTO audit_log (entity, entityId, action, details, at)
                VALUES ('bookings', NEW.id, 'UPDATE',
                        'status: ' || OLD.status || ' -> ' || NEW.status,
                        CAST(strftime('%s', 'now') AS INTEGER) * 1000);
            END
            """.trimIndent(),
        )
    }

    private fun seedEmployees(db: SupportSQLiteDatabase) {
        val rows = listOf(
            arrayOf<Any>(1L, "Иванова Елена Петровна", "Директор агентства", "ADMIN", "admin".sha256()),
            arrayOf<Any>(2L, "Смирнов Алексей Игоревич", "Менеджер по туризму", "MANAGER", "1234".sha256()),
            arrayOf<Any>(3L, "Кузнецова Ольга Дмитриевна", "Менеджер по туризму", "MANAGER", "1234".sha256()),
        )
        rows.forEach { args ->
            db.execSQL(
                "INSERT INTO employees (id, fullName, position, role, passwordHash) VALUES (?, ?, ?, ?, ?)",
                args,
            )
        }
    }

    private fun seedDestinations(db: SupportSQLiteDatabase) {
        val rows = listOf(
            arrayOf<Any>(1L, "Турция", "Анталья"),
            arrayOf<Any>(2L, "Турция", "Стамбул"),
            arrayOf<Any>(3L, "Египет", "Хургада"),
            arrayOf<Any>(4L, "Египет", "Шарм-эль-Шейх"),
            arrayOf<Any>(5L, "ОАЭ", "Дубай"),
            arrayOf<Any>(6L, "Таиланд", "Пхукет"),
            arrayOf<Any>(7L, "Таиланд", "Паттайя"),
            arrayOf<Any>(8L, "Мальдивы", "Мале"),
            arrayOf<Any>(9L, "Россия", "Сочи"),
            arrayOf<Any>(10L, "Россия", "Казань"),
        )
        rows.forEach { args ->
            db.execSQL("INSERT INTO destinations (id, country, city) VALUES (?, ?, ?)", args)
        }
    }

    private fun seedMealTypes(db: SupportSQLiteDatabase) {
        val rows = listOf(
            arrayOf<Any>(1L, "RO", "Без питания"),
            arrayOf<Any>(2L, "BB", "Только завтраки"),
            arrayOf<Any>(3L, "HB", "Завтраки и ужины"),
            arrayOf<Any>(4L, "FB", "Полный пансион"),
            arrayOf<Any>(5L, "AI", "Всё включено"),
            arrayOf<Any>(6L, "UAI", "Ультра всё включено"),
        )
        rows.forEach { args ->
            db.execSQL("INSERT INTO meal_types (id, code, name) VALUES (?, ?, ?)", args)
        }
    }

    private fun seedOperators(db: SupportSQLiteDatabase) {
        val rows = listOf(
            arrayOf<Any>(1L, "Anex Tour", "Морозова Анна", "+74951234501", "sales@anextour.ru"),
            arrayOf<Any>(2L, "Pegas Touristik", "Крылов Игорь", "+74951234502", "info@pegast.ru"),
            arrayOf<Any>(3L, "Coral Travel", "Наумова Мария", "+74951234503", "office@coral.ru"),
            arrayOf<Any>(4L, "TUI Россия", "Волков Павел", "+74951234504", "b2b@tui.ru"),
            arrayOf<Any>(5L, "Библио-Глобус", "Зайцева Дарья", "+74951234505", "trade@bgtour.ru"),
        )
        rows.forEach { args ->
            db.execSQL(
                "INSERT INTO tour_operators (id, name, contactPerson, phone, email) VALUES (?, ?, ?, ?, ?)",
                args,
            )
        }
    }

    private fun seedHotels(db: SupportSQLiteDatabase) {
        val rows = listOf(
            arrayOf<Any>(1L, "Delphin Imperial", 5L, 1L),
            arrayOf<Any>(2L, "Sunis Kum Koob", 4L, 1L),
            arrayOf<Any>(3L, "DoubleTree Hilton", 5L, 2L),
            arrayOf<Any>(4L, "Steigenberger Al Dau", 5L, 3L),
            arrayOf<Any>(5L, "Sunrise Arabian Beach", 4L, 4L),
            arrayOf<Any>(6L, "Atlantis The Palm", 5L, 5L),
            arrayOf<Any>(7L, "Rixos Premium Gocek", 5L, 5L),
            arrayOf<Any>(8L, "Katathani Phuket", 4L, 6L),
            arrayOf<Any>(9L, "Centara Grand Mirage", 4L, 7L),
            arrayOf<Any>(10L, "Sun Siyam Olhuveli", 5L, 8L),
            arrayOf<Any>(11L, "Radisson Collection Парадный", 4L, 9L),
            arrayOf<Any>(12L, "Отель Ривьера", 3L, 10L),
        )
        rows.forEach { args ->
            db.execSQL("INSERT INTO hotels (id, name, stars, destinationId) VALUES (?, ?, ?, ?)", args)
        }
    }

    private fun seedTours(db: SupportSQLiteDatabase) {
        val now = System.currentTimeMillis()
        val day = TimeUnit.DAYS.toMillis(1)
        // id, name, operatorId, destinationId, hotelId, mealTypeId, inDays, nights, priceRub, quota, description
        val tours = listOf(
            TourSeed(1, "Анталья: море и солнце", 1L, 1L, 1L, 5L, 14, 10, 98_900, 16, "Пляжный отдых на берегу Средиземного моря."),
            TourSeed(2, "Анталья бюджетный", 2L, 1L, 2L, 2L, 21, 7, 62_500, 24, "Экономичный тур для семейного отдыха."),
            TourSeed(3, "Стамбул: два берега", 3L, 2L, 3L, 2L, 30, 4, 74_300, 12, "Экскурсионный тур по главным достопримечательностям."),
            TourSeed(4, "Хургада: коралловые рифы", 1L, 3L, 4L, 5L, 18, 10, 87_400, 18, "Дайвинг и снорклинг у рифов Красного моря."),
            TourSeed(5, "Шарм-эль-Шейх: отдых у моря", 2L, 4L, 5L, 5L, 25, 11, 95_700, 20, "Всё включено в отеле на первой линии."),
            TourSeed(6, "Дубай: город будущего", 4L, 5L, 6L, 4L, 40, 7, 154_900, 10, "Небоскрёбы, шопинг и пляжи Персидского залива."),
            TourSeed(7, "Дубай премиум", 4L, 5L, 7L, 6L, 45, 6, 208_400, 6, "Отели 5* ультра всё включено на Palm Jumeirah."),
            TourSeed(8, "Пхукет: тропический рай", 3L, 6L, 8L, 5L, 35, 12, 139_900, 14, "Белоснежные пляжи и экскурсии на острова."),
            TourSeed(9, "Паттайя: активный отдых", 5L, 7L, 9L, 3L, 28, 10, 102_300, 22, "Развлечения, пляжи и поездки в Бангкок."),
            TourSeed(10, "Мальдивы: вилла над водой", 1L, 8L, 10L, 6L, 60, 8, 342_600, 8, "Романтическое путешествие на вилле над водой."),
            TourSeed(11, "Сочи: горы и море", 5L, 9L, 11L, 2L, 17, 7, 48_700, 30, "Комбинированный отдых: пляж и Красная Поляна."),
            TourSeed(12, "Казань: выходные в столице Татарстана", 5L, 10L, 12L, 1L, 10, 3, 27_900, 26, "Короткий экскурсионный тур на выходные."),
        )
        tours.forEach { tour ->
            db.execSQL(
                """
                INSERT INTO tours (id, name, operatorId, destinationId, hotelId, mealTypeId,
                    departureDate, nights, priceMinor, quota, description, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVE')
                """.trimIndent(),
                arrayOf<Any>(
                    tour.id,
                    tour.name,
                    tour.operatorId,
                    tour.destinationId,
                    tour.hotelId,
                    tour.mealTypeId,
                    now + tour.inDays * day,
                    tour.nights,
                    tour.priceRub * 100,
                    tour.quota,
                    tour.description,
                ),
            )
        }
    }

    private fun seedClients(db: SupportSQLiteDatabase) {
        val rows = listOf(
            arrayOf<Any>(1L, "Петров", "Дмитрий", "Сергеевич", parse("12.05.1988"), "9203", "456123", "+79171234501", "petrov@mail.ru"),
            arrayOf<Any>(2L, "Сидорова", "Анна", "Владимировна", parse("03.11.1992"), "9204", "123876", "+79171234502", "sidorova@gmail.com"),
            arrayOf<Any>(3L, "Козлов", "Максим", "Игоревич", parse("23.07.1985"), "9205", "784512", "+79171234503", "kozlov@yandex.ru"),
            arrayOf<Any>(4L, "Морозова", "Елена", "Андреевна", parse("18.02.1990"), "9206", "345298", "+79171234504", "morozova@mail.ru"),
            arrayOf<Any>(5L, "Новиков", "Артём", "Павлович", parse("29.09.1995"), "9207", "956431", "+79171234505", "novikov@inbox.ru"),
            arrayOf<Any>(6L, "Волкова", "Светлана", "Юрьевна", parse("07.06.1983"), "9208", "267384", "+79171234506", "volkova@gmail.com"),
            arrayOf<Any>(7L, "Лебедев", "Олег", "Дмитриевич", parse("15.12.1979"), "9209", "513876", "+79171234507", "lebedev@bk.ru"),
            arrayOf<Any>(8L, "Орлова", "Ксения", "Максимовна", parse("21.04.1998"), "9210", "628495", "+79171234508", "orlova@mail.ru"),
            arrayOf<Any>(9L, "Соколов", "Владимир", "Александрович", parse("02.08.1987"), "9211", "837261", "+79171234509", "sokolov@list.ru"),
            arrayOf<Any>(10L, "Павлова", "Наталья", "Игоревна", parse("26.01.1993"), "9212", "192837", "+79171234510", "pavlova@rambler.ru"),
            arrayOf<Any>(11L, "Гусев", "Роман", "Олегович", parse("09.10.1991"), "9213", "465728", "+79171234511", "gusev@gmail.com"),
            arrayOf<Any>(12L, "Титова", "Мария", "Сергеевна", parse("14.03.1996"), "9214", "739182", "+79171234512", "titova@yandex.ru"),
        )
        rows.forEach { args ->
            db.execSQL(
                """
                INSERT INTO clients (id, lastName, firstName, middleName, birthDate,
                    passportSeries, passportNumber, phone, email)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                args,
            )
        }
    }

    /** Брони + договоры + платежи. Квота списывается триггером при вставке. */
    private fun seedBookingsWithDocuments(db: SupportSQLiteDatabase) {
        val now = System.currentTimeMillis()
        val day = TimeUnit.DAYS.toMillis(1)
        // clientId, tourId, tourists, status, createdDaysAgo, prepayRub
        val bookings = listOf(
            SeedBooking(1L, 1L, 2, "CONFIRMED", 12, 30_000),
            SeedBooking(2L, 4L, 1, "CONFIRMED", 9, 25_000),
            SeedBooking(3L, 6L, 2, "PENDING", 7, 0),
            SeedBooking(4L, 8L, 3, "CONFIRMED", 6, 60_000),
            SeedBooking(5L, 11L, 2, "PENDING", 3, 0),
            SeedBooking(9L, 5L, 1, "CONFIRMED", 2, 28_000),
        )
        val prices = mapOf(1L to 98_900L, 4L to 87_400L, 5L to 95_700L, 6L to 154_900L, 8L to 139_900L, 11L to 48_700L)
        bookings.forEachIndexed { index, booking ->
            val bookingId = index + 1L
            val total = prices.getValue(booking.tourId) * booking.tourists * 100
            val createdAt = now - booking.createdDaysAgo * day
            db.execSQL(
                """
                INSERT INTO bookings (id, clientId, tourId, touristsCount, totalPriceMinor, createdAt, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                arrayOf<Any>(bookingId, booking.clientId, booking.tourId, booking.tourists, total, createdAt, booking.status),
            )
            db.execSQL(
                """
                INSERT INTO contracts (id, bookingId, number, concludedAt, amountMinor, status)
                VALUES (?, ?, ?, ?, ?, 'ACTIVE')
                """.trimIndent(),
                arrayOf<Any>(bookingId, bookingId, "Д-2026-${(100 + bookingId).toString()}", createdAt, total),
            )
            if (booking.prepayRub > 0) {
                db.execSQL(
                    """
                    INSERT INTO payments (id, contractId, kind, method, amountMinor, paidAt)
                    VALUES (?, ?, 'PREPAY', 'CARD', ?, ?)
                    """.trimIndent(),
                    arrayOf<Any>(bookingId, bookingId, booking.prepayRub * 100, createdAt),
                )
            }
        }
    }

    /** Пара смен статусов, чтобы журнал аудита не был пустым при первом запуске. */
    private fun seedAuditExamples(db: SupportSQLiteDatabase) {
        db.execSQL("UPDATE bookings SET status = 'CANCELLED' WHERE id = 3")
        db.execSQL("UPDATE contracts SET status = 'CANCELLED' WHERE bookingId = 3")
    }

    private fun parse(date: String): Long =
        java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.US).parse(date)?.time ?: 0L

    private data class TourSeed(
        val id: Long,
        val name: String,
        val operatorId: Long,
        val destinationId: Long,
        val hotelId: Long,
        val mealTypeId: Long,
        val inDays: Int,
        val nights: Int,
        val priceRub: Long,
        val quota: Int,
        val description: String,
    )

    private data class SeedBooking(
        val clientId: Long,
        val tourId: Long,
        val tourists: Int,
        val status: String,
        val createdDaysAgo: Int,
        val prepayRub: Long,
    )
}
