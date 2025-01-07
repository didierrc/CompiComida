package com.example.compicomida

import com.example.compicomida.model.localDb.LocalDatabase
import com.example.compicomida.model.localDb.dao.GroceryItemDao
import com.example.compicomida.model.localDb.dao.GroceryListDao
import com.example.compicomida.model.localDb.dao.ItemCategoryDao
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.model.localDb.entities.GroceryList
import com.example.compicomida.model.localDb.entities.ItemCategory
import com.example.compicomida.model.GroceryRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class GroceryRepositoryTest {

    private lateinit var mockGroceryItemDao: GroceryItemDao
    private lateinit var mockGroceryListDao: GroceryListDao
    private lateinit var mockItemCategoryDao: ItemCategoryDao
    private lateinit var repository: GroceryRepository

    private val fixedClock = Clock.fixed(LocalDateTime.of(
        2025, 1, 6, 0, 0, 0)
        .toInstant(ZoneOffset.UTC), ZoneId.systemDefault())

    @Before
    fun setUp() {
        mockGroceryItemDao = mock(GroceryItemDao::class.java)
        mockGroceryListDao = mock(GroceryListDao::class.java)
        mockItemCategoryDao = mock(ItemCategoryDao::class.java)

        repository = GroceryRepository(mockGroceryListDao, mockGroceryItemDao, mockItemCategoryDao, fixedClock)
    }

    @Test
    fun `getListSize should call DAO with correct listId`() = runTest {
        val listId = 1
        `when`(mockGroceryListDao.getListSize(listId)).thenReturn(5)

        val result = repository.getListSize(listId)

        verify(mockGroceryListDao).getListSize(listId)
        assertEquals(5, result)
    }

    @Test
    fun `getLastInsertedList should return the last inserted grocery list`() = runTest {
        val groceryList = GroceryList(1, "Groceries", LocalDateTime.now(fixedClock))
        `when`(mockGroceryListDao.getLastInserted()).thenReturn(groceryList)

        val result = repository.getLastInsertedList()

        verify(mockGroceryListDao).getLastInserted()
        assertEquals(groceryList, result)
    }

    @Test
    fun `getAllLists should return all grocery lists`() = runTest {
        val groceryLists = listOf(
            GroceryList(1, "Groceries", LocalDateTime.now(fixedClock)),
            GroceryList(2, "Supplies", LocalDateTime.now(fixedClock))
        )
        `when`(mockGroceryListDao.getAll()).thenReturn(groceryLists)

        val result = repository.getAllLists()

        verify(mockGroceryListDao).getAll()
        assertEquals(groceryLists, result)
    }

    @Test
    fun `deleteGroceryList should call DAO with correct list`() = runTest {
        val groceryList = GroceryList(1, "Groceries", LocalDateTime.now(fixedClock))

        repository.deleteGroceryList(groceryList)

        verify(mockGroceryListDao).delete(groceryList)
    }

    @Test
    fun `getGroceryItemsByListId should return items for the given listId`() = runTest {
        val listId = 1
        val groceryItems = listOf(
            GroceryItem(
                itemId = 1,
                listId = listId,
                categoryId = 1,
                itemName = "Leche",
                quantity = 2.0,
                unit = "L",
                price = 15.0,
                isPurchased = false,
                itemPhotoUri = null
            ),
            GroceryItem(
                itemId = 2,
                listId = listId,
                categoryId = 2,
                itemName = "Pan",
                quantity = 2.0,
                unit = "Unidades",
                price = 15.0,
                isPurchased = false,
                itemPhotoUri = null
            )
        )

        `when`(mockGroceryItemDao.getByListId(listId)).thenReturn(groceryItems)

        val result = repository.getGroceryItemsByListId(listId)

        verify(mockGroceryItemDao).getByListId(listId)
        assertEquals(groceryItems, result)
    }

    @Test
    fun `addGroceryList should call DAO to add a new grocery list`() = runTest {
        val listName = "TestList"

        repository.addGroceryList(listName)

        val groceryList = GroceryList(0,listName,LocalDateTime.now(fixedClock))

        verify(mockGroceryListDao).add(groceryList)
    }

    @Test
    fun `getGroceryListByName should return grocery list for given name`() = runTest {
        val listName = "TestList"
        val groceryList = GroceryList(1, listName, LocalDateTime.now(fixedClock))
        `when`(mockGroceryListDao.getByName(listName)).thenReturn(groceryList)

        val result = repository.getGroceryListByName(listName)

        verify(mockGroceryListDao).getByName(listName)
        assertEquals(groceryList, result)
    }

    @Test
    fun `addGroceryItem should call DAO to add a new grocery item`() = runTest {
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

        repository.addGroceryItem(groceryItem)

        verify(mockGroceryItemDao).add(groceryItem)
    }

    @Test
    fun `checkGroceryItem should update the isPurchased status`() = runTest {
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
        `when`(mockGroceryItemDao.getById(groceryItem.itemId)).thenReturn(groceryItem)

        repository.checkGroceryItem(true, groceryItem.itemId)

        verify(mockGroceryItemDao).update(groceryItem.copy(isPurchased = true))
    }

    @Test
    fun `deleteGroceryItemByID should delete the correct item`() = runTest {
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
        `when`(mockGroceryItemDao.getById(groceryItem.itemId)).thenReturn(groceryItem)

        repository.deleteGroceryItemByID(groceryItem.itemId)

        verify(mockGroceryItemDao).delete(groceryItem)
    }

    @Test
    fun `getAllCategories should return all categories`() = runTest {
        val categories = listOf(
            ItemCategory(1, "Fruta"),
            ItemCategory(2, "Bebida")
        )
        `when`(mockItemCategoryDao.getAll()).thenReturn(categories)

        val result = repository.getAllCategories()

        verify(mockItemCategoryDao).getAll()
        assertEquals(categories, result)
    }

    @Test
    fun `getGroceryListByID should return grocery list for given ID`() = runTest {
        val groceryList = GroceryList(1, "TestList", LocalDateTime.now(fixedClock))
        `when`(mockGroceryListDao.getById(groceryList.listId)).thenReturn(groceryList)

        val result = repository.getGroceryListByID(groceryList.listId)

        verify(mockGroceryListDao).getById(groceryList.listId)
        assertEquals(groceryList, result)
    }
}