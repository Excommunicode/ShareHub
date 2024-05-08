package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * The RequestRepository interface extends the JpaRepository interface and provides methods for accessing and manipulating {@link Request} entities in the database.
 *
 * <p>Common methods provided by JpaRepository include saving, deleting, and finding entities based on their primary key. In addition to these common methods,
 * the RequestRepository interface provides additional methods for querying and retrieving Request entities in various ways.</p>
 */
public interface RequestRepository extends JpaRepository<Request, Long> {

    /**
     * Returns a list of {@link Request} objects based on the ID of the requestor.
     *
     * @param id the ID of the requestor
     * @return a list of {@link Request} objects that were created by the specified requestor
     */
    List<Request> findAllByRequestor_Id(Long id);

    /**
     * Finds all requests with the given requestor ID.
     *
     * @param id       the ID of the requestor
     * @param pageable the pageable information for pagination and sorting
     * @return a {@link Page} of {@link Request} objects representing the requests found
     */
    Page<Request> findAllByRequestor_Id(Long id, Pageable pageable);

    /**
     * Retrieves a page of Request entities where the ID of the requestor is not equal to the specified ID.
     *
     * @param id       the ID of the requestor to exclude
     * @param pageable the pagination information
     * @return a page of Request entities where the ID of the requestor is not equal to the specified ID
     */
    Page<Request> findAllByRequestor_IdNot(Long id, Pageable pageable);
}
