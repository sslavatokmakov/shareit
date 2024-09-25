package ru.tokmakov.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tokmakov.shareit.item.dto.ItemDto;
import ru.tokmakov.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Override
    Optional<Item> findById(Long itemId);

    @Query("SELECT new ru.tokmakov.shareit.item.dto.ItemDto(i.id, i.name, i.description, i.available) " +
           "FROM Item i " +
           "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
           "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
           "AND i.available = TRUE")
    List<ItemDto> search(String text);

    @Query("SELECT new ru.tokmakov.shareit.item.dto.ItemDto(i.id, i.name, i.description, i.available) " +
           "FROM Item i JOIN i.owner u WHERE u.id = ?1")
    List<ItemDto> allItemsFromUser(long userId);
}
