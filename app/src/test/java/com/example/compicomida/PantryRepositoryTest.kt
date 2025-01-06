package com.example.compicomida

import com.example.compicomida.model.localDb.LocalDatabase
import com.example.compicomida.model.localDb.dao.PantryItemDao
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.model.localDb.entities.PantryItem
import com.example.compicomida.model.PantryRepository
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset


class PantryRepositoryTest {

    private lateinit var pantryRepository: PantryRepository
    private lateinit var mockDatabase: LocalDatabase
    private lateinit var mockDao: PantryItemDao

    private val fixedClock = Clock.fixed(LocalDateTime.of(
        2025, 1, 6, 0, 0, 0)
        .toInstant(ZoneOffset.UTC), ZoneId.systemDefault())

    @Before
    fun setUp() {
        mockDatabase = mock(LocalDatabase::class.java)
        mockDao = mock(PantryItemDao::class.java)

        `when`(mockDatabase.pantryItemDao).thenReturn(mockDao)

        pantryRepository = PantryRepository(mockDatabase,fixedClock)
    }

    @Test
    fun `getCloseExpireItems should call DAO without filter`() = runTest {
        pantryRepository.getCloseExpireItems()

        verify(mockDao).getCloseExpireItems()
    }

    @Test
    fun `getCloseExpireItems with TODAY filter should call DAO with correct date`() = runTest {


        pantryRepository.getCloseExpireItems("TODAY")

        verify(mockDao).getCloseExpireItems(LocalDateTime.now(fixedClock))
    }

    @Test
    fun `getAlreadyExpiredItems should call DAO`() = runTest {
        pantryRepository.getAlreadyExpiredItems()

        verify(mockDao).getAlreadyExpiredItems()
    }

    @Test
    fun `getPantryItems should call DAO`() = runTest {
        pantryRepository.getPantryItems()

        verify(mockDao).getAll()
    }

    @Test
    fun `addPantryItem should call DAO with correct item`() = runTest {
        val pantryItem = PantryItem(
            pantryId = 0,
            itemId = null,
            expirationDate = LocalDateTime.now(fixedClock).plusDays(10),
            pantryName = "Manzanas",
            quantity = 2.0,
            unit = "kg",
            lastUpdate = LocalDateTime.now(fixedClock),
            pantryPhotoUri = null
        )

        pantryRepository.addPantryItem(pantryItem)

        verify(mockDao).add(pantryItem)
    }

    @Test
    fun `getPantryItemById should call DAO with correct ID`() = runTest {
        val id = 1

        pantryRepository.getPantryItemById(id)

        verify(mockDao).getById(id)
    }

    @Test
    fun `deletePantryItem should call DAO with correct item`() = runTest {
        val pantryItem = PantryItem(
            pantryId = 0,
            itemId = null,
            expirationDate = LocalDateTime.now(fixedClock).plusDays(10),
            pantryName = "Manzanas",
            quantity = 2.0,
            unit = "kg",
            lastUpdate = LocalDateTime.now(),
            pantryPhotoUri = null
        )

        pantryRepository.deletePantryItem(pantryItem)

        verify(mockDao).delete(pantryItem)
    }

    @Test
    fun `addPantryItemsFromGroceryLists should create and add pantry item`() = runTest {
        val groceryItem = GroceryItem(
            itemId = 1,
            listId = 1,
            categoryId = 1,
            itemName = "Leche",
            quantity = 2.0,
            unit = "L",
            price = 15.0,
            isPurchased = true,
            itemPhotoUri = null
        )

        val expectedPantryItem = PantryItem(
            pantryId = 0,
            itemId = groceryItem.itemId,
            expirationDate = LocalDateTime.now(fixedClock),
            pantryName = groceryItem.itemName,
            quantity = groceryItem.quantity,
            unit = groceryItem.unit,
            lastUpdate = LocalDateTime.now(fixedClock),
            pantryPhotoUri = groceryItem.itemPhotoUri
        )

        pantryRepository.addPantryItemsFromGroceryLists(groceryItem)

        verify(mockDao).add(expectedPantryItem)
    }

    @Test
    fun `deletePantryItemsFromGroceryLists should find and delete pantry item`() = runTest {
        val groceryItem = GroceryItem(
            itemId = 1,
            listId = 1,
            categoryId = 1,
            itemName = "Leche",
            quantity = 2.0,
            unit = "L",
            price = 15.0,
            isPurchased = false,
            itemPhotoUri = null
        )

        val pantryItem = PantryItem(
            pantryId = 1,
            itemId = groceryItem.itemId,
            pantryName = groceryItem.itemName,
            quantity = groceryItem.quantity,
            unit = groceryItem.unit,
            expirationDate = LocalDateTime.now(fixedClock),
            lastUpdate = LocalDateTime.now(fixedClock),
            pantryPhotoUri = groceryItem.itemPhotoUri
        )

        `when`(mockDao.getByGroceryId(groceryItem.itemId)).thenReturn(pantryItem)

        pantryRepository.deletePantryItemsFromGroceryLists(groceryItem)

        verify(mockDao).delete(pantryItem)
    }
}