package org.dhis2.usescases.datasets.datasetDetail

import dhis2.org.analytics.charts.Charts
import io.reactivex.Single
import org.dhis2.commons.resources.DhisPeriodUtils
import org.dhis2.data.dhislogic.AUTH_DATAVALUE_ADD
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.category.CategoryCombo
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.DataAccess
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataset.DataSet
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class DataSetDetailRepositoryTest {

    private lateinit var repository: DataSetDetailRepositoryImpl
    private val d2: D2 = Mockito.mock(D2::class.java, RETURNS_DEEP_STUBS)
    private val dataSetUid = "dataSetUid"
    private val periodUtils: DhisPeriodUtils = mock()
    private val charts: Charts = mock()

    @Before
    fun setUp() {
        repository = DataSetDetailRepositoryImpl(dataSetUid, d2, periodUtils, charts)
    }

    @Test
    fun `Should return false if the user does not have access to write`() {
        val dataSet = dummyDataSet(canWrite = false)

        whenever(d2.dataSetModule().dataSets().uid(dataSetUid).get()) doReturn Single.just(dataSet)

        val testObserver = repository.canWriteAny().test()

        testObserver.assertValueCount(1)
        testObserver.assertNoErrors()
        testObserver.assertValueAt(0) {
            Assert.assertEquals(it, false)
            true
        }
    }

    @Test
    fun `Should return false if the user does not have write access any categoryOptions`() {
        val dataSet = dummyDataSet()
        val categoryOptionCombos = dummyCategoryOptionsCombos(false)

        whenever(d2.dataSetModule().dataSets().uid(dataSetUid).get()) doReturn Single.just(dataSet)
        whenever(
            d2.categoryModule().categoryOptionCombos().withCategoryOptions().byCategoryComboUid(),
        ) doReturn mock()
        whenever(
            d2.categoryModule()
                .categoryOptionCombos().withCategoryOptions()
                .byCategoryComboUid().eq("categoryCombo"),
        ) doReturn mock()
        whenever(
            d2.categoryModule()
                .categoryOptionCombos().withCategoryOptions()
                .byCategoryComboUid().eq("categoryCombo")
                .get(),
        ) doReturn Single.just(categoryOptionCombos)
        whenever(
            d2.userModule()
                .authorities().byName(),
        ) doReturn mock()
        whenever(
            d2.userModule()
                .authorities().byName().eq(AUTH_DATAVALUE_ADD),
        ) doReturn mock()
        whenever(
            d2.userModule()
                .authorities().byName().eq(AUTH_DATAVALUE_ADD)
                .blockingIsEmpty(),
        ) doReturn false

        val testObserver = repository.canWriteAny().test()

        testObserver.assertValueCount(1)
        testObserver.assertNoErrors()
        testObserver.assertValueAt(0) {
            Assert.assertEquals(it, false)
            true
        }
    }

    @Test
    fun `Should return false if the user does not have the authority`() {
        val dataSet = dummyDataSet()
        val categoryOptionCombos = dummyCategoryOptionsCombos(true)

        whenever(d2.dataSetModule().dataSets().uid(dataSetUid).get()) doReturn Single.just(dataSet)
        whenever(
            d2.categoryModule().categoryOptionCombos().withCategoryOptions().byCategoryComboUid(),
        ) doReturn mock()
        whenever(
            d2.categoryModule()
                .categoryOptionCombos().withCategoryOptions()
                .byCategoryComboUid().eq("categoryCombo"),
        ) doReturn mock()
        whenever(
            d2.categoryModule()
                .categoryOptionCombos().withCategoryOptions()
                .byCategoryComboUid().eq("categoryCombo")
                .get(),
        ) doReturn Single.just(categoryOptionCombos)
        whenever(
            d2.userModule()
                .authorities().byName().eq(AUTH_DATAVALUE_ADD),
        ) doReturn mock()
        whenever(
            d2.userModule()
                .authorities().byName().eq(AUTH_DATAVALUE_ADD)
                .blockingIsEmpty(),
        ) doReturn true

        val testObserver = repository.canWriteAny().test()

        testObserver.assertValueCount(1)
        testObserver.assertNoErrors()
        testObserver.assertValueAt(0) {
            Assert.assertEquals(it, false)
            true
        }
    }

    @Test
    fun `Should return false if the user does not have orgUnits of type 'data_capture'`() {
        val dataSet = dummyDataSet()
        val categoryOptionCombos = dummyCategoryOptionsCombos(true)

        whenever(d2.dataSetModule().dataSets().uid(dataSetUid).get()) doReturn Single.just(dataSet)
        whenever(
            d2.categoryModule().categoryOptionCombos().withCategoryOptions().byCategoryComboUid(),
        ) doReturn mock()
        whenever(
            d2.categoryModule()
                .categoryOptionCombos().withCategoryOptions()
                .byCategoryComboUid().eq("categoryCombo"),
        ) doReturn mock()
        whenever(
            d2.categoryModule()
                .categoryOptionCombos().withCategoryOptions()
                .byCategoryComboUid().eq("categoryCombo")
                .get(),
        ) doReturn Single.just(categoryOptionCombos)
        whenever(
            d2.organisationUnitModule()
                .organisationUnits().byDataSetUids(listOf(dataSetUid))
                .byOrganisationUnitScope(any()).blockingCount(),
        ) doReturn 0
        whenever(
            d2.userModule()
                .authorities().byName().eq(AUTH_DATAVALUE_ADD),
        ) doReturn mock()
        whenever(
            d2.userModule()
                .authorities().byName().eq(AUTH_DATAVALUE_ADD)
                .blockingIsEmpty(),
        ) doReturn false

        val testObserver = repository.canWriteAny().test()

        testObserver.assertValueCount(1)
        testObserver.assertNoErrors()
        testObserver.assertValueAt(0) {
            Assert.assertEquals(it, false)
            true
        }
    }

    @Test
    fun `Should return true when user has all write permissions and orgUnits with correct scope`() {
        val dataSet = dummyDataSet()
        val categoryOptionCombos = dummyCategoryOptionsCombos(true)

        whenever(d2.dataSetModule().dataSets().uid(dataSetUid).get()) doReturn Single.just(dataSet)
        whenever(
            d2.categoryModule().categoryOptionCombos().withCategoryOptions().byCategoryComboUid(),
        ) doReturn mock()
        whenever(
            d2.categoryModule()
                .categoryOptionCombos().withCategoryOptions()
                .byCategoryComboUid().eq("categoryCombo"),
        ) doReturn mock()
        whenever(
            d2.categoryModule()
                .categoryOptionCombos().withCategoryOptions()
                .byCategoryComboUid().eq("categoryCombo")
                .get(),
        ) doReturn Single.just(categoryOptionCombos)
        whenever(
            d2.organisationUnitModule()
                .organisationUnits().byDataSetUids(listOf(dataSetUid))
                .byOrganisationUnitScope(any()).blockingCount(),
        ) doReturn 1
        whenever(
            d2.userModule()
                .authorities().byName(),
        ) doReturn mock()
        whenever(
            d2.userModule()
                .authorities().byName().eq(AUTH_DATAVALUE_ADD),
        ) doReturn mock()
        whenever(
            d2.userModule()
                .authorities().byName().eq(AUTH_DATAVALUE_ADD)
                .blockingIsEmpty(),
        ) doReturn false

        val testObserver = repository.canWriteAny().test()

        testObserver.assertValueCount(1)
        testObserver.assertNoErrors()
        testObserver.assertValueAt(0) {
            Assert.assertEquals(it, true)
            true
        }
    }

    private fun dummyDataSet(canWrite: Boolean = true) = DataSet.builder()
        .uid(dataSetUid)
        .displayName("DataSet")
        .categoryCombo(ObjectWithUid.fromIdentifiable(dummyCategoryCombo()))
        .access(Access.create(true, true, DataAccess.create(true, canWrite)))
        .build()

    private fun dummyCategoryCombo(isDefault: Boolean = false) = CategoryCombo.builder()
        .uid("categoryCombo")
        .categoryOptionCombos(dummyCategoryOptionsCombos())
        .isDefault(isDefault)
        .build()

    private fun dummyCategoryOptionsCombos(
        canWrite: Boolean = true,
    ): MutableList<CategoryOptionCombo> {
        val categoryOptionCombo: MutableList<CategoryOptionCombo> = mutableListOf()
        for (i in 1..3)
            categoryOptionCombo.add(
                CategoryOptionCombo
                    .builder()
                    .categoryCombo(ObjectWithUid.create("categoryCombo"))
                    .uid("categoryOptionCombo_$i")
                    .categoryOptions(dummyCategoryOptions(canWrite))
                    .build(),
            )
        return categoryOptionCombo
    }

    private fun dummyCategoryOptions(canWrite: Boolean): MutableList<CategoryOption> {
        val categoryOptions: MutableList<CategoryOption> = mutableListOf()
        for (i in 1..3)
            categoryOptions.add(
                CategoryOption
                    .builder()
                    .uid("categoryOption_$i")
                    .access(Access.create(true, true, DataAccess.create(true, canWrite)))
                    .build(),
            )
        return categoryOptions
    }
}
