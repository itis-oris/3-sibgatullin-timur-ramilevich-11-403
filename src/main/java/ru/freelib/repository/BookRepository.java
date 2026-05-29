package ru.freelib.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.freelib.model.entity.Book;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom {

    List<Book> findByGenresId(Long genreId);

    List<Book> findTop10ByOrderByCreatedAtDesc();

    @Query("SELECT b FROM Book b WHERE b.views > (SELECT AVG(b2.views) FROM Book b2) ORDER BY b.views DESC")
    List<Book> findTop10BooksWithViewsAboveAverage();

    List<Book> findByAuthorId(Long authorId);

    @Modifying
    @Query("UPDATE Book b SET b.views = b.views + 1 WHERE b.id = :id")
    void incrementViews(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Book b SET b.embeddingVector = :vector WHERE b.id = :id")
    void updateEmbeddingVector(@Param("id") Long id, @Param("vector") float[] vector);

    @Query(value = """
            SELECT id FROM books
            WHERE id != :excludeId AND embedding_vector IS NOT NULL
            ORDER BY embedding_vector <=> CAST(:vector AS vector)
            LIMIT :limit
            """, nativeQuery = true)
    List<Long> findSimilarIds(@Param("excludeId") Long excludeId,
                              @Param("vector") String vector,
                              @Param("limit") int limit);
}