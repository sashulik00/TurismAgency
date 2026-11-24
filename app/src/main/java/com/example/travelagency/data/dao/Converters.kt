package com.example.travelagency.data.dao

import androidx.room.TypeConverter
import com.example.travelagency.data.entity.BookingStatus
import com.example.travelagency.data.entity.ContractStatus
import com.example.travelagency.data.entity.EmployeeRole
import com.example.travelagency.data.entity.PaymentKind
import com.example.travelagency.data.entity.PaymentMethod
import com.example.travelagency.data.entity.TourStatus

/** Enum <-> имя строки, по образцу TransactionTypeConverter из практики 08. */
class Converters {
    @TypeConverter fun roleToString(value: EmployeeRole): String = value.name
    @TypeConverter fun stringToRole(value: String): EmployeeRole = enumValueOr(value, EmployeeRole.MANAGER)

    @TypeConverter fun tourStatusToString(value: TourStatus): String = value.name
    @TypeConverter fun stringToTourStatus(value: String): TourStatus = enumValueOr(value, TourStatus.ACTIVE)

    @TypeConverter fun bookingStatusToString(value: BookingStatus): String = value.name
    @TypeConverter fun stringToBookingStatus(value: String): BookingStatus =
        enumValueOr(value, BookingStatus.PENDING)

    @TypeConverter fun contractStatusToString(value: ContractStatus): String = value.name
    @TypeConverter fun stringToContractStatus(value: String): ContractStatus =
        enumValueOr(value, ContractStatus.ACTIVE)

    @TypeConverter fun paymentKindToString(value: PaymentKind): String = value.name
    @TypeConverter fun stringToPaymentKind(value: String): PaymentKind =
        enumValueOr(value, PaymentKind.SURCHARGE)

    @TypeConverter fun paymentMethodToString(value: PaymentMethod): String = value.name
    @TypeConverter fun stringToPaymentMethod(value: String): PaymentMethod =
        enumValueOr(value, PaymentMethod.CASH)
}

private inline fun <reified T : Enum<T>> enumValueOr(name: String, fallback: T): T =
    enumValues<T>().firstOrNull { it.name == name } ?: fallback
