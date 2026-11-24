package com.example.travelagency.data.entity

/** Роли сотрудников корпоративной системы (как в курсовых: администратор и менеджер). */
enum class EmployeeRole { ADMIN, MANAGER }

enum class TourStatus { ACTIVE, DISCONTINUED }

enum class BookingStatus { PENDING, CONFIRMED, CANCELLED }

enum class ContractStatus { ACTIVE, COMPLETED, CANCELLED }

enum class PaymentKind { FULL, PREPAY, SURCHARGE, REFUND }

enum class PaymentMethod { CASH, CARD, TRANSFER, SBP }
