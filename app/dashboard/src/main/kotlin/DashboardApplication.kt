package io.wafflestudio.truffle

import com.querydsl.core.types.Projections
import io.wafflestudio.truffle.core.store.r2dbc.QExceptionEventTable.exceptionEventTable
import io.wafflestudio.truffle.core.store.r2dbc.QExceptionTable.exceptionTable
import io.wafflestudio.truffle.core.store.r2dbc.QueryDslRepository
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DashboardApplication

fun main() {
    val app = runApplication<DashboardApplication>()

    // FIXME
    val querydsl = app.getBean(QueryDslRepository::class.java)

    runBlocking {
        querydsl.query { factory ->
            factory
                .select(
                    Projections.constructor(
                        ExceptionDto::class.java,
                        exceptionTable.id,
                        exceptionEventTable.message
                    )
                )
                .from(exceptionTable)
                .innerJoin(exceptionEventTable)
                .on(exceptionTable.id.eq(exceptionEventTable.exceptionId))
        }
            .all()
            .asFlow()
            .collect {
                println(it)
            }
    }
}

// FIXME
data class ExceptionDto(
    val id: Long,
    val message: String?
)
