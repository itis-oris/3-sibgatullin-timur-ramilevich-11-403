package ru.freelib.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.freelib.model.entity.Book;
import java.util.List;

public interface BookRepositoryCustom {
    Page<Book> findByDynamicFilters(String titleFragment, String authorName, List<Long> genreIds,
                                    Long minViews, Long maxViews, Pageable pageable);
}