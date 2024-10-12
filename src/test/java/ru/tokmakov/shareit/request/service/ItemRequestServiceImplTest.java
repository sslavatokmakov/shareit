package ru.tokmakov.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.tokmakov.shareit.exception.request.ItemRequestNotFoundException;
import ru.tokmakov.shareit.exception.user.UserNotFoundException;
import ru.tokmakov.shareit.item.model.Item;
import ru.tokmakov.shareit.item.repository.ItemRepository;
import ru.tokmakov.shareit.request.dto.ItemRequestDto;
import ru.tokmakov.shareit.request.model.ItemRequest;
import ru.tokmakov.shareit.request.repository.ItemRequestRepository;
import ru.tokmakov.shareit.user.model.User;
import ru.tokmakov.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager em;

    User booker;
    User itemOwner;
    Item availableItem;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        itemOwner = new User();
        itemOwner.setName("Item Owner");
        itemOwner.setEmail("owner@example.com");
        itemOwner = userRepository.save(itemOwner);

        availableItem = new Item();
        availableItem.setName("Available Item");
        availableItem.setDescription("A test item for booking");
        availableItem.setAvailable(true);
        availableItem.setOwner(itemOwner);
        availableItem = itemRepository.save(availableItem);
    }


    @Test
    void testSaveItemRequest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("New item request");

        ItemRequest savedItemRequest = itemRequestService.save(itemRequest, booker.getId());

        TypedQuery<ItemRequest> query = em.createQuery("select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest foundRequest = query.setParameter("id", savedItemRequest.getId()).getSingleResult();

        assertThat(foundRequest.getId(), notNullValue());
        assertThat(foundRequest.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(foundRequest.getUser().getId(), equalTo(booker.getId()));
        assertThat(foundRequest.getCreated(), notNullValue());
    }

    @Test
    void testFindByUserId() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Item request by user");
        itemRequest.setUser(booker);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);

        List<ItemRequestDto> foundRequests = itemRequestService.findByUserId(booker.getId());

        assertThat(foundRequests.getFirst().getId(), equalTo(itemRequest.getId()));

        TypedQuery<ItemRequest> query = em.createQuery("select ir from ItemRequest ir where ir.user.id = :userId", ItemRequest.class);
        List<ItemRequest> requests = query.setParameter("userId", booker.getId()).getResultList();
        assertThat(requests, hasSize(1));
        assertThat(requests.getFirst().getId(), equalTo(itemRequest.getId()));
    }

    @Test
    void testFindAll() {
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("First request");
        itemRequest1.setUser(itemOwner);
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("Second request");
        itemRequest2.setUser(booker);
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest2);

        List<ItemRequest> allRequests = itemRequestService.findAll(booker.getId());

        assertThat(allRequests.size(), equalTo(2));
        assertThat(allRequests.getFirst().getId(), equalTo(itemRequest2.getId()));

        TypedQuery<ItemRequest> query = em.createQuery("select ir from ItemRequest ir where ir.user.id != :userId", ItemRequest.class);
        List<ItemRequest> requests = query.setParameter("userId", booker.getId()).getResultList();
        assertThat(requests, hasSize(1));
        assertThat(requests.getFirst().getId(), equalTo(itemRequest1.getId()));
    }

    @Test
    void testFindById() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Find by ID request");
        itemRequest.setUser(booker);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest = itemRequestRepository.save(itemRequest);

        ItemRequestDto foundRequest = itemRequestService.findById(itemRequest.getId());

        assertThat(foundRequest, notNullValue());
        assertThat(foundRequest.getId(), equalTo(itemRequest.getId()));
        assertThat(foundRequest.getDescription(), equalTo(itemRequest.getDescription()));

        TypedQuery<ItemRequest> query = em.createQuery("select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest dbRequest = query.setParameter("id", itemRequest.getId()).getSingleResult();
        assertThat(dbRequest.getId(), equalTo(itemRequest.getId()));
    }

    @Test
    void testFindByIdNotFound() {
        long nonExistentRequestId = 999L;

        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.findById(nonExistentRequestId));
    }

    @Test
    void testFindAll_UserNotFound() {
        long nonExistentUserId = 999L;

        assertThatThrownBy(() -> itemRequestService.findAll(nonExistentUserId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with id " + nonExistentUserId + " not found");
    }
}
