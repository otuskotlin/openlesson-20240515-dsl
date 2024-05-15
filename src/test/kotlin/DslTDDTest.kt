@file:Suppress("unused")

package ru.otus.otuskotlin.dsl

import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.Test

class DslTDDTest {
    @Test
    fun dslTest() {
        val expected = User(
            fname = "Ivan",
            mname = "Ivanovich",
            lname = "Ivanov",
            gender = User.UserGender.MALE,
            dob = LocalDate.of(1980, 2, 29),
            location = "Kolyma",
            permissions = setOf(
                User.UserPermissions.READ,
                User.UserPermissions.WRITE,
            )
        )
        val actual = createUser {
            name {
                last = "Ivanov"
                first = "Ivan"
                middle = "Ivanovich"
            }
            birth {
                location = "Kolyma"
                date("29.02.1980")
                date(LocalDate.of(1980, 2, 29))
            }
            gender(User.UserGender.MALE)
            permissions {
                +User.UserPermissions.READ
                add(User.UserPermissions.WRITE)
            }
        }.build()

        assertEquals(expected, actual)
    }

    private fun createUser(function: UserDsl.() -> Unit): UserDsl = UserDsl().apply(function)

}

class UserPermissionsDsl {
    private val perm: MutableSet<User.UserPermissions> = mutableSetOf()

    fun add(p: User.UserPermissions) = perm.add(p)
    operator fun User.UserPermissions.unaryPlus() {
        add(this)
    }
    fun build(): Set<User.UserPermissions> = perm.toSet()
}

data class UserDsl(
    private var name_: UserNameDsl = UserNameDsl(),
    private var gender_: User.UserGender = User.UserGender.MALE,
    private var birth_: UserBirthDsl = UserBirthDsl(),
    private var permissions_: UserPermissionsDsl = UserPermissionsDsl(),

) {

    fun name(block: UserNameDsl.() -> Unit) {
        name_ = UserNameDsl().apply(block)
    }

    fun birth(function: UserBirthDsl.() -> Unit) {
        birth_ = UserBirthDsl().apply(function)
    }

    fun gender(g: User.UserGender) {
        gender_ = g
    }

    fun permissions(function: UserPermissionsDsl.() -> Unit) {
        permissions_ = UserPermissionsDsl().apply(function)
    }

    fun build(): User {
        val name = name_.build()
        val birth = birth_.build()
        val perm = permissions_.build()
        return User(
            fname = name.fname,
            mname = name.mname,
            lname = name.lname,
            gender = gender_,
            dob = birth.date,
            location = birth.location,
            permissions = perm
        )
    }

}

data class UserName(
    val fname: String = "",
    val mname: String = "",
    val lname: String = "",
)
class UserNameDsl {
    var last: String = ""
    var first: String = ""
    var middle: String = ""

    fun build(): UserName = UserName(
        fname = first,
        lname = last,
        mname = middle,
    )
}

class UserBirthDsl {
    private var date_: LocalDate = LocalDate.MIN
    var location: String = ""

    fun date(d: LocalDate) {
        date_ = d
    }
    fun date(stringDate: String) {
        date_ = LocalDate.parse(
            stringDate,
            DateTimeFormatter.ofPattern("dd.MM.yyyy")
        )
    }

    fun build(): UserBirth = UserBirth(
        date = date_,
        location = this.location
    )
}

data class UserBirth(
    val date: LocalDate,
    val location: String,
)

data class User(
    val fname: String,
    val mname: String,
    val lname: String,
    val gender: UserGender,

    val dob: LocalDate = LocalDate.now(),
    val location: String = "",

    val permissions: Set<UserPermissions> = emptySet(),
) {
    enum class UserGender {
        MALE,
        FEMALE,
    }

    enum class UserPermissions {
        READ,
        WRITE,
        LIST,
        DELETE,
    }
}
